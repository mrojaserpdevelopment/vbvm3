package com.erpdevelopment.vbvm.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by usuario on 2/10/2016.
 */
public class FontManager {
    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
