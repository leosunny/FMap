package com.jiyouliang.fmap;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AimlessModeListener;
import com.amap.api.navi.enums.AimLessMode;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.AMapGestureListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.LatLonSharePoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.share.ShareSearch;
import com.jiyouliang.fmap.harware.SensorEventHelper;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.ui.ControlActivity;
import com.jiyouliang.fmap.ui.SettingActivity;
import com.jiyouliang.fmap.util.Constants;
import com.jiyouliang.fmap.util.DeviceUtils;
import com.jiyouliang.fmap.util.InputMethodUtils;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.MyAMapUtils;
import com.jiyouliang.fmap.util.SPUtil;
import com.jiyouliang.fmap.util.WechatApi;
import com.jiyouliang.fmap.util.WechatUtil;
import com.jiyouliang.fmap.view.base.MapViewInterface;
import com.jiyouliang.fmap.view.map.AimlessBarView;
import com.jiyouliang.fmap.view.map.EleEyeView;
import com.jiyouliang.fmap.view.map.GPSView;
import com.jiyouliang.fmap.view.map.GroupTeamView;
import com.jiyouliang.fmap.view.map.MapHeaderView;
import com.jiyouliang.fmap.view.map.NearbySearchView;
import com.jiyouliang.fmap.view.map.PoiDetailBottomView;
import com.jiyouliang.fmap.view.map.RouteView;
import com.jiyouliang.fmap.view.map.SupendPartitionView;
import com.jiyouliang.fmap.view.map.TrafficView;
import com.jiyouliang.fmap.view.widget.DialProgress;
import com.jiyouliang.fmap.view.widget.OnFavoriteItemClickListener;
import com.jiyouliang.fmap.view.widget.OnHistoryItemClickListener;
import com.jiyouliang.fmap.view.widget.OnItemClickListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends BaseActivity implements GPSView.OnGPSViewClickListener,
        NearbySearchView.OnNearbySearchViewClickListener,
        AimlessBarView.OnAimlessBarViewClickListener,
        AMapGestureListener,
        AMapLocationListener,
        LocationSource,
        TrafficView.OnTrafficChangeListener,
        View.OnClickListener,
        MapViewInterface,
        PoiDetailBottomView.OnPoiDetailBottomClickListener,
        ShareSearch.OnShareSearchListener,
        AMap.OnPOIClickListener,
        TextWatcher,
        Inputtips.InputtipsListener,
        MapHeaderView.OnMapHeaderViewClickListener,
        OnItemClickListener,
        OnHistoryItemClickListener,
        OnFavoriteItemClickListener,
        AimlessModeListener {
    private static final String TAG = "MapActivity";

    //private PowerManager.WakeLock mWakeLock;
    /**
     * 首次进入申请定位、sd卡权限
     */
    private static final int REQ_CODE_INIT = 0;
    private static final int REQ_CODE_FINE_LOCATION = 1;
    private static final int REQ_CODE_STORAGE = 2;
    private TextureMapView mMapView;
    private AMap mAMap;
    private UiSettings mUiSettings;

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private GPSView mGpsView;
    private NearbySearchView mNearbySearcyView;
    private AimlessBarView mAimlessBarView;
    private DialProgress mCircleProgress;
    private static boolean mFirstLocation = true;//第一次定位
    private int mCurrentGpsState = STATE_UNLOCKED;//当前定位状态
    private static final int STATE_UNLOCKED = 0;//未定位状态，默认状态
    private static final int STATE_LOCKED = 1;//定位状态
    private static final int STATE_ROTATE = 2;//根据地图方向旋转状态
    private int mZoomLevel = 16;//地图缩放级别，最大缩放级别为20
    private LatLng mLatLng;//当前定位经纬度
    private LatLng mClickPoiLatLng;//当前点击的poi经纬度
    private static long mAnimDuartion = 500L;//地图动效时长
    private int mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;//地图状态类型
    private SensorEventHelper mSensorHelper;
    private Marker mLocMarker;//自定义小蓝点
    private Circle mCircle;
    private static final int STROKE_COLOR = Color.argb(240, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private OnLocationChangedListener mLocationListener;
    private float mAccuracy;
    private boolean mMoveToCenter = true;//是否可以移动地图到定位点
    private TrafficView mTrafficView;
    private EleEyeView mEleEyeView;
    private View mBottomSheet;
    private BottomSheetBehavior<View> mBehavior;
    private int mMaxPeekHeight;//最大高的
    private int mMinPeekHeight;//最小高度
    private View mPoiColseView;
    private RouteView mRouteView;
//    private FrequentView mFrequentView;
    private PoiDetailBottomView mPoiDetailTaxi;
    private int mPadding;
    //poi detail动画时长
    private static final int DURATION = 100;
    private View mGspContainer;
    private float mTransY;
    private float mOriginalGspY;
    private int moveY;
    private int[] mBottomSheetLoc = new int[2];
    private String mPoiName;
    private TextView mTvLocation;
    private MapHeaderView mMapHeaderView;
    private View mFeedbackContainer;
    private SupendPartitionView mSupendPartitionView;
    private int mScreenHeight;
    private int mScreenWidth;
    private boolean isMinMap; //缩小显示地图
    private boolean onScrolling;//正在滑动地图
    private MyLocationStyle mLocationStyle;
    private boolean slideDown;//向下滑动
    private LinearLayout mShareContainer;
    private LinearLayout mFavoriteBtn;
    private ImageView mIvPoiFavorite;
    private boolean isPoiFavorite = false;
    private String mPoiFavorite;
    private IWXAPI api;
    private BroadcastReceiver mWechatBroadcast;
    private ShareSearch mShareSearch;
    private AMapLocation mAmapLocation;
    // 分享url到微信图片大小
    private static final int THUMB_SIZE = 150;
    private ImageButton mImgBtnBack;
    private TextView mTvLocTitle;
    // 当前是否正在处理POI点击
    private boolean isPoiClick;
    private TextView mTvRoute;
    private LinearLayout mLLSearchContainer;
    // 搜索结果存储
    private List<Tip> mSearchData = new ArrayList<>();

    /**
     * 当前地图模式
     */
    private MapMode mMapMode = MapMode.NORMAL;
    private RecyclerView mRecycleViewSearch;
    private ImageView mIvLeftSearch;
    private EditText mEtSearchTip;
    private SearchAdapter mSearchAdapter;
    private String mCity;
    private ProgressBar mSearchProgressBar;
    private LocationManager mLocMgr;

    private LinearLayout mHome;
    private LinearLayout mOffice;

    private LinearLayout mAimlessExit;
    private LinearLayout mAimlessSetting;

    //搜索历史
    private RecyclerView mRecycleViewSearchHistory;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private List<String> mSearchHistoryData = new ArrayList<>();
    private List<String> mHistoryList = new ArrayList<>();
    //private LinearLayout mLLSearchHistoryContainer;

    //收藏
    private RecyclerView mRecycleViewFavorite;
    private FavoriteAdapter mFavoriteAdapter;
    private List<String> mFavoriteData = new ArrayList<>();
    private List<String> mFavoriteList = new ArrayList<>();

    private LinearLayout mHistory;
    private LinearLayout mFavorite;
    private ImageView mIvHistory;
    private ImageView mIvFavorite;
    private boolean isHistorySelected = false;
    private boolean isFavoriteSelected = false;

    private GroupTeamView mGroupTeamView;

    private AMapNavi mAMapNavi;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);
        initView(savedInstanceState);
        try {
            initData();
        } catch (AMapException e) {
            e.printStackTrace();
        }
        setListener();

        //PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        //mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");

    }

    private void initView(Bundle savedInstanceState) {
        mGpsView = (GPSView) findViewById(R.id.gps_view);
        mRouteView = (RouteView) findViewById(R.id.route_view);
//        mFrequentView = (FrequentView) findViewById(R.id.fv);
        //获取地图控件引用
        mMapView = (TextureMapView) findViewById(R.id.map);
        //交通流量状态控件
        mTrafficView = (TrafficView) findViewById(R.id.tv);
        mAMap = mMapView.getMap();
        //显示实时交通
        mAMap.setTrafficEnabled(true);
        //电子眼
        mEleEyeView=(EleEyeView)findViewById(R.id.etv);
        //Test
        mGroupTeamView=(GroupTeamView)findViewById(R.id.gtv);

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        mGpsView.setGpsState(mCurrentGpsState);
        mNearbySearcyView = (NearbySearchView) findViewById(R.id.nearby_view);
        mAimlessBarView =(AimlessBarView)findViewById(R.id.exit_aimless_view);
        mCircleProgress =(DialProgress) findViewById(R.id.circle_progress_bar);
        //底部弹出BottomSheet
        mBottomSheet = findViewById(R.id.poi_detail_bottom);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheet.setVisibility(View.GONE);
        mPoiColseView = findViewById(R.id.iv_close);
        //底部：查看详情、打车、路线
        mPoiDetailTaxi = (PoiDetailBottomView) findViewById(R.id.poi_detail_taxi);
        mPoiDetailTaxi.setVisibility(View.GONE);
        mGspContainer = findViewById(R.id.gps_view_container);

        mTvLocTitle = (TextView) findViewById(R.id.tv_title);
        mTvLocation = (TextView) findViewById(R.id.tv_my_loc);
        mMapHeaderView = (MapHeaderView) findViewById(R.id.mhv);
        mFeedbackContainer = findViewById(R.id.feedback_container);
        mFeedbackContainer.setVisibility(View.GONE);
        mSupendPartitionView = (SupendPartitionView) findViewById(R.id.spv);
        // 分享组件
        mShareContainer = (LinearLayout)findViewById(R.id.rl_wxshare);
        mImgBtnBack= (ImageButton)findViewById(R.id.ib_back);
        // 收藏
        mFavoriteBtn = (LinearLayout)findViewById(R.id.rl_favorite);
        mIvPoiFavorite = (ImageView)findViewById(R.id.iv_poi_favorite);
        // 路线
        mTvRoute = (TextView)findViewById(R.id.tv_route);
        // 搜索区域
        mLLSearchContainer = (LinearLayout)findViewById(R.id.ll_search_container);
        mRecycleViewSearch = (RecyclerView)findViewById(R.id.rv_search);
        mIvLeftSearch = (ImageView)findViewById(R.id.iv_search_left);
        mEtSearchTip = (EditText)findViewById(R.id.et_search_tip);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleViewSearch.setLayoutManager(layoutManager);
        mSearchProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        // 搜索历史
        //mLLSearchHistoryContainer=(LinearLayout)findViewById(R.id.ll_search_history_container);
        mRecycleViewSearchHistory=(RecyclerView)findViewById(R.id.rv_search_history);
        LinearLayoutManager layoutHistoryManager = new LinearLayoutManager(this);
        mRecycleViewSearchHistory.setLayoutManager(layoutHistoryManager);

        mHistory = (LinearLayout)findViewById(R.id.ll_history);
        mFavorite = (LinearLayout)findViewById(R.id.ll_favorite);
        mIvHistory = (ImageView)findViewById(R.id.iv_histroy);
        mIvFavorite = (ImageView)findViewById(R.id.iv_favorite);

        mRecycleViewFavorite = (RecyclerView)findViewById(R.id.rv_favorite);
        LinearLayoutManager layoutFavoriteManager = new LinearLayoutManager(this);
        mRecycleViewFavorite.setLayoutManager(layoutFavoriteManager);

        //回家和去公司
        mHome=(LinearLayout)findViewById(R.id.rl_home);
        mOffice=(LinearLayout)findViewById(R.id.rl_office);

        mAimlessExit=(LinearLayout)findViewById(R.id.rl_exit);
        mAimlessSetting=(LinearLayout)findViewById(R.id.rl_setting);

        setBottomSheet();
        setUpMap();

        mPadding = getResources().getDimensionPixelSize(R.dimen.padding_size);
        /*int statusBarHeight = DeviceUtils.getStatusBarHeight(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.WHITE);
        log(String.format("statusBarHeight=%s", statusBarHeight));*/
//        SystemUIModes.setTranslucentStatus(this, true);
    }

    private void initData() throws AMapException {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将应用的appId注册到微信
        api.registerApp(Constants.APP_ID);
        // 高德地图分享
        mShareSearch = new ShareSearch(this);
        // 搜索结果RecyclerView
        mSearchAdapter = new SearchAdapter(mSearchData);
        mRecycleViewSearch.setAdapter(mSearchAdapter);
        // 搜索历史RecyclerView
        mSearchHistoryAdapter = new SearchHistoryAdapter(mSearchHistoryData);
        mRecycleViewSearchHistory.setAdapter(mSearchHistoryAdapter);

        // 搜藏
        mFavoriteAdapter = new FavoriteAdapter(mFavoriteData);
        mRecycleViewFavorite.setAdapter(mFavoriteAdapter);

        mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            mAMapNavi=AMapNavi.getInstance(this);
        } catch (com.amap.api.maps.AMapException e) {
            e.printStackTrace();
        }

        mIvHistory.setSelected(true);
        isHistorySelected = true;

    }

    /**
     * 设置底部POI详细BottomSheet
     */
    private void setBottomSheet() {
        mMinPeekHeight = mBehavior.getPeekHeight();
        //虚拟键盘高度
        int navigationHeight = DeviceUtils.getNavigationBarHeight(this);
        //加上虚拟键盘高度，避免被遮挡
//        mBehavior.setPeekHeight(mMinPeekHeight + navigationHeight);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (null == wm) {
            LogUtil.e(TAG, "获取WindowManager失败:" + wm);
            return;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        //屏幕高度3/5
        mScreenHeight = point.y;
        mScreenWidth = point.x;
        //设置bottomsheet高度为屏幕 3/5
        int height = mScreenHeight * 3 / 5;
        mMaxPeekHeight = height;
        ViewGroup.LayoutParams params = mBottomSheet.getLayoutParams();
        params.height = height;

    }

    /**
     * 事件处理
     */
    private void setListener() {
        mGpsView.setOnGPSViewClickListener(this);
        mNearbySearcyView.setOnNearbySearchViewClickListener(this);
        mAimlessBarView.setOnAimlessBarViewClickListener(this);
        mRouteView.setOnClickListener(this);
        //地图手势事件
        mAMap.setAMapGestureListener(this);
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        if (mTrafficView != null) {
            mTrafficView.setOnTrafficChangeListener(this);
        }
        if(mEleEyeView !=null){
            mEleEyeView.setOnClickListener(this);
        }
        if(mGroupTeamView !=null){
            mGroupTeamView.setOnClickListener(this);
        }

        if (null != mPoiColseView) {
            mPoiColseView.setOnClickListener(this);
        }
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            private float lastSlide;//上次slideOffset
            private float currSlide;//当前slideOffset


            //BottomSheet状态改变回调
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    //展开
                    case BottomSheetBehavior.STATE_EXPANDED:
                        log("STATE_EXPANDED");
                        smoothSlideUpMap();
                        break;
                    //折叠
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        log("STATE_COLLAPSED");
                        /*if (slideDown) {
                            maxMapView();
                            slideDown = false;
                        }*/
                        onPoiDetailCollapsed();
                        slideDown = false;

                        break;
                    //隐藏
                    case BottomSheetBehavior.STATE_HIDDEN:
                        log("STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //拖拽
                        log("STATE_DRAGGING");

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //结束：释放
                        log("STATE_SETTLING");

                        break;

                }
            }

            /**
             * BottomSheet滑动回调
             */

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                currSlide = slideOffset;
                log("onSlide:slideOffset=" + slideOffset + ",getBottom=" + bottomSheet.getBottom() + ",currSlide=" + currSlide + ",lastSlide=" + lastSlide);
                if (slideOffset > 0) {
                    // > 0:向上拖动
                    mPoiColseView.setVisibility(View.GONE);
                    showBackToMapState();
                    if (slideOffset < 1) {

                    }
                    mMoveToCenter = false;
                    if (currSlide - lastSlide > 0) {
                        log(">>>>>向上滑动");
                        slideDown = false;
                        onPoiDetailExpanded();
                        //smoothSlideUpMap(slideOffset);
                    } else if (currSlide - lastSlide < 0) {
                        log("<<<<<向下滑动");
                        //smoothSlideDownMap(slideOffset);
                        if (!slideDown) {
                            smoothSlideDownMap();
                        }
                    }
                } else if (slideOffset == 0) {
                    //滑动到COLLAPSED状态
                    mPoiColseView.setVisibility(View.VISIBLE);
                    showPoiDetailState();
                } else if (slideOffset < 0) {
                    //从COLLAPSED向HIDDEN状态滑动，此处禁止BottomSheet隐藏
                    //setHideable(false)禁止Behavior执行：可以实现禁止向下滑动消失
                    mBehavior.setHideable(false);
                }

                lastSlide = currSlide;

            }
        });

        mPoiDetailTaxi.setOnPoiDetailBottomClickListener(this);

        //头部View点击处理
        mMapHeaderView.setOnMapHeaderViewClickListener(this);

        mShareContainer.setOnClickListener(this);
        mFavoriteBtn.setOnClickListener(this);
        // 注册高德地图分享回调
        mShareSearch.setOnShareSearchListener(this);
        // 头部返回
        mImgBtnBack.setOnClickListener(this);
        // 地图poi点击
        mAMap.setOnPOIClickListener(this);
        // 点击路径进入导航页面
        mTvRoute.setOnClickListener(this);
        // 搜索布局左侧返回箭头图标
        mIvLeftSearch.setOnClickListener(this);
        // 搜索输入框
        mEtSearchTip.addTextChangedListener(this);
        mSearchAdapter.setOnItemClickListener(this);
        // 搜索历史
        mSearchHistoryAdapter.setOnItemClickListener(this);
        // 收藏
        mFavoriteAdapter.setOnItemClickListener(this);
        // 回家和去公司
        mHome.setOnClickListener(this);
        mOffice.setOnClickListener(this);

        mHistory.setOnClickListener(this);
        mFavorite.setOnClickListener(this);
//        mIvHistory.setOnClickListener(this);
//        mIvFavorite.setOnClickListener(this);

        mAMapNavi.addAimlessModeListener(this);

        mAimlessExit.setOnClickListener(this);
        mAimlessSetting.setOnClickListener(this);
    }

    private void setUpMap() {
        mAMap.setLocationSource(this);//设置定位监听
        //隐藏缩放控件
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        //设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.setMyLocationEnabled(true);
        setLocationStyle();
    }

    @Override
    public void onLocationChanged(final AMapLocation location) {

        if (null == mLocationListener || null == location || location.getErrorCode() != 0) {
            if (location != null) {
                LogUtil.d(TAG, "定位失败：errorCode=" + location.getErrorCode() + ",errorMsg=" + location.getErrorInfo());
            }
            return;
        }
        this.mAmapLocation = location;
        if (onScrolling) {
            LogUtil.e(TAG, "MapView is Scrolling by user,can not operate...");
            return;
        }
        //获取经纬度
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        // 当前poiname和上次不相等才更新显示
        if(location.getPoiName() != null && !location.getPoiName().equals(mPoiName)){
            if(!isPoiClick){
                // 点击poi时,定位位置和点击位置不一定一样
                mPoiName = location.getPoiName();
                showPoiNameText(String.format("在%s附近", mPoiName));
            }
        }
        if(mMapMode.equals(MapMode.AIMLESS)){
            double speed = location.getSpeed()* 3.6;
            mCircleProgress.setValue((float) speed);
        }

        //LogUtil.d(TAG, "定位成功，onLocationChanged： lng" + lng + ",lat=" + lat + ",mLocMarker=" + mLocMarker + ",poiName=" + mPoiName+",getDescription="+location.getDescription()+", address="+location.getAddress()+",getLocationDetail"+location.getLocationDetail()+",street="+location.getStreet());

        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        mLatLng = new LatLng(lat, lng);
        if(!location.getCity().equals(mCity)){
            mCity = location.getCity();
        }

        //首次定位,选择移动到地图中心点并修改级别到15级
        //首次定位成功才修改地图中心点，并移动
        mAccuracy = location.getAccuracy();
        LogUtil.d(TAG, "accuracy=" + mAccuracy + ",mFirstLocation=" + mFirstLocation);
        if (mFirstLocation) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, mZoomLevel), new AMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mCurrentGpsState = STATE_LOCKED;
                    mGpsView.setGpsState(mCurrentGpsState);
                    mMapType = MyLocationStyle.LOCATION_TYPE_LOCATE;
                    addCircle(mLatLng, mAccuracy);//添加定位精度圆
                    addMarker(mLatLng);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    mFirstLocation = false;
                }

                @Override
                public void onCancel() {

                }
            });
        } else {
            //BottomSheet顶上显示,地图缩小显示
            mCircle.setCenter(mLatLng);
            mCircle.setRadius(mAccuracy);
            mLocMarker.setPosition(mLatLng);
            if (mMoveToCenter) {
                if(mMapMode.equals(MapMode.AIMLESS)){
                    mAMap.animateCamera(CameraUpdateFactory.newLatLng(mLatLng));
                }else{
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, mZoomLevel));
                }
            }

        }


    }

    /**
     * 激活定位
     *
     * @param listener
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mLocationListener = listener;
        LogUtil.d(TAG, "activate: mLocationListener = " + mLocationListener + "");
        //设置定位回调监听
        if (mLocationClient == null) {
            try {
                mLocationClient = new AMapLocationClient(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationOption.setInterval(2000);//定位时间间隔，默认2000ms
            mLocationOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
            mLocationOption.setLocationCacheEnable(true);//开启定位缓存
            mLocationClient.setLocationOption(mLocationOption);
//            mLocationClient.startLocation();
            if (null != mLocationClient) {
                mLocationClient.setLocationOption(mLocationOption);
                mLocationClient.startLocation();
                //运行时权限
                /*if (PermissionUtil.checkPermissions(this)) {
                    mLocationClient.startLocation();
                } else {
                    //未授予权限，动态申请
                    PermissionUtil.initPermissions(this, REQ_CODE_INIT);
                }*/
            }
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mLocationListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocMarker = null;
        mLocationClient = null;
    }

    /**
     * 设置地图类型
     */
    private void setLocationStyle() {
        // 自定义系统定位蓝点
        if (null == mLocationStyle) {
            mLocationStyle = new MyLocationStyle();
            mLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
            mLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明
        }
        //定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
        mAMap.setMyLocationStyle(mLocationStyle.myLocationType(mMapType));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == REQ_CODE_INIT && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationClient.startLocation();
        }*/
    }


    /**
     * 地图手势事件回调：单指双击
     *
     * @param v
     * @param v1
     */
    @Override
    public void onDoubleTap(float v, float v1) {
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mCurrentGpsState = STATE_UNLOCKED;
        mGpsView.setGpsState(mCurrentGpsState);
        setLocationStyle();
        resetLocationMarker();
        mMoveToCenter = false;
    }

    /**
     * 地体手势事件回调：单指单击
     *
     * @param v
     * @param v1
     */
    @Override
    public void onSingleTap(float v, float v1) {

    }

    /**
     * 地体手势事件回调：单指惯性滑动
     *
     * @param v
     * @param v1
     */
    @Override
    public void onFling(float v, float v1) {
        LogUtil.d(TAG, "onFling,x=" + v + ",y=" + v1);
    }

    /**
     * 地体手势事件回调：单指滑动
     *
     * @param v
     * @param v1
     */
    @Override
    public void onScroll(float v, float v1) {
        if(!mMapMode.equals(MapMode.AIMLESS)){
            mMoveToCenter = false;
        }else{
            mMoveToCenter =true;
        }

        //避免重复调用闪屏，当手指up才重置为false
        /*if (!onScrolling) {
            onScrolling = true;
            LogUtil.d(TAG, "onScroll,x=" + v + ",y=" + v1);
            //旋转不移动到中心点
            mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
            mCurrentGpsState = STATE_UNLOCKED;
            //当前没有正在定位才能修改状态
            if (!mFirstLocation) {
                mGpsView.setGpsState(mCurrentGpsState);
            }
            mMoveToCenter = false;
            setLocationStyle();
            resetLocationMarker();
            if(mClickPoiLatLng !=null){
                mClickPoiLatLng=null;
            }
        }*/
    }

    /**
     * 地体手势事件回调：长按
     *
     * @param v
     * @param v1
     */
    @Override
    public void onLongPress(float v, float v1) {

    }

    /**
     * 地体手势事件回调：单指按下
     *
     * @param v
     * @param v1
     */
    @Override
    public void onDown(float v, float v1) {
        LogUtil.d(TAG, "onDown");
    }

    /**
     * 地体手势事件回调：单指抬起
     *
     * @param v
     * @param v1
     */
    @Override
    public void onUp(float v, float v1) {
        LogUtil.d(TAG, "onUp");
        onScrolling = false;
    }

    /**
     * 地体手势事件回调：地图稳定下来会回到此接口
     */
    @Override
    public void onMapStable() {

    }


    @Override
    public void onGPSClick() {
        if(!isGpsOpen()){
            showToast(getString(R.string.please_open_gps));
            return;
        }
        CameraUpdate cameraUpdate = null;
        mMoveToCenter = true;
        isPoiClick = false;
        //修改定位图标状态
        switch (mCurrentGpsState) {
            case STATE_LOCKED:
                mZoomLevel = 18;
                mAnimDuartion = 500;
                mCurrentGpsState = STATE_ROTATE;
//                setLocationStyle(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
                mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, mZoomLevel, 30, 0));
                break;
            case STATE_UNLOCKED:
            case STATE_ROTATE:
                mZoomLevel = 16;
                mAnimDuartion = 500;
                mCurrentGpsState = STATE_LOCKED;
                mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, mZoomLevel, 0, 0));
                break;
        }
        //显示底部POI详情
        if (mBottomSheet.getVisibility() == View.GONE || mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showPoiDetail("我的位置", String.format("在%s附近", mPoiName));
            moveGspButtonAbove();
        }else{
            mTvLocTitle.setText("我的位置");
            mTvLocation.setText(String.format("在%s附近", mPoiName));
        }

        mAMap.setMyLocationEnabled(true);
        LogUtil.d(TAG, "onGPSClick:mCurrentGpsState=" + mCurrentGpsState + ",mMapType=" + mMapType);
        //改变定位图标状态
        mGpsView.setGpsState(mCurrentGpsState);
        //执行地图动效
        mAMap.animateCamera(cameraUpdate, mAnimDuartion, new AMap.CancelableCallback() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onCancel() {

            }
        });
        setLocationStyle();
        resetLocationMarker();
    }

    /**
     * 根据当前地图状态重置定位蓝点
     */
    private void resetLocationMarker() {
        mAMap.clear();
        mLocMarker = null;
        if (mGpsView.getGpsState() == GPSView.STATE_ROTATE) {
            //ROTATE模式不需要方向传感器
            //mSensorHelper.unRegisterSensorListener();
            addRotateMarker(mLatLng);
        } else {
            //mSensorHelper.registerSensorListener();
            addMarker(mLatLng);
            if (null != mLocMarker) {
                mSensorHelper.setCurrentMarker(mLocMarker);
            }
        }

        addCircle(mLatLng, mAccuracy);
    }

    @Override
    public void onNearbySearchClick() {
        //Toast.makeText(this, "点击附近搜索", Toast.LENGTH_SHORT).show();
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        if (null != mLocationClient) {
            LogUtil.d(TAG, "stopLocation");
            mLocationClient.stopLocation();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        LogUtil.d(TAG, "onResume");
        mMapView.onResume();
        if (null == mSensorHelper) {
            mAMap.clear();
            mSensorHelper = new SensorEventHelper(this);
            //重新注册
            if (mSensorHelper != null) {
                mSensorHelper.registerSensorListener();
                setUpMap();
            }
        }
        if(mLocationOption != null && mLocationClient != null){
            mLocationOption.setInterval(2000);//定位时间间隔，默认2000ms
            mLocationClient.setLocationOption(mLocationOption);
            mAMap.setMyLocationEnabled(true);
        }
        //registerWechatBroadcast();
    }

    /**
     * 注册微信广播
     */
    /*private void registerWechatBroadcast() {
        //建议动态监听微信启动广播进行注册到微信
        IntentFilter filter = new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP);
        // 将该app注册到微信
        mWechatBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // 将该app注册到微信
                api.registerApp(Constants.APP_ID);
            }
        };
        registerReceiver(mWechatBroadcast, filter);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause");
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        mLocationOption.setInterval(20000);//定位时间间隔，默认2000ms
        mLocationClient.setLocationOption(mLocationOption);

        //mWakeLock.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterWechatBroadcast();
    }

    /**
     * 反注册微信广播
     */
  /*  private void unregisterWechatBroadcast() {
        if(mWechatBroadcast != null){
            unregisterReceiver(mWechatBroadcast);
        }
        if(mWechatBroadcast != null){
            mWechatBroadcast = null;
        }

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mFirstLocation = true;
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        deactivate();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
        if (mLocMarker != null) {
            mLocMarker.destroy();
        }

        if (mAMapNavi!=null){
            mAMapNavi.stopAimlessMode();
            mAMapNavi.destroy();
        }

        //mWakeLock.release();

        // leakcanary检测

    }


    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = mAMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        /*if (mLocMarker != null) {
            return;
        }*/
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_locked)));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        mLocMarker = mAMap.addMarker(markerOptions);
    }

    private void addRotateMarker(LatLng latlng) {
       /* if (mLocMarker != null) {
            return;
        }*/
        MarkerOptions markerOptions = new MarkerOptions();
        //3D效果
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_3d)));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        mLocMarker = mAMap.addMarker(markerOptions);
    }

    /**
     * 跳转用户登录
     */
    private void userLogin() {
        startActivity(new Intent(MapActivity.this, ControlActivity.class));
    }


    @Override
    public void onTrafficChanged(boolean selected) {
        mAMap.setTrafficEnabled(selected);
    }

    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }

    @Override
    public void onClick(View v) {
        if(v == null){
            return;
        }
        // 点击关闭POI detail
        if (v == mPoiColseView) {
            mBehavior.setHideable(true);
            resetGpsButtonPosition();
            hidePoiDetail();

            if (!onScrolling) {
                onScrolling = true;
                //LogUtil.d(TAG, "onScroll,x=" + v + ",y=" + v1);
                //旋转不移动到中心点
                mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                mCurrentGpsState = STATE_UNLOCKED;
                //当前没有正在定位才能修改状态
                if (!mFirstLocation) {
                    mGpsView.setGpsState(mCurrentGpsState);
                }
                mMoveToCenter = false;
                setLocationStyle();
                resetLocationMarker();
                if(mClickPoiLatLng !=null){
                    mClickPoiLatLng=null;
                }
            }

            return;
        }

        // 分享
        if(v == mShareContainer){
            if(mAmapLocation != null && mLatLng != null){
                if(isPoiClick){
                    // 分享点击poi的位置
                    shareLocation(mPoiName, mClickPoiLatLng.latitude, mClickPoiLatLng.longitude);
                }else{
                    // 分享当前定位位置
                    shareLocation(mPoiName, mLatLng.latitude, mLatLng.longitude);
                }
            }
            return;
        }

        // 收藏
        if(v == mFavoriteBtn){
            if(isPoiFavorite){
                SPUtil.delFavoriteAddress(mPoiFavorite);
                mIvPoiFavorite.setSelected(false);
                showToast("已取消收藏");
            }else{
                SPUtil.saveFavoriteAddress(mPoiFavorite);
                mIvPoiFavorite.setSelected(true);
                showToast("已收藏");
            }
            return;
        }

        // 头部返回ImageButton
        if(v == mImgBtnBack){
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        // 路线按钮点击处理
        if(v == mRouteView){
            if(mLatLng == null){
                showToast(getString(R.string.location_failed_hold_on));
                return;
            }
            if(mClickPoiLatLng == null){
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), new AmapNaviParams(new Poi("我的位置", mLatLng, ""), null, null, AmapNaviType.DRIVER), MapNaviListner.getInstance());
            }else{
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), new AmapNaviParams(new Poi("我的位置", mLatLng, ""), null, new Poi(mPoiName, mClickPoiLatLng, ""), AmapNaviType.DRIVER), MapNaviListner.getInstance());
            }
        }

        // 路线,进入导航页面
        if(v == mTvRoute){
            if(mLatLng == null){
                showToast(getString(R.string.location_failed_hold_on));
                return;
            }
            if(mClickPoiLatLng == null){
                showToast(getString(R.string.please_select_dest_loc));
                return;
            }
            //Intent intent = new Intent(this, WalkRouteNaviActivity.class);
//            Intent intent = new Intent(this, DriveRouteNaviActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("startLatLng", mLatLng);
//            bundle.putParcelable("stopLatLng", mClickPoiLatLng);
//
//            intent.putExtra("params", bundle);
//            startActivity(intent);
            //进入路径选择界面
//            Intent intent = new Intent(this, RouteSelectActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("startLatLng", mLatLng);
//            bundle.putParcelable("stopLatLng", mClickPoiLatLng);
//
//            intent.putExtra("params", bundle);
//            startActivity(intent);

            //导航组件
            AmapNaviParams params = new AmapNaviParams(new Poi("我的位置", mLatLng, ""), null, new Poi(mPoiName, mClickPoiLatLng, ""), AmapNaviType.DRIVER);
            params.setUseInnerVoice(true);
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, MapNaviListner.getInstance());

            return;
        }

        // 点击搜索左侧返回箭头
        if(v == mIvLeftSearch){
            hideSearchTipView();
            showMapView();
            return;
        }
        // 回家
        if(v == mHome){
            String mHomeAddress = SPUtil.getHomeOfficeAddress(SPUtil.AddressType.HOME);
            if(mHomeAddress != null && !mHomeAddress.equals("")){
                String[] mHomeAddrs=mHomeAddress.split(",");
                //导航组件
                AmapNaviParams params = new AmapNaviParams(
                        new Poi("我的位置", mLatLng, ""),
                        null,
                        new Poi(mHomeAddrs[0], new LatLng(Double.parseDouble(mHomeAddrs[1]),Double.parseDouble(mHomeAddrs[2])), ""),
                        AmapNaviType.DRIVER);
                params.setUseInnerVoice(true);
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, MapNaviListner.getInstance());
            }else{
                showToast("请进入设置菜单设置家的地址");
            }


            return;
        }
        // 去公司
        if(v == mOffice){
            String mOfficeAddress = SPUtil.getHomeOfficeAddress(SPUtil.AddressType.OFFICE);
            if(mOfficeAddress != null && !mOfficeAddress.equals("")){
                String[] mOfficeAddrs=mOfficeAddress.split(",");
                //导航组件
                AmapNaviParams params = new AmapNaviParams(
                        new Poi("我的位置", mLatLng, ""),
                        null,
                        new Poi(mOfficeAddrs[0], new LatLng(Double.parseDouble(mOfficeAddrs[1]),Double.parseDouble(mOfficeAddrs[2])), ""),
                        AmapNaviType.DRIVER);
                params.setUseInnerVoice(true);
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, MapNaviListner.getInstance());
            }else{
                showToast("请进入设置菜单设置公司的地址");
            }
            return;
        }

        //搜索界面中的搜索历史和收藏
        if(v==mHistory){
            if(!isHistorySelected){
                mIvHistory.setSelected(true);
                isHistorySelected = true;
                mIvFavorite.setSelected(false);
                isFavoriteSelected = false;
            }
            hideFavoriteView();

            //显示搜索历史记录
            mHistoryList=SPUtil.getSearchHistory();
            if(mHistoryList.size()>0){
                mSearchHistoryData.clear();
                mSearchHistoryData.addAll(mHistoryList);
                // 刷新RecycleView
                mSearchHistoryAdapter.notifyDataSetChanged();
                showSearchHistoryView();
            }

            return;
        }
        if(v==mFavorite){
            if(!isFavoriteSelected){
                mIvFavorite.setSelected(true);
                isFavoriteSelected = true;
                mIvHistory.setSelected(false);
                isHistorySelected = false;
            }

            //显示收藏记录
            mFavoriteList=SPUtil.getFavoriteAddress();
            if(mFavoriteList.size()>0){
                mFavoriteData.clear();
                mFavoriteData.addAll(mFavoriteList);
                // 刷新RecycleView
                mFavoriteAdapter.notifyDataSetChanged();
            }else{
                showToast("暂无收藏地址");
            }
            showFavoriteView();

            return;
        }

        //电子眼按钮
        if(v == mEleEyeView){
            //mWakeLock.acquire();
            mMapMode=MapMode.AIMLESS;
            hideHeadBottomView();
            mAMapNavi.startAimlessMode(AimLessMode.CAMERA_AND_SPECIALROAD_DETECTED);
            mAMapNavi.setUseInnerVoice(true,false);
            mAMapNavi.playTTS("进入巡航模式，为您持续监测电子眼信息",false);
            mAMap.removeOnPOIClickListener(this);
            if(mLatLng!=null){
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 18));
            }

            if(SPUtil.getAimlessNorthView()){
                mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, 18, 0, 0));
                mAMap.animateCamera(cameraUpdate, 500, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                setLocationStyle();
            }

            mMoveToCenter = true;
            return;
        }

        //退出电子眼
        if(v == mAimlessExit){
            //mWakeLock.release();
            mMapMode=MapMode.NORMAL;
            showHeadBottomView();
            mAMapNavi.stopAimlessMode();
            mAMapNavi.playTTS("退出巡航模式",false);
            mAMap.addOnPOIClickListener(this);
            if(mLatLng!=null){
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
            }

            mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, 16, 0, 0));
            mAMap.animateCamera(cameraUpdate, 500, new AMap.CancelableCallback() {
                @Override
                public void onFinish() {
                }

                @Override
                public void onCancel() {

                }
            });
            setLocationStyle();
            mMoveToCenter = false;

            return;
        }

        //电子眼设置
        if(v == mAimlessSetting){
            startActivity(new Intent(MapActivity.this, SettingActivity.class));
            return;
        }

        if(v == mGroupTeamView){

            return;
        }

    }

    /**
     * 分享网页url到微信
     */
    private void shareToWechat(String url, String title) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        // msg.description = description;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = WechatUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = WechatApi.buildTransaction("webpage");
        req.message = msg;
        req.scene = WechatApi.mTargetScene;
        api.sendReq(req);
    }

    /**
     * 隐藏底部POI详情
     */
    @Override
    public void hidePoiDetail() {
        mBottomSheet.setVisibility(View.GONE);
        //底部：打车、路线...
        mPoiDetailTaxi.setVisibility(View.GONE);
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //gsp控件回退到原来位置、并显示底部其他控件
        mRouteView.setVisibility(View.VISIBLE);
//        mFrequentView.setVisibility(View.VISIBLE);
        mNearbySearcyView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示底部POI详情
     * @param locTitle 定位标题,比如当前所在位置名称
     * @param locInfo 定位信息,比如当前在什么附近/距离当前位置多少米
     */
    @Override
    public void showPoiDetail(String locTitle, String locInfo) {
        mGpsView.setVisibility(View.VISIBLE);
        mBottomSheet.setVisibility(View.VISIBLE);
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mRouteView.setVisibility(View.GONE);
//        mFrequentView.setVisibility(View.GONE);
        mNearbySearcyView.setVisibility(View.GONE);
        //底部：打车、路线...
        mPoiDetailTaxi.setVisibility(View.VISIBLE);
        //我的位置
        mTvLocTitle.setText(locTitle);
        mTvLocation.setText(locInfo);
//        showPoiNameText();

        //int poiTaxiHeight = mPoiDetailTaxi.getMeasuredHeight(); //为0
        int poiTaxiHeight = getResources().getDimensionPixelSize(R.dimen.setting_item_large_height);

        mBehavior.setHideable(true);
        mBehavior.setPeekHeight(mMinPeekHeight + poiTaxiHeight);

    }

    /**
     * 显示当前所在poi点信息
     */
    private void showPoiNameText(String locInfo) {
        mTvLocation.setText(locInfo);
    }

    /**
     * 将GpsButton移动到poi detail上面
     */
    private void moveGspButtonAbove() {

        mBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mGpsView.isAbovePoiDetail()) {
                    //已经在上面，不需要重复调用
                    return;
                }
                LogUtil.d(TAG, "moveGspButtonAbove");
                if (moveY == 0) {
                    //计算Y轴方向移动距离
                    moveY = mGspContainer.getTop() - mBottomSheet.getTop() + mGspContainer.getMeasuredHeight() + mPadding;
                    mBottomSheet.getLocationInWindow(mBottomSheetLoc);
                }
                if (moveY > 0) {
                    mGspContainer.setTranslationY(-moveY);
                    mGpsView.setAbovePoiDetail(true);
                }
            }
        });


    }

    /**
     * 将GpsButton移动到原来位置
     */
    private void resetGpsButtonPosition() {

        mBottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (!mGpsView.isAbovePoiDetail()) {
                    //已经在下面，不需要重复调用
                    return;
                }
                //回到原来位置
                mGspContainer.setTranslationY(0);
                mGpsView.setAbovePoiDetail(false);
                LogUtil.d(TAG, "resetGpsButtonPosition");
            }
        });

    }


    @Override
    public void showBackToMapState() {
        //显示:查看详情
        mPoiDetailTaxi.setPoiDetailState(PoiDetailBottomView.STATE_MAP);
        //BottomSheet展开:这里不建议修改BottomSheet状态，backToMap方法可能在BottomSheet状态回调中调用，避免互相调用死循环
    }

    @Override
    public void showPoiDetailState() {
        mPoiDetailTaxi.setPoiDetailState(PoiDetailBottomView.STATE_DETAIL);
    }

    @Override
    public void minMapView() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mMapView.getLayoutParams();
        //避免重复设置LayoutParams
        if (lp.bottomMargin == mMaxPeekHeight) {
            return;
        }
        lp.bottomMargin = mMaxPeekHeight;
        mMapView.setLayoutParams(lp);


    }

    @Override
    public void maxMapView() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mMapView.getLayoutParams();
        //避免重复设置LayoutParams
        if (lp.bottomMargin == 0) {
            return;
        }
        lp.bottomMargin = 0;
        mMapView.setLayoutParams(lp);
    }

    @Override
    public void onDetailClick() {
        int state = mPoiDetailTaxi.getPoiDetailState();
        switch (state) {
            case PoiDetailBottomView.STATE_DETAIL:
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //minMapView();
                break;
            case PoiDetailBottomView.STATE_MAP:
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


                break;
        }
    }

    @Override
    public void onPoiDetailCollapsed() {
        //BottomSheet折叠：显示头部搜索、隐藏反馈、显示右边侧边栏
        mPoiColseView.setVisibility(View.VISIBLE);
        mMapHeaderView.setVisibility(View.VISIBLE);
        mFeedbackContainer.setVisibility(View.GONE);
        mSupendPartitionView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPoiDetailExpanded() {
        //BottomSheet展开：隐藏头部搜索、显示反馈、隐藏右边侧边栏
        mMapHeaderView.setVisibility(View.GONE);
        mFeedbackContainer.setVisibility(View.VISIBLE);
        mSupendPartitionView.setVisibility(View.GONE);
    }

    /**
     * 地图平滑上移，重置新的marker
     */
    private void slideUpMarker() {
        mAMap.clear();
        mLocMarker = null;
        addRotateMarker(mLatLng);
        if (null != mLocMarker) {
            mSensorHelper.setCurrentMarker(mLocMarker);
        }
        addCircle(mLatLng, mAccuracy);

//        aMap.clear();
//        mLocMarker = null;
//        if (mGpsView.getGpsState() == GPSView.STATE_ROTATE) {
//            //ROTATE模式不需要方向传感器
//            //mSensorHelper.unRegisterSensorListener();
//
//        } else {
//            //mSensorHelper.registerSensorListener();
//            addMarker(mLatLng);
//            if (null != mLocMarker) {
//                mSensorHelper.setCurrentMarker(mLocMarker);
//            }
//        }
//
//        addCircle(mLatLng, mAccuracy);
    }

    @Override
    public void smoothSlideUpMap() {
        switch (mGpsView.getGpsState()) {
            case GPSView.STATE_ROTATE:
                if(!isPoiClick){
                    mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                }
                break;
            case GPSView.STATE_UNLOCKED:
            case GPSView.STATE_LOCKED:
                // 当前没D有操作poi点击
                if(!isPoiClick){
                    mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                }
                break;
        }
        setLocationStyle();
        //禁用手势操作
        mAMap.getUiSettings().setAllGesturesEnabled(false);
        if(!isPoiClick){
            mMoveToCenter = true;
        }else{
            mMoveToCenter = false;
        }
        ViewGroup.LayoutParams lp = mMapView.getLayoutParams();
        lp.height = mScreenHeight * 2 / 5;
        mMapView.setLayoutParams(lp);
    }

    @Override
    public void smoothSlideDownMap() {
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mMoveToCenter = false;
        slideDown = true;
        ViewGroup.LayoutParams lp = mMapView.getLayoutParams();
        lp.height = mScreenHeight;
        mMapView.setLayoutParams(lp);
        //启用手势操作
        mAMap.getUiSettings().setAllGesturesEnabled(true);
        switch (mGpsView.getGpsState()) {
            case GPSView.STATE_ROTATE:
                mMapType = MyLocationStyle.LOCATION_TYPE_MAP_ROTATE;
                break;
            case GPSView.STATE_UNLOCKED:
            case GPSView.STATE_LOCKED:
                mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
                break;
        }
        setLocationStyle();
//        resetLocationMarker();
        mMoveToCenter = false;
    }

    /**
     * 高德地图位置转短串分享
     * @param snippet 位置名称
     * @param lat 维度
     * @param lng 经度
     */
    private void shareLocation(String snippet, double lat, double lng) {
        if(TextUtils.isEmpty(snippet)){
            return;
        }
        // addTestLocationMarker(snippet);
        LatLonSharePoint point = new LatLonSharePoint(lat,
                lng, snippet);
        // showProgressDialog();
        mShareSearch.searchLocationShareUrlAsyn(point);
    }

    /**
     * 高德地图回调
     */
    @Override
    public void onCallTaxiClick() {

    }

    @Override
    public void onRouteClick() {

    }

    @Override
    public void onPoiShareUrlSearched(String s, int i) {

    }

    /**
     * 高德地图分享位置短串回调
     * @param url 网页url
     * @param errorCode 错误码
     */
    @Override
    public void onLocationShareUrlSearched(String url, int errorCode) {
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            shareToWechat(url, mPoiName);
        } else {
            showToast(String.format("分享失败:%s", errorCode));
        }
    }

    @Override
    public void onNaviShareUrlSearched(String s, int i) {

    }

    @Override
    public void onBusRouteShareUrlSearched(String s, int i) {

    }

    @Override
    public void onWalkRouteShareUrlSearched(String s, int i) {

    }

    @Override
    public void onDrivingRouteShareUrlSearched(String s, int i) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 处理返回键
        if(keyCode == KeyEvent.KEYCODE_BACK){
            MapMode mode = mMapMode;
            if(mode == MapMode.NORMAL){
                // BottomSheet展开,折叠BottomSheet不关闭Activity
                if(mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    return true;
                }else{
                    return super.onKeyDown(keyCode, event);
                }
            }else if(mode == MapMode.SEARCH){
                hideSearchTipView();
                showMapView();
                mMapMode = MapMode.NORMAL;

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 地图POI点击
     * @param poi
     */
    @Override
    public void onPOIClick(Poi poi) {
        LogUtil.d(TAG, "onPOIClick,poi="+poi);
        if(poi == null || poi.getCoordinate() == null || TextUtils.isEmpty(poi.getName())){
            return;
        }
        // 当前点击坐标
        mClickPoiLatLng = poi.getCoordinate();
        // 当前正在处理poi点击
        isPoiClick = true;
        addPOIMarderAndShowDetail(poi.getCoordinate(), poi.getName());

    }

    /**
     * 添加POImarker
     */
    private void addPOIMarderAndShowDetail(LatLng latLng, String poiName) {
        animMap(latLng);
        mMapType = MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER;
        mCurrentGpsState = STATE_UNLOCKED;
        //当前没有正在定位才能修改状态
        if (!mFirstLocation) {
            mGpsView.setGpsState(mCurrentGpsState);
        }
        mMoveToCenter = false;
        // 添加marker标记
        addPOIMarker(latLng);
        showClickPoiDetail(latLng, poiName);
    }

    /**
     * 显示poi点击底部BottomSheet
     */
    private void showClickPoiDetail(LatLng latLng, String poiName) {
        mPoiName = poiName;
        mTvLocTitle.setText(poiName);
        String distanceStr = MyAMapUtils.calculateDistanceStr(mLatLng, latLng);
        if (mBottomSheet.getVisibility() == View.GONE || mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showPoiDetail(poiName, String.format("距离您%s", distanceStr));
            moveGspButtonAbove();
        }else{
            mTvLocTitle.setText(poiName);
            mTvLocation.setText(String.format("距离您%s", distanceStr));
        }

        mPoiFavorite = poiName+","+latLng.latitude+","+latLng.longitude;
        if(SPUtil.isFavoriteAddress(mPoiFavorite)){
            mIvPoiFavorite.setSelected(true);
            isPoiFavorite = true;
        }else{
            mIvPoiFavorite.setSelected(false);
            isPoiFavorite = false;
        }
    }

    private void addPOIMarker(LatLng latLng) {
        mAMap.clear();
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(latLng);
        markOptiopns.icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_mark));
        mAMap.addMarker(markOptiopns);
    }

    /**
     * 移动地图中心点到指定位置
     * @param latLng
     */
    private void animMap(LatLng latLng){
        if(latLng != null){
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel));
        }
    }

    private void hideHeadBottomView(){
        mMapHeaderView.setVisibility(View.GONE);
        mSupendPartitionView.setVisibility(View.GONE);
        mNearbySearcyView.setVisibility(View.GONE);
        mGpsView.setVisibility(View.GONE);
        mRouteView.setVisibility(View.GONE);

        mBehavior.setHideable(true);
        resetGpsButtonPosition();
        mBottomSheet.setVisibility(View.GONE);
        mPoiDetailTaxi.setVisibility(View.GONE);
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mAimlessBarView.setVisibility(View.VISIBLE);
        mCircleProgress.setVisibility(View.VISIBLE);


    }

    private void showHeadBottomView(){
        mMapHeaderView.setVisibility(View.VISIBLE);
        mSupendPartitionView.setVisibility(View.VISIBLE);
        mNearbySearcyView.setVisibility(View.VISIBLE);
        mGpsView.setVisibility(View.VISIBLE);
        mRouteView.setVisibility(View.VISIBLE);
        mAimlessBarView.setVisibility(View.GONE);
        mCircleProgress.setVisibility(View.GONE);

    }

    /**
     * 隐藏地图图层
     */
    private void hideMapView(){
        mMapView.setVisibility(View.GONE);
        mMapHeaderView.setVisibility(View.GONE);
        mSupendPartitionView.setVisibility(View.GONE);
    }

    /**
     * 显示地图图层
     */
    private void showMapView(){
        mMapView.setVisibility(View.VISIBLE);
        mMapHeaderView.setVisibility(View.VISIBLE);
        mSupendPartitionView.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏搜索提示布局
     */
    private void hideSearchTipView(){
        InputMethodUtils.hideInput(this);
        mLLSearchContainer.setVisibility(View.GONE);
        mEtSearchTip.setVisibility(View.VISIBLE);
        mEtSearchTip.setFocusable(true);
        mEtSearchTip.setFocusableInTouchMode(true);
        mSearchData.clear();
        mSearchAdapter.notifyDataSetChanged();
        mEtSearchTip.setText("");
    }

    /**
     * 显示搜索提示布局
     */
    private void showSearchTipView(){
        mLLSearchContainer.setVisibility(View.VISIBLE);
        InputMethodUtils.showInput(this, mEtSearchTip);
    }

    /**
     * 隐藏搜索历史和收藏布局
     */
    private void hideSearchHistoryView(){
        //mLLSearchHistoryContainer.setVisibility(View.GONE);
        mSearchHistoryData.clear();
        mSearchHistoryAdapter.notifyDataSetChanged();

        mRecycleViewSearchHistory.setVisibility(View.GONE);
        mRecycleViewFavorite.setVisibility(View.GONE);
        mRecycleViewSearch.setVisibility(View.VISIBLE);
    }

    /**
     * 显示搜索历史布局
     */
    private void showSearchHistoryView(){
        //mLLSearchHistoryContainer.setVisibility(View.VISIBLE);
        mRecycleViewSearchHistory.setVisibility(View.VISIBLE);
        mRecycleViewFavorite.setVisibility(View.VISIBLE);
        mRecycleViewSearch.setVisibility(View.GONE);
    }

    /**
     * 隐藏收藏布局
     */
    private void hideFavoriteView(){
        //mLLSearchHistoryContainer.setVisibility(View.GONE);
        mFavoriteData.clear();
        mFavoriteAdapter.notifyDataSetChanged();

        mRecycleViewFavorite.setVisibility(View.GONE);
        mRecycleViewSearchHistory.setVisibility(View.VISIBLE);
    }

    /**
     * 显示收藏布局
     */
    private void showFavoriteView(){
        //mLLSearchHistoryContainer.setVisibility(View.VISIBLE);
        mRecycleViewFavorite.setVisibility(View.VISIBLE);
        mRecycleViewSearchHistory.setVisibility(View.GONE);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * EditText输入内容后回调
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        if(s == null || TextUtils.isEmpty(s.toString())){
            mSearchProgressBar.setVisibility(View.GONE);
            return;
        }
        hideSearchHistoryView();
        String content = s.toString();
        if(!TextUtils.isEmpty(content) && !TextUtils.isEmpty(mCity)){
            // 调用高德地图搜索提示api
            InputtipsQuery inputquery = new InputtipsQuery(content, mCity);
            inputquery.setCityLimit(true);
            Inputtips inputTips = new Inputtips(this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
            mSearchProgressBar.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 高德地图搜索提示回调
     * @param list
     * @param i
     */
    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        mSearchProgressBar.setVisibility(View.GONE);
        if(list == null || list.size() == 0){
            return;
        }
        mSearchData.clear();
        mSearchData.addAll(list);
        // 刷新RecycleView
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUserClick() {
        userLogin();
    }

    @Override
    public void onSearchClick() {
        // 显示搜索layout,隐藏地图图层,并设置当前地图操作模式
        showSearchTipView();
        hideMapView();
        mMapMode = MapMode.SEARCH;

        //显示搜索历史记录
        mHistoryList=SPUtil.getSearchHistory();
        if(mHistoryList.size()>0){
            mSearchHistoryData.clear();
            mSearchHistoryData.addAll(mHistoryList);
            // 刷新RecycleView
            mSearchHistoryAdapter.notifyDataSetChanged();
            showSearchHistoryView();
        }
    }

    @Override
    public void onVoiceClick() {

    }

    @Override
    public void onSettingClick() {
        startActivity(new Intent(MapActivity.this, SettingActivity.class));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemClick(View v, int position) {

        if(mSearchData != null && mSearchData.size() > 0){
            Tip tip = mSearchData.get(position);
            if(tip == null){
                return;
            }
            hideSearchTipView();
            showMapView();
            mMoveToCenter = false;
            isPoiClick = true;
            LatLonPoint point = tip.getPoint();
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            mClickPoiLatLng=latLng;
            addPOIMarderAndShowDetail(latLng, tip.getName());
            showClickPoiDetail(latLng, tip.getName());

            //存储搜索记录
            String mClickPoiString=tip.getName()+","+tip.getPoint().getLatitude()+","+tip.getPoint().getLongitude()+","+tip.getAddress();
            SPUtil.saveSearchHistory(mClickPoiString);
        }
    }

    @Override
    public void onHistoryItemClick(View v, int position) {
        if(mSearchHistoryData != null && mSearchHistoryData.size() > 0){
            String historyAddressStr = mSearchHistoryData.get(position);
            if(historyAddressStr == null){
                return;
            }
            hideSearchTipView();
            showMapView();
            mMoveToCenter = false;
            isPoiClick = true;
            String[] historyAddressStrs=historyAddressStr.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(historyAddressStrs[1]), Double.parseDouble(historyAddressStrs[2]));
            mClickPoiLatLng=latLng;
            addPOIMarderAndShowDetail(latLng, historyAddressStrs[0]);
            showClickPoiDetail(latLng, historyAddressStrs[0]);

            //存储搜索记录
            SPUtil.saveSearchHistory(historyAddressStr);
        }
    }

    @Override
    public void onFavoriteItemClick(View v, int position) {
        if(mFavoriteData != null && mFavoriteData.size() > 0){
            String favoriteAddressStr = mFavoriteData.get(position);
            if(favoriteAddressStr == null){
                return;
            }
            hideSearchTipView();
            showMapView();
            mMoveToCenter = false;
            isPoiClick = true;
            String[] favoriteAddressStrs=favoriteAddressStr.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(favoriteAddressStrs[1]), Double.parseDouble(favoriteAddressStrs[2]));
            mClickPoiLatLng=latLng;
            addPOIMarderAndShowDetail(latLng, favoriteAddressStrs[0]);
            showClickPoiDetail(latLng, favoriteAddressStrs[0]);

        }
    }

    @Override
    public void onAimlessBarClick() {

    }

    @Override
    public void onUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        showToast("onUpdateTrafficFacility");
    }

    @Override
    public void onUpdateAimlessModeElecCameraInfo(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        mAMap.clear();
        resetLocationMarker();
        for(AMapNaviTrafficFacilityInfo info : aMapNaviTrafficFacilityInfos){
            switch (info.getBroadcastType()){
                case 4:
                case 5:
                case 28:
                case 29:
                case 92:
                case 93:
                case 94:
                    addCameraMarker(new LatLng(info.getCoorY(),info.getCoorX()));
                    break;
                default:
                    break;
            }

        }

    }

    private void addCameraMarker(LatLng latlng) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.icon_camera)));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        mAMap.addMarker(markerOptions);
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        showToast("updateAimlessModeCongestionInfo");
    }


    /**
     * 搜索Adapter
     */
    public static class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> implements View.OnClickListener {

        private List<Tip> mData;
        private OnItemClickListener mListener;

        public SearchAdapter(List<Tip> data) {
            this.mData = data;
        }

        /**
         * 设置RecycleView条目点击
         * @param listener
         */
        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View itemView = ((LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.search_tip_recycle_item, viewGroup, false);
            itemView.setTag(position);
            itemView.setOnClickListener(this);
            return new SearchViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            Tip tip = mData.get(position);
            holder.tvSearchTitle.setText(tip.getName());
            holder.tvSearchLoc.setText(tip.getAddress());
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            if(mData != null && mData.size() > 0){
                return mData.size();
            }
            return 0;
        }

        @Override
        public void onClick(View v) {
            if(v != null && mListener != null){
                int postion = (int) v.getTag();
                mListener.onItemClick(v, postion);
            }
        }
    }


    /**
     * 搜索ViewHolder
     */
    private static class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView tvSearchTitle;
        TextView tvSearchLoc;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchTitle = itemView.findViewById(R.id.tv_search_title);
            tvSearchLoc = itemView.findViewById(R.id.tv_search_loc);
        }
    }

    /**
     * 搜索历史Adapter
     */
    private static class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryViewHolder> implements View.OnClickListener {

        private List<String> mData;
        private OnHistoryItemClickListener mListener;

        public SearchHistoryAdapter(List<String> data) {
            this.mData = data;
        }

        /**
         * 设置RecycleView条目点击
         * @param listener
         */
        public void setOnItemClickListener(OnHistoryItemClickListener listener){
            this.mListener = listener;
        }

        @NonNull
        @Override
        public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View itemView = ((LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.search_tip_recycle_item, viewGroup, false);
            itemView.setTag(position);
            itemView.setOnClickListener(this);
            return new SearchHistoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, int position) {
            String str = mData.get(position);
            String[] strs=str.split(",");
            if(strs.length==4){
                holder.tvSearchTitle.setText(strs[0]);
                holder.tvSearchLoc.setText(strs[3]);
            }else{
                holder.tvSearchTitle.setText(strs[0]);
                holder.tvSearchLoc.setText(strs[0]);
            }
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            if(mData != null && mData.size() > 0){
                return mData.size();
            }
            return 0;
        }

        @Override
        public void onClick(View v) {
            if(v != null && mListener != null){
                int postion = (int) v.getTag();
                mListener.onHistoryItemClick(v, postion);
            }
        }
    }


    /**
     * 搜索历史ViewHolder
     */
    private static class SearchHistoryViewHolder extends RecyclerView.ViewHolder{
        TextView tvSearchTitle;
        TextView tvSearchLoc;
        public SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchTitle = itemView.findViewById(R.id.tv_search_title);
            tvSearchLoc = itemView.findViewById(R.id.tv_search_loc);
        }
    }

    /**
     * 收藏Adapter
     */
    private static class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> implements View.OnClickListener {

        private List<String> mData;
        private OnFavoriteItemClickListener mListener;

        public FavoriteAdapter(List<String> data) {
            this.mData = data;
        }

        /**
         * 设置RecycleView条目点击
         * @param listener
         */
        public void setOnItemClickListener(OnFavoriteItemClickListener listener){
            this.mListener = listener;
        }

        @NonNull
        @Override
        public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View itemView = ((LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.search_tip_recycle_item, viewGroup, false);
            itemView.setTag(position);
            itemView.setOnClickListener(this);
            return new FavoriteViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
            String str = mData.get(position);
            String[] strs=str.split(",");
            if(strs.length==4){
                holder.tvSearchTitle.setText(strs[0]);
                holder.tvSearchLoc.setText(strs[3]);
            }else{
                holder.tvSearchTitle.setText(strs[0]);
                holder.tvSearchLoc.setText(strs[0]);
            }
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            if(mData != null && mData.size() > 0){
                return mData.size();
            }
            return 0;
        }

        @Override
        public void onClick(View v) {
            if(v != null && mListener != null){
                int postion = (int) v.getTag();
                mListener.onFavoriteItemClick(v, postion);
            }
        }
    }


    /**
     * 搜索历史ViewHolder
     */
    private static class FavoriteViewHolder extends RecyclerView.ViewHolder{
        TextView tvSearchTitle;
        TextView tvSearchLoc;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchTitle = itemView.findViewById(R.id.tv_search_title);
            tvSearchLoc = itemView.findViewById(R.id.tv_search_loc);
        }
    } 


    /**
     * 是否打开GPS
     * @return
     */
    private boolean isGpsOpen(){
        return mLocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
