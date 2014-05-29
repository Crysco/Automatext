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

public class AutoReplyListActivity extends FragmentActivity implements AutoReplyListFragment.Communicator, AutoReplyDetailsActivity.Communicator{

    private AutoReplyListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_reply_list);
        if(savedInstanceState == null) {
            fragment = initializeFragment();
        } else {
            fragment = (AutoReplyListFragment) getSupportFragmentManager().findFragmentByTag(Tags.REPLIES_LIST_FRAGMENT);
        }
        AutoReplyDetailsActivity.setCommunicator(this);
        fragment.setCommunicator(this);
        setAlarmService();
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
        fragment.checkForEmptyReplies();
    }

    private void setAlarmService() {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        DBAdapter.getMainDBInstance().open();
        Cursor cursor = DatabaseReplies.getInstance().getMostRecentRecord();
        if(cursor != null && cursor.moveToFirst()) {
            Reply reply = DatabaseReplies.getInstance().convertRowToObject(cursor.getLong(cursor.getColumnIndex(DatabaseReplies.KEY_ROWID)));
            long startMilliseconds = 0;
            long endMilliseconds = 0;
            try {
                startMilliseconds = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(reply.getStartTime()).getTime();
                endMilliseconds = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(reply.getEndTime()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                if(cursor.getInt(reply.getActive()) == ActiveState.ACTIVE) {
                    alarm.set(AlarmManager.RTC_WAKEUP, endMilliseconds, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
                    Log.d(TAG, "Alarm set for " + reply.getStartTime() + ", which is an end time.");
                } else {
                    alarm.set(AlarmManager.RTC_WAKEUP, startMilliseconds, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
                    Log.d(TAG, "Alarm set for " + reply.getEndTime() + ", which is a start time.");
                }
            }
            cursor.close();
        }
        DBAdapter.getMainDBInstance().close();
    }

    private AutoReplyListFragment initializeFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Tags.REPLIES, populateList());
        fragment = new AutoReplyListFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentPlaceHolderReply, fragment, Tags.REPLIES_LIST_FRAGMENT).commit();
        return fragment;
    }

    private ArrayList<Reply> populateList() {
        ArrayList<Reply> list = new ArrayList<Reply>();
        DBAdapter.getMainDBInstance().open();
        if(DatabaseReplies.getInstance().getRecordsCount() != 0) {
            Cursor cursor = DatabaseReplies.getInstance().getAllRecords();
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Reply reply = DatabaseReplies.getInstance().convertRowToObject(cursor.getLong(cursor.getColumnIndex(DatabaseReplies.KEY_ROWID)));
                    list.add(reply);
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
        Cursor cursor = DatabaseReplies.getInstance().getRecord(id);
        if(cursor != null && cursor.moveToFirst()) {
            bundle.putParcelable(Tags.REPLY, DatabaseReplies.getInstance().convertRowToObject(id));
            bundle.putInt(Tags.MODE, mode);
            cursor.close();
        }
        DBAdapter.getMainDBInstance().close();
        Intent intent = new Intent(this, AutoReplyDetailsActivity.class);
        intent.putExtra(Tags.BUNDLE, bundle);
        startActivity(intent);
    }

    @Override
    public void updateReplies(Reply reply, int mode) {
        DBAdapter.getMainDBInstance().open();
        reply.setActive(DatabaseReplies.getInstance().determineActiveState(reply.getID()));
        DatabaseReplies.getInstance().updateRepliesDatabase(reply, mode);
        fragment.updateList(reply, mode);
        DBAdapter.getMainDBInstance().close();
        setAlarmService();
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DBAdapter.getMainDBInstance().initiateDatabase(context);
            DBAdapter.getMainDBInstance().open();
            final DatabaseReplies instance = DatabaseReplies.getInstance();
            Cursor cursor = instance.getMostRecentRecord();
            if(cursor != null && cursor.moveToFirst()) {
                Reply reply = instance.convertRowToObject(cursor.getLong(cursor.getColumnIndex(DatabaseReplies.KEY_ROWID)));
                boolean active = (reply.getActive() == ActiveState.ACTIVE);
                long newMilliseconds = 0;
                if(!active) {
                    instance.updateRecordActiveCheck(reply.getID(), ActiveState.ACTIVE);
                    try {
                        newMilliseconds = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(reply.getEndTime()).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Alarm set for " + reply.getStartTime() + ", which is an end time.");
                    cursor.close();
                } else {
                    instance.updateRecordActiveCheck(reply.getID(), ActiveState.NOT_ACTIVE);
                    if(!instance.updateRecordTime(reply.getID())) {
                        instance.updateRepliesDatabase(reply, Mode.DELETE);
                    }
                    cursor.close();
                    cursor = instance.getMostRecentRecord();
                    reply = instance.convertRowToObject(cursor.getLong(cursor.getColumnIndex(DatabaseReplies.KEY_ROWID)));
                    if(cursor != null && cursor.moveToFirst()) {
                        try {
                            newMilliseconds = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(reply.getStartTime()).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "Alarm set for " + reply.getStartTime() + ", which is a start time.");
                        cursor.close();
                    }
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
