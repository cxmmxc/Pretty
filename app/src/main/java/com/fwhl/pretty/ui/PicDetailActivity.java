package com.fwhl.pretty.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.adapter.PicDetailRecyAdapter;
import com.fwhl.pretty.bean.MainPicBean;
import com.fwhl.pretty.inter.OnItemListener;
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
    @ViewInject(R.id.pagesize_text)
    TextView pagesize_text;
    @ViewInject(R.id.title_text)
    TextView title_text;

    private ArrayList<MainPicBean> mMainPics;

    private RecyclerView.LayoutManager mLayoutManager;

    private PicDetailRecyAdapter mPicAdapter;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        mPicBean = (MainPicBean) getIntent().getSerializableExtra("bean");

        //通过url进行解析
        mNowHerfUrl = mPicBean.getHrefUrl();
        title_text.setText(mPicBean.getTitle());
        toolbar.setTitle(mPicBean.getType());
        mBitmapUtils = new BitmapUtils(this);
        mBitmapUtils.configDiskCacheEnabled(true)
                .configMemoryCacheEnabled(true)
                .configDefaultLoadingImage(R.mipmap.default_pretty_loding)
                .configDefaultLoadFailedImage(R.mipmap.default_meitu_error);

        mLayoutManager = new LinearLayoutManager(this);
        detail_recyView.setLayoutManager(mLayoutManager);
        mPicAdapter = new PicDetailRecyAdapter(this);
        detail_recyView.setAdapter(mPicAdapter);

        mMainPics = new ArrayList<MainPicBean>();
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
            pagesize_text.setText(mMainPics.size() + "p");
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

        mPicAdapter.setOnItemListener(new OnItemListener() {
            @Override
            public void OnItemClickLister(View view, int position) {
                Intent intent = new Intent(mContext, DetailPagerActivity.class);
                intent.putExtra("beans", mMainPics);
                intent.putExtra("position", position);
                intent.putExtra("title", mPicBean.getTitle());
                mContext.startActivity(intent);
            }

            @Override
            public void OnLongPressListener(View view, int position) {

            }
        });
    }


    @Override
    protected void initToolbar() {
        super.initToolbar(toolbar);
    }



}
