package pandoracorporation.com.wildwallpaper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TSMIRANI on 13/07/2015.
 * Title
 * Description
 */
public class PictureSQLiteHelper extends SQLiteOpenHelper {

    //region Keys
    public static final String DATABASE_NAME = "wildwallpaper";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_PICS = "pictures";
    public static final String KEY_ID = "id";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_DESCRIPTION = "description";
    //endregion


    private static final String DATABASE_CREATE = "create table "
            + TABLE_PICS + "("
            + KEY_ID + " integer primary key,"
            + KEY_FILENAME + " text,"
            + KEY_TITLE + " text,"
            + KEY_DATE + " text,"
            + KEY_DESCRIPTION + " text );";


    public PictureSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PICS);
        onCreate(sqLiteDatabase);
    }
}
