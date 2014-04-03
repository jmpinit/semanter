package graphics.epi.test;

import android.test.AndroidTestCase;

import org.opencv.core.Point;

import graphics.epi.utils.Geometry;

public class GeometryTest extends AndroidTestCase {
    public void testLineSegmentCollision() throws Exception {
        final Point a = new Point(-5, 0);
        final Point b = new Point(5, 0);

        final Point c = new Point(0, 5);
        final Point d = new Point(0, -5);

        final Point f = new Point(2, 5);
        final Point e = new Point(2, -5);

        final Geometry.LineSegment seg1 = new Geometry.LineSegment(a, b);
        final Geometry.LineSegment seg2 = new Geometry.LineSegment(c, d);
        final Geometry.LineSegment seg3 = new Geometry.LineSegment(e, f);

        // perpendicular segments collide
        assertTrue(seg1.collides(seg2));
        assertTrue(seg2.collides(seg1));

        // segment collides with self
        assertTrue(seg1.collides(seg1));

        // parallel segments don't collide
        assertFalse(seg2.collides(seg3));
    }
}