package com.asuprun.metertracker.core.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Helper class to centralize common operations with images or separate pixels.
 *
 * @author asuprun
 * @since 1.0
 */
public class ImageUtils {

    /**
     * Converts {@link BufferedImage} to array of bytes using jpg compression
     *
     * @param image source image
     * @return jpg byte array
     * @since 1.0
     */
    public static byte[] imageToJpgBytes(BufferedImage image) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unexpected I/O exception occurred during converting buffered image to jpeg encoded byte array.");
        }
    }

    /**
     * Converts bytes read from image file to BufferedImage
     *
     * @param data bytes read from image file
     * @return instance of {@link BufferedImage}
     * @since 1.0
     */
    public static BufferedImage bytesToImage(byte[] data) {
        try {
            return ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unexpected I/O exception occurred during converting byte array to buffered image.");
        }
    }

    /**
     * Converts {@link BufferedImage} to OpenCV {@link org.opencv.core.Mat} entity
     *
     * @param image source image
     * @return result mat
     * @since 1.0
     */
    public static Mat imageToMat(BufferedImage image) {
        int type = image.getType() == BufferedImage.TYPE_BYTE_GRAY
                ? CvType.CV_8UC1
                : CvType.CV_8UC3;
        Mat mat = new Mat(image.getHeight(), image.getWidth(), type);

        // get pixels array from BufferedImage and put it into created mat
        mat.put(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return mat;
    }

    /**
     * Converts OpenCV {@link org.opencv.core.Mat} to {@link BufferedImage}
     *
     * @param mat source mat
     * @return result image
     * @see java.awt.image.BufferedImage
     * @see org.opencv.core.Mat
     * @since 1.0
     */
    public static BufferedImage matToImage(Mat mat) {
        Mat m = new Mat();
        if (mat.channels() > 1) {
            Imgproc.cvtColor(mat.clone(), m, Imgproc.COLOR_BGR2RGB);
        } else {
            m = mat.clone();
        }
        byte[] resultBytes = new byte[m.rows() * m.cols() * (int) (m.elemSize())];
        m.get(0, 0, resultBytes);
        int type = m.channels() < 3
                ? BufferedImage.TYPE_BYTE_GRAY
                : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        image.getRaster().setDataElements(0, 0, m.cols(), m.rows(), resultBytes);
        return image;
    }
}
