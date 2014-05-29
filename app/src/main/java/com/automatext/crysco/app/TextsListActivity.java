package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TextsListActivity extends FragmentActivity implements TextsListFragment.Communicator, TextDetailsActivity.Communicator {

    private TextsListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texts_list);

        if(savedInstanceState == null)
            fragment = initializeFragment();
        else
            fragment = (TextsListFragment) getSupportFragmentManager().findFragmentByTag(Tags.TEXTS_LIST_FRAGMENT);

        fragment.setCommunicator(this);
        TextDetailsActivity.setCommunicator(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragment.checkForEmptyEntries();
        startAlarmService();
    }

    private TextsListFragment initializeFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Tags.ENTRIES, populateList());
        fragment = new TextsListFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentPlaceHolderMain, fragment, Tags.TEXTS_LIST_FRAGMENT).commit();
        return fragment;
    }

    private void startAlarmService() {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);

        Log.d(TAG, "Starting Alarm Service");

        DBAdapter.getMainDBInstance().open();
        if(DatabaseEntries.getInstance().getRecordsCount() != 0) {
            Cursor cursor = DatabaseEntries.getInstance().getMostRecentRecord();
            if(cursor != null && cursor.moveToFirst()) {
                long newMilliseconds = 0;
                try {
                    newMilliseconds = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(cursor.getString(cursor.getColumnIndex(DatabaseEntries.KEY_DATE))).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, Long.toString(newMilliseconds));
                    alarm.set(AlarmManager.RTC_WAKEUP, newMilliseconds, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
                    Log.d(TAG, "Alarm set for " + cursor.getString(cursor.getColumnIndex(DatabaseEntries.KEY_DATE)) + ", which is a start time.");
                }
                cursor.close();
            } else {
                Log.d(TAG, "Cursor is equal to null");
            }
        }
        DBAdapter.getMainDBInstance().close();
    }

    private ArrayList<Entry> populateList() {
        ArrayList<Entry> list = new ArrayList<Entry>();
        DBAdapter.getMainDBInstance().open();
        if(DatabaseEntries.getInstance().getRecordsCount() != 0) {
            Cursor cursor = DatabaseEntries.getInstance().getAllRecords();
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Entry entry = DatabaseEntries.getInstance().convertRowToObject(cursor.getLong(cursor.getColumnIndex(DatabaseEntries.KEY_ROWID)));
                    list.add(entry);
                } while(cursor.moveToNext());
                cursor.close();
            }
        }

        DBAdapter.getMainDBInstance().close();
        return list;
    }

    @Override
    public void respond(long id, int mode) {
            Bundle bundle = new Bundle();

                DBAdapter.getMainDBInstance().open();

                    Entry entry = DatabaseEntries.getInstance().convertRowToObject(id);
                    Log.d(TAG, "YEAH " + Long.toString(entry.getID()));
                    bundle.putParcelable(Tags.ENTRY, entry);
                    bundle.putInt(Tags.MODE, mode);

                DBAdapter.getMainDBInstance().close();
                Intent intent = new Intent(this, TextDetailsActivity.class);
                intent.putExtra(Tags.BUNDLE, bundle);
                startActivity(intent);
    }

    @Override
    public void updateEntries(Entry entry, int mode) {

        DBAdapter.getMainDBInstance().open();
        DatabaseEntries.getInstance().updateRecordsDatabase(entry, mode);
        DBAdapter.getMainDBInstance().close();
        fragment.updateList(entry, mode);
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DBAdapter.getMainDBInstance().initiateDatabase(context);
            DBAdapter.getMainDBInstance().open();
            final DatabaseEntries instance = DatabaseEntries.getInstance();
            Cursor cursor = instance.getMostRecentRecord();
            if(cursor != null && cursor.moveToFirst()) {
                Entry entry = DatabaseEntries.getInstance().convertRowToObject(cursor.getLong(cursor.getColumnIndex(DatabaseEntries.KEY_ROWID)));
                SmsSender.sendText(Entry.EntryParser.parseNumber(entry.getName()), entry.getContent());
                if(!instance.updateRecordTime(entry.getID())) {
                    instance.updateRecordsDatabase(entry, Mode.DELETE);
                }
                cursor.close();
                cursor = instance.getMostRecentRecord();
                long newMilliseconds = 0;
                if(cursor != null && cursor.moveToFirst()) {
                    try {
                        newMilliseconds = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(entry.getDate() + " " + entry.getTime()).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Date could not be parsed in AlarmReceiver");
                    }
                    Log.d(TAG, "Alarm set for " + entry.getDate() + " " + entry.getTime());
                    cursor.close();
                }

                AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if(newMilliseconds != 0) {
                    alarm.set(AlarmManager.RTC_WAKEUP, newMilliseconds, PendingIntent.getBroadcast(context, 1, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
                }
            }
            DBAdapter.getMainDBInstance().close();
        }
    }
}
