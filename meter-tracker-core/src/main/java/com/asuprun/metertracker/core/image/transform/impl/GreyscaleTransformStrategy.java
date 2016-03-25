package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import com.asuprun.metertracker.core.utils.ImageTracer;
import com.asuprun.metertracker.core.utils.ImageUtils;
import org.opencv.core.CvType;
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
        Mat target = new Mat(source.height(), source.width(), CvType.CV_8UC1);
        Imgproc.cvtColor(source, target, Imgproc.COLOR_RGB2GRAY);
        ImageTracer.getInstance().trace(ImageUtils.matToImage(target), "greyscale");
        return target;
    }
}
