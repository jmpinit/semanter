package us.semanter.app.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import us.semanter.app.model.Tag;

public class TagSorterView extends ImageView {
    private Context context;
    private List<Tag> tags;
    private List<Bitmap> frames;

    public TagSorterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        tags = new ArrayList<Tag>();
        frames = new ArrayList<Bitmap>();

        setBackgroundColor(Color.BLACK);
    }

    public void addTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }

    public TagSorterView(Context context) {
        this(context, null);
    }

    private void generateFrames() {
        List<TagParticle> particles = new ArrayList<TagParticle>(tags.size());

        for(Tag t: tags) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            particles.add(new TagParticle(t, new Point(x, y)));
        }

        Bitmap frame = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setARGB(255, 255, 0, 0);
        paint.setStrokeWidth(4);

        Canvas canvas = new Canvas(frame);

        for(TagParticle p: particles) {
            canvas.drawCircle(p.x(), p.y(), 10, paint);
        }

        setImageBitmap(frame);
        frames.add(frame);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        generateFrames();
    }

    private class TagParticle {
        private Tag tag;
        private Point pos;

        public TagParticle(Tag tag, Point pos) {
            this.tag = tag;
            this.pos = pos;
        }

        public Tag getTag() {
            return tag;
        }

        public Point getPos() {
            return pos;
        }

        public int x() {
            return pos.x;
        }

        public int y() {
            return pos.y;
        }
    }
}

