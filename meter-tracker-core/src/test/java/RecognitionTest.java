import com.asuprun.metertracker.core.exception.BorderNotFoundException;
import com.asuprun.metertracker.core.image.IndicationImageProcessor;
import com.asuprun.metertracker.core.image.IndicationImageProcessorImpl;
import com.asuprun.metertracker.core.image.transform.TransformSequence;
import com.asuprun.metertracker.core.image.transform.impl.GaussianBlurTransformStrategy;
import com.asuprun.metertracker.core.image.transform.impl.GreyscaleTransformStrategy;
import com.asuprun.metertracker.core.image.transform.impl.ThresholdTransformStrategy;
import com.asuprun.metertracker.core.utils.ImageTracer;
import com.asuprun.metertracker.core.utils.ImageUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by asuprun on 2/24/15.
 */
public class RecognitionTest {

    public static final String NAME = "data/input/full/IMG_0024_IN.JPG";

    @BeforeClass
    public static void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void testSplit() throws BorderNotFoundException, IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(NAME);
        Assert.assertNotNull(url);
        BufferedImage image = ImageIO.read(new File(url.toURI()));

        IndicationImageProcessor indicationImageProcessor = new IndicationImageProcessorImpl();
        image = indicationImageProcessor.extractIndication(image);

        int width = image.getWidth() / 8;
        for (int i = 0; i < 8; i++) {
            Raster raster = image.getSubimage(width * i, 0, width, image.getHeight()).getRaster();
            BufferedImage digit = new BufferedImage(width, image.getHeight(), image.getType());
            digit.setData(raster);

            Mat original = ImageUtils.imageToMat(digit);
            Mat result = new TransformSequence()
                    .transform(new GaussianBlurTransformStrategy(6))
                    .transform(new GreyscaleTransformStrategy())
                    .transform(new ThresholdTransformStrategy(355))
                    .execute(original);
            result = eliminateGarbage(result);

            ImageTracer.getInstance().trace(ImageUtils.matToImage(result), "FINAL");
        }
    }

    @Ignore
    @Test
    public void testRecognize() throws IOException {
        Mat[] zero = new Mat[]{
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/00.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/01.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/02.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/03.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/04.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/05.jpg")))
        };

        Mat[] one = new Mat[]{
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/10.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/10.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/10.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/10.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/10.jpg"))),
                ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/10.jpg")))
        };

        Mat trainData = new Mat();
        Mat trainLabels = new Mat();
        for (Mat mat : zero) {
            trainData.push_back(prepareTrainData(mat));
            trainLabels.push_back(prepareLabelData(0));
        }
        for (Mat mat : one) {
            trainData.push_back(prepareTrainData(mat));
            trainLabels.push_back(prepareLabelData(1));
        }

        KNearest kNearest = KNearest.create();
        boolean trained = kNearest.train(trainData, Ml.ROW_SAMPLE, trainLabels);
        Assert.assertTrue(trained);

        Mat results = new Mat();
        Mat responses = new Mat();
        Mat dists = new Mat();
        float result = kNearest.findNearest(prepareTrainData(zero[1]), 3, results, responses, dists);
        System.out.println(result);

        result = kNearest.findNearest(prepareTrainData(one[0]), 3, new Mat(), new Mat(), new Mat());
        System.out.println(result);

        Mat two = ImageUtils.imageToMat(ImageIO.read(new File("/Users/asuprun/Documents/Development/Private/Bitbucket/meter-recognizer/src/test/resources/20.jpg")));
        result = kNearest.findNearest(prepareTrainData(two), 3, new Mat(), new Mat(), new Mat());
        System.out.println(result);
    }

    private Mat prepareTrainData(Mat mat) {
        Imgproc.resize(mat, mat, new Size(84, 155));
        Mat floatData = new Mat();
        mat.convertTo(floatData, CvType.CV_32FC1);
        return floatData.reshape(1, 1);
    }

    private Mat prepareLabelData(int value) {
        Mat label = new Mat(1, 1, CvType.CV_8U);
        label.put(0, 0, value);
        Mat floatData = new Mat();
        label.convertTo(floatData, CvType.CV_32FC1);
        return floatData.reshape(1, 1);
    }

    private Mat eliminateGarbage(Mat source) {
        MatOfPoint contour = findNumberContour(source);
        Mat mask = Mat.zeros(source.size(), CvType.CV_8UC1);
        Imgproc.drawContours(mask, Collections.singletonList(contour), -1, new Scalar(255, 255, 255), -1);
        Mat result = new Mat();
        Core.bitwise_and(source, source, result, mask);
        return source.submat(Imgproc.boundingRect(contour));
    }

    private MatOfPoint findNumberContour(Mat source) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(source.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
        return contours.stream().max((f, s) -> Double.compare(Imgproc.contourArea(f), Imgproc.contourArea(s))).get();
    }
}
