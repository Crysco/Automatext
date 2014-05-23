package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Crys on 5/14/14.
 */
public class MessageSender extends IntentService {
    Handler mHandler;

    public MessageSender() {
        super("MessageSender");
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while(true) {
            synchronized (this) {
                try {
                    wait(6000);
                    DBAdapter.getInstance().open();
                    if(DBAdapter.getInstance().getCount() != 0) {
                        checkingEntries();
                    }
                    DBAdapter.getInstance().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkingEntries() {

        DBAdapter.getInstance().open();
        if(DBAdapter.getInstance().getCount() != 0) {
            String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            String currentHour = new SimpleDateFormat("hh").format(new Date());
            String currentMeridian = new SimpleDateFormat("a").format(new Date());
            Cursor cursor = DBAdapter.getInstance().getAllEntries();
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    String currentTime = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(new Date());

                    /*
                    Log.d(TAG, currentTime);
                    Log.d(TAG, entryTime);
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CONTACT)));
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_NUMBER)));
                    Log.d(TAG, cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CONTENT)));
                    */

                    String contact = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CONTACT));
                    String number = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_NUMBER));
                    String date = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_TIME));
                    String content = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CONTENT));
                    int frequency = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_FREQUENCY));
                    long id = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));

                    if(currentTime.equals(date + " " + time)) {
                        sendEntry(number, content);
                        checkDateChange(id, contact, number, date, time, content, frequency);
                    }
                } while(cursor.moveToNext());
                cursor.close();
            }
        }
        DBAdapter.getInstance().close();
    }

    private void sendEntry(String number, String content) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), 0);
        SmsManager.getDefault().sendTextMessage(number, null, content, pi, null);
    }

    private void checkDateChange(long id, String contact, String number, String date, String time, String content, int frequency) {
        Date newDate;
        Calendar cal = Calendar.getInstance();
        String newDateString;
        try {
            newDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
            cal.setTime(newDate);
            switch(frequency) {
                case Frequency.ONCE:
                    TextsListActivity.instance.updateEntries(id, null, null, null, null, null, frequency, DELETE);
                    break;
                case Frequency.DAILY:
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case Frequency.WEEKLY:
                    cal.add(Calendar.DAY_OF_MONTH, 7);
                    break;
                case Frequency.MONTHLY:
                    cal.add(Calendar.MONTH, 1);
                    break;
            }
            newDate = cal.getTime();
            newDateString = new SimpleDateFormat("MM/dd/yyyy").format(newDate);
            TextsListActivity.instance.updateEntries(id, contact, number, newDateString, time, content, frequency, UPDATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
