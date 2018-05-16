package com.asuprun.metertracker.core.image.transform;

import com.asuprun.metertracker.core.utils.OpenCvLoader;

public abstract class AbstractTransformStrategy implements TransformStrategy {

    static {
        OpenCvLoader.load();
    }
}
