package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.jiyouliang.fmap.MapActivity;
import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseFragment;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.SPUtil;
import com.jiyouliang.fmap.view.widget.ButtonLoadingView;
import com.jiyouliang.fmap.view.widget.ClearEditText;
import com.jiyouliang.fmap.view.widget.OnItemClickListener;
import com.jiyouliang.fmap.view.widget.TopTitleView;

import java.util.ArrayList;
import java.util.List;


public class AimlessSettingFragment extends BaseFragment implements View.OnClickListener,
        TopTitleView.OnTopTitleViewClickListener,
        CompoundButton.OnCheckedChangeListener{

    private static final boolean DEBUGGING = true;
    private static final String TAG = "AimlessSettingFragment";
    private Context mContext;

    private CheckBox mCBBroadcastEye;
    private CheckBox mCBBroadcastRoad;
    private CheckBox mCBBroadcastSafe;

    private RadioButton mRBCarNorth;
    private RadioButton mRBNorth;

    private OnFragmentInteractionListener mListener;
    private TopTitleView mTopTitleView;

    public AimlessSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserSendSmsFragment.
     */
    public static AimlessSettingFragment newInstance() {
        AimlessSettingFragment fragment = new AimlessSettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_aimless_setting, container, false);
        initView(rootView);
        initData();
        setListener();
        return rootView;
    }

    private void initView(View rootView) {
        mCBBroadcastEye=(CheckBox)rootView.findViewById(R.id.cb_broadcast_eye);
        mCBBroadcastRoad=(CheckBox)rootView.findViewById(R.id.cb_broadcast_road);
        mCBBroadcastSafe=(CheckBox)rootView.findViewById(R.id.cb_broadcast_safe);
        mRBCarNorth=(RadioButton)rootView.findViewById(R.id.rb_car_north);
        mRBNorth=(RadioButton)rootView.findViewById(R.id.rb_north);
        mTopTitleView = (TopTitleView)rootView.findViewById(R.id.ttv_aimless);
    }

    private void setListener() {
        mCBBroadcastEye.setOnCheckedChangeListener(this);
        mCBBroadcastRoad.setOnCheckedChangeListener(this);
        mCBBroadcastSafe.setOnCheckedChangeListener(this);
        mRBCarNorth.setOnCheckedChangeListener(this);
        mRBNorth.setOnCheckedChangeListener(this);
        mTopTitleView.setOnTopTitleViewClickListener(this);

    }

    private void initData() {
        mContext = getContext();
        boolean isCarNorth = SPUtil.getAimlessNorthView();
        if(isCarNorth){
            mRBCarNorth.setChecked(true);
        }else{
            mRBNorth.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {

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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.rb_north:
                break;
            case R.id.rb_car_north:
                SPUtil.saveAimlessNorthView(isChecked);
                break;
            default:
                break;
        }
    }
}
