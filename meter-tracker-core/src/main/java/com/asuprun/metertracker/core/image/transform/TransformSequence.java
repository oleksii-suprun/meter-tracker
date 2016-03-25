package com.asuprun.metertracker.core.image.transform;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.asuprun.metertracker.core.utils.ImageUtils.imageToMat;
import static com.asuprun.metertracker.core.utils.ImageUtils.matToImage;

public class TransformSequence {

    private Queue<TransformStrategy> sequence;

    public TransformSequence() {
        sequence = new ArrayDeque<>();
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
        return execute(sequence.remove().transform(source));
    }
}
