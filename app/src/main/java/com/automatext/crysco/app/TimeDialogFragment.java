package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

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
    private Field field;

    public TimeDialogFragment(Field field) {
        this.field = field;
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String output, Field field);
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

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                                String time = new SimpleDateFormat("HH:mm").format(calendar.getTime());

                                nListener.onDialogPositiveClick(TimeDialogFragment.this, time, field);
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
