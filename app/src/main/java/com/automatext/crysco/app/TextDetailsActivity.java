package com.automatext.crysco.app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import static com.automatext.crysco.app.GlobalConstants.*;


public class TextDetailsActivity extends FragmentActivity implements TextDetailsFragment.Communicator, TimeDialogFragment.NoticeDialogListener, DateDialogFragment.NoticeDialogListener, ContactsDialogFragment.NoticeDialogListener, ClearEntryDialogFragment.NoticeDialogListener {

    private TextDetailsFragment fragText;
    private static Communicator communicator;
    public static TextDetailsActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_details);

        instance = this;

        if(savedInstanceState == null) {
            fragText = new TextDetailsFragment();
            fragText.setArguments(getIntent().getBundleExtra(Tags.BUNDLE));
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentPlaceHolderEdit, fragText, Tags.TEXT_DETAILS_FRAGMENT).commit();
        } else
            fragText = (TextDetailsFragment) getSupportFragmentManager().findFragmentByTag(Tags.TEXT_DETAILS_FRAGMENT);

        fragText.setCommunicator(this);
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
    protected void onStop() {
        super.onStop();
        //if(!fragText.getSaved() && fragText.getMode() == NEW)
        //  communicator.updateEntries(fragText.getID(), null, null, null, null, null, DELETE);
    }

    @Override
    public void onBackPressed() {
        if(fragText.getChanged()) {
            ClearEntryDialogFragment clearEntryDialogFragment = new ClearEntryDialogFragment("This will undo any unsaved changes. Continue?", true);
            clearEntryDialogFragment.show(getSupportFragmentManager(), "ClearEntryDialogFragment");
        } else
            finish();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String output, Field field) {
        fragText.updateField(output, field);
    }

    @Override
    public void onClearEntryDialogPositiveClick(DialogFragment dialog, boolean backPressed) {
        if(!backPressed || fragText.getMode() == Mode.NEW) {
            Entry entry = new Entry();
            entry.setID(fragText.getID());
            communicator.updateEntries(entry, Mode.DELETE);
        }
        finish();
    }

    @Override
    public void updateEntries(Entry entry) {
        communicator.updateEntries(entry, fragText.getMode());
    }

    public interface Communicator {
        public void updateEntries(Entry entry, int mode);
    }

    public static void setCommunicator(Communicator comm) {
        communicator = comm;
    }
}
