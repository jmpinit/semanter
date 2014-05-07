package us.semanter.app.ui.review;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import us.semanter.app.model.Note;
import us.semanter.app.ui.VisionView;
import us.semanter.app.vision.VisionPipeline;

public class NoteView extends VisionView {
    private Note note;
    private Bitmap noteImage;

    public NoteView(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
    }

    public void setNote(Note note) {
        this.note = note;
        String lastTask = VisionPipeline.TASK_POSITION.get(note.getResultCount()-1).getName();
        noteImage = BitmapFactory.decodeFile(note.getResult(lastTask).getCurrent().getPath());
    }

    @Override
    protected void render(Canvas canvas) {
        Paint p = new Paint();

        canvas.drawColor(Color.BLACK);
        //canvas.drawBitmap(note, 0, 0, p);
    }
}
