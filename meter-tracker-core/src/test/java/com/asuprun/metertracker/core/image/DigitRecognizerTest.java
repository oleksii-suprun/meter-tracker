package com.asuprun.metertracker.core.image;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencv.core.Core;
import org.opencv.ml.KNearest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class DigitRecognizerTest {

    private static final String TRAIN_DATA_PATH = "data/input/digits/train";
    private DigitRecognizer digitRecognizer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Before
    public void before() throws IOException, URISyntaxException {
        trainFrom(TRAIN_DATA_PATH, "8");
    }

    @Test
    public void testRecognize() throws URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/0/0-422769501189955.jpeg");
        assertNotNull(url);
        BufferedImage image = ImageIO.read(new File(url.toURI()));
        String value = digitRecognizer.recognize(image).orElse(null);
        assertEquals("0", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/1/1-422769542952297.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertEquals("1", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/3/3-422769572186429.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertEquals("3", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/7/7-422769592940273.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertEquals("7", value);
    }

    @Test
    public void testNotRecognize() throws URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/test/2-422769570573111.jpeg");
        assertNotNull(url);
        BufferedImage image = ImageIO.read(new File(url.toURI()));
        String value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("2", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/test/2-422769545719517.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("2", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/8/8-422769601279099.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("8", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/8/8-422769613580907.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("8", value);
    }

    @Test
    public void testAdditionalTraining() throws URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/test/2-422769570573111.jpeg");
        assertNotNull(url);
        BufferedImage image = ImageIO.read(new File(url.toURI()));
        String value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("2", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/test/2-422769545719517.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("2", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/8/8-422769601279099.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("8", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/8/8-422769613580907.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("8", value);

        trainFrom(TRAIN_DATA_PATH);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/test/2-422769570573111.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("2", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/test/2-422769545719517.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertNotEquals("2", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/8/8-422769601279099.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertEquals("8", value);

        url = Thread.currentThread().getContextClassLoader().getResource("data/input/digits/train/8/8-422769613580907.jpeg");
        assertNotNull(url);
        image = ImageIO.read(new File(url.toURI()));
        value = digitRecognizer.recognize(image).orElse(null);
        assertEquals("8", value);
    }

    @Test
     public void testRecognizeNotTrained() throws NoSuchFieldException, IllegalAccessException {
        Field kNearestField = digitRecognizer.getClass().getDeclaredField("kNearest");
        kNearestField.setAccessible(true);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Recognition algorithm is not trained.");

        kNearestField.set(digitRecognizer, KNearest.create());
        digitRecognizer.recognize(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
    }

    @Test
    public void testRecognizeNullImage() throws NoSuchFieldException, IllegalAccessException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Digit image must not be null.");
        digitRecognizer.recognize(null);
    }

    private void trainFrom(String dir, String... except) throws URISyntaxException, IOException {
        List<String> labels = new ArrayList<>();
        List<BufferedImage> images = new ArrayList<>();
        Set<String> exceptions = new HashSet<>(Arrays.asList(except));

        URL url = Thread.currentThread().getContextClassLoader().getResource(dir);
        assertNotNull(url);
        Files.walk(Paths.get(url.toURI())).forEach(filePath -> {
            File imageFile = new File(filePath.toUri());
            String parentDirName = imageFile.getParentFile().getName();
            if (Files.isRegularFile(filePath) && !exceptions.contains(parentDirName)) {
                try {
                    images.add(ImageIO.read(imageFile));
                    labels.add(parentDirName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        digitRecognizer = new DigitRecognizer();
        boolean trained = digitRecognizer.train(images, labels);
        assertTrue(trained);
    }
}
