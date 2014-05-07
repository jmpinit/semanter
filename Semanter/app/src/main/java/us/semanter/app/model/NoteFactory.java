package us.semanter.app.model;

import android.content.Context;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;

public class NoteFactory {
    /**
     * @param sourcePath image filepath
     * @return filepath of note folder (assumed to be parent directory of image)
     */
    public static String getNotePath(String sourcePath) {
        return new File(sourcePath).getParent();
    }

    /**
     * @param imagePath path of an image to use as the source of the note
     */
    public static void createNewNote(Context ctx, String imagePath) throws IOException {
        File image = new File(imagePath);
        String extension = FilenameUtils.getExtension(image.getName());

        // get when the image was taken or last modified
        // TODO use EXIF if possible
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String dateTaken = df.format("yyyy-MM-dd", image.lastModified()).toString();

        String noteName = dateTaken + "-" + UUID.randomUUID();

        File dataPath = ctx.getExternalFilesDir(null);
        File noteBaseDir = new File(dataPath + "/notes/");
        File noteDir = new File(noteBaseDir + "/" + noteName);

        // create the folders if they don't exist

        if(!noteBaseDir.exists())
            noteBaseDir.mkdir();

        if(!noteDir.exists())
            noteDir.mkdir();

        // copy in the image
        copy(image, new File(noteDir + "/" + noteName + "-source." + extension));
    }

    private static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
