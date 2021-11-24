package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.view.base.BaseIconView;

/**
 * 打的
 */
public class EleEyeView extends BaseIconView {

    private static final String TAG = "EleEyeView";


    public EleEyeView(Context context) {
        this(context, null);
    }

    public EleEyeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EleEyeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean createBackground() {
        setBackgroundResource(R.drawable.icon_middle_selector);
        return true;
    }

    @Override
    public boolean createIcon() {
        setIconBackground(R.drawable.icon_cctv);
        return true;
    }
}
