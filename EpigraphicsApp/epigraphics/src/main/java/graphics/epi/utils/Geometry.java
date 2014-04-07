package graphics.epi.utils;

import android.os.Parcel;
import android.os.Parcelable;

import org.opencv.core.Point;

public class Geometry {
    /**
     * Immutable square shape
     */
    public static class Quad implements Parcelable {
        private final Point[] points;

        public Quad(Point first, Point second, Point third, Point fourth) throws RepException {
            points = new Point[4];

            points[0] = first.clone();
            points[1] = second.clone();
            points[2] = third.clone();
            points[3] = fourth.clone();

            // FIXME don't include in release
            checkRep();
        }

        public Quad(Point[] points) {
            this.points = new Point[4];

            for(int i = 0; i < 4; i++)
                this.points[i] = points[i].clone();

            checkRep();
        }

        @Override
        public String toString() {
            String text = "Quad { ";

            for(int i = 0; i < points.length; i++) {
                String delimiter = (i == points.length - 1)? "" : ", ";
                text += points[i].toString() + delimiter;
            }

            text += " }";

            return text;
        }

        private void checkRep() throws RepException {
            if(points.length != 4) throw new RepException("Wrong # of points. Should be 4.");

            LineSegment[] segs = new LineSegment[4];
            for(int i = 0; i < segs.length - 1; i++) {
                segs[i] = new LineSegment(points[i], points[i+1]);
            }

            // FIXME check for intersection except at ends of edges
            /*for(int i = 0; i < segs.length; i++) {
                for(int j = 0; j < segs.length; j++) {
                    if(i != j) {
                        if(segs[i].collides(segs[j])) {
                            throw new RepException("Sides of quad should not intersect");
                        }
                    }
                }
            }*/
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            for(int i = 0; i < 4; i++) {
                out.writeDouble(points[i].x);
                out.writeDouble(points[i].y);
            }
        }

        public static final Parcelable.Creator<Quad> CREATOR
                = new Parcelable.Creator<Quad>() {

            public Quad createFromParcel(Parcel in) {
                return new Quad(in);
            }

            public Quad[] newArray(int size) {
                return new Quad[size];
            }
        };

        private Quad(Parcel in) {
            points = new Point[4];

            for(int i = 0; i < 4; i++) {
                points[i] = new Point(in.readDouble(), in.readDouble());
            }
        }
    }

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
