package com.asuprun.metertracker.core.image;

import com.asuprun.metertracker.core.image.transform.TransformStrategy;
import com.asuprun.metertracker.core.image.transform.impl.ResizeTransformStrategy;
import com.asuprun.metertracker.core.utils.Settings;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import static com.asuprun.metertracker.core.utils.ImageUtils.imageToMat;

/**
 * @author asuprun
 * @since 1.0
 */
public class DigitRecognizer {

    private static final Logger logger = LoggerFactory.getLogger(DigitRecognizer.class);

    private KNearest kNearest;
    private TransformStrategy resizeTransformStrategy;

    public DigitRecognizer() {
        this.kNearest = KNearest.create();
        this.resizeTransformStrategy = new ResizeTransformStrategy(
                Settings.getInstance().getInt("cv.digit.width"),
                Settings.getInstance().getInt("cv.digit.height")
        );
    }

    public boolean train(List<BufferedImage> images, List<String> labels) {
        if (images == null || labels == null || images.isEmpty() || labels.isEmpty()) {
            throw new IllegalArgumentException("Train data and train labels cannot be null or empty.");
        } else if (images.size() != labels.size()) {
            throw new IllegalArgumentException("Number of labels and number of images are mismatched");
        }

        Mat trainData = new Mat();
        Mat trainLabels = new Mat();
        for (int i = 0; i < images.size(); i++) {
            trainData.push_back(prepareTrainData(imageToMat(images.get(i))));
            trainLabels.push_back(prepareLabelData(Integer.valueOf(labels.get(i))));
        }
        return kNearest.train(trainData, Ml.ROW_SAMPLE, trainLabels);
    }

    public Optional<String> recognize(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Digit image must not be null.");
        }
        if (!kNearest.isTrained()) {
            logger.error("Recognition algorithm is not trained. Method 'train' must be called first.");
            throw new IllegalStateException("Cannot recognize digit. Recognition algorithm is not trained.");
        }

        Mat source = prepareTrainData(imageToMat(image));
        Mat results = new Mat();
        Mat neighborResponses = new Mat();
        float result = kNearest.findNearest(source, 3, results, neighborResponses, new Mat());

        if (results.get(0, 0) == null || neighborResponses.get(0, 0) == null
                || results.get(0, 0)[0] != neighborResponses.get(0, 0)[0]) {
            return Optional.empty();
        }
        return Optional.of(String.valueOf((int) result));
    }

    public boolean isTrained() {
        return kNearest.isTrained();
    }

    private Mat prepareTrainData(Mat mat) {
        Mat floatData = new Mat();
        resizeTransformStrategy.transform(mat).convertTo(floatData, CvType.CV_32FC1);
        return floatData.reshape(1, 1);
    }

    private Mat prepareLabelData(int value) {
        Mat label = new Mat(1, 1, CvType.CV_8U);
        label.put(0, 0, value);
        Mat floatData = new Mat();
        label.convertTo(floatData, CvType.CV_32FC1);
        return floatData.reshape(1, 1);
    }
}
