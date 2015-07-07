package com.fwhl.pretty;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by Terry.Chen on 2015/6/29 16:25.
 * Description:基类Activity
 * Email:cxm_lmz@163.com
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected BitmapUtils mBitmapUtils;
    protected Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initWindow();
        LogUtils.customTagPrefix = "cxm";
        ViewUtils.inject(this);
        mBitmapUtils = new BitmapUtils(this);
        initToolbar();
        initView();
        initData();
        setListener();
    }

    @TargetApi(19)
    private void initWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getColor(R.color.title_bg));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    protected void initToolbar(Toolbar toolbar){
        if (toolbar == null)
            return;
        toolbar.setBackgroundColor(getResources().getColor(R.color.title_bg));
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getColor(R.color.action_bar_title_color));
        toolbar.collapseActionView();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    protected int getColor(int res){
        if (res <= 0)
            throw new IllegalArgumentException("resource id can not be less 0");
        return getResources().getColor(res);
    }
    protected abstract void initView();

    protected abstract void initData();

    protected abstract void setListener();


    protected abstract void initToolbar();
}
