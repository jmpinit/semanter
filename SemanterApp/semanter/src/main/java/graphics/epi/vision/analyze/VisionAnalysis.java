package graphics.epi.vision.analyze;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionListener;

abstract public class VisionAnalysis extends VisionAction implements RunnableFuture {
    protected Bundle result;

    public VisionAnalysis(VisionListener caller, Bitmap source) {
        super(caller, source);
    }

    @Override
    public Bundle get() throws InterruptedException, ExecutionException {
        while(result == null) { }
        return result;
    }

    @Override
    public Bundle get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
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
