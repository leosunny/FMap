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

    public void draw(){}

    public void zoomIn(){
        if(mListener == null) return;
        mListener.zoomIn();
    }

    public void zoomOut(){
        if(mListener == null) return;
        mListener.zoomOut();
    }
}
