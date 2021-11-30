package com.jiyouliang.fmap.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jiyouliang.fmap.MapApplication;
import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.ControlEventUtils;
import com.jiyouliang.fmap.view.widget.TopTitleView;

public class ControlActivity extends BaseActivity implements View.OnClickListener,
        TopTitleView.OnTopTitleViewClickListener {

    private ControlEventUtils mControlEventUtils;
    private ImageView mIvZoomIn;
    private ImageView mIvZoomOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        initView();
        initData();
        setListener();
    }

    private void initView(){
        mIvZoomIn = (ImageView) findViewById(R.id.iv_zoom_in);
        mIvZoomOut = (ImageView) findViewById(R.id.iv_zoom_out);
    }

    private void initData(){
        mControlEventUtils = ((MapApplication)getApplication()).getControlEventUtils();
    }

    private void setListener(){
        mIvZoomIn.setOnClickListener(this);
        mIvZoomOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mIvZoomIn){
            mControlEventUtils.zoomIn();
            return;
        }

        if (v == mIvZoomOut){
            mControlEventUtils.zoomOut();
            return;
        }
    }

    @Override
    public void onLeftClick(View v) {

    }

    @Override
    public void onRightClick(View v) {

    }
}