package us.semanter.app.ui.review;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import us.semanter.app.model.Note;
import us.semanter.app.model.NoteFactory;
import us.semanter.app.ui.VisionView;

public class NoteView extends VisionView {
    private final static float ZOOM_FACTOR = 4.0f;

    private Note note;
    private Bitmap noteImage;

    private float lastZoom, zoom;
    private PointF startPosition, currentPosition, imagePosition;
    private boolean inMotion;

    public NoteView(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
    }

    public NoteView(Context ctx) {
        super(ctx);
    }

    public void setNote(Note note) {
        zoom = 1.0f;
        lastZoom = 1.0f;
        currentPosition = new PointF(0, 0);
        startPosition = new PointF(0, 0);
        imagePosition = new PointF(0, 0);
        inMotion = false;

        this.note = note;

        String sourcePath = NoteFactory.getPathTo(getContext(), note.getName(), NoteFactory.FILE_SOURCE);
        noteImage = BitmapFactory.decodeFile(sourcePath);
    }

    @Override
    protected void render(Canvas canvas) {
        Paint p = new Paint();

        canvas.drawColor(Color.BLACK);

        if(noteImage != null) {
            if(inMotion) {
                float dx = currentPosition.x - startPosition.x;
                float dy = currentPosition.y - startPosition.y;
                canvas.translate(imagePosition.x + dx, imagePosition.y + dy);
            } else {
                canvas.translate(imagePosition.x, imagePosition.y);
            }

            float z = lastZoom + zoom;
            //canvas.scale(z, z);
            canvas.drawBitmap(noteImage, 0, 0, p);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(event.getPointerCount() == 1) {
                    currentPosition.set(event.getX(), event.getY());
                    startPosition.set(event.getX(), event.getY());

                    inMotion = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 1) {
                    currentPosition.set(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_UP:
                if(event.getPointerCount() == 1) {
                    currentPosition.set(event.getX(), event.getY());

                    float dx = currentPosition.x - startPosition.x;
                    float dy = currentPosition.y - startPosition.y;

                    imagePosition.set(imagePosition.x + dx, imagePosition.y + dy);

                    inMotion = false;
                }
                break;
        }

        return true;
    }
}
