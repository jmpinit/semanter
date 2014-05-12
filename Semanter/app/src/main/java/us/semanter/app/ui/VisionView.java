package us.semanter.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class VisionView extends SurfaceView implements SurfaceHolder.Callback {
    protected Context mContext;
    private VisionViewUpdater updater;
    private Thread updaterThread;

    public VisionView(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);

        mContext = ctx;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);
    }

    public VisionView(Context ctx) {
        super(ctx);

        mContext = ctx;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);
    }

    abstract protected void render(Canvas canvas);

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