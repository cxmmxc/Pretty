package com.fwhl.pretty.ui;

import android.support.v7.widget.Toolbar;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.bean.CategoryBean;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

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
    
    private CategoryBean mCateBean;
    
    
    @Override
    protected void initView() {
        
    }

    @Override
    protected void initData() {
        mCateBean = (CategoryBean) getIntent().getSerializableExtra("bean");
    }

    @Override
    protected void setListener() {
        
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(toolbar);
        toolbar.setTitle(mCateBean.getTitle());
    }
}
