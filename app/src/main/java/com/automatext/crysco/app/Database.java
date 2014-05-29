package com.automatext.crysco.app;

import android.database.Cursor;

/**
 * Created by Crys on 5/25/14.
 */
public abstract class Database {

    public abstract long getRecordsCount();
    public abstract Cursor getRecord(long rowId);
    public abstract Cursor getAllRecords();
    public abstract Cursor getMostRecentRecord();
    public abstract long insertRecord(String a, String b, String c, String d, String e, int frequency);
    public abstract boolean updateRecord(long rowId, String a, String b, String c, String d, String e, int frequency);
    public abstract boolean updateRecordTime(long rowId);
    public abstract boolean deleteRecord(long rowId);
    public abstract boolean deleteAllRecords();

}
