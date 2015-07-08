package com.fwhl.pretty.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fwhl.pretty.R;
import com.fwhl.pretty.bean.CategoryBean;
import com.fwhl.pretty.inter.OnItemListener;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Terry.Chen on 2015/6/29 18:21.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class CategoryMainAdapter extends RecyclerView.Adapter<CategoryMainAdapter.MainHolder> {
    
    private LayoutInflater mLayoutInflater;
    private BitmapUtils mBitmapUtil;
    
    private ArrayList<CategoryBean> mPicBeans;

    private OnItemListener mOnItemLisnter;
    
    public void setOnItemListener(OnItemListener listener) {
        this.mOnItemLisnter = listener;
    }
    
    
    
    public CategoryMainAdapter(Context context) {
        mPicBeans = new ArrayList<CategoryBean>();
        mLayoutInflater = LayoutInflater.from(context);
        mBitmapUtil = new BitmapUtils(context);
        mBitmapUtil.configDefaultLoadingImage(R.mipmap.default_pretty_loding);
        mBitmapUtil.configMemoryCacheEnabled(true);
        mBitmapUtil.configDiskCacheEnabled(true);
    }
    
    public void setData(ArrayList<CategoryBean> beans) {
        if(beans != null && !beans.isEmpty()) {
            mPicBeans.clear();
            mPicBeans.addAll(beans);
        }
        notifyDataSetChanged();
    }
    
    public void addData(ArrayList<CategoryBean> beans) {
        if(beans != null && !beans.isEmpty()) {
            mPicBeans.addAll(beans);
        }
        notifyDataSetChanged();
    }

    @Override
    public MainHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = mLayoutInflater.inflate(R.layout.category_listitem, null);
        return new MainHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainHolder viewHolder, final int position) {
        CategoryBean picBean = mPicBeans.get(position);
        mBitmapUtil.display(viewHolder.getImg_view(), picBean.getImg_url());
        viewHolder.getTitle_text().setText(picBean.getTitle());
        viewHolder.getImg_view().setOnClickListener(new View.OnClickListener(){

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                if(mOnItemLisnter != null) {
                    mOnItemLisnter.OnItemClickLister(v, viewHolder.getLayoutPosition());
                    mOnItemLisnter.OnLongPressListener(v, viewHolder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPicBeans.size();
    }
    
    public static class MainHolder extends RecyclerView.ViewHolder{

        ImageView img_view;
        TextView title_text;
        public MainHolder(View itemView) {
            super(itemView);
            img_view = (ImageView) itemView.findViewById(R.id.img_view);
            title_text = (TextView) itemView.findViewById(R.id.title_text);
        }

        public ImageView getImg_view() {
            return img_view;
        }

        public TextView getTitle_text() {
            return title_text;
        }
    }
}
