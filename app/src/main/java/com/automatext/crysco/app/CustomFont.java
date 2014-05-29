package com.automatext.crysco.app;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.io.FileNotFoundException;
import java.util.Hashtable;

/**
 * Created by Crys on 5/23/14.
 */
public class CustomFont {

    private static final Hashtable<String, Typeface> CACHE = new Hashtable<String, Typeface>();

    public static void addFont(AssetManager manager, String fontName)  {
        synchronized (CACHE) {
            if (!CACHE.containsKey(fontName)) {
                Typeface t = Typeface.createFromAsset(manager, "fonts/" + fontName + ".ttf");
                CACHE.put(fontName, t);
            }
        }
    }

    public static Typeface getFont(String fontName) throws FileNotFoundException {
        if(!CACHE.containsKey(fontName))
            return null;
        else
            return CACHE.get(fontName);
    }

}
