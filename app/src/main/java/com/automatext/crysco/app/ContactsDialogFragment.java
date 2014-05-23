package com.automatext.crysco.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ClassCastException;import java.lang.Override;import java.lang.String;import java.util.ArrayList;

/**
 * Created by Crys on 5/12/14.
 */
public class ContactsDialogFragment extends DialogFragment {

    private Activity activity;
    private ArrayList<ContactListGenerator.Contact> contacts;
    private ArrayAdapter<ContactListGenerator.Contact> adapter;
    private NoticeDialogListener mListener;

    public ContactsDialogFragment(Activity a, ArrayList<ContactListGenerator.Contact> c) {
        activity = a;
        contacts = c;
        adapter = new ContactsAdapter();
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
        alertDialogBuilder.setTitle("Chose Contact");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_fragment_contacts, null);
        alertDialogBuilder.setView(v);

        final ListView list = (ListView) v.findViewById(R.id.listViewContacts);
        list.setAdapter(adapter);

        final TextView name = (TextView) v.findViewById(R.id.textViewContactName);
        final TextView number = (TextView) v.findViewById(R.id.textViewContactNumber);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    ContactsDialogFragment.this.getDialog().dismiss();
                    NewContactDialogFragment newContactDialogFragment = new NewContactDialogFragment();
                    newContactDialogFragment.show(getFragmentManager(), "NewContactDialogFragment");
                } else {
                    name.setText(contacts.get(i).getName());
                    number.setText(contacts.get(i).getNumber());
                    number.setVisibility(View.VISIBLE);
                }
            }
        });

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String output = name.getText().toString() + " #" + number.getText().toString();
                        if(!output.equals("")) {
                            //mListener.onDialogPositiveClick(ContactsDialogFragment.this, output, TextDetailsFragment.Field.CONTACT);
                            mListener.onDialogPositiveClick(ContactsDialogFragment.this, output, TextDetailsFragment.Field.CONTACT);
                        }
                        else {
                            //MAKE TOAST
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContactsDialogFragment.this.getDialog().dismiss();
                            }
                        });

        return alertDialogBuilder.create();
    }

    public class NewContactDialogFragment extends DialogFragment {

        public NewContactDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Enter name and number");

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View v = inflater.inflate(R.layout.dialog_fragment_new_contact, null);
            alertDialogBuilder.setView(v);

            final EditText name = (EditText) v.findViewById(R.id.editTextNewContactName);
            final EditText number = (EditText) v.findViewById(R.id.editTextNewContactNumber);

            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String theName = name.getText().toString();
                            String theNumber = number.getText().toString();
                            if(!theName.equals("") && !theNumber.equals("")) {
                                mListener.onDialogPositiveClick(ContactsDialogFragment.this, theName + " #" + theNumber, TextDetailsFragment.Field.CONTACT);
                            } else {
                                //MAKE TOAST!!!!!!!!!
                            }
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NewContactDialogFragment.this.getDialog().dismiss();
                                    ContactsDialogFragment contactsDialogFragment = new ContactsDialogFragment(activity, contacts);
                                    contactsDialogFragment.show(getFragmentManager(), "ContactsDialogFragment");
                                }
                            });

            return alertDialogBuilder.create();
        }
    }

    private class ContactsAdapter extends ArrayAdapter<ContactListGenerator.Contact> {

        public ContactsAdapter() {
            super(activity, R.layout.contact, contacts);
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            View v = view;
            if(v == null)
                v = getActivity().getLayoutInflater().inflate(R.layout.contact, parent, false);

            TextView name = (TextView) v.findViewById(R.id.textViewContactName);
            TextView number = (TextView) v.findViewById(R.id.textViewContactNumber);

            name.setText(contacts.get(i).getName());
            number.setText(contacts.get(i).getNumber());

            return v;
        }

    }

}
