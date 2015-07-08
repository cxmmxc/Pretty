package com.fwhl.pretty.ui;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Arrays;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    
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
    private FragmentChangeManager mFragmentManager;

    private ActionBarDrawerToggle mDrawerToggle;

    private LeftListAdapter mListAdapter;
    
    private int mCurrentPosition;
    @Override
    protected void initView() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                toolbar.setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                toolbar.setTitle(getResources().getString(R.string.app_name));
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setScrimColor(getColor(R.color.drawer_scrim_color));
    }

    @Override
    protected void initData() {
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
        if (toolbar != null){
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
        if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(drawerRootView)){
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
        press_i+=1;
        ToastAlone.show(R.string.str_exit_toast);
        mHand.sendEmptyMessageDelayed(1,1500);
        if(press_i >=2 ){
            exitApp();
        }
    }


    @Override
    protected void initToolbar(){
        super.initToolbar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.meinv_tuijian));
    }
    
    Handler mHand = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            press_i = 0;
        }
    };
    
    private void exitApp(){
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
                if(mCurrentPosition == position) {
                    return;
                }
                openOrCloseDrawer();
                mCurrentPosition = position;
                left_drawer_listview.setItemChecked(position, true);
                EneterFragment(position);
            }
        });

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

}
