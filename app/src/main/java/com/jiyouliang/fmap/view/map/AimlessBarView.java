package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.jiyouliang.fmap.R;

/**
 * 附近搜索
 */
public class AimlessBarView extends ConstraintLayout implements View.OnClickListener {

    private OnAimlessBarViewClickListener mListener;

    public AimlessBarView(Context context) {
        this(context, null);
    }

    public AimlessBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AimlessBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_aimless_bar, this, true);
        this.setOnClickListener(this);
    }

    public void setOnAimlessBarViewClickListener(OnAimlessBarViewClickListener listener) {
        if (listener == null) {
            return;
        }
        this.mListener = listener;
    }

    public interface OnAimlessBarViewClickListener {
        /**
         * 附近搜索点击回调
         */
        void onAimlessBarClick();
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        mListener.onAimlessBarClick();
    }
}
