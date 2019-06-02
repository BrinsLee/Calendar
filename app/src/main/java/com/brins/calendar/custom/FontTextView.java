package com.brins.calendar.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.brins.calendar.R;
import com.brins.calendar.utils.TypefaceUtils;

public class FontTextView extends AppCompatTextView {

    public FontTextView(Context context) {
        this(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.FontTextView);
        int fontType = attributes.getInteger(R.styleable.FontTextView_font_type, 0);
        attributes.recycle();
        initTypeface(context, fontType);
    }



    private void initTypeface(Context context, int fontType) {
        if(fontType > 0) {
            Typeface typeface = TypefaceUtils.Companion.getTypeface(context, fontType);
            if (typeface != null) {
                setTypeface(typeface);
            }
        }
    }

}
