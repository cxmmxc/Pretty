package com.fwhl.pretty.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fwhl.pretty.PrettyApp;
import com.fwhl.pretty.R;
import com.fwhl.pretty.constant.Constant;
import com.lidroid.xutils.util.LogUtils;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SplashView;
import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;

/**
 * Created by Terry.Chen on 2015/7/1 14:15.
 * Description:欢迎广告页面
 * Email:cxm_lmz@163.com
 */
public class WelcomeActivity extends Activity {

    private LinearLayout ad_layout;
    
    private TextView version_ext;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.welcome_layout);

        ad_layout = (LinearLayout) findViewById(R.id.ad_layout);
        version_ext = (TextView) findViewById(R.id.version_ext);

        LogUtils.v("version="+PrettyApp.mVersion);
        version_ext.setText("V"+ PrettyApp.mVersion);

        // 初始化接口，应用启动的时候调用
        // 参数：appId, appSecret, 调试模式
        AdManager.getInstance(this).init(Constant.YOUMI_APPID, Constant.YOUMI_APPSECRET, false);

        // 如果仅仅使用开屏，需要取消注释以下注释，如果使用了开屏和插屏，则不需要。
        SpotManager.getInstance(this).loadSplashSpotAds();

        // 开屏的两种调用方式：请根据使用情况选择其中一种调用方式。
        // 1.可自定义化调用：
        // 此方式能够将开屏适应一些应用的特殊场景进行使用。
        // 传入需要跳转的activity
        SplashView splashView = new SplashView(this, MainActivity.class);
        ad_layout.addView(splashView.getSplashView());
        SpotManager.getInstance(this).showSplashSpotAds(this, splashView,
                new SpotDialogListener() {

                    @Override
                    public void onShowSuccess() {
                        Log.i("YoumiAdDemo", "开屏展示成功");
                    }

                    @Override
                    public void onShowFailed() {
                        Log.i("YoumiAdDemo", "开屏展示失败。");
                    }

                    @Override
                    public void onSpotClosed() {
                        Log.i("YoumiAdDemo", "开屏关闭。");
                    }
                });
    }



    // 请务必加上词句，否则进入网页广告后无法进去原sdk
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 10045) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        // 取消后退键
    }
    
}
