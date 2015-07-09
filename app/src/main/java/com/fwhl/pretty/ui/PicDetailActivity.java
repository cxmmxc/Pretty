package com.fwhl.pretty.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.adapter.PicDetailRecyAdapter;
import com.fwhl.pretty.bean.MainPicBean;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Terry.Chen on 2015/6/29 19:58.
 * Description:图片的详细信息，轮训查看
 * Email:cxm_lmz@163.com
 */
@ContentView(R.layout.picdetail_layout)
public class PicDetailActivity extends BaseActivity {
    private MainPicBean mPicBean;
    private String mNowHerfUrl;

    @ViewInject(R.id.detail_recyView)
    RecyclerView detail_recyView;
    @ViewInject(R.id.progress)
    ProgressBar progress;
    @ViewInject(R.id.toolbar)
    Toolbar toolbar;
    @ViewInject(R.id.empty_layout)
    LinearLayout empty_layout;

    private ArrayList<MainPicBean> mMainPics;

    private RecyclerView.LayoutManager mLayoutManager;

    private PicDetailRecyAdapter mPicAdapter;
//    private ViewPagerAdapter mAdapter;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        mPicBean = (MainPicBean) getIntent().getSerializableExtra("bean");

        //通过url进行解析
        mNowHerfUrl = mPicBean.getHrefUrl();

        toolbar.setTitle(mPicBean.getTitle());
        mBitmapUtils = new BitmapUtils(this);
        mBitmapUtils.configDiskCacheEnabled(true);
        mBitmapUtils.configMemoryCacheEnabled(true);
        mBitmapUtils.configDefaultLoadingImage(R.mipmap.default_pretty_loding);

        mLayoutManager = new LinearLayoutManager(this);
        detail_recyView.setLayoutManager(mLayoutManager);
        mPicAdapter = new PicDetailRecyAdapter(this);
        detail_recyView.setAdapter(mPicAdapter);

        mMainPics = new ArrayList<MainPicBean>();
//        mAdapter = new ViewPagerAdapter();
//        viewpager.setAdapter(mAdapter);
        new JsoupTask().execute(mNowHerfUrl);
    }

    class JsoupTask extends AsyncTask<String, Integer, Object> {


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
            if (mMainPics.isEmpty()) {
                empty_layout.setVisibility(View.VISIBLE);
                return;
            }
            Log.e("cxm", mMainPics.toString());
            mPicAdapter.setData(mMainPics);
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Object doInBackground(String[] params) {
            String url = params[0];
            try {


                Document document = Jsoup.connect(url).timeout(8000).get();
                Element select = document.select("meta[content]").last();
                String string = select.attr("content");
                int fir_index = string.indexOf("[");
                int sec_index = string.indexOf("]");
                int size = Integer.parseInt(string.substring(fir_index + 2, sec_index - 3));

                Element element = document.select("div.pp").first();
                Element src_fir = element.getElementsByAttribute("src").last();
                String img_url_one = src_fir.attr("src");
                String[] img_str_split = img_url_one.split("-");
                String img_url_base = img_str_split[0];
                for (int i = 1; i <= size; i++) {
                    MainPicBean bean = new MainPicBean();
                    bean.setPicUrl(img_url_base + "-" + i + ".jpg");
                    mMainPics.add(bean);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void setListener() {
        empty_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empty_layout.setVisibility(View.GONE);
                new JsoupTask().execute(mNowHerfUrl);
            }
        });
    }


    @Override
    protected void initToolbar() {
        super.initToolbar(toolbar);
    }


//    class ViewPagerAdapter extends PagerAdapter {
//
//        private ArrayList<MainPicBean> picBeans;
//
//        public ViewPagerAdapter() {
//            picBeans = new ArrayList<MainPicBean>();
//        }
//
//        /**
//         * Return the number of views available.
//         */
//        @Override
//        public int getCount() {
//            return picBeans.size();
//        }
//
//        public void setData(ArrayList<MainPicBean> beans) {
//            if (beans != null && !beans.isEmpty()) {
//                picBeans.clear();
//                picBeans.addAll(beans);
//            }
//            notifyDataSetChanged();
//        }
//
//
//        /**
//         * Create the page for the given position.  The adapter is responsible
//         * for adding the view to the container given here, although it only
//         * must ensure this is done by the time it returns from
//         * {@link #finishUpdate(ViewGroup)}.
//         *
//         * @param container The containing View in which the page will be shown.
//         * @param position  The page position to be instantiated.
//         * @return Returns an Object representing the new page.  This does not
//         * need to be a View, but can be some other container of the page.
//         */
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            MainPicBean picBean = picBeans.get(position);
//            PhotoView photoView = new PhotoView(container.getContext());
//            mBitmapUtils.display(photoView, picBean.getPicUrl());
//            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            return photoView;
//        }
//
//        /**
//         * Remove a page for the given position.  The adapter is responsible
//         * for removing the view from its container, although it only must ensure
//         * this is done by the time it returns from {@link #finishUpdate(ViewGroup)}.
//         *
//         * @param container The containing View from which the page will be removed.
//         * @param position  The page position to be removed.
//         * @param object    The same object that was returned by
//         *                  {@link #instantiateItem(View, int)}.
//         */
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        /**
//         * Determines whether a page View is associated with a specific key object
//         * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
//         * required for a PagerAdapter to function properly.
//         *
//         * @param view   Page View to check for association with <code>object</code>
//         * @param object Object to check for association with <code>view</code>
//         * @return true if <code>view</code> is associated with the key object <code>object</code>
//         */
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//    }
}
