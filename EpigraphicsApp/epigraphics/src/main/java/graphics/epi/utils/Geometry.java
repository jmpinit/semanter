package graphics.epi.utils;

import org.opencv.core.Point;

public class Geometry {
    /**
     * Immutable line segment
     */
    public static class LineSegment {
        private Point p1, p2;

        public LineSegment(Point p1, Point p2) {
            this.p1 = new Point(p1.x, p1.y);
            this.p2 = new Point(p2.x, p2.y);
        }

        public boolean collides(LineSegment other) {
            double cross1 = (other.p2.x - other.p1.x) * (p1.x - other.p2.y) - (other.p2.y - other.p1.y) * (p1.x - other.p2.x);
            double cross2 = (other.p2.x - other.p1.x) * (p2.y - other.p2.y) - (other.p2.y - other.p1.y) * (p2.x - other.p2.x);

            return !((int)Math.signum(cross1) == (int)Math.signum(cross2));
        }
    }
}
