package com.fwhl.pretty;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.fwhl.pretty.constant.Constant;
import com.fwhl.pretty.util.ToastAlone;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;

/**
 * Created by Terry.Chen on 2015/7/2 16:09.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class PrettyApp extends Application {
    
    public static String mVersion;
    
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.customTagPrefix = "cxm";
        initData();
        ToastAlone.init(this);

    }

    private void initData() {
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(Constant.CaceFileDir);
        LogUtils.v("mkdir=" + path);
        if(!file.exists()) {
            boolean mkdir = file.mkdir();
        }

        mVersion = getVersionName();
    }


    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
