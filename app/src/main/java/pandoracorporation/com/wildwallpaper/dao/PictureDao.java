package pandoracorporation.com.wildwallpaper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.github.jreddit.entity.Submission;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pandoracorporation.com.wildwallpaper.database.PictureSQLiteHelper;

/**
 * Created by TSMIRANI on 13/07/2015.
 * Title
 * Description
 */
public class PictureDao {

    private SQLiteDatabase mDatabase;
    private PictureSQLiteHelper mDbHelper;
    private String[] allColumns =  {
            PictureSQLiteHelper.KEY_ID,
            PictureSQLiteHelper.KEY_FILENAME,
            PictureSQLiteHelper.KEY_TITLE,
            PictureSQLiteHelper.KEY_DATE,
            PictureSQLiteHelper.KEY_DESCRIPTION
    };

    public PictureDao(Context context) {
        mDbHelper = new PictureSQLiteHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public boolean add(String filename, Submission submission) {
        ContentValues values = new ContentValues();
        values.put(PictureSQLiteHelper.KEY_FILENAME, filename);
        values.put(PictureSQLiteHelper.KEY_TITLE, submission.getTitle());
        values.put(PictureSQLiteHelper.KEY_DATE, submission.getCreated());
        values.put(PictureSQLiteHelper.KEY_DESCRIPTION, submission.getTitle());

        long insertId = mDatabase.insert(PictureSQLiteHelper.TABLE_PICS, null, values);

        if(insertId != -1) {
            Log.d("DatabaseDao", "Element "+filename+" added");
            return true;
        }

        return false;
    }

    public boolean delete(String filename) {
        int result = mDatabase.delete(PictureSQLiteHelper.TABLE_PICS,
                PictureSQLiteHelper.KEY_FILENAME + " = " + "\"" + filename + "\"", null);

        return result != 0;
    }

    public List<ContentValues> getAll() {
        List<ContentValues> pictures = new ArrayList<>();

        Cursor cursor = mDatabase.query(PictureSQLiteHelper.TABLE_PICS,
                allColumns,
                null, null, null, null, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            ContentValues contentValues = cursorToContentValues(cursor);
            pictures.add(contentValues);
            cursor.moveToNext();
        }
        cursor.close();
        return pictures;
    }

    public void deleteAll() {
        mDatabase.execSQL("delete from "+ PictureSQLiteHelper.TABLE_PICS);
    }

    private ContentValues cursorToContentValues(Cursor cursor) {
        ContentValues values = new ContentValues();
        values.put(PictureSQLiteHelper.KEY_FILENAME, cursor.getString(1));
        values.put(PictureSQLiteHelper.KEY_TITLE, cursor.getString(2));
        values.put(PictureSQLiteHelper.KEY_DATE, cursor.getString(3));
        values.put(PictureSQLiteHelper.KEY_DESCRIPTION, cursor.getString(4));

        return values;
    }

    public int getCount() {
        Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM pictures;",null);
        cursor.moveToFirst();
        int cnt = cursor.getInt(0);
        cursor.close();

        return cnt;
    }
}
