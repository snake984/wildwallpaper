package pandoracorporation.com.wildwallpaper.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by snake984 on 08/07/2015.
 * Title
 * Description
 */
public class FileHelper {

    private static String PICS_DIR="/sdcard/wildwallpaper/";

    public FileHelper() {

    }

    public static String createDirectoryAndSaveFile(Bitmap imageToSave) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "JPEG_" + timeStamp + "_" + ".jpg";
        File direct = new File(Environment.getExternalStorageDirectory() + "/wildwallpaper");

        if (!direct.exists()) {
            File wallpaperDirectory = new File(PICS_DIR);
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File(PICS_DIR), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap openThumbnailFromFile(String filename) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(filename, options);
    }

    public static Bitmap openFullSizePicFromFile(String filename) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(filename, options);
    }

    public static boolean deleteFile(String filename) {
        File file = new File(filename);

        return file.exists() && file.delete();
    }
}
