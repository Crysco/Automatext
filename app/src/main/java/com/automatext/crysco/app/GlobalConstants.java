package com.automatext.crysco.app;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Crys on 5/14/14.
 */
public class GlobalConstants {

    public static final String TAG = "TAG";
    public static final String PREFS_NAME = "PreferencesFile";
    public static final String SWITCH_STATE = "reply_state";
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static void setContextDependencies(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        setWidth(wm.getDefaultDisplay(), size);
        setHeight(wm.getDefaultDisplay(), size);
    }

    private static void setWidth(Display display, Point size) {
        if(Build.VERSION.SDK_INT > 11) {
            display.getSize(size);
            SCREEN_WIDTH = size.x;
        } else
            SCREEN_WIDTH = display.getWidth();
    }

    private static void setHeight(Display display, Point size) {
        if(Build.VERSION.SDK_INT > 11) {
            display.getSize(size);
            SCREEN_HEIGHT = size.y;
        } else
            SCREEN_HEIGHT = display.getHeight();
    }

    public interface Mode {
        public static final int NEW = 0;
        public static final int UPDATE = 1;
        public static final int DELETE = 2;
    }

    public interface Frequency {
        public static final int ONCE = 0;
        public static final int DAILY = 1;
        public static final int WEEKLY = 2;
        public static final int MONTHLY = 3;
    }

    public interface Tags {
        public static final String TEXT_DETAILS_FRAGMENT = "TextDetailsFragment";
        public static final String TEXTS_LIST_FRAGMENT = "TextsListFragment";
        public static final String REPLIES_LIST_FRAGMENT = "AutoReplyListFragment";
        public static final String REPLY_DETAILS_FRAGMENT = "AutoReplyDetailsFragment";
        public static final String ENTRIES = "entries";
        public static final String REPLIES = "replies";
        public static final String REPLY = "reply";
        public static final String ENTRY = "entry";
        public static final String ID = "id";
        public static final String MODE = "mode";
        public static final String BUNDLE = "bundle";
        public static final String NAME = "name";
        public static final String NUMBER = "number";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String CONTENT = "content";
        public static final String FREQUENCY = "frequency";
        public static final String TITLE = "title";
        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";
        public static final String DAYS = "days";
        public static final String SILENCE = "silenced";
    }

    public interface ActiveState {
        public static final int ACTIVE = 1;
        public static final int NOT_ACTIVE = 0;
    }

    public interface SilenceState {
        public static final int SILENCED = 1;
        public static final int NOT_SILENCED = 0;
    }

    public interface SwitchState {
        public static final boolean OFF = false;
        public static final boolean ON = true;
    }

    public static enum Field {
        TITLE,
        START_TIME,
        END_TIME,
        TIME,
        DATE,
        CONTENT,
        CONTACT
    }

}
