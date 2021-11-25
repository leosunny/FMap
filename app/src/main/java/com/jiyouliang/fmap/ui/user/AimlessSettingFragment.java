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


public class AimlessSettingFragment extends BaseFragment implements View.OnClickListener, TopTitleView.OnTopTitleViewClickListener {

    private ClearEditText mEtHome;
    private ClearEditText mEtOffice;
    private ButtonLoadingView mBtnSave;
    private static final boolean DEBUGGING = true;
    private static final String TAG = "ChooseHomeOfficeFragment";
    private Context mContext;

    private OnFragmentInteractionListener mListener;
    private TopTitleView mTopTitleView;

    private String mHomeAddress;
    private String mOfficeAddress;

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
        mEtHome = (ClearEditText) rootView.findViewById(R.id.et_home);
        mEtOffice = (ClearEditText) rootView.findViewById(R.id.et_office);
        mBtnSave = (ButtonLoadingView) rootView.findViewById(R.id.btn_save);
        mBtnSave.setEnabled(false);
        mTopTitleView = (TopTitleView)rootView.findViewById(R.id.ttv);
    }

    private void setListener() {
        mEtHome.setOnClearEditClickListener(new ClearEditText.OnClearEditClickListener() {
            @Override
            public void onDelete() {
                //清空文字
                mEtHome.setText("");
            }
        });

        mEtOffice.setOnClearEditClickListener(new ClearEditText.OnClearEditClickListener() {
            @Override
            public void onDelete() {
                //清空文字
                mEtOffice.setText("");
            }
        });

        mBtnSave.setOnClickListener(this);
        mTopTitleView.setOnTopTitleViewClickListener(this);

    }

    private void initData() {
        mContext = getContext();

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
}
