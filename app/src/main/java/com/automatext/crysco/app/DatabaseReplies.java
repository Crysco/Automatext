package com.automatext.crysco.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.automatext.crysco.app.GlobalConstants.*;

/**
 * Created by Crys on 5/25/14.
 */
public class DatabaseReplies extends Database {

    public static final String KEY_ROWID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_END_TIME = "end_ime";
    public static final String KEY_DAYS = "days";
    public static final String KEY_SILENCE = "silenced";
    public static final String KEY_ACTIVE = "active_two";
    public static final String KEY_CONTENT = "content";

    public static final String DATABASE_REPLIES_TABLE = "replies";

    public static final String DATABASE_CREATE_REPLIES = "CREATE TABLE " + DATABASE_REPLIES_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " VARCHAR, " +
            KEY_START_TIME + " TEXT, " +
            KEY_END_TIME + " TEXT, " +
            KEY_CONTENT + " VARCHAR, " +
            KEY_DAYS + " VARCHAR, " +
            KEY_SILENCE + " INTEGER, " +
            KEY_ACTIVE + " INTEGER);";

    private static DatabaseReplies repliesInstance;

    public static DatabaseReplies getInstance() {
        if(repliesInstance == null) {
            repliesInstance = new DatabaseReplies();
        }
        return repliesInstance;
    }

    protected DatabaseReplies() {
    }

    @Override
    public long getRecordsCount() {
        Cursor mCount = DBAdapter.getMainDBInstance().getDatabase().rawQuery("SELECT * FROM " + DATABASE_REPLIES_TABLE, null);
        mCount.moveToFirst();
        int count = mCount.getCount();
        mCount.close();

        return count;
    }

    public void updateRepliesDatabase(Reply reply, int mode) {
        if (mode == GlobalConstants.Mode.UPDATE || mode == GlobalConstants.Mode.NEW) {
            updateRecord(reply.getID(), reply.getTitle(),
                    daysToDateParser(reply.getStartTime(), reply.getDays(), true),
                    daysToDateParser(reply.getEndTime(), reply.getDays(), true),
                    reply.getContent(), reply.getDays(), reply.getSilence());
            updateRecordActiveCheck(reply.getID(), reply.getActive());
        }
        else if (mode == GlobalConstants.Mode.DELETE)
            deleteRecord(reply.getID());
    }

    @Override
    public long insertRecord(String title, String startTime, String endTime, String content, String days, int silence) {
        ContentValues initialValues = new ContentValues();
        if(title != null)
            initialValues.put(KEY_TITLE, title);
        if(startTime != null)
            initialValues.put(KEY_START_TIME, startTime);
        if(endTime != null)
            initialValues.put(KEY_END_TIME, endTime);
        if(content != null)
            initialValues.put(KEY_CONTENT, content);
        if(days != null)
            initialValues.put(KEY_DAYS, days);
        if(silence != -1)
            initialValues.put(KEY_SILENCE, silence);
        return DBAdapter.getMainDBInstance().getDatabase().insertOrThrow(DATABASE_REPLIES_TABLE, null, initialValues);
    }

    @Override
    public boolean updateRecord(long rowId, String title, String startTime, String endTime, String content, String days, int silence) {
        ContentValues args = new ContentValues();
        if(title != null)
            args.put(KEY_TITLE, title);
        if(startTime != null)
            args.put(KEY_START_TIME, startTime);
        if(endTime != null)
            args.put(KEY_END_TIME, endTime);
        if(content != null)
            args.put(KEY_CONTENT, content);
        if(days != null)
            args.put(KEY_DAYS, days);
        if(silence != -1)
            args.put(KEY_SILENCE, silence);
        return DBAdapter.getMainDBInstance().getDatabase().update(DATABASE_REPLIES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    @Override
    public boolean deleteRecord(long rowId) {
        //TODO: MAKE TOAST ABOUT APP DELETION
        return DBAdapter.getMainDBInstance().getDatabase().delete(DATABASE_REPLIES_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    @Override
    public boolean deleteAllRecords() {
        return DBAdapter.getMainDBInstance().getDatabase().delete(DATABASE_REPLIES_TABLE, null, null)  > 0;
    }


    @Override
    public Cursor getRecord(long rowId) throws SQLException {
        Cursor mCursor = DBAdapter.getMainDBInstance().getDatabase().query(DATABASE_REPLIES_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_START_TIME, KEY_END_TIME, KEY_CONTENT, KEY_DAYS, KEY_ACTIVE, KEY_SILENCE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if(mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }

    @Override
    public Cursor getAllRecords() {
        return DBAdapter.getMainDBInstance().getDatabase().query(DATABASE_REPLIES_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_START_TIME, KEY_END_TIME, KEY_CONTENT, KEY_DAYS, KEY_ACTIVE, KEY_SILENCE}, null, null, null, null, null);
    }

    @Override
    public Cursor getMostRecentRecord() {
        Cursor newDateCursor = DBAdapter.getMainDBInstance().getDatabase().query(DATABASE_REPLIES_TABLE, null, null, null, null, null, KEY_START_TIME + " ASC LIMIT 1");
        if(newDateCursor != null) {
            newDateCursor.moveToFirst();
        }
        return newDateCursor;
    }

    public boolean updateRecordActiveCheck(long rowId, int check) {
        ContentValues args = new ContentValues();
        if(check != -1)
            args.put(KEY_ACTIVE, check);
        return DBAdapter.getMainDBInstance().getDatabase().update(DATABASE_REPLIES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    @Override
    public boolean updateRecordTime(long rowId) {
        ContentValues args = new ContentValues();
        Cursor cursor = getRecord(rowId);
        if(!cursor.getString(cursor.getColumnIndex(KEY_DAYS)).equals("0000000")) {
            args.put(KEY_START_TIME, daysToDateParser(cursor.getString(cursor.getColumnIndex(KEY_START_TIME)), cursor.getString(cursor.getColumnIndex(KEY_DAYS)), false));
            args.put(KEY_END_TIME, daysToDateParser(cursor.getString(cursor.getColumnIndex(KEY_END_TIME)), cursor.getString(cursor.getColumnIndex(KEY_DAYS)), false));
            return DBAdapter.getMainDBInstance().getDatabase().update(DATABASE_REPLIES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        } else {
            return false;
        }
    }

    public Reply convertRowToObject(long rowId) {
        Cursor cursor = getRecord(rowId);
        Reply reply = new Reply();
        if(cursor != null && cursor.moveToFirst()) {
            reply.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_TITLE)));
            reply.setStartTime(cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_START_TIME)).substring(11, 16));
            reply.setEndTime(cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_END_TIME)).substring(11, 16));
            reply.setContent(cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_CONTENT)));
            reply.setDays(cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_DAYS)));
            reply.setActive(cursor.getInt(cursor.getColumnIndex(DatabaseReplies.KEY_ACTIVE)));
            reply.setSilence(cursor.getInt(cursor.getColumnIndex(DatabaseReplies.KEY_SILENCE)));
            reply.setId(rowId);
        }
        return reply;
    }


    private String daysToDateParser(String time, String days, boolean sameDay) {

        Calendar calendar = Calendar.getInstance();

        int inc = 0;
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Sun = 0, Mon = 1, etc.
        if(!sameDay) {
            dayOfWeek += 1;
            inc = 1;
        }

        while (inc != 8) {
            if(dayOfWeek > 6) {
                dayOfWeek = 0;
            }
            if(days.charAt(dayOfWeek) == '0') {
                inc++;
                dayOfWeek++;
            } else {
                break;
            }
        }

        calendar.add(Calendar.DAY_OF_MONTH, inc);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(11,13)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(14, 16)));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(calendar.getTime());
    }

    public int determineActiveState(long rowId) {
        int activeState = -1;
        Cursor cursor = DatabaseReplies.getInstance().getRecord(rowId);
        if(cursor != null && cursor.moveToFirst()) {
            long startMilliseconds = 0;
            try {
                startMilliseconds = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_START_TIME))).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                long currentMilliseconds = new Date().getTime();
                if(currentMilliseconds > startMilliseconds) {
                    activeState = ActiveState.ACTIVE;
                } else {
                    activeState = ActiveState.NOT_ACTIVE;
                }
            }
            cursor.close();
        }
        return activeState;
    }
}
