package upsales.com.upsalesandroidtest;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by Goran on 20.4.2018.
 */

public class FontCache {

    private static final String FONT_DIR_PREFIX = "fonts/";
    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(Context context, String fontname) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), FONT_DIR_PREFIX + fontname);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontname, typeface);
        }

        return typeface;
    }

}