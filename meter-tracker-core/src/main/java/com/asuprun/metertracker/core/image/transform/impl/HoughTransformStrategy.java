package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.AbstractTransformStrategy;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by asuprun on 1/9/15.
 */
public class HoughTransformStrategy extends AbstractTransformStrategy {

    private int threshold;

    public HoughTransformStrategy(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Mat execute(Mat source) {
        Mat target = source.clone();
        Imgproc.HoughLines(target, source, 1, Math.PI / 180, threshold);
        return target;
    }
}
