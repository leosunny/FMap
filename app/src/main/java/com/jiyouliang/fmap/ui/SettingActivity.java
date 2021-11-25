package com.jiyouliang.fmap.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.server.task.SharedPreferencesTask;
import com.jiyouliang.fmap.ui.user.AimlessSettingFragment;
import com.jiyouliang.fmap.ui.user.ChooseHomeOfficeFragment;
import com.jiyouliang.fmap.ui.user.UserDetailFragment;
import com.jiyouliang.fmap.ui.user.UserInfoFragment;
import com.jiyouliang.fmap.ui.user.UserLoginBySmsFragment;
import com.jiyouliang.fmap.ui.user.UserSendSmsFragment;
import com.jiyouliang.fmap.ui.user.UserSettingFragment;
import com.jiyouliang.fmap.util.InputMethodUtils;
import com.jiyouliang.fmap.util.LogUtil;

/**
 * 用户相关Activity,通过该Activity分发用户相关Fragment,比如UerLoginFragment
 * UserSendSmsFragment
 */
public class SettingActivity extends FragmentActivity implements BaseFragment.OnFragmentInteractionListener {

    private FrameLayout mFragmentContainer;
    private FragmentManager fm;
    private static final String TAG = "SettingActivity";
    private LinearLayout mRootContainer;

    /**
     * 用户设置Fragment栈名
     */
    private static final String STACK_NAME_SETTING = "user_setting";

    /**
     * 选择家和公司地址Fragment栈名
     */
    private static final String STACK_NAME_HOME_OFFICE = "home_office";

    /**
     * 巡航设置Fragment栈名
     */
    private static final String STACK_NAME_AIMLESS = "aimless_setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
    }

    private void initView() {
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mRootContainer = (LinearLayout) findViewById(R.id.ll_root_container);
        fm = getSupportFragmentManager();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (fm != null) {
            int count = fm.getBackStackEntryCount();
            // 回退栈已经存在fragment, onResume后不重复添加详情页Fragment
            // 这里>1是为了避免多线程穿透
            if (count >= 1) {
                return;
            }

        }
        showUserSettingFragment(null);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        log(uri.toString());

        dispatchFragment(uri);

    }

    /**
     * 分发Fragment
     */
    private void dispatchFragment(Uri uri) {
        // FragmentManager默认不会为空,这里添加是为了为了代码安全,防范于未然
        if (fm == null) {
            return;
        }
        String fragment = uri.getQueryParameter("fragment");
        if (TextUtils.isEmpty(fragment)) {
            LogUtil.e(TAG, "fragment name cannot be null");
            return;
        }


        // 用户设置Fragment
        if (fragment.equals(UserSettingFragment.class.getSimpleName())) {
            String phone = uri.getQueryParameter("phone");
            showUserSettingFragment(phone);
            return;
        }

        // 选择家和公司地址Fragment
        if (fragment.equals(ChooseHomeOfficeFragment.class.getSimpleName())) {
            showChooseHomeOfficeFragment();
            return;
        }

        // 选择巡航设置Fragment
        if (fragment.equals(AimlessSettingFragment.class.getSimpleName())) {
            showAimlessSettingFragment();
            return;
        }

        // 返回上一页
        if (fragment.equals("back")) {
            back();
            return;
        }

        // 关闭软键盘
        if (fragment.equals("hideInput")) {
            hideInput();
            return;
        }
    }


    /**
     * 显示用户设置Fragment
     *
     * @param phone
     */
    private void showUserSettingFragment(String phone) {
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        UserSettingFragment fragment = UserSettingFragment.newInstance(phone);
        ft.replace(R.id.fragment_container, fragment);
        // 添加到回退栈
        ft.addToBackStack(STACK_NAME_SETTING);
        ft.commit();
    }

    /**
     * 选择家和公司地址Fragment
     */
    public void showChooseHomeOfficeFragment() {
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        ChooseHomeOfficeFragment fragment = ChooseHomeOfficeFragment.newInstance();
        ft.replace(R.id.fragment_container, fragment);
        // 添加到回退栈
        ft.addToBackStack(STACK_NAME_HOME_OFFICE);
        ft.commit();
    }

    /**
     * 显示巡航设置Fragment
     */
    public void showAimlessSettingFragment() {
        mRootContainer.setBackgroundColor(Color.WHITE);
        FragmentTransaction ft = fm.beginTransaction();
        AimlessSettingFragment fragment = AimlessSettingFragment.newInstance();
        ft.replace(R.id.fragment_container, fragment);
        // 添加到回退栈
        ft.addToBackStack(STACK_NAME_AIMLESS);
        ft.commit();
    }

    /**
     * 返回上一页,通过Fragment回退栈管理
     */
    private void back() {
        int count = fm.getBackStackEntryCount();
        if (count <= 1) {
            // 只有一个Fragment关闭当前Activity
            finish();
            return;
        }
        if (count == 2) {
            // 避免回退背景变成白色
            mRootContainer.setBackground(getResources().getDrawable(R.drawable.user_detail_bg));
        }
        fm.popBackStack();
    }

    /**
     * 清空回退栈
     */
    private void clearFragmentStacks() {
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(i);
            if (!TextUtils.isEmpty(entry.getName())) {
                fm.popBackStack();
            }
        }
    }

    private void hideInput() {
        InputMethodUtils.hideInput(this);
    }

    private void log(String msg) {
        LogUtil.d(TAG, msg);
    }

    /**
     * 处理返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
