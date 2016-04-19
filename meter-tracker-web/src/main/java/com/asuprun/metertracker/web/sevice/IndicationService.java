package com.asuprun.metertracker.web.sevice;

import com.asuprun.metertracker.core.exception.BorderNotFoundException;
import com.asuprun.metertracker.core.image.DigitRecognizer;
import com.asuprun.metertracker.core.image.IndicationImageProcessor;
import com.asuprun.metertracker.core.image.IndicationImageProcessorImpl;
import com.asuprun.metertracker.core.utils.ImageUtils;
import com.asuprun.metertracker.web.domain.*;
import com.asuprun.metertracker.web.exception.DataConflictException;
import com.asuprun.metertracker.web.repository.*;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.asuprun.metertracker.core.utils.ImageUtils.bytesToImage;
import static com.asuprun.metertracker.core.utils.ImageUtils.imageToJpgBytes;
import static com.asuprun.metertracker.web.domain.ResourceBinding.Type.INDICATION;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Service
public class IndicationService {

    private static final Logger logger = LoggerFactory.getLogger(IndicationService.class);

    public static final String HASH_ALGORITHM = "SHA-256";

    private IndicationImageProcessor extractor;
    private DigitRecognizer recognizer;

    @Autowired
    private IndicationRepository indicationRepository;

    @Autowired
    private DigitRepository digitRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceBindingRepository resourceBindingRepository;

    @Autowired
    private MeterRepository meterRepository;

    @PostConstruct
    private void init() {
        extractor = new IndicationImageProcessorImpl();
        recognizer = new DigitRecognizer();
        trainRecognizer();
    }

    @Transactional
    public Indication parseAndSaveIndication(byte[] bytes, long meterId) {
        Meter meter = meterRepository.findById(meterId).orElseThrow(() -> {
            logger.debug("Cannot parse and save image. Meter with id '{}' not found", meterId);
            return new NoSuchElementException("Meter not found");
        });

        Indication indication = new Indication();
        indication.setMeter(meter);
        indication.setUploaded(new Date());
        indication.setCreated(readCreatedOnMetadata(bytes).orElse(null));

        String hash = calculateHash(bytes);
        indication.setHash(hash);
        indication = saveIndication(indication);

        ResourceBinding bindingFull = new ResourceBinding();
        bindingFull.setResourceBindingId(new ResourceBindingId(ResourceBinding.Type.ORIGINAL, indication));
        bindingFull.setResource(resourceRepository.save(new Resource(bytes)));

        ResourceBinding bindingIndication = new ResourceBinding();
        try {
            bytes = imageToJpgBytes(extractor.extractIndicationRegion(bytesToImage(bytes)));
            bindingIndication.setResourceBindingId(new ResourceBindingId(INDICATION, indication));
            bindingIndication.setResource(resourceRepository.save(new Resource(bytes)));
        } catch (BorderNotFoundException e) {
            logger.warn("Cannot detect indication borders.", e);
            throw new IllegalArgumentException("Cannot detect indication region.");
        } catch (Exception e) {
            logger.warn("Cannot uploaded image.", e);
            throw new IllegalArgumentException("Bad image file provided.");
        }

        indication.getImages().put(ResourceBinding.Type.ORIGINAL, resourceBindingRepository.save(bindingFull));
        indication.getImages().put(INDICATION, resourceBindingRepository.save(bindingIndication));
        return indication;
    }

    private Optional<Metadata> readMetadata(byte[] bytes) {
        try {
            return Optional.of(ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes)));
        } catch (ImageProcessingException | IOException e) {
            logger.warn("Cannot read image metadata");
            logger.debug("Cannot read image metadata", e);
        }
        return Optional.empty();
    }

    private Optional<Date> readCreatedOnMetadata(byte[] bytes) {
        return readMetadata(bytes)
                .map(m -> m.getFirstDirectoryOfType(ExifSubIFDDirectory.class))
                .map(d -> d.getDateOriginal(TimeZone.getDefault()));
    }

    @Transactional
    public Indication saveIndication(Indication indication) {
        indicationRepository.findByHash(indication.getHash())
                .ifPresent(i -> {
                    logger.debug("Cannot save indication because image with hash '{}' already exists");
                    throw new DataConflictException("This image was already uploaded");
                });
        return indicationRepository.save(indication);
    }

    public Optional<Indication> findById(long id) {
        return Optional.ofNullable(indicationRepository.findOne(id));
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

    public void delete(long id) {
        findById(id).orElseThrow(() -> {
            logger.debug("Cannot delete not existing indication with id: {}", id);
            return new NoSuchElementException("No such indication");
        });
        indicationRepository.delete(id);
    }

    public List<Digit> recognize(long indicationId) {
        Indication indication = findById(indicationId).orElseThrow(() -> {
            logger.debug("Cannot recognize not existing indication with id: {}", indicationId);
            return new NoSuchElementException("No such indication");
        });
        BufferedImage extracted = bytesToImage(indication.getImages().get(INDICATION).getResource().getData());
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
        digits.stream().filter(d -> d.getImage() != null).map(digitRepository::save).collect(toList());
        String[] value = splitValue(digits.stream().map(Digit::getValue).collect(joining()), indication.getMeter());

        indication.setValue(Double.parseDouble(String.join(".", value[0], value[1])));
        indicationRepository.save(indication);

        trainRecognizer();
    }

    private String[] splitValue(String value, Meter meter) {
        int splitIndex = meter.getCapacity() - meter.getMinorDigits();
        return new String[]{
                value.substring(0, splitIndex),
                value.substring(splitIndex)
        };
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

    private String calculateHash(byte[] image) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(image);
            return DatatypeConverter.printHexBinary(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.warn("Cannot calculate image hash.", e);
        }
        return null;
    }

}
