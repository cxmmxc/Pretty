package com.fwhl.pretty.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.fwhl.pretty.constant.Constant;

/**
 * Created by Terry.Chen on 2015/6/29 16:32.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class Sputil {
    private  SharedPreferences mSharedPrefrence;
    private SharedPreferences.Editor mEditor;
    private static Sputil mSputil = null;
    
    private Sputil(Context context) {
        mSharedPrefrence = context.getSharedPreferences(Constant.SHAREPREFENCE_NAME, Context.MODE_PRIVATE);
        mEditor  =mSharedPrefrence.edit();
    }
    
    public static Sputil getInstance(Context context) {
        if(mSputil == null) {
            synchronized (Sputil.class) {
                if (mSputil == null) {
                    mSputil = new Sputil(context);
                }
            }
        }
        return mSputil;
    }
    
    public void putString(String name, String str) {
        mEditor.putString(name, str).commit();
    }
    
    public String getString(String name){
        return mSharedPrefrence.getString(name, "");
    }
    
}
