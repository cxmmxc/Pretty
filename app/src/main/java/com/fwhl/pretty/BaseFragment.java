package com.fwhl.pretty;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lidroid.xutils.BitmapUtils;

/**
 * Created by Terry.Chen on 2015/7/6 14:30.
 * Description:
 * Email:cxm_lmz@163.com
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected BitmapUtils mBitmapUtils;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mBitmapUtils = new BitmapUtils(mContext);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setListener();
    }

    protected abstract void initData();

    protected abstract void setListener();
}
