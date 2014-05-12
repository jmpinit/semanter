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
import us.semanter.app.ui.VisionView;

public class NoteView extends VisionView {
    private final static float ZOOM_FACTOR = 4.0f;

    private Note note;
    private Bitmap noteImage;

    private float startZoom, currentZoom, imageZoom;
    private PointF startPosition, currentPosition, imagePosition;
    private boolean panning, zooming;

    public NoteView(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
    }

    public NoteView(Context ctx) {
        super(ctx);
    }

    public void setNote(Note note) {
        startZoom = 1.0f;
        currentZoom = 1.0f;
        imageZoom = 1.0f;

        currentPosition = new PointF(0, 0);
        startPosition = new PointF(0, 0);
        imagePosition = new PointF(0, 0);

        panning = false;
        zooming = false;

        this.note = note;

        noteImage = BitmapFactory.decodeFile(note.getLast().getPath());
    }

    @Override
    protected void render(Canvas canvas) {
        Paint p = new Paint();

        canvas.drawColor(Color.BLACK);

        if(noteImage != null) {
            float z;
            if(zooming) {
                z = imageZoom + (currentZoom - startZoom);
            } else {
                z = imageZoom;
            }

            canvas.scale(z, z, getWidth()/2.0f, getHeight()/2.0f);

            if(panning) {
                float dx = currentPosition.x - startPosition.x;
                float dy = currentPosition.y - startPosition.y;

                canvas.translate((imagePosition.x + dx)/z, (imagePosition.y + dy)/z);
            } else {
                canvas.translate(imagePosition.x/z, imagePosition.y/z);
            }

            canvas.drawBitmap(noteImage, 0, 0, p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                // panning
                currentPosition.set(event.getX(), event.getY());
                startPosition.set(event.getX(), event.getY());

                panning = true;

                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                panning = false;

                // zooming
                float x1 = event.getX(0);
                float y1 = event.getY(0);
                float x2 = event.getX(1);
                float y2 = event.getY(1);

                float distance = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                currentZoom = distance / (Math.max(getWidth(), getHeight()) / ZOOM_FACTOR);
                startZoom = currentZoom;

                zooming = true;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (event.getPointerCount() == 1) {
                    if(panning)
                        currentPosition.set(event.getX(), event.getY());
                } else if (event.getPointerCount() == 2) {
                    if(zooming) {
                        float x1 = event.getX(0);
                        float y1 = event.getY(0);
                        float x2 = event.getX(1);
                        float y2 = event.getY(1);

                        float distance = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                        currentZoom = distance / (Math.max(getWidth(), getHeight()) / ZOOM_FACTOR);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if(panning) {
                    currentPosition.set(event.getX(), event.getY());

                    // panning
                    float dx = currentPosition.x - startPosition.x;
                    float dy = currentPosition.y - startPosition.y;

                    imagePosition.set(imagePosition.x + dx, imagePosition.y + dy);

                    panning = false;
                }

                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                if(zooming) {
                    // zooming
                    float deltaZoom = currentZoom - startZoom;
                    imageZoom += deltaZoom;

                    zooming = false;
                }

                break;
            }
        }

        return true;
    }
}
