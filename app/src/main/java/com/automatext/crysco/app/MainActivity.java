package com.automatext.crysco.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileNotFoundException;

import static com.automatext.crysco.app.GlobalConstants.TAG;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomFont.addFont(getAssets(), "CrayonCrumble");
        GlobalConstants.setContextDependencies(this);
        ContactListGenerator.getInstance().initializeContacts(this);
        DBAdapter.getMainDBInstance().initiateDatabase(this);

        Intent intent = new Intent(this, IncomingSmsReceiver.class);
        startService(intent);

        Intent intent2 = new Intent(this, IncomingCallReceiver.class);
        startService(intent2);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private TextView title, autoReply, scheduledTexts;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);

            initializeViews(view);

            return view;
        }

        private void initializeViews(View v) {

            title = (TextView) v.findViewById(R.id.textViewMainTitle);
            autoReply = (TextView) v.findViewById(R.id.textViewAutoReply);
            scheduledTexts = (TextView) v.findViewById(R.id.textViewScheduledTexts);

            Typeface font = null;
                try {
                    font = CustomFont.getFont("CrayonCrumble");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Font Not Found");
                    font = Typeface.DEFAULT;
                } finally {
                    title.setTypeface(font);
                    autoReply.setTypeface(font);
                    scheduledTexts.setTypeface(font);
                }

            autoReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AutoReplyListActivity.class);
                    startActivity(intent);
                }
            });
            scheduledTexts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), TextsListActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

}
