package com.admin.advisementplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 2017/6/13.
 */

public class ScrollTextView extends TextView {

    public ScrollTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollTextView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}