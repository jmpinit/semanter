package graphics.epi.vision.operations;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionListener;

abstract public class VisionOp extends VisionAction implements RunnableFuture {
    protected Bitmap result;

    public VisionOp(VisionListener caller, Bitmap source) {
        super(caller, source);
        result = source;
    }

    @Override
    public Bitmap get() throws InterruptedException, ExecutionException {
        while(result == null) { }
        return result;
    }

    @Override
    public Bitmap get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long milliTimeout = unit.toMillis(timeout);
        long startTime = System.currentTimeMillis();

        // if not done, wait for a bit
        while(result == null) {
            if(System.currentTimeMillis() - startTime > milliTimeout) {
                throw new TimeoutException();
            }
        }

        return result;
    }
}
