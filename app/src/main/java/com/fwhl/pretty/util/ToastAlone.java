package com.fwhl.pretty.util;

import android.app.Application;
import android.widget.Toast;

/**
 * Created by Terry.Chen on 2015/7/2 16:04.
 * Description:单例Toast
 * Email:cxm_lmz@163.com
 */
public class ToastAlone {

    /**
     * 唯一的toast
     */
    private static Toast mToast = null;

    private static Application mApplication;

    public static ToastAlone init(Application application){
        mApplication = application;
        return null;
    }

    public static void show(String text){
        if(null == mToast){
            mToast = Toast.makeText(mApplication, text, Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.show();
    }
    public static void show(int textRid){
        if(null == mToast){
            mToast = Toast.makeText(mApplication, textRid, Toast.LENGTH_SHORT);
        }
        mToast.setText(textRid);
        mToast.show();
    }
}
