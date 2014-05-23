package com.automatext.crysco.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateDialogFragment extends DialogFragment {

    private NoticeDialogListener mListener;

    public DateDialogFragment() {
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
        alertDialogBuilder.setTitle("Set Date");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        alertDialogBuilder.setView(inflater.inflate(R.layout.dialog_fragment_date, null))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatePicker datePicker = (DatePicker) ((Dialog) dialogInterface).findViewById(R.id.datePicker);

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.MONTH, datePicker.getMonth());
                                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                                calendar.set(Calendar.YEAR, datePicker.getYear());

                                String date = new SimpleDateFormat("MM/dd/yyyy").format(calendar.getTime());

                                mListener.onDialogPositiveClick(DateDialogFragment.this, date, TextDetailsFragment.Field.DATE);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DateDialogFragment.this.getDialog().cancel();
                            }
                        });

        return alertDialogBuilder.create();
    }
}
