package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import com.asuprun.metertracker.core.utils.ImageTracer;
import com.asuprun.metertracker.core.utils.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by asuprun on 1/17/15.
 */
public class ThresholdTransformStrategy implements TransformStrategy {

    private double maxValue;

    public ThresholdTransformStrategy(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public Mat transform(Mat source) {
        Imgproc.threshold(source, source, 0, maxValue, Imgproc.THRESH_OTSU);
        ImageTracer.getInstance().trace(ImageUtils.matToImage(source), "threshold");
        return source;
    }
}
