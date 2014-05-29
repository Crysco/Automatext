package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TextsListFragment extends Fragment {

    private Communicator communicator;
    private ArrayAdapter<Entry> adapter;
    private ArrayList<Entry> entries;
    private GridView entryList;
    private TextView add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_texts_list, container, false);

        entries = new ArrayList<Entry>();

        if(getArguments() != null)
            entries = getArguments().getParcelableArrayList(Tags.ENTRIES);
        else if(savedInstanceState != null)
            entries = savedInstanceState.getParcelableArrayList(Tags.ENTRIES);

        initializeViews(v);
        registerListItemClickCallback();
        registerAddEntryClickCallback();

        return v;
    }

    @Override
    public void onStart() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Tags.ENTRIES, entries);
    }


    public void checkForEmptyEntries() {
        DBAdapter.getMainDBInstance().open();
        Cursor cursor = DatabaseEntries.getInstance().getAllRecords();
        if(cursor != null && cursor.moveToFirst()) {
            do {
                if(cursor.getString(cursor.getColumnIndex(DatabaseEntries.KEY_CONTACT)).isEmpty()) {
                    Log.d(TAG, "Clearing empty reply");
                    Entry entry = new Entry();
                    entry.setID(cursor.getLong(cursor.getColumnIndex(DatabaseReplies.KEY_ROWID)));
                    communicator.updateEntries(entry, Mode.DELETE);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        DBAdapter.getMainDBInstance().close();
    }

    private void initializeViews(View v) {
        entryList = (GridView) v.findViewById(R.id.gridViewEntries);
        add = (TextView) v.findViewById(R.id.textViewAdd);
        Typeface font = null;
        try {
            font = CustomFont.getFont("CrayonCrumble");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "Font could not be found");
            font = Typeface.DEFAULT;
        } finally {
            add.setTypeface(font);
        }
    }

    private void registerListItemClickCallback() {
        adapter = new CustomAdapter();
        entryList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Id is " + Long.toString((entries.get(i).getID())));
                communicator.respond(entries.get(i).getID(), Mode.UPDATE);
            }
        });
    }

    private void registerAddEntryClickCallback() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter.getMainDBInstance().open();
                long newId = DatabaseEntries.getInstance().insertRecord("", "", new SimpleDateFormat("yyyy-MM-dd").format(new Date()), new SimpleDateFormat("HH:mm").format(new Date()), "", 0);
                Log.d(TAG, "New id is " + Long.toString(newId));
                DBAdapter.getMainDBInstance().close();
                communicator.respond(newId, Mode.NEW);
            }
        });
    }

    public void updateList(Entry entry, int mode) {
        if(mode == Mode.NEW)
            entries.add(entry);
        else {
            int index = -1;
            for (int i = 0; i < entries.size(); i++) {
                if(entries.get(i).getID() == entry.getID()) {
                    index = i;
                    break;
                }
            }
            if(index == -1)
                Log.d(TAG, "Could not locate entry.");
            else {
                if(mode == Mode.UPDATE) {
                    entries.set(index, entry);
                } else if (mode == Mode.DELETE)
                    entries.remove(index);
            }
        }

        refreshList();
    }

    private void refreshList() {
        adapter.notifyDataSetChanged();
        entryList.invalidateViews();
    }


    private class CustomAdapter extends ArrayAdapter<Entry> {

        public CustomAdapter() {
            super(getActivity(), R.layout.entry, entries);
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            final View v = getActivity().getLayoutInflater().inflate(R.layout.entry, parent, false);

            TextView contact = (TextView) v.findViewById(R.id.textViewContactList);
            contact.setText(entries.get(i).getName());
            TextView date = (TextView) v.findViewById(R.id.textViewDateList);
            date.setText(entries.get(i).getDate());
            TextView time = (TextView) v.findViewById(R.id.textViewTimeList);
            time.setText(entries.get(i).getTime());

            String timeString = entries.get(i).getTime();
            String dateString = entries.get(i).getDate();
            try {
                timeString = new SimpleDateFormat("h:mm a").format(new SimpleDateFormat("HH:mm").parse(timeString));
                dateString = new SimpleDateFormat("MM/dd/yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                time.setText(timeString);
                date.setText(dateString);
            }

            Typeface font = null;
            try {
                font = CustomFont.getFont("CrayonCrumble");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "Font Not Found");
                font = Typeface.DEFAULT;
            } finally {
                contact.setTypeface(font);
                date.setTypeface(font);
                time.setTypeface(font);
            }
            return v;
        }
    }

    public interface Communicator {
        public void respond(long index, int a);
        public void updateEntries(Entry entry, int mode);
    }

    public void setCommunicator(Communicator comm) {
        communicator = comm;
    }
}
