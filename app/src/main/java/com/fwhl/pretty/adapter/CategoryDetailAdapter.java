package com.fwhl.pretty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fwhl.pretty.R;
import com.fwhl.pretty.bean.MainPicBean;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Terry.Chen on 2015/7/9 14:41.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class CategoryDetailAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    private ArrayList<MainPicBean> mCateBeans;


    private BitmapUtils mBitmapUtil;
    public CategoryDetailAdapter(Context context) {
        mContext = context;
        mCateBeans = new ArrayList<MainPicBean>();
        mInflater = LayoutInflater.from(context);
        mBitmapUtil = new BitmapUtils(context);
        mBitmapUtil.configDefaultLoadingImage(R.mipmap.default_pretty_loding);
        mBitmapUtil.configMemoryCacheEnabled(true);
        mBitmapUtil.configDiskCacheEnabled(true);
    }


    public void setData(ArrayList<MainPicBean> beans) {
        if(beans != null && !beans.isEmpty()) {
            mCateBeans.clear();
            mCateBeans.addAll(beans);
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<MainPicBean> beans) {
        if(beans != null && !beans.isEmpty()) {
            mCateBeans.addAll(beans);
        }
        notifyDataSetChanged();
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mCateBeans.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public MainPicBean getItem(int position) {
        return mCateBeans.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        MainPicBean bean = getItem(position);
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.main_item, null);
            viewHolder.imageview = (ImageView) convertView.findViewById(R.id.imageview);
            viewHolder.textview_descrip = (TextView) convertView.findViewById(R.id.textview_descrip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        mBitmapUtil.display(viewHolder.imageview, bean.getPicUrl());
        viewHolder.textview_descrip.setText(bean.getTitle());
        return convertView;
    }
    
    class ViewHolder{
        ImageView imageview;
        TextView textview_descrip;
    }
}
