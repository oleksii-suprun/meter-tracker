package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import com.asuprun.metertracker.core.utils.ImageTracer;
import com.asuprun.metertracker.core.utils.ImageUtils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 * Created by asuprun on 1/11/15.
 */
public class RotateTransformStrategy implements TransformStrategy {

    private Mat rotationMat;

    public RotateTransformStrategy(double x, double y, double angle, double scale) {
        this(new Point(x, y), angle, scale);
    }

    public RotateTransformStrategy(Point center, double angle, double scale) {
        this(Imgproc.getRotationMatrix2D(center, angle, scale));
    }

    public RotateTransformStrategy(Mat rotationMat) {
        this.rotationMat = rotationMat;
    }

    @Override
    public Mat transform(Mat source) {
        Imgproc.warpAffine(source, source, rotationMat, source.size());
        ImageTracer.getInstance().trace(ImageUtils.matToImage(source), "rotate");
        return source;
    }
}
