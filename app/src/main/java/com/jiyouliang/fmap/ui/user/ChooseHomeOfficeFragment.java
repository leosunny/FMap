package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.jiyouliang.fmap.MapActivity;
import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseFragment;
import com.jiyouliang.fmap.util.InputMethodUtils;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.SPUtil;
import com.jiyouliang.fmap.view.widget.ButtonLoadingView;
import com.jiyouliang.fmap.view.widget.ClearEditText;
import com.jiyouliang.fmap.view.widget.OnItemClickListener;
import com.jiyouliang.fmap.view.widget.TopTitleView;

import java.util.ArrayList;
import java.util.List;


public class ChooseHomeOfficeFragment extends BaseFragment implements View.OnClickListener, TopTitleView.OnTopTitleViewClickListener, OnItemClickListener, Inputtips.InputtipsListener,TextWatcher, View.OnFocusChangeListener {

    private ClearEditText mEtHome;
    private ClearEditText mEtOffice;
    private ButtonLoadingView mBtnSave;
    private static final boolean DEBUGGING = true;
    private static final String TAG = "ChooseHomeOfficeFragment";
    private Context mContext;

    private OnFragmentInteractionListener mListener;
    private TopTitleView mTopTitleView;

    private RecyclerView mRecycleViewSearch;
    private MapActivity.SearchAdapter mSearchAdapter;
    private List<Tip> mSearchData = new ArrayList<>();

    private EditTextState mETState=EditTextState.HOME;
    private String mHomeAddress;
    private String mOfficeAddress;

    public ChooseHomeOfficeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserSendSmsFragment.
     */
    public static ChooseHomeOfficeFragment newInstance() {
        ChooseHomeOfficeFragment fragment = new ChooseHomeOfficeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_home_office, container, false);
        initView(rootView);
        initData();
        setListener();
        return rootView;
    }

    private void initView(View rootView) {
        mEtHome = (ClearEditText) rootView.findViewById(R.id.et_home);
        mEtOffice = (ClearEditText) rootView.findViewById(R.id.et_office);
        mBtnSave = (ButtonLoadingView) rootView.findViewById(R.id.btn_save);
        mBtnSave.setEnabled(false);
        mTopTitleView = (TopTitleView)rootView.findViewById(R.id.ttv);
        mRecycleViewSearch = (RecyclerView) rootView.findViewById(R.id.rv_home_office_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecycleViewSearch.setLayoutManager(layoutManager);
    }

    private void setListener() {
        mEtHome.addTextChangedListener(this);
        mEtHome.setOnFocusChangeListener(this);
        mEtHome.setOnClearEditClickListener(new ClearEditText.OnClearEditClickListener() {
            @Override
            public void onDelete() {
                //清空文字
                mEtHome.setText("");
            }
        });

        mEtOffice.addTextChangedListener(this);
        mEtOffice.setOnFocusChangeListener(this);
        mEtOffice.setOnClearEditClickListener(new ClearEditText.OnClearEditClickListener() {
            @Override
            public void onDelete() {
                //清空文字
                mEtOffice.setText("");
            }
        });

        mBtnSave.setOnClickListener(this);
        mTopTitleView.setOnTopTitleViewClickListener(this);

        mSearchAdapter.setOnItemClickListener(this);
    }

    private void initData() {
        mContext = getContext();

        // 搜索结果RecyclerView
        mSearchAdapter = new MapActivity.SearchAdapter(mSearchData);
        mRecycleViewSearch.setAdapter(mSearchAdapter);

        // 获取家和公司的地址
        mHomeAddress = SPUtil.getHomeOfficeAddress(SPUtil.AddressType.HOME);
        mOfficeAddress = SPUtil.getHomeOfficeAddress(SPUtil.AddressType.OFFICE);

        if(mHomeAddress != null){
            mEtHome.setText(mHomeAddress.split(",")[0]);
        }
        if(mOfficeAddress != null){
            mEtOffice.setText(mOfficeAddress.split(",")[0]);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                mSearchData.clear();
                mSearchAdapter.notifyDataSetChanged();
                mBtnSave.setEnabled(false);
                if(mEtHome.getText().toString().equals("")){
                    SPUtil.delHomeOfficeAddress(SPUtil.AddressType.HOME);
                }
                if(mEtOffice.getText().toString().equals("")){
                    SPUtil.delHomeOfficeAddress(SPUtil.AddressType.OFFICE);
                }
                mBtnSave.setEnabled(false);
                break;
        }
    }

    private void log(String msg) {
        if (DEBUGGING) {
            LogUtil.d(TAG, msg);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLeftClick(View v) {
        back();
    }

    private void back() {
        if (mListener != null) {
            Uri.Builder builder = Uri.parse("user://fragment").buildUpon();
            // 返回上一页
            builder.appendQueryParameter("fragment", "back");
            Uri uri = Uri.parse(builder.toString());
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void onItemClick(View v, int position) {
        if(mSearchData != null && mSearchData.size() > 0){
            Tip tip = mSearchData.get(position);
            if(tip == null){
                return;
            }

            //存储搜索记录
            String mClickPoiString=tip.getName()+","+tip.getPoint().getLatitude()+","+tip.getPoint().getLongitude();

            if(mETState.equals(EditTextState.HOME)){
                SPUtil.saveHomeOfficeAddress(mClickPoiString, SPUtil.AddressType.HOME);
                mEtHome.setText(tip.getName());
            }else if(mETState.equals(EditTextState.OFFICE)){
                SPUtil.saveHomeOfficeAddress(mClickPoiString, SPUtil.AddressType.OFFICE);
                mEtOffice.setText(tip.getName());
            }

            mSearchData.clear();
            // 刷新RecycleView
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if(list == null || list.size() == 0){
            return;
        }
        mSearchData.clear();
        mSearchData.addAll(list);
        // 刷新RecycleView
        mSearchAdapter.notifyDataSetChanged();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String content = s.toString();
        if(!TextUtils.isEmpty(content)){
            // 调用高德地图搜索提示api
            InputtipsQuery inputquery = new InputtipsQuery(content, null);
            inputquery.setCityLimit(true);
            Inputtips inputTips = new Inputtips(mContext, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }

        //输入框状态设置
        mBtnSave.setEnabled(true);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_home:
                if(hasFocus){
                    mETState = EditTextState.HOME;
                }else{
                    mSearchData.clear();
                    // 刷新RecycleView
                    mSearchAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.et_office:
                if(hasFocus){
                    mETState = EditTextState.OFFICE;
                }else{
                    mSearchData.clear();
                    // 刷新RecycleView
                    mSearchAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 当前在哪个编辑框
     */
    private enum EditTextState{
        HOME,
        OFFICE
    }
}
