package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

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
    public void onResume() {
        super.onResume();
        checkForEmptyEntries();
        refreshList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Tags.ENTRIES, entries);
    }


    private void checkForEmptyEntries() {
        for(Entry entry : entries) {
            if(entry.getContact().contains("(empty)"))
                communicator.updateEntries(entry.getId(), null, null, null, null, null, 0, Mode.DELETE);
        }
    }

    private void initializeViews(View v) {
        entryList = (GridView) v.findViewById(R.id.gridViewEntries);
        add = (TextView) v.findViewById(R.id.textViewAdd);
        add.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/BeeMarkerInk.ttf"));
    }

    private void registerListItemClickCallback() {
        adapter = new CustomAdapter();
        entryList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                communicator.respond(entries.get(i).getId(), Mode.UPDATE);
            }
        });
    }

    private void registerAddEntryClickCallback() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter.getInstance().open();
                long newId = DBAdapter.getInstance().insertEntry("(empty)", "", "", "", "", 0);
                DBAdapter.getInstance().close();
                communicator.respond(newId, Mode.NEW);
            }
        });
    }

    public void updateList(long id, String name, String date, String time, String content, int frequency, int mode) {
        if(mode == Mode.NEW)
            entries.add(new Entry(name, date, time, content, frequency, id));
        else {
            int index = -1;
            for (int i = 0; i < entries.size(); i++) {
                if(entries.get(i).getId() == id) {
                    index = i;
                    break;
                }
            }
            if(index == -1)
                Log.d(TAG, "Could not locate entry.");
            else {
                if(mode == Mode.UPDATE) {
                    entries.get(index).setContact(name);
                    entries.get(index).setDate(date);
                    entries.get(index).setTime(time);
                    entries.get(index).setContent(content);
                    entries.get(index).setFrequency(frequency);
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
            contact.setText(entries.get(i).getContact());
            TextView date = (TextView) v.findViewById(R.id.textViewDateList);
            date.setText(entries.get(i).getDate());
            TextView time = (TextView) v.findViewById(R.id.textViewTimeList);
            time.setText(entries.get(i).getTime());

            RelativeLayout entryLayout = (RelativeLayout) v.findViewById(R.id.entryLayout);
            Bitmap temp = null, tempResized;
            int frequency = entries.get(i).getFrequency();
            switch(frequency) {
                case Frequency.ONCE:
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.note_yellow);
                    break;
                case Frequency.DAILY:
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.note_green);
                    break;
                case Frequency.WEEKLY:
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.note_pink);
                    break;
                case Frequency.MONTHLY:
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.note_blue);
                    break;
            }
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                tempResized = Bitmap.createScaledBitmap(temp, (int)(0.2f * SCREEN_HEIGHT), (int)(0.2f * SCREEN_HEIGHT), false);
            else
                tempResized = Bitmap.createScaledBitmap(temp, (int)(0.2f * SCREEN_WIDTH), (int)(0.2f * SCREEN_WIDTH), false);
            entryLayout.setBackground(new BitmapDrawable(getResources(), tempResized));

            contact.setTypeface(CUSTOM_FONT);
            date.setTypeface(CUSTOM_FONT);
            time.setTypeface(CUSTOM_FONT);

            return v;
        }
    }

    public interface Communicator {
        public void respond(long index, int a);
        public void updateEntries(long id, String name, String number, String date, String time, String content, int frequency, int mode);
    }

    public void setCommunicator(Communicator comm) {
        communicator = comm;
    }
}
