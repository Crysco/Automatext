package com.automatext.crysco.app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

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
    public void onDialogPositiveClick(DialogFragment dialog, String output, TextDetailsFragment.Field field) {
        fragText.updateField(output, field);
    }

    @Override
    public void onClearEntryDialogPositiveClick(DialogFragment dialog, boolean backPressed) {
        if(!backPressed || fragText.getMode() == NEW)
            communicator.updateEntries(fragText.getID(), null, null, null, null, null, 0, DELETE);
        finish();
    }

    @Override
    public void updateEntries(long id, String name, String number, String date, String time, String content, int frequency) {
        communicator.updateEntries(id, name, number, date, time, content, frequency, fragText.getMode());
    }

    public interface Communicator {
        public void updateEntries(long id, String name, String number, String date, String time, String content, int frequency, int mode);
    }

    public static void setCommunicator(Communicator comm) {
        communicator = comm;
    }
}
