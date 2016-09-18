package com.asuprun.metertracker.web.service;

import com.asuprun.metertracker.web.domain.Digit;
import com.asuprun.metertracker.web.domain.Indication;
import com.asuprun.metertracker.web.domain.Meter;
import com.asuprun.metertracker.web.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IndicationServiceTest {

    @InjectMocks
    private IndicationService indicationService;

    @Mock
    private IndicationRepository indicationRepository;

    @Mock
    private DigitRepository digitRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCalculateConsumptionForFirstIndication() {
        Meter meter = new Meter();
        meter.setId(1);
        meter.setCapacity(8);
        meter.setMinorDigits(3);

        Indication indication = new Indication();
        indication.setId(1);
        indication.setMeter(meter);
        indication.setCreated(new Date());

        List<Digit> digits = Arrays.stream(new int[]{0, 1, 2, 3, 4, 5, 6, 7})
                .mapToObj(i -> new Digit(null, String.valueOf(i)))
                .collect(Collectors.toList());

        when(indicationRepository.findById(indication.getId())).thenReturn(Optional.of(indication));
        when(indicationRepository.findRecognizedAfter(eq(meter.getId()), eq(indication.getCreated())))
                .thenReturn(Optional.empty());
        when(indicationRepository.findRecognizedBefore(eq(meter.getId()), eq(indication.getCreated())))
                .thenReturn(Optional.empty());
        when(indicationRepository.save(same(indication))).thenReturn(indication);
        when(digitRepository.findAll()).thenReturn(Collections.emptyList());

        indicationService.saveDigits(indication.getId(), digits);
        assertEquals(0, indication.getConsumption().intValue());

        verify(indicationRepository).findById(indication.getId());
        verify(indicationRepository).findRecognizedBefore(eq(meter.getId()), eq(indication.getCreated()));
        verify(indicationRepository).findRecognizedAfter(eq(meter.getId()), eq(indication.getCreated()));
        verify(indicationRepository).save(same(indication));
        verify(digitRepository).findAll();
    }

    @Test
    public void testCalculateConsumptionForSubsequentIndication() {
        Meter meter = new Meter();
        meter.setId(1);
        meter.setCapacity(8);
        meter.setMinorDigits(3);

        Indication firstIndication = new Indication();
        firstIndication.setId(1);
        firstIndication.setMeter(meter);
        firstIndication.setCreated(new Date());
        firstIndication.setValue(59.942);
        firstIndication.setConsumption(0);

        Indication secondIndication = new Indication();
        secondIndication.setId(2);
        secondIndication.setMeter(meter);
        secondIndication.setCreated(new Date());

        List<Digit> digits = "00062752".chars()
                .mapToObj(i -> new Digit(null, String.valueOf((char) i)))
                .collect(Collectors.toList());

        when(indicationRepository.findById(secondIndication.getId())).thenReturn(Optional.of(secondIndication));
        when(indicationRepository.findRecognizedAfter(eq(meter.getId()), eq(secondIndication.getCreated())))
                .thenReturn(Optional.empty());
        when(indicationRepository.findRecognizedBefore(eq(meter.getId()), eq(secondIndication.getCreated())))
                .thenReturn(Optional.of(firstIndication));
        when(indicationRepository.save(same(secondIndication))).thenReturn(secondIndication);
        when(digitRepository.findAll()).thenReturn(Collections.emptyList());

        indicationService.saveDigits(secondIndication.getId(), digits);
        assertEquals(3, secondIndication.getConsumption().intValue());

        verify(indicationRepository).findById(secondIndication.getId());
        verify(indicationRepository).findRecognizedBefore(eq(meter.getId()), eq(secondIndication.getCreated()));
        verify(indicationRepository).findRecognizedAfter(eq(meter.getId()), eq(secondIndication.getCreated()));
        verify(indicationRepository).save(same(secondIndication));
        verify(digitRepository).findAll();
    }

    @Test
    public void testCalculateConsumptionForIntermediateIndication() {
        Meter meter = new Meter();
        meter.setId(1);
        meter.setCapacity(8);
        meter.setMinorDigits(3);

        Indication firstIndication = new Indication();
        firstIndication.setId(1);
        firstIndication.setMeter(meter);
        firstIndication.setCreated(new Date());
        firstIndication.setValue(59.942);
        firstIndication.setConsumption(0);

        Indication secondIndication = new Indication();
        secondIndication.setId(2);
        secondIndication.setMeter(meter);
        secondIndication.setCreated(new Date());
        secondIndication.setValue(62.752);

        Indication indication = new Indication();
        indication.setId(3);
        indication.setMeter(meter);
        indication.setCreated(new Date());

        List<Digit> digits = "00060122".chars()
                .mapToObj(i -> new Digit(null, String.valueOf((char) i)))
                .collect(Collectors.toList());

        when(indicationRepository.findById(indication.getId())).thenReturn(Optional.of(indication));
        when(indicationRepository.findRecognizedBefore(eq(meter.getId()), eq(indication.getCreated())))
                .thenReturn(Optional.of(firstIndication));
        when(indicationRepository.findRecognizedAfter(eq(meter.getId()), eq(indication.getCreated())))
                .thenReturn(Optional.of(secondIndication));
        when(indicationRepository.save(same(indication))).thenReturn(indication);
        when(digitRepository.findAll()).thenReturn(Collections.emptyList());

        indicationService.saveDigits(indication.getId(), digits);
        assertEquals(0, firstIndication.getConsumption().intValue());
        assertEquals(1, indication.getConsumption().intValue());
        assertEquals(2, secondIndication.getConsumption().intValue());

        verify(indicationRepository).findById(indication.getId());
        verify(indicationRepository).findRecognizedBefore(eq(meter.getId()), eq(indication.getCreated()));
        verify(indicationRepository).findRecognizedAfter(eq(meter.getId()), eq(indication.getCreated()));
        verify(indicationRepository).save(same(indication));
        verify(digitRepository).findAll();
    }
}
