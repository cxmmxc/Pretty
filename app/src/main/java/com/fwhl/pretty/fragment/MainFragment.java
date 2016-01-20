package com.fwhl.pretty.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fwhl.pretty.BaseFragment;
import com.fwhl.pretty.R;
import com.fwhl.pretty.adapter.RecyeAdapter;
import com.fwhl.pretty.bean.MainPicBean;
import com.fwhl.pretty.constant.Constant;
import com.fwhl.pretty.inter.OnItemListener;
import com.fwhl.pretty.ui.PicDetailActivity;
import com.fwhl.pretty.util.StrUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewListener;
import net.youmi.android.spot.SpotManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Terry.Chen on 2015/7/6 14:40.
 * Description:首页的Fragment
 * Email:cxm_lmz@163.com
 */
public class MainFragment extends BaseFragment {


    @ViewInject(R.id.recyclerView)
    RecyclerView mRecyView;

    @ViewInject(R.id.progress)
    private ProgressBar progress;

    @ViewInject(R.id.viewpager_layout)
    ViewPager viewpager_layout;

    @ViewInject(R.id.dot_layout)
    LinearLayout dot_layout;

    @ViewInject(R.id.header_layout)
    RelativeLayout header_layout;
    
    @ViewInject(R.id.empty_layout)
    LinearLayout empty_layout;
    
    @ViewInject(R.id.adLayout)
    LinearLayout adLayout;


    private RecyclerView.LayoutManager mlayoutManager;
    private RecyeAdapter mAdapter;
    private ArrayList<MainPicBean> mMainPics;

    private ArrayList<MainPicBean> mPagerPics;
    private int mHeaderHeight;

    private MainPagerAdapter mMainAdapter;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_fragment, null);
        ViewUtils.inject(this, view);
        return view;
    }


    @Override
    protected void initData() {
        mlayoutManager = new GridLayoutManager(mContext, 2);
        mAdapter = new RecyeAdapter(mContext);
        mRecyView.setLayoutManager(mlayoutManager);
        mRecyView.setAdapter(mAdapter);
        mMainPics = new ArrayList<MainPicBean>();
        mHeaderHeight = header_layout.getLayoutParams().height;
        mPagerPics = new ArrayList<MainPicBean>();
        mMainAdapter = new MainPagerAdapter();
        viewpager_layout.setAdapter(mMainAdapter);

        getInterlData();
        
        initYoumi();
    }

    private void initYoumi() {
        // 加载插播资源
        SpotManager.getInstance(mContext).loadSpotAds();
        // 插屏出现动画效果，0:ANIM_NONE为无动画，1:ANIM_SIMPLE为简单动画效果，2:ANIM_ADVANCE为高级动画效果
        SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIM_ADVANCE);
        // 设置插屏动画的横竖屏展示方式，如果设置了横屏，则在有广告资源的情况下会是优先使用横屏图。
        SpotManager.getInstance(mContext).setSpotOrientation(
                SpotManager.ORIENTATION_PORTRAIT);
        showBanner();
    }

    private void showBanner() {

        // 广告条接口调用（适用于应用）
        // 将广告条adView添加到需要展示的layout控件中
         AdView adView = new AdView(mContext, AdSize.FIT_SCREEN);
         adLayout.addView(adView);


        // 监听广告条接口
        adView.setAdListener(new AdViewListener() {

            @Override
            public void onSwitchedAd(AdView arg0) {
                Log.i("YoumiAdDemo", "广告条切换");
            }

            @Override
            public void onReceivedAd(AdView arg0) {
                Log.i("YoumiAdDemo", "请求广告成功");

            }

            @Override
            public void onFailedToReceivedAd(AdView arg0) {
                Log.i("YoumiAdDemo", "请求广告失败");
            }
        });
    }

    private void getInterlData() {

        AsyncTask<Integer, Integer, Document> asyncTask = new AsyncTask() {

            /**
             * Runs on the UI thread before {@link #doInBackground}.
             *
             * @see #onPostExecute
             * @see #doInBackground
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setVisibility(View.VISIBLE);
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p/>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param o The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                progress.setVisibility(View.GONE);
                if (o != null) {
                    //解析推荐页面
                    Document document = (Document) o;
                    Element element = document.select("div#slideshow1").first();
                    Elements elements = element.select("a[href]");
                    if(elements != null) {
                        for (Element element2 : elements) {
                            MainPicBean pic = new MainPicBean();
                            String old_href = element2.attr("href");
                            String type = StrUtil.getBeanStrType(old_href);
                            type = StrUtil.getTypeToStr(type);
                            pic.setType(type);
                            String href = Constant.JSOUP_SIMEI_URL + old_href;
                            String title = element2.attr("title");
                            Element src_ele = element2.getElementsByAttribute("src").first();
                            String imgurl = Constant.JSOUP_SIMEI_URL + src_ele.attr("src");
                            pic.setPicUrl(imgurl);
                            pic.setHrefUrl(href);
                            pic.setTitle(title);
                            mPagerPics.add(pic);
                        }
                    }
                    LogUtils.i(mPagerPics.toString());
                    initDot(mPagerPics.size());
                    mMainAdapter.setData(mPagerPics);

                    //解析列表页面
                    Elements elements_other = document.select("div#tu");
                    if (elements_other != null) {
                        for(Element ele_other: elements_other) {

                            Elements children = ele_other.child(0).children();
                            if(children != null) {
                                for(Element child1: children) {
                                    MainPicBean bean = new MainPicBean();
                                    Element href_1 = child1.select("a[href]").first();
                                    Element src_1 = href_1.getElementsByAttribute("src").first();
                                    String old_href = href_1.attr("href");
                                    String type = StrUtil.getBeanStrType(old_href);
                                    type = StrUtil.getTypeToStr(type);
                                    bean.setType(type);
                                    String href = Constant.JSOUP_SIMEI_URL + old_href;
                                    String title = href_1.attr("title");
                                    String img_url = src_1.attr("src");
                                    bean.setTitle(title);
                                    bean.setHrefUrl(href);
                                    bean.setPicUrl(img_url);
                                    mMainPics.add(bean);
                                }
                            }
                        }
                        mAdapter.setData(mMainPics);
                    }
                }else {
                    empty_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                Document document = null;
                try {
                    document = Jsoup.connect(
                            Constant.JSOUP_SIMEI_URL).timeout(8000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return document;
            }


        };
        asyncTask.execute();
    }


    @Override
    protected void setListener() {
        mAdapter.setOnItemListener(new OnItemListener() {
            @Override
            public void OnItemClickLister(View view, int position) {
                Intent itent = new Intent();
                itent.setClass(mContext, PicDetailActivity.class);
                itent.putExtra("bean", mMainPics.get(position));
                startActivity(itent);
            }

            @Override
            public void OnLongPressListener(View view, int position) {

            }
        });


        final ArrayList<Integer> isHaveValue = new ArrayList<Integer>();

        mRecyView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            ViewGroup.LayoutParams layoutParams = header_layout.getLayoutParams();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LogUtils.v(newState + "");
            }

            int i = 1;

            int j = 1;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
                if (verticalScrollOffset > 30 && isHaveValue.isEmpty()) {
                    //从开始位置滑走，gone
                    j = 1;
                    if (i == 1) {
                        LogUtils.w("gone");
                        ObjectAnimator animator = ObjectAnimator.ofInt(header_layout, "height", mHeaderHeight, 0);
                        animator.setDuration(1000);
                        animator.start();

                        animator.addListener(new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                header_layout.setVisibility(View.GONE);
                                isHaveValue.add(1);
                            }
                        });

                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                Integer aValue = (Integer) animation.getAnimatedValue();
                                layoutParams.height = aValue;
                                header_layout.setLayoutParams(layoutParams);
                                LogUtils.v("gone-height=" + aValue);
                            }
                        });
                    }
                    i++;
                }

                if (verticalScrollOffset < 20 && !isHaveValue.isEmpty()) {
                    //已经滑到开始位置了，则显示出headerView
                    i = 1;
                    LogUtils.w("visible");
                    if (j == 1) {
                        ObjectAnimator animator = ObjectAnimator.ofInt(header_layout, "height", 0, mHeaderHeight);
                        animator.setDuration(1000);
                        animator.start();

                        header_layout.setVisibility(View.VISIBLE);

                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                isHaveValue.clear();
                            }
                        });

                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                Integer aValue = (Integer) animation.getAnimatedValue();
                                layoutParams.height = aValue;
                                header_layout.setLayoutParams(layoutParams);
                                LogUtils.v("visible-height=" + aValue + ",layoutParams.height=" + layoutParams.height);
                            }
                        });
                    }
                    j++;
                }
                LogUtils.v(verticalScrollOffset + "");
            }
        });

        viewpager_layout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        empty_layout.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                empty_layout.setVisibility(View.GONE);
                getInterlData();
            }
        });
    }


    private void initDot(int size) {
        for (int i = 0; i < size; i++) {
            View view = new View(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(25, 4);
            params.leftMargin = 10;
            view.setLayoutParams(params);
            view.setBackgroundColor(getResources().getColor(R.color.dot_uncheck_color));
            if (i == 0) {
                view.setBackgroundColor(getResources().getColor(R.color.title_bg));
            }
            dot_layout.addView(view, i);
        }
    }



    private void changeDot(int position) {
        int childCount = dot_layout.getChildCount();
        for(int i=0; i<childCount; i++) {
            View view = dot_layout.getChildAt(i);
            int color = i == position ? getResources().getColor(R.color.title_bg) :getResources().getColor(R.color.dot_uncheck_color);
            view.setBackgroundColor(color);
        }
    }

    class MainPagerAdapter extends PagerAdapter {

        private ArrayList<MainPicBean> picBeans;

        public MainPagerAdapter() {
            picBeans = new ArrayList<MainPicBean>();
            mBitmapUtils.configDiskCacheEnabled(true)
                    .configMemoryCacheEnabled(true)
                    .configDefaultLoadingImage(R.mipmap.default_meitu_class_loding)
                    .configDefaultLoadFailedImage(R.mipmap.default_meitu_class_error);
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return picBeans.size();
        }

        public void setData(ArrayList<MainPicBean> beans) {
            if (beans != null && !beans.isEmpty()) {
                picBeans.clear();
                picBeans.addAll(beans);
            }
            notifyDataSetChanged();
        }


        /**
         * Create the page for the given position.  The adapter is responsible
         * for adding the view to the container given here, although it only
         * must ensure this is done by the time it returns from
         * {@link #finishUpdate(ViewGroup)}.
         *
         * @param container The containing View in which the page will be shown.
         * @param position  The page position to be instantiated.
         * @return Returns an Object representing the new page.  This does not
         * need to be a View, but can be some other container of the page.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final MainPicBean picBean = picBeans.get(position);
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mBitmapUtils.display(imageView, picBean.getPicUrl());
            container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent itent = new Intent();
                    itent.setClass(mContext, PicDetailActivity.class);
                    itent.putExtra("bean", picBean);
                    startActivity(itent);
                }
            });
            return imageView;
        }

        /**
         * Remove a page for the given position.  The adapter is responsible
         * for removing the view from its container, although it only must ensure
         * this is done by the time it returns from {@link #finishUpdate(ViewGroup)}.
         *
         * @param container The containing View from which the page will be removed.
         * @param position  The page position to be removed.
         * @param object    The same object that was returned by
         *                  {@link #instantiateItem(View, int)}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view   Page View to check for association with <code>object</code>
         * @param object Object to check for association with <code>view</code>
         * @return true if <code>view</code> is associated with the key object <code>object</code>
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
    
}
