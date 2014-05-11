package us.semanter.app.util;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtility {
    public static void printException(Object obj, String message) {
        Log.e(obj.getClass().getName(), message);
    }

    public static void printException(Class c, String message) {
        Log.e(c.getName(), message);
    }

    public static void printException(Object obj, Exception e, String message) {
        StringWriter exceptionWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(exceptionWriter));
        printException(obj, message + "\n\n" + exceptionWriter.toString());
    }

    public static void printException(Class c, Exception e, String message) {
        StringWriter exceptionWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(exceptionWriter));
        printException(c, message + "\n\n" + exceptionWriter.toString());
    }
}
