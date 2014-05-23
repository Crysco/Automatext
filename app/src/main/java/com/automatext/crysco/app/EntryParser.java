package com.automatext.crysco.app;

import android.util.Log;

/**
 * Created by Crys on 5/12/14.
 */
public class EntryParser {

    public static String parseNumber(String contact) {
        Log.d(GlobalConstants.TAG, contact);
        String number = "";
        int start = contact.indexOf('#');
        start++;
        do {
            number += contact.charAt(start);
            start++;
        } while (start != contact.length());

        return number;
    }

    public static String parseName(String contact) {
        String name = "";
        int start = 0;
        do {
            name += contact.charAt(start);
            start++;
        } while (start != contact.indexOf('#'));

        return name;
    }
}
