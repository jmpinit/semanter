package us.semanter.app.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import us.semanter.app.R;
import us.semanter.app.model.Note;

public class VisionView extends SurfaceView implements SurfaceHolder.Callback {
    private Context mContext;
    private VisionViewUpdater updater;
    private Thread updaterThread;

    private Bitmap noteBitmap;

    public VisionView(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);

        mContext = ctx;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);
    }

    public void render(Canvas canvas) {
        Paint paint = new Paint();

        if(noteBitmap != null) {
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(noteBitmap, 0, 0, paint);
        } else {
            canvas.drawColor(Color.RED);
        }
    }

    public void open(Note note) {
        // TODO load composite note image

        // FIXME test
        switch(note.getProgress()) {
            case 0:
                noteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_progress_1);
                break;
            case 1:
                noteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_progress_2);
                break;
            case 2:
                noteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_progress_3);
                break;
            case 3:
                noteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_progress_4);
                break;
            case 4:
                noteBitmap = null;
                break;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            updater.stop();
            updaterThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("VisionView", "Shutting down updater thread failed.");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        updater = new VisionViewUpdater(this, holder);
        updaterThread = new Thread(updater);
        updaterThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    class VisionViewUpdater implements Runnable {
        private VisionView parent;
        private SurfaceHolder holder;
        private boolean running = true;

        public VisionViewUpdater(VisionView parent, SurfaceHolder holder) {
            this.parent = parent;
            this.holder = holder;
        }

        public void run() {
            while(running) {
                Canvas canvas = null;

                try {
                    canvas = holder.lockCanvas();

                    if(canvas != null) {
                        synchronized(holder) {
                            parent.render(canvas);
                        }
                    }
                } finally {
                    if(canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        public void stop() {
            running = false;
        }
    }
}