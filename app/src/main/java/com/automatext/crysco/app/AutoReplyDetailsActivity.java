package com.automatext.crysco.app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import static com.automatext.crysco.app.GlobalConstants.*;


public class AutoReplyDetailsActivity extends FragmentActivity implements AutoReplyDetailsFragment.Communicator, TimeDialogFragment.NoticeDialogListener, ClearEntryDialogFragment.NoticeDialogListener {

    private AutoReplyDetailsFragment fragment;
    private static Communicator communicator;
    public static AutoReplyDetailsActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_reply_details);

        instance = this;

        if(savedInstanceState == null) {
            fragment = new AutoReplyDetailsFragment();
            fragment.setArguments(getIntent().getBundleExtra(Tags.BUNDLE));
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentPlaceHolderReplyDetails, fragment, Tags.REPLY_DETAILS_FRAGMENT).commit();
        } else
            fragment = (AutoReplyDetailsFragment) getSupportFragmentManager().findFragmentByTag(Tags.REPLY_DETAILS_FRAGMENT);

        fragment.setCommunicator(this);
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
        if(fragment.getChanged()) {
            ClearEntryDialogFragment clearEntryDialogFragment = new ClearEntryDialogFragment("This will undo any unsaved changes. Continue?", true);
            clearEntryDialogFragment.show(getSupportFragmentManager(), "ClearEntryDialogFragment");
        } else {
            finish();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String output, Field field) {
        fragment.updateField(output, field);
    }

    @Override
    public void onClearEntryDialogPositiveClick(DialogFragment dialog, boolean backPressed) {
        if(!backPressed || fragment.getMode() == Mode.NEW) {
            Reply reply = new Reply();
            reply.setId(fragment.getID());
            communicator.updateReplies(reply, Mode.DELETE);
        }
        finish();
    }

    @Override
    public void updateReplies(Reply reply) {
        communicator.updateReplies(reply, fragment.getMode());
    }

    public interface Communicator {
        public void updateReplies(Reply reply, int mode);
    }

    public static void setCommunicator(Communicator comm) {
        communicator = comm;
    }
}

