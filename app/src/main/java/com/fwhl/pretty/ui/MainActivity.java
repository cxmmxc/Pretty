package com.fwhl.pretty.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.adapter.RecyeAdapter;
import com.fwhl.pretty.bean.MainPicBean;
import com.fwhl.pretty.constant.Constant;
import com.fwhl.pretty.inter.OnItemListener;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

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
    
    private RecyeAdapter mAdapter;
    private ArrayList<MainPicBean> mMainPics;

    @Override
    protected void initView() {
        title_text.setText(getResources().getString(R.string.app_name));
        serch_btn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        mAdapter = new RecyeAdapter(this);
        mRecyView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyView.setAdapter(mAdapter);
        mMainPics = new ArrayList<MainPicBean>();

        initDot(5);
        
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
                    Document document = (Document) o;
                    Element element = document.select("div#recshowBox").first();
                    Elements childrens = element.children();
                    for (int i = 0; i < childrens.size(); i++) {
                        MainPicBean picBean = new MainPicBean();
                        Element childElement = childrens.get(i);
                        picBean.setHrefUrl("http://www.mnsfz.com" + childElement.attr("href"));
                        Element byAttribute = childElement.getElementsByAttribute("src").first();
                        picBean.setPicUrl(byAttribute.attr("src"));
                        picBean.setTitle(childElement.attr("title"));
                        mMainPics.add(picBean);
                    }
                    mAdapter.setData(mMainPics);
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                Document document = null;
                try {
                    document = Jsoup.connect(
                            Constant.JSOUP_URL).get();
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
                itent.setClass(MainActivity.this, PicDetailActivity.class);
                itent.putExtra("bean", mMainPics.get(position));
                startActivity(itent);
            }

            @Override
            public void OnLongPressListener(View view, int position) {

            }
        });
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
        for (int i=0; i<size; i++) {
            View view = new View(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15,4);
            params.leftMargin = 4;
            view.setLayoutParams(params);
            view.setBackgroundColor(getResources().getColor(R.color.dot_uncheck_color));
            if(i == 3){
                view.setBackgroundColor(getResources().getColor(R.color.title_bg));
            }
            dot_layout.addView(view, i);
        }
    }
}
