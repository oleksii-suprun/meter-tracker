package com.asuprun.metertracker.core.image;

import com.asuprun.metertracker.TestUtils;
import com.asuprun.metertracker.core.exception.BorderNotFoundException;
import com.asuprun.metertracker.core.utils.Settings;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class IndicationImageProcessorTest {

    private static final String[] ORIGINAL_NAMES = {
            "data/input/full/IMG_0021_IN.JPG",
            "data/input/full/IMG_0023_IN.JPG",
            "data/input/full/IMG_0024_IN.JPG",
            "data/input/full/IMG_0025_IN.JPG"
    };

    private static final String[] EXTRACTED_NAMES = {
            "data/output/extracted/IMG_0021_OUT.JPG",
            "data/output/extracted/IMG_0023_OUT.JPG",
            "data/output/extracted/IMG_0024_OUT.JPG",
            "data/output/extracted/IMG_0025_OUT.JPG"
    };

    private List<BufferedImage> originalImages;
    private List<BufferedImage> extractedImages;
    private IndicationImageProcessor indicationImageProcessor;

    @Before
    public void before() throws URISyntaxException, IOException {
        indicationImageProcessor = new IndicationImageProcessorImpl();

        originalImages = new ArrayList<>(ORIGINAL_NAMES.length);
        extractedImages = new ArrayList<>(ORIGINAL_NAMES.length);
        for (int i = 0; i < ORIGINAL_NAMES.length; i++) {
            originalImages.add(TestUtils.imageByPath(ORIGINAL_NAMES[i]));
            extractedImages.add(TestUtils.imageByPath(EXTRACTED_NAMES[i]));
        }
    }

    @Test
    public void testExtract() throws BorderNotFoundException {
        // data/output/extracted/IMG_0021_IN.JPG
        BufferedImage result = indicationImageProcessor.extractIndicationRegion(originalImages.get(0));
        assertEquals(extractedImages.get(0).getHeight(), result.getHeight());
        assertEquals(extractedImages.get(0).getWidth(), result.getWidth());
        assertTrue(TestUtils.compareByContours(extractedImages.get(0), result));

        // data/output/extracted/IMG_0023_IN.JPG
        result = indicationImageProcessor.extractIndicationRegion(originalImages.get(1));
        assertEquals(extractedImages.get(1).getHeight(), result.getHeight());
        assertEquals(extractedImages.get(1).getWidth(), result.getWidth());
        assertTrue(TestUtils.compareByContours(extractedImages.get(1), result));

        // data/output/extracted/IMG_0024_IN.JPG
        result = indicationImageProcessor.extractIndicationRegion(originalImages.get(2));
        assertEquals(extractedImages.get(2).getHeight(), result.getHeight());
        assertEquals(extractedImages.get(2).getWidth(), result.getWidth());
        assertTrue(TestUtils.compareByContours(extractedImages.get(2), result));

        // data/output/extracted/IMG_0025_IN.JPG
        result = indicationImageProcessor.extractIndicationRegion(originalImages.get(3));
        assertEquals(extractedImages.get(3).getHeight(), result.getHeight());
        assertEquals(extractedImages.get(3).getWidth(), result.getWidth());
        assertTrue(TestUtils.compareByContours(extractedImages.get(3), result));
    }

    @Test
    public void testSplitToDigits() throws IOException, URISyntaxException {
        List<BufferedImage> digits = indicationImageProcessor.extractDigits(TestUtils.imageByPath(EXTRACTED_NAMES[0]), 8);
        assertNotNull(digits);
        assertEquals(digits.size(), 8);
        for (BufferedImage digit : digits) {
            assertEquals(Settings.getInstance().getInt("cv.digit.width"), digit.getWidth());
            assertEquals(Settings.getInstance().getInt("cv.digit.height"), digit.getHeight());
        }

        digits = indicationImageProcessor.extractDigits(TestUtils.imageByPath(EXTRACTED_NAMES[2]), 8);
        assertNotNull(digits);
        assertEquals(digits.size(), 8);
        for (BufferedImage digit : digits) {
            assertEquals(Settings.getInstance().getInt("cv.digit.width"), digit.getWidth());
            assertEquals(Settings.getInstance().getInt("cv.digit.height"), digit.getHeight());
        }
    }
}
