package com.jiyouliang.fmap.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jiyouliang.fmap.MapActivity;
import com.jiyouliang.fmap.R;

public class PrivacyActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        mBtnAgree =(Button) findViewById(R.id.btn_agree);
        mBtnAgree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == null){
            return;
        }

        if(v == mBtnAgree){
            SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putBoolean("isFirstStart",false);
            editor.apply();

            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            finish();

            return;
        }
    }
}