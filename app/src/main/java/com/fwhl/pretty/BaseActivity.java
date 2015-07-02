package com.fwhl.pretty;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;

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
        LogUtils.customTagPrefix = "cxm";
        ViewUtils.inject(this);
        mBitmapUtils = new BitmapUtils(this);
        initView();
        initData();
        setListener();
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void setListener();
}
