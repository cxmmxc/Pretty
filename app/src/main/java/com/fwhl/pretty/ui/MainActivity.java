package com.fwhl.pretty.ui;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.adapter.RecyeAdapter;
import com.fwhl.pretty.bean.MainPicBean;
import com.fwhl.pretty.constant.Constant;
import com.fwhl.pretty.inter.OnItemListener;
import com.fwhl.pretty.util.ToastAlone;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.recyclerView)
    RecyclerView mRecyView;

    @ViewInject(R.id.progress)
    private ProgressBar progress;

    @ViewInject(R.id.viewpager_layout)
    ViewPager viewpager_layout;

    @ViewInject(R.id.dot_layout)
    LinearLayout dot_layout;

    @ViewInject(R.id.title_text)
    TextView title_text;

    @ViewInject(R.id.serch_btn)
    Button serch_btn;

    @ViewInject(R.id.header_layout)
    RelativeLayout header_layout;

    private RecyclerView.LayoutManager mlayoutManager;
    private RecyeAdapter mAdapter;
    private ArrayList<MainPicBean> mMainPics;

    private ArrayList<MainPicBean> mPagerPics;
    private int mHeaderHeight;
    
    private MainPagerAdapter mMainAdapter;

    @Override
    protected void initView() {
        title_text.setText(getResources().getString(R.string.app_name));
//        serch_btn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        mlayoutManager = new GridLayoutManager(this, 2);
        mAdapter = new RecyeAdapter(this);
        mRecyView.setLayoutManager(mlayoutManager);
        mRecyView.setAdapter(mAdapter);
        mMainPics = new ArrayList<MainPicBean>();
        mHeaderHeight = header_layout.getLayoutParams().height;
        mPagerPics = new ArrayList<MainPicBean>();
        mMainAdapter = new MainPagerAdapter();
        viewpager_layout.setAdapter(mMainAdapter);
        getInterlData();
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
                            String href = Constant.JSOUP_SIMEI_URL + element2.attr("href");
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
                                    String href = Constant.JSOUP_SIMEI_URL + href_1.attr("href");
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
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                Document document = null;
                try {
                    document = Jsoup.connect(
                            Constant.JSOUP_SIMEI_URL).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return document;
            }


        };
        asyncTask.execute();
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
        mAdapter.setOnItemListener(new OnItemListener() {
            @Override
            public void OnItemClickLister(View view, int position) {
                Intent itent = new Intent();
                itent.setClass(MainActivity.this, PicDetailActivity.class);
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

    }

    private void changeDot(int position) {
        int childCount = dot_layout.getChildCount();
        for(int i=0; i<childCount; i++) {
            View view = dot_layout.getChildAt(i);
            int color = i == position ? getResources().getColor(R.color.title_bg) :getResources().getColor(R.color.dot_uncheck_color);
                view.setBackgroundColor(color);
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

    class MainPagerAdapter extends PagerAdapter {

        private ArrayList<MainPicBean> picBeans;

        public MainPagerAdapter() {
            picBeans = new ArrayList<MainPicBean>();
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
            PhotoView photoView = new PhotoView(container.getContext());
            mBitmapUtils.display(photoView, picBean.getPicUrl());
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    Intent itent = new Intent();
                    itent.setClass(MainActivity.this, PicDetailActivity.class);
                    itent.putExtra("bean", picBean);
                    startActivity(itent);
                }
            });
            return photoView;
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
