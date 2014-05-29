package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AutoReplyDetailsFragment extends Fragment {

    private String mTitle, mStartTime, mEndTime, mContent, mDays;
    private long id;
    private int mode, mSpeaker;

    private TextView asterisk, title, startTime, endTime, content;
    private EditText contentEdit, titleEdit;
    private ImageView titleButton, startTimeButton, endTimeButton, contentButton, checkBoxSun, checkBoxMon, checkBoxTue, checkBoxWed, checkBoxThu, checkBoxFri, checkBoxSat, speaker;

    private GestureDetector gDetector;

    private Communicator communicator;

    private boolean editMode = false;
    private boolean changed = false;
    private boolean saved = false;

    public AutoReplyDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_auto_reply_details, container, false);

        if(getArguments() != null) {
            mode = getArguments().getInt(Tags.MODE);
            Reply reply = getArguments().getParcelable(Tags.REPLY);
            id = reply.getID();
            mTitle = reply.getTitle();
            mStartTime = reply.getStartTime();
            mEndTime = reply.getEndTime();
            mContent = reply.getContent();
            mDays = reply.getDays();
            mSpeaker = reply.getSilence();
        } else if(savedInstanceState != null) {
            id = savedInstanceState.getLong(Tags.ID);
            mode = savedInstanceState.getInt(Tags.MODE);
            mTitle = savedInstanceState.getString(Tags.TITLE);
            mStartTime = savedInstanceState.getString(Tags.START_TIME);
            mEndTime = savedInstanceState.getString(Tags.END_TIME);
            mContent = savedInstanceState.getString(Tags.CONTENT);
            mDays = savedInstanceState.getString(Tags.DAYS);
            mSpeaker = savedInstanceState.getInt(Tags.SILENCE);
        }

        initializeViews(v);
        registerButtonClickCallback();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Tags.TITLE, mTitle);
        outState.putString(Tags.START_TIME, mStartTime);
        outState.putString(Tags.END_TIME, mEndTime);
        outState.putString(Tags.CONTENT, mContent);
        outState.putInt(Tags.MODE, mode);
        outState.putString(Tags.DAYS, mDays);
        outState.putLong(Tags.ID, id);
        super.onSaveInstanceState(outState);
    }


    private void initializeViews(View v) {
        asterisk = (TextView) v.findViewById(R.id.textViewChanged);
        asterisk.setVisibility(View.INVISIBLE);
        title = (TextView) v.findViewById(R.id.textViewReplyTitle);
        title.setText(mTitle);
        content = (TextView) v.findViewById(R.id.textViewReplyContent);
        content.setText(mContent);
        startTime = (TextView) v.findViewById(R.id.textViewReplyStartTime);
        startTime.setText(mStartTime);
        endTime = (TextView) v.findViewById(R.id.textViewReplyEndTime);
        endTime.setText(mEndTime);

        checkBoxSun = (ImageView) v.findViewById(R.id.imageViewSun);
        checkBoxMon = (ImageView) v.findViewById(R.id.imageViewMon);
        checkBoxTue = (ImageView) v.findViewById(R.id.imageViewTue);
        checkBoxWed = (ImageView) v.findViewById(R.id.imageViewWed);
        checkBoxThu = (ImageView) v.findViewById(R.id.imageViewThu);
        checkBoxFri = (ImageView) v.findViewById(R.id.imageViewFri);
        checkBoxSat = (ImageView) v.findViewById(R.id.imageViewSat);
        titleButton = (ImageView) v.findViewById(R.id.imageViewTitle);
        startTimeButton = (ImageView) v.findViewById(R.id.imageViewStartTime);
        endTimeButton = (ImageView) v.findViewById(R.id.imageViewEndTime);
        contentButton = (ImageView) v.findViewById(R.id.imageViewReplyContent);
        speaker = (ImageView) v.findViewById(R.id.imageViewSpeaker);

        ImageView[] days = {checkBoxSun, checkBoxMon, checkBoxTue, checkBoxWed, checkBoxThu, checkBoxFri, checkBoxSat};
        for(int j = 0; j < days.length; j++) {
            if(mDays.charAt(j) == '1') {
                days[j].setImageDrawable(getResources().getDrawable(R.drawable.check_white));
            } else if (mDays.charAt(j) == '0') {
                days[j].setImageDrawable(getResources().getDrawable(R.drawable.checkbox_white));
            }
        }

        if(mSpeaker == SilenceState.NOT_SILENCED) {
            speaker.setImageDrawable(getResources().getDrawable(R.drawable.speaker_on));
        } else {
            speaker.setImageDrawable(getResources().getDrawable(R.drawable.speaker_off));
        }

        ArrayList<TextView> textViews = new ArrayList<TextView>();
        textViews.add(title);
        textViews.add(startTime);
        textViews.add(endTime);
        textViews.add(asterisk);
        textViews.add(content);
        textViews.add((TextView) v.findViewById(R.id.textViewTitleTitle));
        textViews.add((TextView) v.findViewById(R.id.textViewTitleStartTime));
        textViews.add((TextView) v.findViewById(R.id.textViewTitleEndTime));
        textViews.add((TextView) v.findViewById(R.id.textViewReplyTitleContent));
        textViews.add((TextView) v.findViewById(R.id.textViewTitleReply));
        textViews.add((TextView) v.findViewById(R.id.textViewSun));
        textViews.add((TextView) v.findViewById(R.id.textViewMon));
        textViews.add((TextView) v.findViewById(R.id.textViewTue));
        textViews.add((TextView) v.findViewById(R.id.textViewWed));
        textViews.add((TextView) v.findViewById(R.id.textViewThu));
        textViews.add((TextView) v.findViewById(R.id.textViewFri));
        textViews.add((TextView) v.findViewById(R.id.textViewSat));
        changeAllFonts(textViews);

        contentEdit = (EditText) v.findViewById(R.id.editTextReplyContent);
        titleEdit = (EditText) v.findViewById(R.id.editTextReplyTitle);

        gDetector = new GestureDetector(getActivity(), new GestureListener());

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.containerReply);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    private void changeAllFonts(ArrayList<TextView> textViews) {
        Typeface font = null;
        for(TextView textView : textViews) {
            try {
                font = CustomFont.getFont("CrayonCrumble");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "Font Not Found");
                font = Typeface.DEFAULT;
            } finally {
                textView.setTypeface(font);
            }
        }
    }

    private void registerButtonClickCallback() {

        titleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editMode) {
                    // NEED TO ADD TITLE EDITTEXT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    titleEdit.setText(mTitle);
                    titleButton.setImageDrawable(getResources().getDrawable(R.drawable.save));
                    titleEdit.setVisibility(View.VISIBLE);
                    titleEdit.requestFocus();
                    title.setVisibility(View.INVISIBLE);
                } else {
                    titleButton.setImageDrawable(getResources().getDrawable(R.drawable.pencil));
                    updateField(titleEdit.getText().toString(), Field.TITLE);
                    titleEdit.setVisibility(View.INVISIBLE);
                    title.setVisibility(View.VISIBLE);
                }
                editMode = !editMode;
            }
        });

        startTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeDialogFragment timeDialogFragment = new TimeDialogFragment(Field.START_TIME);
                timeDialogFragment.show(getFragmentManager(), "TimeDialogFragment");
            }
        });
        endTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeDialogFragment timeDialogFragment = new TimeDialogFragment(Field.END_TIME);
                timeDialogFragment.show(getFragmentManager(), "TimeDialogFragment");
            }
        });
        contentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //handles switching views for input vs. display
                if(!editMode) {
                    contentEdit.setText(mContent);
                    editMode = true;
                    contentButton.setImageDrawable(getResources().getDrawable(R.drawable.save));
                    contentEdit.setVisibility(View.VISIBLE);
                    contentEdit.requestFocus();
                    content.setVisibility(View.INVISIBLE);
                } else {
                    editMode = false;
                    contentButton.setImageDrawable(getResources().getDrawable(R.drawable.pencil));
                    updateField(contentEdit.getText().toString(), Field.CONTENT);
                    contentEdit.setVisibility(View.INVISIBLE);
                    content.setVisibility(View.VISIBLE);
                }
            }
        });
        checkBoxSun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckBoxes(0, checkBoxSun);
            }
        });
        checkBoxMon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckBoxes(1, checkBoxMon);
            }
        });
        checkBoxTue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckBoxes(2, checkBoxTue);
            }
        });
        checkBoxWed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckBoxes(3, checkBoxWed);
            }
        });
        checkBoxThu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckBoxes(4, checkBoxThu);
            }
        });
        checkBoxFri.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckBoxes(5, checkBoxFri);
            }
        });
        checkBoxSat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCheckBoxes(6, checkBoxSat);
            }
        });
        speaker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int drawable = (mSpeaker == SilenceState.NOT_SILENCED) ? R.drawable.speaker_off : R.drawable.speaker_on;
                mSpeaker = (mSpeaker == SilenceState.NOT_SILENCED) ? SilenceState.SILENCED : SilenceState.NOT_SILENCED;
                speaker.setImageDrawable(getResources().getDrawable(drawable));
            }
        });
    }

    private void updateCheckBoxes(int index, ImageView image) {
        if(mDays.charAt(index) == '0') {
            if(index == 6) {
                mDays = mDays.substring(0, 6) + '1';
            } else {
                mDays = mDays.substring(0, index) + '1' + mDays.substring(index + 1);
            }
            image.setImageDrawable(getResources().getDrawable(R.drawable.check_white));
        } else {
            if(index == 6) {
                mDays = mDays.substring(0, index) + '0';
            } else {
                mDays = mDays.substring(0, index) + '0' + mDays.substring(index + 1);
            }
            image.setImageDrawable(getResources().getDrawable(R.drawable.checkbox_white));
        }
    }

    public boolean saveEntry() {
        if(checkFields()) {
            saved = true;
            Reply reply = new Reply();
            reply.setTitle(mTitle);
            reply.setId(id);
            reply.setDays(mDays);
            reply.setStartTime(mStartTime);
            reply.setEndTime(mEndTime);
            reply.setContent(mContent);
            reply.setSilence(mSpeaker);
            communicator.updateReplies(reply);
            Toast.makeText(getActivity(), "Entry Saved", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return true;
        }
        return false;
    }

    public void updateField(String output, Field field) {
        switch (field) {
            case TITLE:
                title.setText(output);
                mTitle = output;
                break;
            case START_TIME:
                try {
                    startTime.setText(new SimpleDateFormat("h:mm a").format(new SimpleDateFormat("HH:mm").parse(output)));
                } catch (ParseException e) {
                    e.printStackTrace();
                    startTime.setText(output);
                }
                mStartTime = output;
                break;
            case END_TIME:
                try {
                    endTime.setText(new SimpleDateFormat("h:mm a").format(new SimpleDateFormat("HH:mm").parse(output)));
                } catch (ParseException e) {
                    e.printStackTrace();
                    endTime.setText(output);
                }
                mEndTime = output;
                break;
            case CONTENT:
                content.setText(output);
                mContent = output;
                break;
            default:
                //do nothing
                break;
        }
        changed = true;
        asterisk.setVisibility(View.VISIBLE);
    }

    private boolean checkFields() {
        return !mTitle.isEmpty() && !mStartTime.isEmpty() && !mEndTime.isEmpty() && !mContent.isEmpty();
    }

    public int getMode() {
        return this.mode;
    }

    public long getID() {
        return this.id;
    }

    public boolean getChanged() {
        return changed;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private final int SWIPE_MIN_DISTANCE = (int)(0.3 * SCREEN_WIDTH);
        private final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY ) {
                //bottom to top
                ClearEntryDialogFragment clearEntryDialogFragment = new ClearEntryDialogFragment("This will delete the entry. Continue?", false);
                clearEntryDialogFragment.show(getFragmentManager(), "ClearEntryDialogFragment");
            } else if(e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                //top to bottom
                if(!saveEntry())
                    Toast.makeText(getActivity(), "Did you leave any fields blank?", Toast.LENGTH_SHORT).show();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public interface Communicator {
        public void updateReplies(Reply reply);
    }

    public void setCommunicator(Communicator comm) {
        communicator = comm;
    }
}
