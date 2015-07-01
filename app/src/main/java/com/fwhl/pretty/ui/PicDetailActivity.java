package com.fwhl.pretty.ui;

import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.bean.MainPicBean;
import com.fwhl.pretty.view.HackyViewPager;
import com.fwhl.pretty.view.LoadingDialog;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Terry.Chen on 2015/6/29 19:58.
 * Description:图片的详细信息，轮训查看
 * Email:cxm_lmz@163.com
 */
@ContentView(R.layout.picdetail_layout)
public class PicDetailActivity extends BaseActivity {
    private MainPicBean mPicBean;
    private String mNowHerfUrl;
    @ViewInject(R.id.viewpager)
    HackyViewPager viewpager;

    private ArrayList<MainPicBean> mMainPics;

    private ViewPagerAdapter mAdapter;
    
    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mPicBean = (MainPicBean) getIntent().getSerializableExtra("bean");
        this.setTitle(mPicBean.getTitle());
        //通过url进行解析
        mNowHerfUrl = mPicBean.getHrefUrl();

        mBitmapUtils = new BitmapUtils(this);
        mBitmapUtils.configDiskCacheEnabled(true);
        mBitmapUtils.configMemoryCacheEnabled(true);
        mBitmapUtils.configDefaultLoadingImage(R.mipmap.default_pretty_loding);

        mMainPics = new ArrayList<MainPicBean>();
        mAdapter = new ViewPagerAdapter();
        viewpager.setAdapter(mAdapter);
        new JsoupTask().execute(mNowHerfUrl);
    }

    class JsoupTask extends AsyncTask<String, Integer, Object> {

        final LoadingDialog dialog = new LoadingDialog(PicDetailActivity.this);

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
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
            dialog.dismiss();
            if (mMainPics.isEmpty())
                return;
            Log.e("cxm",mMainPics.toString());
            mAdapter.setData(mMainPics);
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
                
                
                Document document = Jsoup.connect(url).get();
                Element first = document.select("strong.diblcok").first();
                String[] strings = first.toString().split("/");
                String str = strings[strings.length - 2];
                str = str.substring(0, str.length() - 1);
                Element element = document.select("div#imgString").first();
                String attr = element.child(0).attr("src");
                MainPicBean picBean_1 = new MainPicBean();
                picBean_1.setPicUrl(JpgToChange(attr));
                mMainPics.add(picBean_1);

                int html_size = Integer.parseInt(str);
                Log.e("cxm",html_size+""+","+url);
                for (int i = 2; i <= html_size; i++) {
                    MainPicBean picBean_child = new MainPicBean();
                    String child_url = url.replace(".html", "") + "-" + i + ".html";
                    Log.e("cxm",child_url);
                    Document document_child = Jsoup.connect(child_url).get();
                    Element element_child = document_child.select("div#imgString").first();
                    String attr_child = element_child.child(0).attr("src");
                    picBean_child.setPicUrl(JpgToChange(attr_child));
                    mMainPics.add(picBean_child);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    private String JpgToChange(String jpgUrl) {

        String url_title = "http://mnsfz-img.xiuna.com";
        String[] split_1 = jpgUrl.split("/");
        StringBuilder sb_1 = new StringBuilder();
        for(int i=0; i<split_1.length; i++) {
            if(i>2) {
                sb_1.append("/"+split_1[i]);
            }
        }

       return url_title+sb_1.toString();
    }

    @Override
    protected void setListener() {

    }

    class ViewPagerAdapter extends PagerAdapter {

        private ArrayList<MainPicBean> picBeans;

        public ViewPagerAdapter() {
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
            MainPicBean picBean = picBeans.get(position);
            PhotoView photoView = new PhotoView(container.getContext());
            mBitmapUtils.display(photoView, picBean.getPicUrl());
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
