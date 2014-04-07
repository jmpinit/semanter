package graphics.epi.vision.operations;

import android.graphics.Bitmap;

import java.util.concurrent.RunnableFuture;

import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionListener;

abstract public class VisionOp extends VisionAction implements RunnableFuture {
    protected Bitmap result;

    public VisionOp(VisionListener caller, Bitmap source) {
        super(caller, source);
    }
}
