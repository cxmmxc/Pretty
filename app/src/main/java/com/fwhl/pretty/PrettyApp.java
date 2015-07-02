package com.fwhl.pretty;

import android.app.Application;

import com.fwhl.pretty.util.ToastAlone;

/**
 * Created by Terry.Chen on 2015/7/2 16:09.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class PrettyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ToastAlone.init(this);
    }
}
