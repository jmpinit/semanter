package us.semanter.app.ui.review;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import org.opencv.core.Point;

import java.util.List;

import us.semanter.app.ui.VisionView;
import us.semanter.app.vision.result.FlattenerResult;
import us.semanter.app.vision.result.OutlineGuess;

public class FlattenerView extends VisionView {
    private FlattenerResult result;
    private Bitmap prior;

    public FlattenerView(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
    }

    public void review(FlattenerResult result) {
        this.result = result;
        prior = BitmapFactory.decodeFile(result.getPrior().getPath());
    }

    @Override
    protected void render(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.MAGENTA);
        p.setStrokeWidth(4);

        canvas.drawBitmap(prior, 0, 0, p);

        // draw outlines of paper
        for(OutlineGuess outline: result.getOutlines()) {
            try {
                p.setAlpha((int)(128 + outline.getConfidence() * 128));

                List<Point> points = outline.getPolygon().getPoints();
                for(int i=0; i < points.size(); i++) {
                    Point first = points.get(i);
                    Point second = points.get((i+1)%points.size());
                    canvas.drawLine((float)first.x, (float)first.y, (float)second.x, (float)second.y, p);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
