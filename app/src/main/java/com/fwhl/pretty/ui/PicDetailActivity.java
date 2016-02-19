package com.fwhl.pretty.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
        SpotManager.getInstance(this).loadSpotAds();
        // 插屏出现动画效果，0:ANIM_NONE为无动画，1:ANIM_SIMPLE为简单动画效果，2:ANIM_ADVANCE为高级动画效果
        SpotManager.getInstance(this).setAnimationType(SpotManager.ANIM_ADVANCE);
        // 设置插屏动画的横竖屏展示方式，如果设置了横屏，则在有广告资源的情况下会是优先使用横屏图。
        SpotManager.getInstance(this).setSpotOrientation(
                SpotManager.ORIENTATION_PORTRAIT);
        showSpotAds();
    }

    private void showSpotAds() {
        // 展示插播广告，可以不调用loadSpot独立使用
        SpotManager.getInstance(mContext).showSpotAds(
                mContext, new SpotDialogListener() {
                    @Override
                    public void onShowSuccess() {
                        Log.i("YoumiAdDemo", "展示成功");
                    }

                    @Override
                    public void onShowFailed() {
                        Log.i("YoumiAdDemo", "展示失败");
                    }

                    @Override
                    public void onSpotClosed() {
                        Log.i("YoumiAdDemo", "展示关闭");
                    }

                }); // //

    }


    @Override
    public void onBackPressed() {
        // 如果有需要，可以点击后退关闭插播广告。
        if (!SpotManager.getInstance(this).disMiss()) {
            // 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
            super.onBackPressed();
        }
    }


    @Override
    protected void onStop() {
        // 如果不调用此方法，则按home键的时候会出现图标无法显示的情况。
        SpotManager.getInstance(this).onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        SpotManager.getInstance(this).onDestroy();
        super.onDestroy();
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

    class JsoupTask extends AsyncTask<String, Integer, Document> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            progress.setVisibility(View.GONE);
            if (document == null) {
                return;
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/pretty_detail.txt");

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter writer = new FileWriter(file.getAbsolutePath());
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                    bufferedWriter.write(document.toString());
                    bufferedWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            Element page_Elem = document.select("div.article_page").first();
            String page_str = page_Elem.child(0).child(0).select("a").first().ownText();
            int total = getPageTotal(page_str);

            Element toElem = document.select("div.ArticleImageBox").first();
            Element img_Elem = toElem.child(0).select("img[src]").first();
            String src_url = img_Elem.attr("src");
            String[] img_bages_strs = src_url.split("-");
            String img_baseurl = img_bages_strs[0];
            for (int i = 1; i <= total * 2; i++) {
                MainPicBean bean = new MainPicBean();
                bean.setPicUrl(img_baseurl + "-" + i + ".jpg");
                mMainPics.add(bean);
            }
            if (mMainPics.isEmpty()) {
                empty_layout.setVisibility(View.VISIBLE);
                return;
            }
            Log.e("cxm", mMainPics.toString());
            pagesize_text.setText(mMainPics.size() + "p");
            mPicAdapter.setData(mMainPics);

            /*Element select = document.select("meta[content]").last();
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
            if (mMainPics.isEmpty()) {
                empty_layout.setVisibility(View.VISIBLE);
                return;
            }
            Log.e("cxm", mMainPics.toString());
            pagesize_text.setText(mMainPics.size() + "p");
            mPicAdapter.setData(mMainPics);*/
        }

        @Override
        protected Document doInBackground(String[] params) {
            String url = params[0];
            try {
                Document document = Jsoup.connect(url).timeout(8000).get();
                return document;
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


    private int getPageTotal(String str) {
        //共30页：----将30分割出来
        String[] ye_strs = str.split("页");
        String gong_strs = ye_strs[0];
        String count_str = gong_strs.substring(1, gong_strs.length());
        return Integer.parseInt(count_str);
    }


}
