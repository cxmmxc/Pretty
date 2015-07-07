package com.fwhl.pretty.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.util.HashMap;

/**
 * KunShan_talent -- com.kunshan.talent.util<br>
 * User: lizheng<br>
 * Date: 13-9-17<br>
 * Time: 下午3:03<br>
 * Email: kenny.li@itotemdeveloper.com<br>
 * Fragment切换管理器
 */
public class FragmentChangeManager {

    private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
    TabInfo mLastTab;
    private FragmentActivity mActivity;
    private int mContainerId;

    public FragmentChangeManager(FragmentActivity activity, int containerId) {
        this.mActivity = activity;
        this.mContainerId = containerId;
    }

    public void addFragment(String tag, Class<?> clss, Bundle args) {

        TabInfo info = new TabInfo(tag, clss, args);

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state. If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        info.fragment = mActivity.getSupportFragmentManager()
                .findFragmentByTag(tag);
        if (info.fragment != null && !info.fragment.isDetached()) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager()
                    .beginTransaction();
            ft.detach(info.fragment);
            ft.commit();
        }

        mTabs.put(tag, info);
    }

    public void onFragmentChanged(String tabId) {
        TabInfo newTab = mTabs.get(tabId);
        if (mLastTab != newTab) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager()
                    .beginTransaction();
//			if (mLastTab != null) {
//				if (mLastTab.fragment != null) {
//					ft.detach(mLastTab.fragment);
//				}
//			}
//			if (newTab != null) {
//				if (newTab.fragment == null) {
//					newTab.fragment = Fragment.instantiate(mActivity,
//							newTab.clss.getName(), newTab.args);
//					ft.add(mContainerId, newTab.fragment, newTab.tag);
//				} else {
//					ft.attach(newTab.fragment);
//				}
//			}
//
//			mLastTab = newTab;
//			ft.commit();
//			mActivity.getSupportFragmentManager().executePendingTransactions();

            if (null != mLastTab) {
                if (null != mLastTab.fragment) {
                    ft.hide(mLastTab.fragment);
                }
            }

            if (null != newTab) {
                if (null == newTab.fragment) {
                    newTab.fragment = Fragment.instantiate(mActivity,
                            newTab.clss.getCanonicalName(), newTab.args);
                }
                if (!newTab.fragment.isAdded()) {
                    ft.add(mContainerId, newTab.fragment, newTab.tag);
                }
                ft.show(newTab.fragment);
            }
            mLastTab = newTab;
            ft.commit();
            mActivity.getSupportFragmentManager().executePendingTransactions();

        }
    }

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }
}
