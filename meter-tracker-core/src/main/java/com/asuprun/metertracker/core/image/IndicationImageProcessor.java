package com.asuprun.metertracker.core.image;

import com.asuprun.metertracker.core.exception.BorderNotFoundException;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Class designed to get extract region of image which contains numeric indication of meter value. The algorithm of
 * extraction is based on contour detection. Suitable could be only contours which has rectangular shape and ratio
 * of two dimensions is grater than specified value.
 *
 * @author asuprun
 * @since 1.0
 */
public interface IndicationImageProcessor {

    /**
     * Extracts numeric indicator of meter image to subimage
     *
     * @param source source image which contains meter
     * @return extracted region (subimage) which contains only numeric indications
     * @throws BorderNotFoundException if borders of numeric indicator could not be found.
     * @since 1.0
     */
    BufferedImage extractIndication(BufferedImage source) throws BorderNotFoundException;

    List<BufferedImage> extractDigits(BufferedImage source, int digits);
}
