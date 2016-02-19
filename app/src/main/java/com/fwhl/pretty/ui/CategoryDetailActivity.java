package com.fwhl.pretty.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.adapter.CategoryDetailAdapter;
import com.fwhl.pretty.bean.CategoryBean;
import com.fwhl.pretty.bean.MainPicBean;
import com.fwhl.pretty.util.ToastAlone;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Terry.Chen on 2015/7/8 16:29.
 * Description:
 * Email:cxm_lmz@163.com
 */
@ContentView(R.layout.category_detail)
public class CategoryDetailActivity extends BaseActivity {
    @ViewInject(R.id.pull_refresh_grid)
    PullToRefreshGridView pull_refresh_grid;
    @ViewInject(R.id.toolbar)
    Toolbar toolbar;
    @ViewInject(R.id.progress)
    ProgressBar progress;
    @ViewInject(R.id.empty_layout)
    LinearLayout empty_layout;

    private GridView mGridView;
    private CategoryBean mCateBean;

    private String mStart_Href;

    private int mPageSize;

    private int mStart = 1;

    private CategoryDetailAdapter mCateAdapter;

    private boolean isFirstEnter = true;

    private String mFirstIndexUrl = "index1.html";

    private String mNextIndexUrl = "";

    @Override
    protected void initView() {
        mGridView = pull_refresh_grid.getRefreshableView();
    }

    @Override
    protected void initData() {
        mCateBean = (CategoryBean) getIntent().getSerializableExtra("bean");
        mStart_Href = mCateBean.getHref_url();
        LogUtils.v("href=" + mStart_Href);
        toolbar.setTitle(mCateBean.getTitle());
        mCateAdapter = new CategoryDetailAdapter(this);
        mGridView.setAdapter(mCateAdapter);
        getDetailData(mFirstIndexUrl);
    }

    class DetailTask extends AsyncTask<String, Integer, Document> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            progress.setVisibility(View.GONE);
            if (document != null) {

                //解析一共多少页
                Element listpage_Elem = document.select("div.list_page").first();
                Element href_Elem = listpage_Elem.select("a[href]").first();
                String to_text = href_Elem.ownText();
                if ("上一页".equals(to_text)) {
                    mNextIndexUrl = null;
                } else {
                    mNextIndexUrl = href_Elem.attr("href");
                }
                resolveData(document);
            } else {
                if (isFirstEnter) {
                    empty_layout.setVisibility(View.VISIBLE);
                }
                pull_refresh_grid.onRefreshComplete();
            }
            isFirstEnter = false;
        }

        @Override
        protected Document doInBackground(String[] params) {
            Document document = null;
            String url = mStart_Href + params[0];
            try {
                document = Jsoup.connect(
                        url).timeout(9000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return document;
        }
    }

    private void getDetailData(String url) {

        new DetailTask().execute(url);
    }


    private void resolveData(Document document) {
        ArrayList<MainPicBean> beans = new ArrayList<MainPicBean>();
        Element ul_Elem = document.select("div.PictureList").first().child(0);
        Elements elements = ul_Elem.children();
        for (Element child : elements) {
            Element ahref_Elem = child.select("a[href]").first();
            String href = mStart_Href + ahref_Elem.attr("href");
            Element img_Elem = child.select("img[src]").first();
            String src_url = img_Elem.attr("src");
            String title = img_Elem.attr("alt");
            MainPicBean bean = new MainPicBean();
            bean.setTitle(title);
            bean.setPicUrl(src_url);
            bean.setType(mCateBean.getTitle());
            bean.setHrefUrl(href);
            beans.add(bean);
        }
        /*int size = elements.size();
        for (int i = 0; i < size - 2; i++) {
            MainPicBean bean = new MainPicBean();
            Element element3 = elements.get(i).child(0);
            String href = element3.attr("href");
            String title = element3.attr("title");
            String src_img = element3.getElementsByAttribute("src").first().attr("src");
            bean.setTitle(title);
            bean.setPicUrl(src_img);
            bean.setType(mCateBean.getTitle());
            bean.setHrefUrl("" + href);
            beans.add(bean);
        }*/
        pull_refresh_grid.onRefreshComplete();
        if (mStart == 1) {
            mCateAdapter.setData(beans);
        } else {
            mCateAdapter.addData(beans);
        }

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastAlone.show("已全部加载!");
            pull_refresh_grid.onRefreshComplete();
        }
    };

    @Override
    protected void setListener() {
        pull_refresh_grid.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> pullToRefreshBase) {
                //下拉刷新
                mStart = 1;
                getDetailData(mFirstIndexUrl);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> pullToRefreshBase) {
                //上啦加载

                if (mNextIndexUrl == null) {
                    mHandler.sendEmptyMessageDelayed(1, 800);
                } else {
                    mStart++;
                    getDetailData(mNextIndexUrl);
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainPicBean mCateAdapterItem = mCateAdapter.getItem(position);
                Intent itent = new Intent();
                itent.setClass(mContext, PicDetailActivity.class);
                itent.putExtra("bean", mCateAdapterItem);
                startActivity(itent);
            }
        });
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
    protected void initToolbar() {
        super.initToolbar(toolbar);
    }
}
