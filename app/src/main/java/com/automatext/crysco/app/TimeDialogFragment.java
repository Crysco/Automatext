package com.automatext.crysco.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeDialogFragment extends DialogFragment {

    private NoticeDialogListener nListener;

    public TimeDialogFragment() {
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String output, TextDetailsFragment.Field field);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            nListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Set Time");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        alertDialogBuilder.setView(inflater.inflate(R.layout.dialog_fragment_time, null))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TimePicker timePicker = (TimePicker) ((Dialog) dialogInterface).findViewById(R.id.timePicker);

                                String meridian = timePicker.getCurrentHour() > 11 ? "PM" : "AM";

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR, timePicker.getCurrentHour());
                                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                                String time = new SimpleDateFormat("hh:mm").format(calendar.getTime());

                                nListener.onDialogPositiveClick(TimeDialogFragment.this, time + " " + meridian, TextDetailsFragment.Field.TIME);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TimeDialogFragment.this.getDialog().dismiss();
                            }
                        });

        return alertDialogBuilder.create();
    }
}
