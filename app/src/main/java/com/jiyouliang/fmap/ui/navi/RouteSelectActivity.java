package com.jiyouliang.fmap.ui.navi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.EditText;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.jiyouliang.fmap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteSelectActivity extends Activity implements AMapNaviListener {

    private static final String PARAMS = "params";;
    private static final String KEY_STARTLATLNG = "startLatLng";;
    private static final String KEY_STOPTLATLNG = "stopLatLng";;
    private LatLng startLatLng;
    private LatLng stopLatLng;

    /**
     * 导航对象(单例)
     */
    private AMapNavi mAMapNavi;
    private AMap mAmap;
    /**
     * 地图对象
     */
    private MapView mRouteMapView;
    private Marker mStartMarker;
    private Marker mEndMarker;
    private NaviLatLng endLatlng = new NaviLatLng(39.955846, 116.352765);
    private NaviLatLng startLatlng = new NaviLatLng(39.925041, 116.437901);
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();

    /**
     * 当前用户选中的路线，在下个页面进行导航
     */
    private int routeIndex;
    /**
     * 路线的权值，重合路线情况下，权值高的路线会覆盖权值低的路线
     **/
    private int zindex = 1;
    /**
     * 路线计算成功标志位
     */
    private boolean calculateSuccess = false;
    private boolean chooseRouteSuccess = false;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_select);
        initView(savedInstanceState);
        initData();
    }

    private void initView(Bundle savedInstanceState) {
        mRouteMapView = (MapView) findViewById(R.id.route_select_view);
        mRouteMapView.onCreate(savedInstanceState);
        mAmap = mRouteMapView.getMap();
        // 初始化Marker添加到地图
        mStartMarker = mAmap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.start))));
        mEndMarker = mAmap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.end))));

        try {
            mAMapNavi = AMapNavi.getInstance(getApplicationContext());
            mAMapNavi.addAMapNaviListener(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        Intent intent = getIntent();

        if(intent != null && intent.getBundleExtra(PARAMS) != null){
            // 获取页面传递过来的起点和终点坐标
            Bundle bundle = intent.getBundleExtra(PARAMS);

            startLatLng = bundle.getParcelable(KEY_STARTLATLNG);
            stopLatLng = bundle.getParcelable(KEY_STOPTLATLNG);
            mStartMarker.setPosition(startLatLng);
            mEndMarker.setPosition(stopLatLng);

            mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        if(startLatLng != null && stopLatLng != null){
            // 起点信息
            List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
            startList.add(new NaviLatLng(startLatLng.latitude, startLatLng.longitude));
            // 终点信息
            List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
            endList.add(new NaviLatLng(stopLatLng.latitude, stopLatLng.longitude));
            /**
             * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
             *
             * @congestion 躲避拥堵
             * @avoidhightspeed 不走高速
             * @cost 避免收费
             * @hightspeed 高速优先
             * @multipleroute 多路径
             *
             *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
             *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
             */
            int strategy = 0;
            try {
                //再次强调，最后一个参数为true时代表多路径，否则代表单路径
                strategy = mAMapNavi.strategyConvert(true, false, false, true, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mAMapNavi.calculateDriveRoute(startList,endList,null, strategy);
        }
    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        //清空上次计算的路径列表。
        routeOverlays.clear();

        int[] routeIds = aMapCalcRouteResult.getRouteid();
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        for (int i = 0; i < routeIds.length; i++) {
            AMapNaviPath path = paths.get(routeIds[i]);
            if (path != null) {
                drawRoutes(routeIds[i], path);
            }
        }
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
        calculateSuccess = true;
        mAmap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(mAmap, path, this);
        routeOverLay.setTrafficLine(false);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        calculateSuccess = false;
    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void onGpsSignalWeak(boolean b) {

    }
}