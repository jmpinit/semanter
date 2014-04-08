package graphics.epi.vision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.opencv.core.Point;

import java.util.List;

import graphics.epi.util.Geometry;

public class VisionUtil {
    public static void drawQuad(Bitmap image, Geometry.Quad quad) {
        Canvas canvas = new Canvas(image);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        Point[] points = quad.getPoints();
        for(int i = 0; i < 4; i++) {
            Point p1 = points[i];
            Point p2 = points[(i+1)%4];

            canvas.drawLine((float)p1.x, (float)p1.y, (float)p2.x, (float)p2.y, paint);
        }
    }

    public static void drawQuads(Bitmap image, List<Geometry.Quad> quads) {
        for(Geometry.Quad quad: quads)
            drawQuad(image, quad);
    }
}
