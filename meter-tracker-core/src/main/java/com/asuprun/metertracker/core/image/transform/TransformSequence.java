package com.asuprun.metertracker.core.image.transform;

import com.asuprun.metertracker.core.logger.CvLogger;
import com.asuprun.metertracker.core.logger.NoOpCvLogger;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.asuprun.metertracker.core.utils.ImageUtils.imageToMat;
import static com.asuprun.metertracker.core.utils.ImageUtils.matToImage;

public class TransformSequence {

    private Queue<TransformStrategy> sequence;
    private CvLogger cvLogger;

    public TransformSequence(CvLogger cvLogger) {
        this.sequence = new ArrayDeque<>();
        this.cvLogger = cvLogger;
    }

    public TransformSequence() {
        this(new NoOpCvLogger());
    }

    public TransformSequence transform(TransformStrategy strategy) {
        sequence.add(strategy);
        return this;
    }

    public BufferedImage execute(BufferedImage source) {
        return matToImage(execute(imageToMat(source)));
    }

    public Mat execute(Mat source) {
        if (sequence.isEmpty()) {
            return source;
        }
        TransformStrategy strategy = sequence.remove();
        Mat transformed = strategy.transform(source);
        cvLogger.trace(transformed, strategy.getClass().getSimpleName());
        return execute(transformed);
    }
}
