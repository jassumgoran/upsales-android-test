package upsales.com.upsalesandroidtest.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import upsales.com.upsalesandroidtest.FontCache;
import upsales.com.upsalesandroidtest.R;

/**
 * Created by Goran on 20.4.2018.
 */

public class TypefacedTextView extends android.support.v7.widget.AppCompatTextView {

    public TypefacedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
        String fontName = styledAttrs.getString(R.styleable.TypefacedTextView_typeface);
        styledAttrs.recycle();

        if (fontName != null) {
            setTypeface(FontCache.getTypeface(context, fontName));
        }
    }

}