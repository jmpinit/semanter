package graphics.epi.vision.analyze;

import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.concurrent.RunnableFuture;

import graphics.epi.vision.VisionAction;
import graphics.epi.vision.VisionListener;

abstract public class VisionAnalysis extends VisionAction implements RunnableFuture {
    protected Bundle result;

    public VisionAnalysis(VisionListener caller, Bitmap source) {
        super(caller, source);
    }
}
