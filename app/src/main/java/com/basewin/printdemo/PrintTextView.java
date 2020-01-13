package com.basewin.printdemo;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;


/**
 * Author:28936
 * Date:2019/9/18 13:46
 * Description:
 */

public class PrintTextView extends AppCompatTextView {
    // font 字体
    private Typeface font = Typeface.DEFAULT;

    public PrintTextView(Context context) {
        this(context,null);
    }

    public PrintTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PrintTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.PrintTextView);
        int fontRes = array.getResourceId(R.styleable.PrintTextView_fontType,0);
        array.recycle();
        getPaint().setAntiAlias(false);
        if(fontRes!=0){
            font= ResourcesCompat.getFont(context, fontRes);
            setTypeface(font,getTypeface().getStyle());
        }
    }
}
