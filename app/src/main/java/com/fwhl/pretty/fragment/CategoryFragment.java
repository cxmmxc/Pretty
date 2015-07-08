package com.fwhl.pretty.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fwhl.pretty.BaseFragment;
import com.fwhl.pretty.R;
import com.fwhl.pretty.adapter.CategoryMainAdapter;
import com.fwhl.pretty.bean.CateGoryBeans;
import com.fwhl.pretty.bean.CategoryBean;
import com.fwhl.pretty.constant.Constant;
import com.fwhl.pretty.inter.OnItemListener;
import com.fwhl.pretty.ui.CategoryDetailActivity;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by Terry.Chen on 2015/7/7 15:08.
 * Description:美女分类Fragment
 * Email:cxm_lmz@163.com
 */
public class CategoryFragment extends BaseFragment {

    @ViewInject(R.id.recyclerView)
    RecyclerView recyclerView;
    
    private ArrayList<CategoryBean> mCatebeans;
    
    private RecyclerView.LayoutManager mLayoutManager;
    
    private CategoryMainAdapter mCateAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_fragment, null);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    protected void initData() {
        Gson gson = new Gson();
        CateGoryBeans cateGoryBeans = gson.fromJson(Constant.Category_Josn, CateGoryBeans.class);
        mCatebeans = cateGoryBeans.getList();
        
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        mCateAdapter = new CategoryMainAdapter(mContext);
        recyclerView.setAdapter(mCateAdapter);
        mCateAdapter.setData(mCatebeans);

    }

    @Override
    protected void setListener() {
        mCateAdapter.setOnItemListener(new OnItemListener() {
            @Override
            public void OnItemClickLister(View view, int position) {
                //进入新的刷新页面
                CategoryBean categoryBean = mCatebeans.get(position);
                Intent intent = new Intent();
                intent.setClass(mContext, CategoryDetailActivity.class);
                intent.putExtra("bean", categoryBean);
                startActivity(intent);
            }

            @Override
            public void OnLongPressListener(View view, int position) {

            }
        });
    }
    
}
