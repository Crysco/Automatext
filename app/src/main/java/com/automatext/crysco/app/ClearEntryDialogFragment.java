package com.automatext.crysco.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
/**
 * Created by Crys on 5/14/14.
 */
public class ClearEntryDialogFragment extends DialogFragment {
    private NoticeDialogListener mListener;
    private String message;
    private boolean backPressed;

    public ClearEntryDialogFragment(String message, boolean backPressed) {
        this.message = message;
        this.backPressed = backPressed;
    }

    public interface NoticeDialogListener {
        public void onClearEntryDialogPositiveClick(DialogFragment dialog, boolean backPressed);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(message);

        alertDialogBuilder
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mListener.onClearEntryDialogPositiveClick(ClearEntryDialogFragment.this, backPressed);
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ClearEntryDialogFragment.this.getDialog().cancel();
                            }
                        });

        return alertDialogBuilder.create();
    }
}
