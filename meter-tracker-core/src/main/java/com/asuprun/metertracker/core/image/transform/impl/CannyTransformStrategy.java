package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * This transformation strategy uses Canny algorithm to detect borders. This strategy works only for greyscale images.
 *
 * @author asuprun
 * @since 1.0
 */
public class CannyTransformStrategy implements TransformStrategy {

    private final int minThreshold;
    private final int maxThreshold;

    /**
     * Creates instance of Canny transformation strategy
     *
     * @param lowThreshold  low hysteresis threshold
     * @param highThreshold high hysteresis threshold
     */
    public CannyTransformStrategy(int lowThreshold, int highThreshold) {
        this.minThreshold = lowThreshold;
        this.maxThreshold = highThreshold;
    }

    @Override
    public Mat transform(Mat source) {
        Mat target = source.clone();
        Imgproc.Canny(target, source, minThreshold, maxThreshold);
        return target;
    }
}
