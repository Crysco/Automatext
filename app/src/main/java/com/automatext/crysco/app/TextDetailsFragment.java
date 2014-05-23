package com.automatext.crysco.app;
import static com.automatext.crysco.app.GlobalConstants.*;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TextDetailsFragment extends Fragment {

    private String mContact, mDate, mTime, mContent;
    private long id;
    private int mode, mFrequency;

    private TextView asterisk, contact, date, time, content;
    private EditText contentEdit;
    private ImageView contactButton, dateButton, timeButton, contentButton, checkBoxOnce, checkBoxDaily, checkBoxWeekly, checkBoxMonthly;
    private ViewGroup layout;

    private GestureDetector gDetector;

    private Communicator communicator;

    public static enum Field {
        NULL,
        CONTACT,
        DATE,
        TIME,
        CONTENT
    }

    private boolean editMode = false;
    private boolean changed = false;
    private boolean saved = false;

    private Field field;

    public TextDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_text_details, container, false);

        if(getArguments() != null) {
            mode = getArguments().getInt(Tags.MODE);
            id = getArguments().getLong(Tags.ID);
            if(mode == UPDATE) {
                mContact = getArguments().getString(Tags.CONTACT);
                mDate = getArguments().getString(Tags.DATE);
                mTime = getArguments().getString(Tags.TIME);
                mContent = getArguments().getString(Tags.CONTENT);
                mFrequency = getArguments().getInt(Tags.FREQUENCY);
            }
        } else if(savedInstanceState != null) {
            mContact = savedInstanceState.getString(Tags.CONTACT);
            mContent = savedInstanceState.getString(Tags.CONTENT);
            mDate = savedInstanceState.getString(Tags.DATE);
            mTime = savedInstanceState.getString(Tags.TIME);
            mFrequency = savedInstanceState.getInt(Tags.FREQUENCY);
            id = savedInstanceState.getLong(Tags.ID);
            mode = savedInstanceState.getInt(Tags.MODE);
        }

        initializeViews(v);
        registerButtonClickCallback();

        // DELETE THIS!!!!!!!!!!!!!!!
        initializeScreenRotateButton(v);

        return v;
    }

    private void initializeScreenRotateButton(View v) {
        Button screenChange = (Button) v.findViewById(R.id.buttonScreenChange);
        screenChange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Tags.CONTACT, mContact);
        outState.putString(Tags.DATE, mDate);
        outState.putString(Tags.TIME, mTime);
        outState.putString(Tags.CONTENT, mContent);
        outState.putInt(Tags.MODE, mode);
        outState.putInt(Tags.FREQUENCY, mFrequency);
        outState.putLong(Tags.ID, id);
        super.onSaveInstanceState(outState);
    }


    private void initializeViews(View v) {
        asterisk = (TextView) v.findViewById(R.id.textViewChanged);
        asterisk.setVisibility(View.INVISIBLE);
        contact = (TextView) v.findViewById(R.id.textViewContact);
        contact.setText(mContact);
        content = (TextView) v.findViewById(R.id.textViewContent);
        content.setText(mContent);
        date = (TextView) v.findViewById(R.id.textViewDate);
        date.setText(mDate);
        time = (TextView) v.findViewById(R.id.textViewTime);
        time.setText(mTime);

        checkBoxDaily = (ImageView) v.findViewById(R.id.imageViewDaily);
        checkBoxOnce = (ImageView) v.findViewById(R.id.imageViewOnce);
        checkBoxWeekly = (ImageView) v.findViewById(R.id.imageViewWeekly);
        checkBoxMonthly = (ImageView) v.findViewById(R.id.imageViewMonthly);

        layout = (ViewGroup) v.findViewById(R.id.relativeLayoutNote);
        updateCheckBoxes(false);

        ArrayList<TextView> textViews = new ArrayList<TextView>();
        textViews.add(date);
        textViews.add(content);
        textViews.add(time);
        textViews.add(asterisk);
        textViews.add(contact);
        textViews.add((TextView) v.findViewById(R.id.textViewTitle));
        textViews.add((TextView) v.findViewById(R.id.textViewTitleContact));
        textViews.add((TextView) v.findViewById(R.id.textViewTitleContent));
        textViews.add((TextView) v.findViewById(R.id.textViewTitleDate));
        textViews.add((TextView) v.findViewById(R.id.textViewTitleTime));
        textViews.add((TextView) v.findViewById(R.id.textViewOnce));
        textViews.add((TextView) v.findViewById(R.id.textViewDaily));
        textViews.add((TextView) v.findViewById(R.id.textViewWeekly));
        textViews.add((TextView) v.findViewById(R.id.textViewMonthly));
        changeAllFonts(textViews);

        contentEdit = (EditText) v.findViewById(R.id.editTextContent);

        contactButton = (ImageView) v.findViewById(R.id.imageViewContact);
        dateButton = (ImageView) v.findViewById(R.id.imageViewDate);
        timeButton = (ImageView) v.findViewById(R.id.imageViewTime);
        contentButton = (ImageView) v.findViewById(R.id.imageViewContent);

        gDetector = new GestureDetector(getActivity(), new GestureListener());

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.containerEdit);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    private void changeAllFonts(ArrayList<TextView> textViews) {
        for(TextView textView : textViews) {
            textView.setTypeface(CUSTOM_FONT);
        }
    }

    private void registerButtonClickCallback() {

        contactButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                field = Field.CONTACT;
                ContactsDialogFragment contactsDialogFragment = new ContactsDialogFragment(getActivity(), ContactListGenerator.getContacts());
                contactsDialogFragment.show(getFragmentManager(), "ContactsDialogFragment");
            }
        });

        dateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                field = Field.DATE;
                DateDialogFragment dateDialogFragment = new DateDialogFragment();
                dateDialogFragment.show(getFragmentManager(), "DateDialogFragment");
            }
        });
        timeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                field = Field.TIME;
                TimeDialogFragment timeDialogFragment = new TimeDialogFragment();
                timeDialogFragment.show(getFragmentManager(), "TimeDialogFragment");
            }
        });
        contentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //handles switching views for input vs. display
                if(!editMode) {
                    field = Field.CONTENT;
                    contentEdit.setText(mContent);
                    editMode = true;
                    contentButton.setImageDrawable(getResources().getDrawable(R.drawable.save));
                    contentEdit.setVisibility(View.VISIBLE);
                    contentEdit.requestFocus();
                    content.setVisibility(View.INVISIBLE);
                } else if (editMode) {
                    editMode = false;
                    contentButton.setImageDrawable(getResources().getDrawable(R.drawable.pencil));
                    updateField(contentEdit.getText().toString(), Field.CONTENT);
                    contentEdit.setVisibility(View.INVISIBLE);
                    content.setVisibility(View.VISIBLE);
                }
            }
        });
        checkBoxOnce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrequency = Frequency.ONCE;
                updateCheckBoxes(true);
            }
        });
        checkBoxDaily.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrequency = Frequency.DAILY;
                updateCheckBoxes(true);
            }
        });
        checkBoxWeekly.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrequency = Frequency.WEEKLY;
                updateCheckBoxes(true);
            }
        });
        checkBoxMonthly.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrequency = Frequency.MONTHLY;
                updateCheckBoxes(true);
            }
        });
    }

    private void updateCheckBoxes(boolean changed) {
        clearAllCheckBoxes();
        Drawable check = getResources().getDrawable(R.drawable.check);
        switch(mFrequency) {
            case Frequency.ONCE:
                layout.setBackground(getResources().getDrawable(R.drawable.note_yellow));
                checkBoxOnce.setImageDrawable(check);
                break;
            case Frequency.DAILY:
                layout.setBackground(getResources().getDrawable(R.drawable.note_green));
                checkBoxDaily.setImageDrawable(check);
                break;
            case Frequency.WEEKLY:
                layout.setBackground(getResources().getDrawable(R.drawable.note_pink));
                checkBoxWeekly.setImageDrawable(check);
                break;
            case Frequency.MONTHLY:
                layout.setBackground(getResources().getDrawable(R.drawable.note_blue));
                checkBoxMonthly.setImageDrawable(check);
                break;
            default:
                break;
        }
        if(changed) {
            this.changed = true;
            asterisk.setVisibility(View.VISIBLE);
        }
    }

    private void clearAllCheckBoxes() {
        Drawable clear = getResources().getDrawable(R.drawable.checkbox);
        checkBoxOnce.setImageDrawable(clear);
        checkBoxDaily.setImageDrawable(clear);
        checkBoxWeekly.setImageDrawable(clear);
        checkBoxMonthly.setImageDrawable(clear);
    }

    public boolean saveEntry() {
        if(checkFields()) {
            saved = true;
            communicator.updateEntries(id, EntryParser.parseName(mContact), EntryParser.parseNumber(mContact), mDate, mTime, mContent, mFrequency);
            Toast.makeText(getActivity(), "Entry Saved", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return true;
        }
        return false;
    }

    public void updateField(String output, Field field) {
        switch (field) {
            case CONTACT:
                contact.setText(output);
                mContact = output;
                break;
            case DATE:
                date.setText(output);
                mDate = output;
                break;
            case TIME:
                time.setText(output);
                mTime = output;
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
        return !mContact.contains("(empty)") && !mDate.isEmpty() && !mTime.isEmpty() && !mContent.isEmpty();
    }

    public String getContact() {
        return this.mContact;
    }

    public String getDate() {
        return this.mDate;
    }

    public String getTime() {
        return this.mTime;
    }

    public String getContent() {
        return this.mContent;
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

    public boolean getSaved() {
        return saved;
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
                    Toast.makeText(getActivity(), "Did you leave any fields blank?", Toast.LENGTH_SHORT);
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public interface Communicator {
        public void updateEntries(long id, String name, String number, String date, String time, String content, int frequency);
    }

    public void setCommunicator(Communicator comm) {
        communicator = comm;
    }
}
