package com.asuprun.metertracker.core.utils;

import org.junit.Test;
import org.opencv.core.Point;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class GeomTest {

    @Test
    public void testLineAngle() {
        Point[] line = new Point[]{new Point(0, 0), new Point(1, 1)};
        assertEquals(45, Geom.angle(line), 1e-6);

        line = new Point[]{new Point(0, 0), new Point(0, 1)};
        assertEquals(90, Geom.angle(line), 1e-6);

        line = new Point[]{new Point(0, 0), new Point(1, 0)};
        assertEquals(0, Geom.angle(line), 1e-6);

        line = new Point[]{new Point(0, 0), new Point(1, -1)};
        assertEquals(-45, Geom.angle(line), 1e-6);

        line = new Point[]{new Point(0, 0), new Point(-1, 1)};
        assertEquals(-45, Geom.angle(line), 1e-6);

        line = new Point[]{new Point(0, 0), new Point(2, 2)};
        assertEquals(45, Geom.angle(line), 1e-6);

        line = new Point[]{new Point(1, 1), new Point(0, 0)};
        assertEquals(45, Geom.angle(line), 1e-6);
    }

    @Test
    public void testDistance() {
        double result = Geom.distance(new Point(0, 0), new Point(1, 0), new Point(2, 0));
        assertEquals(0, result, 1e-6);

        result = Geom.distance(new Point[]{new Point(0, 0), new Point(1, 1)}, new Point(1, 0));
        assertEquals(-Math.sqrt(2) / 2, result, 1e-6);

        result = Geom.distance(new Point(0, 0), new Point(1, 1), new Point(3, 2));
        assertEquals(-Math.sqrt(2) / 2, result, 1e-6);

        result = Geom.distance(new Point[]{new Point(0, 0), new Point(1, 1)}, new Point(-2, 2));
        assertEquals(2 * Math.sqrt(2), result, 1e-6);

        result = Geom.distance(new Point(0, 0), new Point(1, 0));
        assertEquals(1, result, 1e-6);
    }

    @Test
    public void testMassCenter() {
        Point result = Geom.massCenter(
                new Point(-1, 0),
                new Point(1, 0)
        );
        assertEquals(0, result.x, 1e-6);
        assertEquals(0, result.y, 1e-6);

        result = Geom.massCenter(
                new Point(-1, 0),
                new Point(1, 0),
                new Point(0, 1),
                new Point(0, -1)
        );
        assertEquals(0, result.x, 1e-6);
        assertEquals(0, result.y, 1e-6);

        result = Geom.massCenter(Arrays.asList(
                        new Point(-1, 0),
                        new Point(1, 0))
        );
        assertEquals(0, result.x, 1e-6);
        assertEquals(0, result.y, 1e-6);

        result = Geom.massCenter(Arrays.asList(
                new Point(-1, 0),
                new Point(1, 0),
                new Point(0, 1),
                new Point(0, -1)
        ));
        assertEquals(0, result.x, 1e-6);
        assertEquals(0, result.y, 1e-6);
    }

    @Test
    public void testAngle() {
        double angle = Geom.angle(new Point(0, 0), new Point(1, 1));
        assertEquals(45, angle, 1e-6);

        angle = Geom.angle(new Point(1, 0), new Point(0, 1));
        assertEquals(-45, angle, 1e-6);

        angle = Geom.angle(new Point[]{new Point(0, 0), new Point(1, -1)});
        assertEquals(-45, angle, 1e-6);

        angle = Geom.angle(new Point(0, 0), new Point(0, -1));
        assertEquals(-90, angle, 1e-6);

        angle = Geom.angle(new Point[]{new Point(0, 0), new Point(0, 1)});
        assertEquals(90, angle, 1e-6);
    }
}
