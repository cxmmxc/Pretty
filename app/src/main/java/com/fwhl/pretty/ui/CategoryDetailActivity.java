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

    @Override
    protected void initView() {
        mGridView = pull_refresh_grid.getRefreshableView();
    }

    @Override
    protected void initData() {
        mCateBean = (CategoryBean) getIntent().getSerializableExtra("bean");
        mStart_Href = mCateBean.getHref_url();
        LogUtils.v("href="+mStart_Href);
        toolbar.setTitle(mCateBean.getTitle());
        mCateAdapter = new CategoryDetailAdapter(this);
        mGridView.setAdapter(mCateAdapter);
        getDetailData();
    }


    private void getDetailData() {

        AsyncTask<Integer, Integer, Document> asyncTask = new AsyncTask() {
            String url = "";

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
                if (mStart == 1) {
                    url = mStart_Href;
                } else {
                    //进行URL分解重组
                    url = mStart_Href + "index" + mStart + ".html";
                }
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
                    Document document = (Document) o;
                    if (mStart == 1) {
                        //解析一共多少页
                        Element element = document.select("div.page").first();
                        Element element2 = element.child(0);
                        Element element3 = element2.child(element2.childNodeSize() - 2);
                        String str_more = element3.text();
                        if (str_more.contains("...")) {
                            mPageSize = Integer.parseInt(str_more.substring(3, str_more.length()));
                        } else {
                            mPageSize = Integer.parseInt(str_more);
                        }
                        resolveData(document);
                    } else {
                        resolveData(document);
                    }
                } else {
                    if(isFirstEnter) {
                        empty_layout.setVisibility(View.VISIBLE);
                    }
                    pull_refresh_grid.onRefreshComplete();
                }
                isFirstEnter = false;
            }

            @Override
            protected Object doInBackground(Object[] params) {
                Document document = null;
                try {
                    document = Jsoup.connect(
                            url).timeout(8000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return document;
            }


        };
        asyncTask.execute();
    }

    private String mHref_Base_Url = "http://www.simei8.com";

    private void resolveData(Document document) {
        ArrayList<MainPicBean> beans = new ArrayList<MainPicBean>();
        Element element1 = document.select("div#tu").first();
        Element element2 = element1.child(0);
        Elements elements = element2.children();
        int size = elements.size();
        for (int i = 0; i < size-2; i++) {
            MainPicBean bean = new MainPicBean();
            Element element3 = elements.get(i).child(0);
            String href = element3.attr("href");
            String title = element3.attr("title");
            String src_img = element3.getElementsByAttribute("src").first().attr("src");
            bean.setTitle(title);
            bean.setPicUrl(src_img);
            bean.setType(mCateBean.getTitle());
            bean.setHrefUrl(mHref_Base_Url + href);
            beans.add(bean);
        }
        pull_refresh_grid.onRefreshComplete();
        if (mStart == 1) {
            mCateAdapter.setData(beans);
        } else {
            mCateAdapter.addData(beans);
        }

    }
    
    Handler mHandler = new Handler(){
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
                getDetailData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> pullToRefreshBase) {
                //上啦加载
                if (mStart >= mPageSize) {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                } else {
                    mStart++;
                    getDetailData();
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
