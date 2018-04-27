package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * This transformation strategy is designed to convert image color schema from GRB to greyscale.
 *
 * @author asuprun
 * @since 1.0
 */
public class GreyscaleTransformStrategy implements TransformStrategy {

    @Override
    public Mat transform(Mat source) {
        Mat target = source.clone();
        Imgproc.cvtColor(source, target, Imgproc.COLOR_RGB2GRAY);
        return target;
    }
}
