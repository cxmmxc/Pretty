package com.fwhl.pretty.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fwhl.pretty.R;
import com.fwhl.pretty.bean.MainPicBean;
import com.fwhl.pretty.inter.OnItemListener;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Terry.Chen on 2015/6/29 18:21.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class RecyeAdapter extends RecyclerView.Adapter<RecyeAdapter.MainHolder> {
    
    private LayoutInflater mLayoutInflater;
    private BitmapUtils mBitmapUtil;
    
    private ArrayList<MainPicBean> mPicBeans;

    private OnItemListener mOnItemLisnter;
    
    public void setOnItemListener(OnItemListener listener) {
        this.mOnItemLisnter = listener;
    }
    
    
    
    public RecyeAdapter(Context context) {
        mPicBeans = new ArrayList<MainPicBean>();
        mLayoutInflater = LayoutInflater.from(context);
        mBitmapUtil = new BitmapUtils(context);
        mBitmapUtil.configDefaultLoadingImage(R.mipmap.default_pretty_loding);
        mBitmapUtil.configMemoryCacheEnabled(true);
        mBitmapUtil.configDiskCacheEnabled(true);
    }
    
    public void setData(ArrayList<MainPicBean> beans) {
        if(beans != null && !beans.isEmpty()) {
            mPicBeans.clear();
            mPicBeans.addAll(beans);
        }
        notifyDataSetChanged();
    }
    
    public void addData(ArrayList<MainPicBean> beans) {
        if(beans != null && !beans.isEmpty()) {
            mPicBeans.addAll(beans);
        }
        notifyDataSetChanged();
    }

    @Override
    public MainHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = mLayoutInflater.inflate(R.layout.main_item, null);
        return new MainHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainHolder viewHolder, final int position) {
        MainPicBean picBean = mPicBeans.get(position);
        mBitmapUtil.display(viewHolder.getImageview(), picBean.getPicUrl());
        viewHolder.getTextview_descrip().setText(picBean.getTitle());
        viewHolder.getImageview().setOnClickListener(new View.OnClickListener(){

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

        ImageView imageview;
        TextView textview_descrip;
        public MainHolder(View itemView) {
            super(itemView);
            imageview = (ImageView) itemView.findViewById(R.id.imageview);
            textview_descrip = (TextView) itemView.findViewById(R.id.textview_descrip);
        }
        
        public ImageView getImageview(){
            return imageview;
        }
        
        public TextView getTextview_descrip() {
            return textview_descrip;
        }
    }
}
