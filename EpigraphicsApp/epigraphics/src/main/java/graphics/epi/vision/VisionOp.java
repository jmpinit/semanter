package graphics.epi.vision;

import android.graphics.Bitmap;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

abstract public class VisionOp implements RunnableFuture {
    protected Bitmap source, result;
    protected boolean finished, cancelled;
    protected VisionListener caller;

    public VisionOp(VisionListener caller, Bitmap source) {
        this.caller = caller;
        this.source = source;
        finished = false;
        cancelled = false;
    }

    protected void finish() {
        finished = true;
        ((VisionListener)caller).OnVisionOpComplete(this);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        cancelled = true;
        finished = true;

        return isCancelled();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return finished;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        while(result == null) { }
        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long milliTimeout = unit.toMillis(timeout);
        long startTime = System.currentTimeMillis();

        // if not done, wait for a bit
        while(result != null) {
            if(System.currentTimeMillis() - startTime > milliTimeout) {
                throw new TimeoutException();
            }
        }

        return result;
    }
}
