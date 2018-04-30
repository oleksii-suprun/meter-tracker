package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 * Created by asuprun on 1/17/15.
 */
public class MorphologyTransformStrategy implements TransformStrategy {

    private int kWidth;
    private int kHeight;
    private int operation;
    private int iterations;

    public MorphologyTransformStrategy(int kWidth, int kHeight, int operation, int iterations) {
        this.kWidth = kWidth;
        this.kHeight = kHeight;
        this.operation = operation;
        this.iterations = iterations;
    }

    @Override
    public Mat transform(Mat source) {
        Mat target = source.clone();
        Imgproc.morphologyEx(source, target, operation, Mat.ones(kHeight, kWidth, CvType.CV_8U), new Point(-1, -1), iterations);
        return target;
    }
}
