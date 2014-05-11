package us.semanter.app.model;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import us.semanter.app.util.ExceptionUtility;

public class NoteFactory {
    public final static String FILE_META_DATA = "meta.json";
    public final static String FILE_SOURCE = "source.png";

    // TODO validate note directories and content (checkrep)g

    /**
     * @param sourcePath image filepath
     * @return filepath of note folder (assumed to be parent directory of image)
     */
    public static String getNotePath(String sourcePath) {
        return new File(sourcePath).getParent();
    }

    public static String getSourcePath(String notePath) {
        return new File(notePath + "/" + new File(notePath).getName() + "-" + FILE_SOURCE).toString();
    }

    /**
     * @param imagePath path of an image to use as the source of the note
     */
    public static File createNewNote(Context ctx, String imagePath) throws IOException {
        File image = new File(imagePath);
        String extension = FilenameUtils.getExtension(image.getName());

        // get when the image was taken or last modified
        // TODO use EXIF if possible
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        Date date = new Date(image.lastModified());
        String dateTaken = df.format("yyyy_MM_dd", date).toString();

        String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 8); // friendly almost-UUID
        String noteName = dateTaken + "-" + uid;

        File noteBaseDir = getNotesDir(ctx);
        File noteDir = new File(noteBaseDir + "/" + noteName);

        // create the folders if they don't exist

        if(!noteBaseDir.exists())
            noteBaseDir.mkdir();

        if(!noteDir.exists())
            noteDir.mkdir();

        // copy in the image
        File destination = new File(noteDir + "/" + noteName + "-" + FILE_SOURCE);
        copy(image, destination);

        // create a note
        Note newNote = new Note(noteName, date);
        saveMeta(ctx, newNote);

        return destination;
    }

    public static Note noteFromPath(String rawPath) {
        File rawFile = new File(rawPath);

        // accept note directory or file in directory of note

        if(!rawFile.exists())
            return null;

        File noteDir;
        if(rawFile.isFile()) {
            noteDir = new File(rawFile.getParent());

            if(!noteDir.isDirectory())
                return null;
        } else {
            noteDir = rawFile;
        }

        // create note

        try {
            JSONObject json = new JSONObject(getStringFromFile(getNoteItem(noteDir, FILE_META_DATA).toString()));
            return new Note(json);
        } catch(Exception e) {
            ExceptionUtility.printException(NoteFactory.class, e, "Couldn't create new note from meta file.");
            return null;
        }
    }

    public static List<Note> getAllNotes(String rootPath) {
        File rootDir = new File(rootPath);

        if(!rootDir.isDirectory()) {
            Log.e("NoteFactory", "Can't get multiple notes from a file. Must be directory.");
            return null;
        }

        File[] files = rootDir.listFiles();
        List<Note> notes = new ArrayList<Note>();
        for(File file : files) {
            Log.d("NoteFactory", "file " + file);
            if(file.isDirectory()) {
                notes.add(noteFromPath(file.getPath()));
                Log.d("NoteFactory", "File to read into note is " + file);
            }
        }

        return notes;
    }

    public static File getNoteDir(Context ctx, String noteName) {
        return new File(ctx.getExternalFilesDir(null) + "/notes/" + noteName);
    }

    public static File getNotesDir(Context ctx) {
        return new File(ctx.getExternalFilesDir(null) + "/notes");
    }

    public static void saveMeta(Context ctx, Note note) {
        File noteFile = new File(getNoteDir(ctx, note.getName()) + "/" + note.getName() + "-" + FILE_META_DATA);

        try {
            PrintWriter noteWriter = new PrintWriter(noteFile, "UTF-8");
            noteWriter.print(note.toJSON().toString());
            noteWriter.close();
        } catch(JSONException e) {
            ExceptionUtility.printException(NoteFactory.class, e, "Couldn't write initial note meta file to " + noteFile + " because of JSONException.");
        } catch(Exception e) {
            ExceptionUtility.printException(NoteFactory.class, e, "Couldn't write initial note meta file to " + noteFile + ".");
        }
    }

    /*
    Helper methods
     */

    private static File getNoteItem(File noteDir, String item) {
        String noteName = noteDir.getName();
        return new File(noteDir + "/" + noteName + "-" + item);
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile (String filePath) throws IOException {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
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
