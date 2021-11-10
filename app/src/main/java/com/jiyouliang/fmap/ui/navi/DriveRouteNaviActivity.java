package com.jiyouliang.fmap.ui.navi;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.jiyouliang.fmap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 驾车导航
 */
public class DriveRouteNaviActivity extends BaseAMapNaviActivity {

    private static final String PARAMS = "params";;
    private static final String KEY_STARTLATLNG = "startLatLng";;
    private static final String KEY_STOPTLATLNG = "stopLatLng";;
    private LatLng startLatLng;
    private LatLng stopLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_amap_navi);
        initView(savedInstanceState);
        initData();

    }

    private void initView(Bundle savedInstanceState) {
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);
    }

    private void initData() {
        Intent intent = getIntent();

        if(intent != null && intent.getBundleExtra(PARAMS) != null){
            // 获取页面传递过来的起点和终点坐标
            Bundle bundle = intent.getBundleExtra(PARAMS);

            startLatLng = bundle.getParcelable(KEY_STARTLATLNG);
            stopLatLng = bundle.getParcelable(KEY_STOPTLATLNG);
        }
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        if(startLatLng != null && stopLatLng != null){
//            mAMapNavi.calculateWalkRoute(new NaviLatLng(22.560888, 113.884791), new NaviLatLng(22.552335, 113.887538));
            //mAMapNavi.calculateWalkRoute(new NaviLatLng(startLatLng.latitude, startLatLng.longitude),
            //        new NaviLatLng(stopLatLng.latitude, stopLatLng.longitude));
            // 起点信息
            List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
            startList.add(new NaviLatLng(startLatLng.latitude, startLatLng.longitude));
            // 终点信息
            List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
            endList.add(new NaviLatLng(stopLatLng.latitude, stopLatLng.longitude));
            mAMapNavi.calculateDriveRoute(startList,endList,null, PathPlanningStrategy.DRIVING_DEFAULT);
        }

    }




}
