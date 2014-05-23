package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

public class TextsListActivity extends FragmentActivity implements TextsListFragment.Communicator, TextDetailsActivity.Communicator {

    public static TextsListActivity instance;

    private TextsListFragment fragMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texts_list);

        instance = this;

        TextDetailsActivity.setCommunicator(this);

        GlobalConstants.setContextDependencies(this);
        ContactListGenerator.getInstance().initializeContacts(this);
        DBAdapter.getInstance().initiateDatabase(this);

        Intent intent = new Intent(this, MessageSender.class);
        startService(intent);

        if(savedInstanceState == null)
            initializeFragment();
        else
            fragMain = (TextsListFragment) getSupportFragmentManager().findFragmentByTag(Tags.TEXTS_LIST_FRAGMENT);

        fragMain.setCommunicator(this);
    }

    private void initializeFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Tags.ENTRIES, populateList());
        fragMain = new TextsListFragment();
        fragMain.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentPlaceHolderMain, fragMain, Tags.TEXTS_LIST_FRAGMENT).commit();
    }

    private ArrayList<Entry> populateList() {
        ArrayList<Entry> list = new ArrayList<Entry>();
        DBAdapter.getInstance().open();
        if(DBAdapter.getInstance().getCount() != 0) {
            Cursor cursor = DBAdapter.getInstance().getAllEntries();
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Entry entry = new Entry();
                    entry.setContact(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CONTACT)));
                    entry.setDate(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_DATE)));
                    entry.setTime(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_TIME)));
                    entry.setContent(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CONTENT)));
                    entry.setFrequency(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_FREQUENCY)));
                    entry.setId(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
                    list.add(entry);
                } while(cursor.moveToNext());
                cursor.close();
            }
        }
        DBAdapter.getInstance().close();
        return list;
    }

    @Override
    public void respond(long id, int mode) {
            Bundle bundle = new Bundle();
            if(mode == Mode.UPDATE) {
                DBAdapter.getInstance().open();
                Cursor cursor = DBAdapter.getInstance().getEntry(id);
                if(cursor != null && cursor.moveToFirst()) {
                    bundle.putString(Tags.CONTACT, cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CONTACT)) + "#" + cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_NUMBER)));
                    bundle.putString(Tags.DATE, cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_DATE)));
                    bundle.putString(Tags.TIME, cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_TIME)));
                    bundle.putString(Tags.CONTENT, cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CONTENT)));
                    bundle.putInt(Tags.FREQUENCY, cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_FREQUENCY)));
                    cursor.close();
                }
                DBAdapter.getInstance().close();
            }
            bundle.putLong(Tags.ID, id);
            bundle.putInt(Tags.MODE, mode);


                Intent intent = new Intent(this, TextDetailsActivity.class);
                intent.putExtra(Tags.BUNDLE, bundle);
                startActivity(intent);
    }

    @Override
    public void updateEntries(long id, String name, String number, String date, String time, String content, int frequency, int mode) {

        DBAdapter.getInstance().open();
        DBAdapter.getInstance().updateDatabase(id, name, number, date, time, content, frequency, mode);
        DBAdapter.getInstance().close();
        fragMain.updateList(id, name, date, time, content, frequency, mode);
    }
}
