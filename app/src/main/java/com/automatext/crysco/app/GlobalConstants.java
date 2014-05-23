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
    private static Display display;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static Typeface CUSTOM_FONT;

    public static void setContextDependencies(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        Point size = new Point();
        setWidth(size);
        setHeight(size);

        CUSTOM_FONT = Typeface.createFromAsset(c.getAssets(), "fonts/BeeMarkerInk.ttf");
    }

    private static void setWidth(Point size) {
        if(Build.VERSION.SDK_INT > 11) {
            display.getSize(size);
            SCREEN_WIDTH = size.x;
        } else
            SCREEN_WIDTH = display.getWidth();
    }

    private static void setHeight(Point size) {
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
        public static final String ENTRIES = "entries";
        public static final String ID = "id";
        public static final String MODE = "mode";
        public static final String BUNDLE = "bundle";
        public static final String CONTACT = "contact";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String CONTENT = "content";
        public static final String FREQUENCY = "frequency";
    }

}
