package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * This transformation strategy designed to apply Gaussian Blur to provided image.
 *
 * @author asuprun
 * @since 1.0
 */
public class GaussianBlurTransformStrategy implements TransformStrategy {

    private final double sigma;
    private final int kernelSize;

    /**
     * Constructs instance of Gaussian Blur transformation strategy. Sigma will be default to radius.
     * Simplified version of {@link #GaussianBlurTransformStrategy(int, double)}. Gaussian standard deviation is
     * computed from radius as {@code sigma = 0.3 * ((ksize - 1) * 0.5 - 1) + 0.8}.
     *
     * @param radius how many pixels around target pixel will participate in calculation
     * @since 1.0
     */
    public GaussianBlurTransformStrategy(int radius) {
        this(radius, -1); // sigma = 0.3 * ((ksize - 1) * 0.5 - 1) + 0.8
    }

    /**
     * Constructs instance of Gaussian Blur transformation strategy. Kernel of Gaussian blur is calculated by radius as
     * ksize = 2 * radius + 1
     *
     * @param radius how many pixels around target pixel will participate in calculation. (ksize = 2 * radius + 1)
     * @param sigma  Gaussian standard deviation. If it is non-positive, it is computed from ksize as {@code sigma = 0.3 * ((ksize - 1) * 0.5 - 1) + 0.8}.
     * @since 1.0
     */
    public GaussianBlurTransformStrategy(int radius, double sigma) {
        this.sigma = sigma;
        this.kernelSize = 2 * radius + 1;
    }

    @Override
    public Mat transform(Mat source) {
        Mat target = source.clone();
        Imgproc.GaussianBlur(source, target, new Size(kernelSize, kernelSize), sigma);
        return target;
    }
}
