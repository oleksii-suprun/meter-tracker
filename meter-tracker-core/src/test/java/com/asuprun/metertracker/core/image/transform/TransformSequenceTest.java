package com.asuprun.metertracker.core.image.transform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by asuprun on 1/9/15.
 */
public class TransformSequenceTest {

    private TransformSequence sequence;

    @Before
    public void before() {
        sequence = new TransformSequence();
    }

    @Test
    public void testExecute() {
        byte[] data = new byte[]{0};
        Mat mat = new Mat(1, 1, CvType.CV_8UC1);
        mat.put(0, 0, data);

        Mat result = sequence
                .transform(new TestTransformStrategy())
                .transform(new TestTransformStrategy())
                .transform(new TestTransformStrategy())
                .execute(mat);
        result.get(0, 0, data);

        Assert.assertEquals(3, data[0]);
    }

    private class TestTransformStrategy extends AbstractTransformStrategy {

        @Override
        public Mat execute(Mat source) {
            byte[] data = new byte[1];
            source.get(0, 0, data);
            data[0]++;
            source.put(0, 0, data);
            return source;
        }
    }
}
