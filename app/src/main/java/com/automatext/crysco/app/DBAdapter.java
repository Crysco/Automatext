package com.automatext.crysco.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.automatext.crysco.app.GlobalConstants.*;
import static com.automatext.crysco.app.GlobalConstants.NEW;
import static com.automatext.crysco.app.GlobalConstants.UPDATE;

/**
 * Created by Crys on 5/12/14.
 */
public class DBAdapter {

    public static final String KEY_ROWID = "id";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_FREQUENCY = "frequency";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "EntriesDB";
    private static final String DATABASE_TABLE = "entries";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "CREATE TABLE " +  DATABASE_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_CONTACT + " VARCHAR, " +
            KEY_NUMBER + " VARCHAR, " +
            KEY_DATE + " VARCHAR, " +
            KEY_TIME + " VARCHAR, " +
            KEY_CONTENT + " VARCHAR, " +
            KEY_FREQUENCY + " INTEGER);";

    private Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private static DBAdapter instance;

    public static DBAdapter getInstance() {
        if(instance == null) {
            instance = new DBAdapter();
        }
        return instance;
    }

    protected DBAdapter() {
    }

    public void initiateDatabase(Context c) {
        this.context = c;
        DBHelper = new DatabaseHelper(context);
        accessDatabase();
    }

    private void accessDatabase() {
        try {
            String destPath = "/data/data/" + context.getPackageName() + "/database/EntriesDB";
            File f = new File(destPath);
            if(!f.exists()) {
                instance.CopyDB(context.getAssets().open("mydb"), new FileOutputStream(destPath));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CopyDB(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    public void updateDatabase(long rowId, String name, String number, String date, String time, String content, int frequency, int mode) {
        if (mode == UPDATE || mode == NEW)
            instance.updateEntry(rowId, name, number, date, time, content, frequency);
        else if (mode == GlobalConstants.DELETE)
            instance.deleteEntry(rowId);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long getCount() {
        Cursor mCount = db.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
        mCount.moveToFirst();
        int count = mCount.getCount();
        mCount.close();

        return count;
    }

    public long insertEntry(String contact, String number, String date, String time, String content, int frequency) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CONTACT, contact);
        initialValues.put(KEY_NUMBER, number);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_TIME, time);
        initialValues.put(KEY_CONTENT, content);
        initialValues.put(KEY_FREQUENCY, frequency);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteEntry(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteAllEntries() {
        return db.delete(DATABASE_TABLE, null, null)  > 0;
    }

    public Cursor getAllEntries() {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTACT, KEY_NUMBER, KEY_DATE, KEY_TIME, KEY_CONTENT, KEY_FREQUENCY}, null, null, null, null, null);
    }

    public Cursor getAllEntries(String date, String hour, String meridian) {
        String query = "SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_DATE + " = '" + date + "' AND " + KEY_TIME + " LIKE '" + hour + "%' AND " + KEY_TIME + " LIKE '%" + meridian + "'";
        return db.rawQuery(query, null);
    }

    public Cursor getEntry(long rowId) throws SQLException {
        Cursor mCursor = db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTACT, KEY_NUMBER, KEY_DATE, KEY_TIME, KEY_CONTENT, KEY_FREQUENCY}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if(mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }

    public boolean updateEntry(long rowId, String contact, String number, String date, String time, String content, int frequency) {
        ContentValues args = new ContentValues();
        args.put(KEY_CONTACT, contact);
        args.put(KEY_NUMBER, number);
        args.put(KEY_DATE, date);
        args.put(KEY_TIME, time);
        args.put(KEY_CONTENT, content);
        args.put(KEY_FREQUENCY, frequency);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
