package com.cornford.games.pipepower;

import android.os.Environment;

import java.io.File;

/**
 * Created by kprotasov on 13.06.2016.
 */
public class FileUtils {

    private static final String PATH_WITH_SD = "/pipepower/record/";
    private static final String EXTENSION = ".3gp";
    private static final String FILE_PREFIX = "record";

    public static boolean isSdCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String createFile() {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_WITH_SD);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        final File file = new File(dir, FILE_PREFIX + timestamp + EXTENSION);
        return file.getAbsolutePath();
    }

}
