package us.semanter.app.vision;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.core.Mat;

import java.io.FileOutputStream;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

public class VisionUtil {
    public static Mat matFromFile(String sourcePath) {
        Bitmap bmp = BitmapFactory.decodeFile(sourcePath);

        Mat mat = new Mat();
        bitmapToMat(bmp, mat);

        return mat;
    }

    public static void saveMat(Mat image, Bitmap.Config config, String destinationPath) {
        Bitmap bmp = Bitmap.createBitmap(image.width(), image.height(), config);
        matToBitmap(image, bmp);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destinationPath);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                out.close();
            } catch(Throwable ignore) {}
        }
    }
}
