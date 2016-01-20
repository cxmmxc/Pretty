package com.fwhl.pretty.ui;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.adapter.LeftListAdapter;
import com.fwhl.pretty.constant.Constant;
import com.fwhl.pretty.fragment.CategoryFragment;
import com.fwhl.pretty.fragment.MainFragment;
import com.fwhl.pretty.util.FragmentChangeManager;
import com.fwhl.pretty.util.ToastAlone;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.update.AppUpdateInfo;
import net.youmi.android.update.CheckAppUpdateCallBack;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Locale;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements CheckAppUpdateCallBack {


    @ViewInject(R.id.toolbar)
    Toolbar toolbar;

    @ViewInject(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @ViewInject(R.id.left_drawer)
    View drawerRootView;

    @ViewInject(R.id.content_layout)
    FrameLayout content_layout;

    @ViewInject(R.id.left_drawer_listview)
    ListView left_drawer_listview;
    
    @ViewInject(R.id.edit_note_type)
    Button edit_note_type;
    
    private FragmentChangeManager mFragmentManager;

    private ActionBarDrawerToggle mDrawerToggle;

    private LeftListAdapter mListAdapter;

    private int mCurrentPosition;

    private SharedPreferences prefs;
    private static final String DL_ID = "downloadId";

    @Override
    protected void initView() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
//                toolbar.setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
//                toolbar.setTitle(getResources().getString(R.string.app_name));
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setScrimColor(getColor(R.color.drawer_scrim_color));
    }

    @Override
    protected void initData() {
        // 如果使用积分广告，请务必调用积分广告的初始化接口:
        OffersManager.getInstance(this).onAppLaunch();
        downloadManager =  (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        /**
         * 调用本方法当检查更新结束时会回调CheckAppUpdateCallBack接口中的回调方法
         */
        AdManager.getInstance(this).asyncCheckAppUpdate(this);

        mFragmentManager = new FragmentChangeManager(this, R.id.content_layout);
        mFragmentManager.addFragment(Constant.HOME_FRAGMENT, MainFragment.class, null);
        mFragmentManager.addFragment(Constant.CATEGORY_FRAGMENT, CategoryFragment.class, null);
        mFragmentManager.onFragmentChanged(Constant.HOME_FRAGMENT);

        mListAdapter = new LeftListAdapter(this);
        left_drawer_listview.setAdapter(mListAdapter);

        mListAdapter.setData(Arrays.asList(getResources().getStringArray(R.array.drawble_left_list)));
        left_drawer_listview.setItemChecked(0, true);
        mCurrentPosition = 0;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOrCloseDrawer();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(drawerRootView)) {
            mDrawerLayout.closeDrawer(drawerRootView);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openDrawer() {
        if (!mDrawerLayout.isDrawerOpen(drawerRootView)) {
            mDrawerLayout.openDrawer(drawerRootView);
        }
    }

    private void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(drawerRootView)) {
            mDrawerLayout.closeDrawer(drawerRootView);
        }
    }

    private void openOrCloseDrawer() {
        if (mDrawerLayout.isDrawerOpen(drawerRootView)) {
            mDrawerLayout.closeDrawer(drawerRootView);
        } else {
            mDrawerLayout.openDrawer(drawerRootView);
        }
    }


    int press_i = 0;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        press_i += 1;
        ToastAlone.show(R.string.str_exit_toast);
        mHand.sendEmptyMessageDelayed(1, 1500);
        if (press_i >= 2) {
            exitApp();
        }
    }


    @Override
    protected void initToolbar() {
        super.initToolbar(toolbar);
        toolbar.setTitle(R.string.meinv_tuijian);
    }

    Handler mHand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            press_i = 0;
        }
    };

    private void exitApp() {
        // 加判断
        ActivityManager manager = (ActivityManager) mContext
                .getSystemService(ACTIVITY_SERVICE);
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 8) {
            manager.killBackgroundProcesses(mContext.getPackageName());
            System.exit(0);
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(startMain);
            System.exit(0);
        }
    }

    @Override
    protected void setListener() {
        left_drawer_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentPosition == position) {
                    return;
                }
                openOrCloseDrawer();
                mCurrentPosition = position;
                left_drawer_listview.setItemChecked(position, true);
                EneterFragment(position);
            }
        });

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOrCloseDrawer();
                }
            });
        }

    }

    private void EneterFragment(int position) {
        switch (position) {
            case 0:
                //进入MainFragment
                mFragmentManager.onFragmentChanged(Constant.HOME_FRAGMENT);
                toolbar.setTitle(getResources().getString(R.string.meinv_tuijian));
                break;
            case 1:
                //进入分类Fragment
                mFragmentManager.onFragmentChanged(Constant.CATEGORY_FRAGMENT);
                toolbar.setTitle(getResources().getString(R.string.meinv_fenlei));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private DownloadManager downloadManager;

    @Override
    public void onCheckAppUpdateFinish(final AppUpdateInfo appUpdateInfo) {
        // 检查更新回调，注意，这里是在 UI 线程回调的，因此您可以直接与 UI 交互，但不可以进行长时间的操作（如在这里访问网络是不允许的）
        if (appUpdateInfo == null || appUpdateInfo.getUrl() == null) {
            // 当前已经是最新版本
            LogUtils.v("the newst Version");
        } else {
            // 有更新信息，开发者应该在这里实现下载新版本
            // 这里简单示例使用一个对话框来显示更新信息
            new AlertDialog.Builder(mContext)
                    .setTitle("发现新版本")
                    .setMessage(appUpdateInfo.getUpdateTips()) // 这里是版本更新信息
                    .setNegativeButton("马上升级",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //使用DownloadManager进行下载
                                    startDownload(appUpdateInfo.getUrl());
                                }
                            })
                    .setPositiveButton("下次再说",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create().show();
        }
    }

    private void startDownload(final String apk_url) {
        if (!prefs.contains(DL_ID)) {
            downLoadApk(apk_url);
        } else {
            // 下载已经开始，检查状态
            queryDownloadStatus(apk_url);
        }
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    private void downLoadApk(final String apk_url) {
        ToastAlone.show("开始下载");
        // 开始下载
        Uri resource = Uri.parse(encodeGB(apk_url));
        DownloadManager.Request request = new DownloadManager.Request(
                resource);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        // 设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap
                .getMimeTypeFromExtension(MimeTypeMap
                        .getFileExtensionFromUrl(apk_url));
        request.setMimeType(mimeString);
        // 在通知栏中显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        // sdcard的目录下的download文件夹
        request.setDestinationInExternalPublicDir("/huidiancai/",
                "temp.apk");
        request.setTitle("1024MM更新");
        long id = downloadManager.enqueue(request);
        // 保存id
        prefs.edit().putLong(DL_ID, id).commit();
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     *
     * @param string
     * @return
     */
    public String encodeGB(String string) {
        // 转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
        return split[0];
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                queryDownloadStatus(null);
            }
        }
    };

    private void queryDownloadStatus(String apk_url) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(prefs.getLong(DL_ID, 0));
        Cursor c = downloadManager.query(query);
        Log.i("cxm", "c==" + c);
        if (c.moveToFirst()) {
            Log.i("cxm", "c.moveToFirst()");
            int status = c.getInt(c
                    .getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    LogUtils.v("STATUS_PAUSED");
                case DownloadManager.STATUS_PENDING:
                    LogUtils.v("STATUS_PENDING");
                case DownloadManager.STATUS_RUNNING:
                    // 正在下载，不做任何事情
                    LogUtils.v("STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    LogUtils.v("STATUS_FINISHED");
                    // 完成
                    // Log.v("down", "下载完成");
                    // 下载完成，自动安装
                    installApk(new File(Constant.UPDATE_DIR + "/temp.apk"));
                    break;
                case DownloadManager.STATUS_FAILED:
                    // 清除已下载的内容，重新下载
                    LogUtils.v( "STATUS_FAILED");
                    ToastAlone.show("下载失败，请点击重新下载！");
                    downloadManager.remove(prefs.getLong(DL_ID, 0));
                    prefs.edit().clear().commit();
                    break;
            }
        } else {
            try {
                downloadManager.remove(prefs.getLong(DL_ID, 0));
                prefs.edit().clear().commit();
                if(!TextUtils.isEmpty(apk_url)) {
                    downLoadApk(apk_url);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private void installApk(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        // ((Activity) mContext).startActivityForResult(intent, 1);
        startActivity(intent);
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase(Locale.getDefault());
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }
    
    @OnClick(R.id.edit_note_type)
    public void suggestBest(View view) {
        OffersManager.getInstance(this).showOffersWall();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 回收积分广告占用的资源
        OffersManager.getInstance(this).onAppExit();
    }
}

