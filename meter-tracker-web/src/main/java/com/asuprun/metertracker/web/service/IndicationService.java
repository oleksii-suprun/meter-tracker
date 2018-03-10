package com.asuprun.metertracker.web.service;

import com.asuprun.metertracker.core.exception.BorderNotFoundException;
import com.asuprun.metertracker.core.image.DigitRecognizer;
import com.asuprun.metertracker.core.image.IndicationImageProcessor;
import com.asuprun.metertracker.core.image.IndicationImageProcessorImpl;
import com.asuprun.metertracker.core.utils.ImageUtils;
import com.asuprun.metertracker.web.domain.Digit;
import com.asuprun.metertracker.web.domain.ImageInfo;
import com.asuprun.metertracker.web.domain.Indication;
import com.asuprun.metertracker.web.domain.Meter;
import com.asuprun.metertracker.web.filestorage.FileMetaData;
import com.asuprun.metertracker.web.filestorage.FileStorage;
import com.asuprun.metertracker.web.repository.DigitRepository;
import com.asuprun.metertracker.web.repository.IndicationRepository;
import com.asuprun.metertracker.web.repository.MeterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.awt.image.BufferedImage;
import java.util.*;

import static com.asuprun.metertracker.core.utils.ImageUtils.bytesToImage;
import static com.asuprun.metertracker.core.utils.ImageUtils.imageToJpgBytes;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Service
public class IndicationService {

    private static final Logger logger = LoggerFactory.getLogger(IndicationService.class);

    private IndicationImageProcessor extractor;
    private DigitRecognizer recognizer;

    private IndicationRepository indicationRepository;
    private DigitRepository digitRepository;
    private MeterRepository meterRepository;
    private FileStorage fileStorage;

    @Autowired
    public IndicationService(IndicationRepository indicationRepository,
                             DigitRepository digitRepository,
                             MeterRepository meterRepository,
                             FileStorage fileStorage) {
        this.indicationRepository = indicationRepository;
        this.digitRepository = digitRepository;
        this.meterRepository = meterRepository;
        this.fileStorage = fileStorage;
    }

    @PostConstruct
    private void init() {
        extractor = new IndicationImageProcessorImpl();
        recognizer = new DigitRecognizer();
        trainRecognizer();
    }

    @Transactional
    public Indication parseAndSaveIndication(String fileName, byte[] bytes, long meterId) {
        Meter meter = meterRepository.findById(meterId).orElseThrow(() -> {
            logger.debug("Cannot parse and save image. Meter with id '{}' not found", meterId);
            return new NoSuchElementException("Meter not found");
        });

        // extract region first in order to avoid files save if extraction not possible
        byte[] indicationData = extractIndicationRegion(bytes);

        // we wont save files if we cannot extract indication region because exception will be thrown
        FileMetaData originalFileMeta = fileStorage.save(bytes, fileName);
        FileMetaData indicationFileMeta = fileStorage.save(indicationData, "i_" + fileName);
        Date uploadedAt = new Date();

        Indication indication = new Indication();
        indication.setMeter(meter);
        indication.setOriginalImageInfo(new ImageInfo() {{
            setUploadedAt(uploadedAt);
            setFileName(originalFileMeta.getFileName());
            setHash(originalFileMeta.getHash());
            setStorageId(originalFileMeta.getFileId());
            setCreatedAt(originalFileMeta.getCreatedAt());
        }});
        indication.setIndicationImageInfo(new ImageInfo() {{
            setUploadedAt(uploadedAt);
            setFileName(indicationFileMeta.getFileName());
            setStorageId(indicationFileMeta.getFileId());
            setCreatedAt(indicationFileMeta.getCreatedAt());
        }});
        indication.setCreatedAt(indication.getOriginalImageInfo().getCreatedAt());

        try {
            return indicationRepository.save(indication);
        } catch (Throwable e) {
            // we have to delete files manually to keep data consistent
            fileStorage.delete(indication.getIndicationImageInfo().toFileMetaData());
            fileStorage.delete(indication.getOriginalImageInfo().toFileMetaData());
            throw e;
        }
    }

    private byte[] extractIndicationRegion(byte[] original) {
        try {
            return imageToJpgBytes(extractor.extractIndicationRegion(bytesToImage(original)));
        } catch (BorderNotFoundException e) {
            logger.warn("Cannot detect indication borders.", e);
            throw new IllegalArgumentException("Cannot detect indication region.");
        } catch (Exception e) {
            logger.warn("Cannot process image.", e);
            throw new IllegalArgumentException("Bad image file provided.");
        }
    }

    public Optional<Indication> findById(long id) {
        return indicationRepository.findById(id);
    }

    /**
     * Searches for indications by meter and state. State is represented by boolean value which means if
     * indication has been already recognized or not.
     *
     * @param meterId      id of {@link Meter} entity. If value is
     *                     {@code null} - indications for all meters will be returned.
     * @param unrecognized nullable boolean value. Represents state of indication. If value is {@code true} -
     *                     only unrecognized indications will be returned, {@code false} - otherwise. If value
     *                     is {@code null} - both recognized and unrecognized indications will be returned.
     * @return list of indications according to passed parameters.
     */
    public List<Indication> findByTypeAndState(Long meterId, Boolean unrecognized) {
        return indicationRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Optional.ofNullable(meterId).ifPresent(id -> predicates.add(cb.equal(root.get("meter").get("id"), id)));
            Optional.ofNullable(unrecognized).ifPresent(u -> predicates.add(unrecognized
                    ? cb.isNull(root.get("value"))
                    : cb.isNotNull(root.get("value"))));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });
    }

    @Transactional
    public void delete(long id) {
        Indication indication = indicationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.debug("Cannot delete not existing indication with id: {}", id);
                    return new NoSuchElementException("No such indication");
                });

        indicationRepository.deleteById(indication.getId());
        fileStorage.delete(indication.getIndicationImageInfo().toFileMetaData());
        fileStorage.delete(indication.getOriginalImageInfo().toFileMetaData());
    }

    @Transactional
    public void update(Indication indication) {
        Indication persisted = indicationRepository.findById(indication.getId()).orElseThrow(() -> {
            logger.debug("Cannot delete not existing indication with id: {}", indication.getId());
            return new NoSuchElementException("No such indication");
        });

        // only value and consumption can be updated
        persisted.setValue(indication.getValue());
        persisted.setConsumption(calculateConsumption(
                persisted.getMeter().getId(),
                persisted.getCreatedAt(),
                persisted.getValue()));

        indicationRepository.save(persisted);
    }

    public List<Digit> recognize(long indicationId) {
        Indication indication = findById(indicationId).orElseThrow(() -> {
            logger.debug("Cannot recognize not existing indication with id: {}", indicationId);
            return new NoSuchElementException("No such indication");
        });
        BufferedImage extracted = bytesToImage(fileStorage.read(indication.getIndicationImageInfo().toFileMetaData()));
        return extractor.extractDigits(extracted, indication.getMeter().getCapacity()).stream()
                .map(i -> new Digit(ImageUtils.imageToJpgBytes(i), recognizer.isTrained()
                        ? recognizer.recognize(i).orElse(null)
                        : null))
                .collect(toList());
    }

    @Transactional
    public void saveDigits(long indicationId, List<Digit> digits) {
        Indication indication = findById(indicationId).orElseThrow(() -> {
            logger.debug("Cannot save digits for not existing indication with id: {}", indicationId);
            return new NoSuchElementException("No such indication");
        });
        if (digits.size() != indication.getMeter().getCapacity()) {
            logger.debug("Digits number mismatch. Expected number of digits = {}, but actual = {}",
                    indication.getMeter().getCapacity(), digits.size());
            throw new IllegalArgumentException("Incorrect number of digits");
        }
        digits.stream().filter(d -> d.getImage() != null).forEach(digitRepository::save);

        double value = parseValue(digits.stream().map(Digit::getValue).collect(joining()), indication.getMeter());
        int consumption = calculateConsumption(indication.getMeter().getId(), indication.getCreatedAt(), value);

        indication.setValue(value);
        indication.setConsumption(consumption);
        indicationRepository.save(indication);

        trainRecognizer();
    }

    private int calculateConsumption(long meterId, Date created, double value) {
        // update subsequent indication if present
        indicationRepository.findRecognizedAfter(meterId, created).ifPresent(i -> {
            i.setConsumption((int) Math.floor(i.getValue()) - ((int) value));
            indicationRepository.save(i);
        });

        return indicationRepository.findRecognizedBefore(meterId, created)
                .map(i -> ((int) value) - (int) Math.floor(i.getValue()))
                .orElse(0);
    }

    private double parseValue(String value, Meter meter) {
        int splitIndex = meter.getCapacity() - meter.getMinorDigits();
        return Double.parseDouble(String.join(".", value.substring(0, splitIndex), value.substring(splitIndex)));
    }

    private void trainRecognizer() {
        List<Digit> all = digitRepository.findAll();
        if (!all.isEmpty()) {
            recognizer.train(
                    all.stream().map(d -> bytesToImage(d.getImage())).collect(toList()),
                    all.stream().map(Digit::getValue).collect(toList())
            );
        }
    }
}
