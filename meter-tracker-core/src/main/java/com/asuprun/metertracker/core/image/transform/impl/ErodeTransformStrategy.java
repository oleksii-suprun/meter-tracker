package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by asuprun on 2/21/15.
 */
public class ErodeTransformStrategy implements TransformStrategy {
    private Size size;
    private int shape;

    public ErodeTransformStrategy(int kernelWidth, int kernelHeight, int shape) {
        this.size = new Size(kernelWidth, kernelHeight);
        this.shape = shape;
    }

    @Override
    public Mat transform(Mat source) {
        Mat target = source.clone();
        Mat kernel = Imgproc.getStructuringElement(shape, size);
        Imgproc.erode(target, source, kernel);
        return target;
    }
}
