package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.automatext.crysco.app.GlobalConstants.TAG;

/**
 * Created by Crys on 5/23/14.
 */
public class AutoReplyListFragment extends Fragment {

    private Communicator communicator;
    private ArrayAdapter<Reply> adapter;
    private ArrayList<Reply> replies;
    private ListView replyList;
    private TextView add;
    private ImageView switchSlider, switchOutline;
    private RelativeLayout layout;
    private boolean touched;
    int xDelta = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_auto_reply_list, container, false);

        replies = new ArrayList<Reply>();

        if(getArguments() != null)
            replies = getArguments().getParcelableArrayList(GlobalConstants.Tags.REPLIES);
        else if(savedInstanceState != null)
            replies = savedInstanceState.getParcelableArrayList(GlobalConstants.Tags.REPLIES);

        initializeViews(v);
        registerListItemClickCallback();
        registerAddEntryClickCallback();
        registerReplySwitchClickCallback();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setReplySwitchState(prefs.getBoolean(SWITCH_STATE, SwitchState.OFF));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(GlobalConstants.Tags.REPLIES, replies);
    }


    public void checkForEmptyReplies() {
        DBAdapter.getMainDBInstance().open();
        Cursor cursor = DatabaseReplies.getInstance().getAllRecords();
        if(cursor != null && cursor.moveToFirst()) {
            do {
                if(cursor.getString(cursor.getColumnIndex(DatabaseReplies.KEY_TITLE)).isEmpty()) {
                    Log.d(TAG, "Clearing empty reply");
                    Reply reply = new Reply();
                    reply.setId(cursor.getLong(cursor.getColumnIndex(DatabaseReplies.KEY_ROWID)));
                    communicator.updateReplies(reply, Mode.DELETE);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        DBAdapter.getMainDBInstance().close();
    }

    private void initializeViews(View v) {
        replyList = (ListView) v.findViewById(R.id.listViewReplies);
        switchSlider = (ImageView) v.findViewById(R.id.imageViewSlider);
        switchOutline = (ImageView) v.findViewById(R.id.imageViewSwitchOutline);
        layout = (RelativeLayout) v.findViewById(R.id.relativeLayoutSwitch);
        add = (TextView) v.findViewById(R.id.textViewAddReply);
        TextView on = (TextView) v.findViewById(R.id.textViewOn);
        TextView off = (TextView) v.findViewById(R.id.textViewOff);
        Typeface font = null;
        try {
            font = CustomFont.getFont("CrayonCrumble");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "Font could not be found");
            font = Typeface.DEFAULT;
        } finally {
            add.setTypeface(font);
            on.setTypeface(font);
            off.setTypeface(font);
        }
    }

    private void setReplySwitchState(boolean switchState) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) switchSlider.getLayoutParams();
        if(switchState) {
            params.leftMargin = switchOutline.getWidth() - switchSlider.getWidth();
        } else {
            params.leftMargin = 0;
        }
        switchSlider.setLayoutParams(params);

        Log.d(TAG, Boolean.toString(switchState));
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SWITCH_STATE, switchState);
        editor.commit();
    }

    private void registerListItemClickCallback() {
        adapter = new CustomAdapter();
        replyList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        replyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                communicator.respond(replies.get(i).getID(), Mode.UPDATE);
            }
        });
    }

    private void registerAddEntryClickCallback() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter.getMainDBInstance().open();
                long newId = DatabaseReplies.getInstance().insertRecord("", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), "", "0000000", SilenceState.NOT_SILENCED);
                DBAdapter.getMainDBInstance().close();
                communicator.respond(newId, GlobalConstants.Mode.NEW);
            }
        });
    }

    private void registerReplySwitchClickCallback() {
        switchSlider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int x = (int) motionEvent.getRawX();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touched = true;
                        xDelta = x - layoutParams.leftMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(layoutParams.leftMargin < 0) {
                            touched = false;
                            layoutParams.leftMargin = 0;
                        } else if (layoutParams.leftMargin > switchOutline.getWidth() - view.getWidth()) {
                            touched = false;
                            layoutParams.leftMargin = switchOutline.getWidth() - view.getWidth();
                        } else if (touched) {
                            layoutParams.leftMargin = x - xDelta;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (layoutParams.leftMargin < (int)(0.5f * (switchOutline.getWidth() - view.getWidth()))) {
                            setReplySwitchState(SwitchState.OFF);
                        } else {
                            setReplySwitchState(SwitchState.ON);
                        }
                        break;
                }
                view.setLayoutParams(layoutParams);
                layout.invalidate();
                return false;
            }
        });

    }

    public void updateList(Reply r, int mode) {
        if(mode == Mode.NEW) {
            replies.add(r);
        } else {
            // Searches for the id within the ArrayList
            int index = -1;
            for (int i = 0; i < replies.size(); i++) {
                if(replies.get(i).getID() == r.getID()) {
                    index = i;
                    break;
                }
            }
            if(index == -1)
                Log.d(TAG, "Could not locate entry.");
            else {
                if(mode == Mode.UPDATE) {
                    replies.set(index, r);
                } else if (mode == Mode.DELETE)
                    replies.remove(index);
            }
        }
        refreshList();
    }

    private void refreshList() {
        adapter.notifyDataSetChanged();
        replyList.invalidateViews();
    }


    private class CustomAdapter extends ArrayAdapter<Reply> {

        public CustomAdapter() {
            super(getActivity(), R.layout.reply, replies);
        }

        @Override
        public View getView(final int i, View view, ViewGroup parent) {
            final View v = getActivity().getLayoutInflater().inflate(R.layout.reply, parent, false);

            if(v !=null) {
                TextView title = (TextView) v.findViewById(R.id.textViewReplyTitleList);
                title.setText(replies.get(i).getTitle());
                TextView startTime = (TextView) v.findViewById(R.id.textViewReplyStartTimeList);
                TextView endTime = (TextView) v.findViewById(R.id.textViewReplyEndTimeList);
                TextView active = (TextView) v.findViewById(R.id.textViewActive);
                ImageView silence = (ImageView) v.findViewById(R.id.imageViewSilence);
                TextView sun = (TextView) v.findViewById(R.id.textViewReplySun);
                TextView mon = (TextView) v.findViewById(R.id.textViewReplyMon);
                TextView tue = (TextView) v.findViewById(R.id.textViewReplyTue);
                TextView wed = (TextView) v.findViewById(R.id.textViewReplyWed);
                TextView thu = (TextView) v.findViewById(R.id.textViewReplyThu);
                TextView fri = (TextView) v.findViewById(R.id.textViewReplyFri);
                TextView sat = (TextView) v.findViewById(R.id.textViewReplySat);
                TextView[] days = {sun, mon, tue, wed, thu, fri, sat};

                String startTimeString = replies.get(i).getStartTime();
                String endTimeString = replies.get(i).getEndTime();
                try {
                    startTimeString = new SimpleDateFormat("h:mm a").format(new SimpleDateFormat("HH:mm").parse(startTimeString));
                    endTimeString = new SimpleDateFormat("h:mm a").format(new SimpleDateFormat("HH:mm").parse(endTimeString));
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    startTime.setText("(" + startTimeString + "-");
                    endTime.setText(endTimeString + ")");
                }

                int visibility = (replies.get(i).getActive() == ActiveState.ACTIVE) ? View.VISIBLE : View.INVISIBLE;
                active.setVisibility(visibility);

                int drawable = (replies.get(i).getSilence() == SilenceState.NOT_SILENCED) ? R.drawable.speaker_on : R.drawable.speaker_off;
                silence.setImageDrawable(getResources().getDrawable(drawable));

                Typeface font;
                try {
                    font = CustomFont.getFont("CrayonCrumble");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Font Not Found");
                    font = Typeface.DEFAULT;
                }

                title.setTypeface(font);
                startTime.setTypeface(font);
                endTime.setTypeface(font);
                active.setTypeface(font);

                String theDays = replies.get(i).getDays();
                for(int j = 0; j < days.length; j++) {
                    int color = (theDays.charAt(j) == '1') ? Color.parseColor("#FFFFFF") : Color.parseColor("#1AFFFFFF");
                    days[j].setTextColor(color);
                    days[j].setTypeface(font);
                }
            }
            return v;
        }
    }

    public interface Communicator {
        public void respond(long index, int a);
        public void updateReplies(Reply reply, int mode);
    }

    public void setCommunicator(Communicator comm) {
        communicator = comm;
    }

}
