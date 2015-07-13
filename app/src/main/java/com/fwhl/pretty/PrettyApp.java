package com.fwhl.pretty;

import android.app.Application;
import android.os.Environment;

import com.fwhl.pretty.util.ToastAlone;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;

/**
 * Created by Terry.Chen on 2015/7/2 16:09.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class PrettyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.customTagPrefix = "cxm";
        initData();
        ToastAlone.init(this);

    }

    private void initData() {
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(path+"/DCIM/1024MM");
        LogUtils.v("mkdir="+path);
        if(!file.exists()) {
            boolean mkdir = file.mkdir();
        }
    }
}
