package com.automatext.crysco.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.automatext.crysco.app.GlobalConstants.*;

/**
 * Created by Crys on 5/12/14.
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "Database";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private static DBAdapter mainDBInstance;

    public static DBAdapter getMainDBInstance() {
        if(mainDBInstance == null) {
            mainDBInstance = new DBAdapter();
        }
        return mainDBInstance;
    }

    public SQLiteDatabase getDatabase() {
        return db;
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
            String destPath = "/data/data/" + context.getPackageName() + "/database/" + DATABASE_NAME;
            File f = new File(destPath);
            if(!f.exists()) {
                mainDBInstance.CopyDB(context.getAssets().open("mydb"), new FileOutputStream(destPath));
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

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DatabaseEntries.DATABASE_CREATE_ENTRIES);
                db.execSQL(DatabaseReplies.DATABASE_CREATE_REPLIES);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseEntries.DATABASE_ENTRIES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseReplies.DATABASE_REPLIES_TABLE);
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
}
