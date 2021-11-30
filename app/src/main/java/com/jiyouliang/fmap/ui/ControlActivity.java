package com.jiyouliang.fmap.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jiyouliang.fmap.MapApplication;
import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.util.ControlEventUtils;
import com.jiyouliang.fmap.view.widget.RemoteControllerView;

public class ControlActivity extends BaseActivity implements View.OnClickListener,
        RemoteControllerView.OnRemoteControllerClickListener {

    private ControlEventUtils mControlEventUtils;
    private ImageView mIvZoomIn;
    private ImageView mIvZoomOut;
    private RemoteControllerView mRemoteControllerView;

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
        mRemoteControllerView = (RemoteControllerView) findViewById(R.id.rcv_view);
    }

    private void initData(){
        mControlEventUtils = ((MapApplication)getApplication()).getControlEventUtils();
    }

    private void setListener(){
        mIvZoomIn.setOnClickListener(this);
        mIvZoomOut.setOnClickListener(this);
        mRemoteControllerView.setRemoteControllerClickListener(this);
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
    public void topClick() {
        mControlEventUtils.topClick();
    }

    @Override
    public void leftClick() {
        mControlEventUtils.leftClick();
    }

    @Override
    public void rightClick() {
        mControlEventUtils.rightClick();
    }

    @Override
    public void bottomClick() {
        mControlEventUtils.bottomClick();
    }

    @Override
    public void centerOkClick() {
        mControlEventUtils.centerOkClick();
    }
}