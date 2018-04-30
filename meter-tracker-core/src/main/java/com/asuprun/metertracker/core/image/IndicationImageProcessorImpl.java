package com.asuprun.metertracker.core.image;

import com.asuprun.metertracker.core.exception.BorderNotFoundException;
import com.asuprun.metertracker.core.image.transform.TransformSequence;
import com.asuprun.metertracker.core.image.transform.impl.*;
import com.asuprun.metertracker.core.logger.CvLogger;
import com.asuprun.metertracker.core.logger.CvLoggerFactory;
import com.asuprun.metertracker.core.utils.Geom;
import com.asuprun.metertracker.core.utils.ImageUtils;
import com.asuprun.metertracker.core.utils.Settings;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.asuprun.metertracker.core.utils.ImageUtils.drawPolygon;

public class IndicationImageProcessorImpl implements IndicationImageProcessor {

    private static final int DIMENSIONS_RATIO = 4;

    @Override
    public BufferedImage extractIndicationRegion(BufferedImage source) throws BorderNotFoundException {
        CvLogger cvLogger = CvLoggerFactory.getLogger("IndicationImageProcessorImpl#extractIndicationRegion");
        Mat original = ImageUtils.imageToMat(source);

        // greyscale and threshold source image
        Mat transformed = new TransformSequence(cvLogger)
                .transform(new GaussianBlurTransformStrategy(Settings.getInstance().getInt("cv.gaussian.radius")))
                .transform(new GreyscaleTransformStrategy())
                .transform(new ThresholdTransformStrategy(Settings.getInstance().getInt("cv.threshold.max")))
                .execute(original);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(transformed, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // remove contours with ratio of dimensions less than DIMENSIONS_RATIO
        MatOfPoint contour = contours.stream()
                .filter(c -> {
                    RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(c.toArray()));
                    return rect.size.width / rect.size.height > DIMENSIONS_RATIO
                            || rect.size.height / rect.size.width > DIMENSIONS_RATIO;
                })
                .max(Comparator.comparingDouble(Imgproc::contourArea))
                .orElseThrow(() -> new BorderNotFoundException("Unable to detect indication contours"));

        // identify corners of target rect
        List<Point[]> borders = findBorderLines(contour);
        List<Point> corners = findCorners(borders, original.size(), cvLogger, original);

        Mat result = new PerspectiveTransformStrategy(corners).transform(original);
        cvLogger.debug(result, "indication");
        return ImageUtils.matToImage(result);
    }

    @Override
    public List<BufferedImage> extractDigits(BufferedImage source, int digits) {
        List<BufferedImage> digitImages = new ArrayList<>(digits);
        int width = source.getWidth() / digits;
        for (int i = 0; i < digits; i++) {
            Raster raster = source.getSubimage(width * i, 0, width, source.getHeight()).getRaster();
            BufferedImage digit = new BufferedImage(width, source.getHeight(), source.getType());
            digit.setData(raster);

            BufferedImage digitImage = new TransformSequence()
                    .transform(new GaussianBlurTransformStrategy(Settings.getInstance().getInt("cv.gaussian.radius")))
                    .transform(new GreyscaleTransformStrategy())
                    .transform(new ThresholdTransformStrategy(Settings.getInstance().getInt("cv.threshold.max")))
                    .transform(this::cleanDigitImage)
                    .transform(new ResizeTransformStrategy(Settings.getInstance().getInt("cv.digit.width"),
                            Settings.getInstance().getInt("cv.digit.height")))
                    .execute(digit);
            digitImages.add(digitImage);
        }
        return digitImages;
    }

    // remove "garbage" from digit image
    private Mat cleanDigitImage(Mat source) {
        MatOfPoint contour = findNumberContour(source);
        Mat mask = Mat.zeros(source.size(), CvType.CV_8UC1);
        Imgproc.drawContours(mask, Collections.singletonList(contour), -1, new Scalar(255, 255, 255), -1);
        Mat result = new Mat();
        Core.bitwise_and(source, source, result, mask);
        return source.submat(Imgproc.boundingRect(contour));
    }

    private MatOfPoint findNumberContour(Mat source) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(source.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
        return contours.stream().max(Comparator.comparingDouble(Imgproc::contourArea))
                .orElseThrow(() -> new RuntimeException("Unable to find number contour"));
    }

    private List<Point[]> findBorderLines(MatOfPoint contour) {
        // approximate found contour
        MatOfPoint2f approximated = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approximated, 10, true);

        // find all lines in provided contour
        List<Point[]> contourLine = new ArrayList<>();
        contourLine.add(new Point[]{new Point(approximated.get(approximated.rows() - 1, 0)), new Point(approximated.get(0, 0))});
        for (int i = 1; i < approximated.rows(); i++) {
            contourLine.add(new Point[]{new Point(approximated.get(i - 1, 0)), new Point(approximated.get(i, 0))});
        }

        // sort found lines by length and merge similar lines (lines which could be replaced with single line)
        return mergeContourLines(contourLine.stream()
                .sorted((f, s) -> Double.compare(Geom.distance(f[0], f[1]), Geom.distance(s[0], s[1])) * -1)
                .collect(Collectors.toList())).subList(0, 4);
    }

    private List<Point[]> mergeContourLines(List<Point[]> contourLines) {
        List<Point[]> lines = new ArrayList<>();
        for (Point[] contourLine : contourLines) {
            boolean merged = false;

            // iterate over all lines and try to merge current contour line
            for (Point[] line : lines) {
                if (merged = mergeLines(line, contourLine)) {
                    break;
                }
            }
            if (!merged) {
                lines.add(contourLine);
            }
        }
        return lines;
    }

    private boolean mergeLines(Point[] first, Point[] second) {
        // check if provided lines could be merged (angle between them is less than 3 degrees and )
        if (!(Math.abs(Geom.angle(first) - Geom.angle(second)) < 3
                && Math.abs(Geom.distance(first, second[0])) < 15
                && Math.abs(Geom.distance(first, second[1])) < 15)) {
            return false;
        }

        // calculate center of mass of two provided lines
        Point center = Geom.massCenter(first[0], first[1], second[0], second[1]);

        // identify which point should be start or end of new line after merge; interested points must have the larges distance from center point
        first[0] = Math.abs(Geom.distance(center, first[0])) > Math.abs(Geom.distance(center, first[1]))
                ? first[0]
                : first[1];
        first[1] = Math.abs(Geom.distance(center, second[0])) > Math.abs(Geom.distance(center, second[1]))
                ? second[0]
                : second[1];
        return true;
    }

    private List<Point> findCorners(List<Point[]> borders,
                                    Size imageSize,
                                    CvLogger cvLogger,
                                    Mat original) throws BorderNotFoundException {

        // calculate intersection of all provided borders
        List<Point> corners = new ArrayList<>();
        for (int i = 0; i < borders.size(); i++) {
            for (int j = i + 1; j < borders.size(); j++) {
                Point pt = Geom.intersection(borders.get(i)[0], borders.get(i)[1], borders.get(j)[0], borders.get(j)[1]);
                if (pt.x >= 0 && pt.y >= 0 && pt.x <= imageSize.width && pt.y <= imageSize.height) {
                    corners.add(pt);
                }
            }
        }

        // sort corners in following order (tl, tr, br, bl)
        MatOfInt ouput = new MatOfInt();
        Imgproc.convexHull(new MatOfPoint(corners.toArray(new Point[0])), ouput, true);
        corners = ouput.toList().stream().map(corners::get).collect(Collectors.toList());
        Collections.reverse(corners);

        if (!validateCorners(corners)) {
            cvLogger.error(drawPolygon(original, corners, new Scalar(0, 255, 0)), "IndicationRegion");
            throw new BorderNotFoundException("Detected borders are not a rect");
        }

        cvLogger.info(drawPolygon(original, corners, new Scalar(0, 255, 0)), "IndicationRegion");
        return corners;
    }

    private boolean validateCorners(List<Point> corners) {
        MatOfPoint2f approx = new MatOfPoint2f();
        MatOfPoint2f cornersMat = new MatOfPoint2f(Converters.vector_Point2f_to_Mat(corners));
        Imgproc.approxPolyDP(cornersMat, approx, Imgproc.arcLength(cornersMat, true) * 0.02, true);
        return approx.rows() == 4;
    }
}
