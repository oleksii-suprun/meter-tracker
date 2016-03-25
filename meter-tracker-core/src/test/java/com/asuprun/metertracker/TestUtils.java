package com.asuprun.metertracker;

import com.asuprun.metertracker.core.image.transform.impl.GreyscaleTransformStrategy;
import com.asuprun.metertracker.core.utils.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class TestUtils {

    public static boolean compareByContours(BufferedImage img1, BufferedImage img2) {
        List<MatOfPoint> contours1 = new ArrayList<>();
        Mat mat1 = new GreyscaleTransformStrategy().transform(ImageUtils.imageToMat(img1));
        Imgproc.findContours(mat1, contours1, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> contours2 = new ArrayList<>();
        Mat mat2 = new GreyscaleTransformStrategy().transform(ImageUtils.imageToMat(img2));
        Imgproc.findContours(mat2, contours2, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contours1.size() != contours2.size()) {
            return false;
        }
        for (int i = 0; i < contours1.size(); i++) {
            MatOfPoint contour1 = contours1.get(i);
            MatOfPoint contour2 = contours2.get(i);
            if (!contour1.size().equals(contour2.size())) {
                return false;
            }
            for (int j = 0; j < contour1.rows(); j++) {
                if (!Arrays.equals(contour1.get(j, 0), contour2.get(j, 0))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static BufferedImage imageByPath(String name) throws URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(name);
        assertNotNull(url);
        return ImageIO.read(new File(url.toURI()));
    }
}
