package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import com.asuprun.metertracker.core.utils.ImageTracer;
import com.asuprun.metertracker.core.utils.ImageUtils;
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
        Mat kernel = Imgproc.getStructuringElement(shape, size);
        Imgproc.erode(source, source, kernel);
        ImageTracer.getInstance().trace(ImageUtils.matToImage(source), "erode");
        return source;
    }
}
