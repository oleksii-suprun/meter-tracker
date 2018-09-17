package com.asuprun.metertracker.core.image.transform;

import org.opencv.core.Mat;

/**
 * Transformation strategy interface. All implementations of this interfaces provides some algorithms for image (matrix)
 * transformation.
 *
 * @author asuprun
 * @since 1.0
 */

public interface TransformStrategy {


    /**
     * Executes transformation on provided source image
     *
     * @param source source object
     * @return copy of source object with applied execute algorithm
     * @since 1.0
     */
    Mat execute(Mat source);
}
