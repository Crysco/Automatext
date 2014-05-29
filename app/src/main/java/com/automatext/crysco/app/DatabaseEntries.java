package com.automatext.crysco.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Crys on 5/25/14.
 */
public class DatabaseEntries extends Database {

    public static final String KEY_ROWID = "id";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_DATE = "date";
    //public static final String KEY_TIME = "time";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_FREQUENCY = "frequency";

    public static final String DATABASE_ENTRIES_TABLE = "entries";

    public static final String DATABASE_CREATE_ENTRIES = "CREATE TABLE " + DATABASE_ENTRIES_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_CONTACT + " VARCHAR, " +
            KEY_NUMBER + " VARCHAR, " +
            KEY_DATE + " TEXT, " +
            //KEY_TIME + " VARCHAR, " +
            KEY_CONTENT + " VARCHAR, " +
            KEY_FREQUENCY + " INTEGER);";

    private static DatabaseEntries entriesInstance;

    public static DatabaseEntries getInstance() {
        if(entriesInstance == null) {
            entriesInstance = new DatabaseEntries();
        }
        return entriesInstance;
    }

    protected DatabaseEntries() {
    }

    @Override
    public long getRecordsCount() {
        Cursor mCount = DBAdapter.getMainDBInstance().getDatabase().rawQuery("SELECT * FROM " + DATABASE_ENTRIES_TABLE, null);
        mCount.moveToFirst();
        int count = mCount.getCount();
        mCount.close();

        return count;
    }

    public void updateRecordsDatabase(Entry entry, int mode) {
        if (mode == GlobalConstants.Mode.UPDATE || mode == GlobalConstants.Mode.NEW) {
            updateRecord(entry.getID(), entry.getName(), entry.getNumber(), entry.getDate(), entry.getTime(), entry.getContent(), entry.getFrequency());
        } else if (mode == GlobalConstants.Mode.DELETE) {
            deleteRecord(entry.getID());
        }
    }

    @Override
    public long insertRecord(String contact, String number, String date, String time, String content, int frequency) {
        ContentValues initialValues = new ContentValues();
        if(contact != null)
            initialValues.put(KEY_CONTACT, contact);
        if(number != null)
            initialValues.put(KEY_NUMBER, number);
        if(date != null)
            initialValues.put(KEY_DATE, date + " " + time);
        if(content != null)
            initialValues.put(KEY_CONTENT, content);
        if(frequency != -1)
            initialValues.put(KEY_FREQUENCY, frequency);
        return DBAdapter.getMainDBInstance().getDatabase().insert(DATABASE_ENTRIES_TABLE, null, initialValues);
    }

    @Override
    public boolean updateRecord(long rowId, String contact, String number, String date, String time, String content, int frequency) {
        ContentValues args = new ContentValues();
        if(contact != null)
            args.put(KEY_CONTACT, contact);
        if(number != null)
            args.put(KEY_NUMBER, number);
        if(date != null)
            args.put(KEY_DATE, date + " " + time);
        if(content != null)
            args.put(KEY_CONTENT, content);
        if(frequency != -1)
            args.put(KEY_FREQUENCY, frequency);
        return DBAdapter.getMainDBInstance().getDatabase().update(DATABASE_ENTRIES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    @Override
    public boolean deleteRecord(long rowId) {
        return DBAdapter.getMainDBInstance().getDatabase().delete(DATABASE_ENTRIES_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    @Override
    public boolean deleteAllRecords() {
        return DBAdapter.getMainDBInstance().getDatabase().delete(DATABASE_ENTRIES_TABLE, null, null)  > 0;
    }

    @Override
    public Cursor getRecord(long rowId) throws SQLException {
        Cursor mCursor = DBAdapter.getMainDBInstance().getDatabase().query(DATABASE_ENTRIES_TABLE, new String[]{KEY_ROWID, KEY_CONTACT, KEY_NUMBER, KEY_DATE, KEY_CONTENT, KEY_FREQUENCY}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if(mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }

    @Override
    public Cursor getAllRecords() {
        return DBAdapter.getMainDBInstance().getDatabase().query(DATABASE_ENTRIES_TABLE, new String[]{KEY_ROWID, KEY_CONTACT, KEY_NUMBER, KEY_DATE, KEY_CONTENT, KEY_FREQUENCY}, null, null, null, null, null);
    }

    @Override
    public Cursor getMostRecentRecord() {
        Cursor newDateCursor = DBAdapter.getMainDBInstance().getDatabase().query(DATABASE_ENTRIES_TABLE, null, null, null, null, null, KEY_DATE + " ASC LIMIT 1");
        if(newDateCursor != null) {
            newDateCursor.moveToFirst();
        }
        return newDateCursor;
    }

    @Override
    public boolean updateRecordTime(long rowId) {

        ContentValues args = new ContentValues();
        Cursor cursor = getRecord(rowId);
        final String newDate = changeDate(cursor.getInt(cursor.getColumnIndex(KEY_FREQUENCY)), cursor.getString(cursor.getColumnIndex(KEY_DATE)));
        if(cursor.getInt(cursor.getColumnIndex(KEY_FREQUENCY)) != GlobalConstants.Frequency.ONCE && newDate != null) {
            args.put(KEY_DATE, newDate);
            return DBAdapter.getMainDBInstance().getDatabase().update(DATABASE_ENTRIES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        } else {
            return false;
        }
    }

    public Entry convertRowToObject(long rowId) {
        Cursor cursor = getRecord(rowId);
        Entry entry = new Entry();
        if(cursor != null && cursor.moveToFirst()) {
            entry.setName(cursor.getString(cursor.getColumnIndex(DatabaseEntries.KEY_CONTACT)));
            entry.setNumber(cursor.getString(cursor.getColumnIndex(DatabaseEntries.KEY_NUMBER)));
            entry.setDate(cursor.getString(cursor.getColumnIndex(DatabaseEntries.KEY_DATE)).substring(0, 10));
            entry.setTime(cursor.getString(cursor.getColumnIndex(DatabaseEntries.KEY_DATE)).substring(11, 16));
            entry.setContent(cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_CONTENT)));
            entry.setFrequency(cursor.getInt(cursor.getColumnIndex(DatabaseEntries.KEY_FREQUENCY)));
            entry.setID(cursor.getLong(cursor.getColumnIndex(DatabaseEntries.KEY_ROWID)));
            cursor.close();
        } else {
            Log.d(GlobalConstants.TAG, "Entry could not be found");
        }
        return entry;
    }

    private String changeDate(int frequency, String date) {
        Date newDate;
        Calendar cal = Calendar.getInstance();
        String newDateString = null;
        try {
            newDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
            cal.setTime(newDate);
            switch(frequency) {
                case GlobalConstants.Frequency.DAILY:
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case GlobalConstants.Frequency.WEEKLY:
                    cal.add(Calendar.DAY_OF_MONTH, 7);
                    break;
                case GlobalConstants.Frequency.MONTHLY:
                    cal.add(Calendar.MONTH, 1);
                    break;
            }
            newDate = cal.getTime();
            newDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(GlobalConstants.TAG, "Could not parse date.");
        }
        return newDateString;
    }
}
