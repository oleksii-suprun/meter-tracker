package com.asuprun.metertracker.core.image.transform.impl;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import com.asuprun.metertracker.core.utils.ImageTracer;
import com.asuprun.metertracker.core.utils.ImageUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.Arrays;
import java.util.List;

/**
 * Created by asuprun on 2/23/15.
 */
public class PerspectiveTransformStrategy implements TransformStrategy {

    private int height;
    private int width;
    private List<Point> corners;

    public PerspectiveTransformStrategy(int height, int width, List<Point> corners) {
        this.height = height;
        this.width = width;
        this.corners = corners;
    }

    @Override
    public Mat transform(Mat source) {
        Mat quad = Mat.zeros(height, width, CvType.CV_8UC3);
        List<Point> quadPts = Arrays.asList(
                new Point(0, 0),
                new Point(quad.cols() - 1, 0),
                new Point(quad.cols() - 1, quad.rows() - 1),
                new Point(0, quad.rows()));
        Mat transmtx = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(corners), Converters.vector_Point2f_to_Mat(quadPts));
        Imgproc.warpPerspective(source, quad, transmtx, quad.size());
        ImageTracer.getInstance().trace(ImageUtils.matToImage(quad), "perspective");
        return quad;
    }
}
