package com.jiyouliang.fmap.util;

import com.jiyouliang.fmap.ui.OnControlListener;

public class ControlEventUtils {

    private OnControlListener mListener;

    public ControlEventUtils(){}

    public void setOnControlListener(OnControlListener listener){
        this.mListener = listener;
    }

    public void avoidAheadRoad(){}

    public void routeOverview(){}

    public void routeRefresh(){}

    public void voiceBroadcast(boolean isOpen){}

    public void alongSearch(){}

    public void zoomIn(){
        if(mListener == null) return;
        mListener.zoomIn();
    }

    public void zoomOut(){
        if(mListener == null) return;
        mListener.zoomOut();
    }

    public void topClick(){
        if(mListener == null) return;
        mListener.topClick();
    }

    public void leftClick(){
        if(mListener == null) return;
        mListener.leftClick();
    }

    public void rightClick(){
        if(mListener == null) return;
        mListener.rightClick();
    }

    public void bottomClick(){
        if(mListener == null) return;
        mListener.bottomClick();
    }

    public void centerOkClick(){
        if(mListener == null) return;
        mListener.centerOkClick();
    }
}
