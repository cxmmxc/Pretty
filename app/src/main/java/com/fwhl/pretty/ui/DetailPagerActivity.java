package com.fwhl.pretty.ui;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fwhl.pretty.BaseActivity;
import com.fwhl.pretty.R;
import com.fwhl.pretty.bean.MainPicBean;
import com.fwhl.pretty.constant.Constant;
import com.fwhl.pretty.util.ToastAlone;
import com.fwhl.pretty.view.HackyViewPager;
import com.fwhl.pretty.view.LoadingDialog;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Terry.Chen on 2015/7/10 14:52.
 * Description:图片详情的ViewPager展示页面
 * Email:cxm_lmz@163.com
 */
@ContentView(R.layout.detail_pager_layout)
public class DetailPagerActivity extends BaseActivity {

    @ViewInject(R.id.viewpager)
    HackyViewPager viewpager;
    @ViewInject(R.id.current_text)
    TextView current_text;
    @ViewInject(R.id.root_layout)
    RelativeLayout root_layout;
    @ViewInject(R.id.title_text)
    TextView title_text;
    @ViewInject(R.id.back_btn)
    Button back_btn;

    String title;
    private ViewPagerAdapter mAdapter;
    ArrayList<MainPicBean> mMainPics;
    private int position;
    private int totalSize;

    private int mCurrentPosition;
    LoadingDialog loadingDialog;

    @Override
    protected void initView() {
        root_layout.setBackgroundColor(getResources().getColor(android.R.color.black));
        back_btn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {

        loadingDialog = new LoadingDialog(mContext);

        mAdapter = new ViewPagerAdapter();
        viewpager.setAdapter(mAdapter);

        mMainPics = (ArrayList<MainPicBean>) getIntent().getSerializableExtra("beans");
        position = getIntent().getIntExtra("position", 0);
        title = getIntent().getStringExtra("title");
        title_text.setText(title);
        mCurrentPosition = position;
        totalSize = mMainPics.size();
        mAdapter.setData(mMainPics);
        viewpager.setCurrentItem(position);
    }

    @Override
    protected void setListener() {
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                current_text.setText((position + 1) + "/" + totalSize);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.download_text)
    public void downLoadImg(View view) {
        //将图片保存到本地的目录中
        MainPicBean picBean = mMainPics.get(mCurrentPosition);

        //循环cache目录下的文件，看是否存在


        File diskCache = mBitmapUtils.getBitmapFileFromDiskCache(picBean.getPicUrl());
        String fileName = urlToFileName(picBean.getPicUrl());
        boolean isPicCached = isPicCached(fileName);
        if (isPicCached) {
            ToastAlone.show("图片已存在，请查看目录DCIM/1024MM");
        } else {
            File file_dicm = new File(Constant.CaceFileDir + "/" + fileName);
            copyFile(diskCache.getAbsolutePath(), file_dicm.getAbsolutePath(), false);
        }
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName String 原文件路径 如：c:/fqf.txt
     * @param destFileName String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public boolean  copyFile(String srcFileName, String destFileName, boolean overlay) {

        //判断原文件是否存在
        File srcFile = new File(srcFileName);
        if (!srcFile.exists()) {
            ToastAlone.show("保存失败，文件不存在");
            return false;
        } else if (!srcFile.isFile()) {
            ToastAlone.show("保存失败，文件格式错误");
            return false;
        }
        //判断目标文件是否存在
        File destFile = new File(destFileName);
        
        //准备复制文件
        int byteread = 0;//读取的位数
        InputStream in = null;
        OutputStream out = null;
        try {
        //打开原文件
            in = new FileInputStream(srcFile);
            //打开连接到目标文件的输出流
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            //一次读取1024个字节，当byteread为-1时表示文件已经读完
            while ((byteread = in.read(buffer)) != -1) {
            //将读取的字节写入输出流
                out.write(buffer, 0, byteread);
            }
            ToastAlone.show("图片保存成功，请查看目录DCIM/1024MM");
            return true;
        } catch (Exception e) {
            ToastAlone.show("图片保存失败");
            return false;
        } finally {
        //关闭输入输出流，注意先关闭输出流，再关闭输入流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String urlToFileName(String imgUrl) {
        String[] split = imgUrl.split("/");
        String fileName = split[split.length - 1];
        return fileName;
    }


    @OnClick(R.id.paper_text)
    public void setWrallPaper(View view) {
        loadingDialog.show();
//        new WrallTask().execute();
        mHandler.sendEmptyMessageDelayed(1, 1500);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
            MainPicBean picBean = mMainPics.get(mCurrentPosition);
            String diskCache = mBitmapUtils.getBitmapFileFromDiskCache(picBean.getPicUrl()).getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(diskCache);
            try {
                wallpaperManager.setBitmap(bitmap);
                ToastAlone.show("设置成功");
            } catch (IOException e) {
                ToastAlone.show("设置失败");
                e.printStackTrace();
            }
            loadingDialog.dismiss();
        }
    };

    class WrallTask extends AsyncTask<String, Integer, Void> {

        final LoadingDialog loadingDialog = new LoadingDialog(mContext);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!DetailPagerActivity.this.isFinishing())
                loadingDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
            MainPicBean picBean = mMainPics.get(mCurrentPosition);
            String diskCache = mBitmapUtils.getBitmapFileFromDiskCache(picBean.getPicUrl()).getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(diskCache);
            try {
                wallpaperManager.setBitmap(bitmap);
                ToastAlone.show("设置成功");
            } catch (IOException e) {
                ToastAlone.show("设置失败");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!DetailPagerActivity.this.isFinishing())
                loadingDialog.dismiss();
        }
    }

    private boolean isPicCached(String img_url) {
        File file = new File(Constant.CaceFileDir);
        File[] files = file.listFiles();
        if (null != files) {
            int file_size = files.length;
            if (file_size == 0) {
                return false;
            } else {
                for (int i = 0; i < file_size; i++) {
                    if (img_url.equalsIgnoreCase(files[i].getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void initToolbar() {

    }

    class ViewPagerAdapter extends PagerAdapter {

        private ArrayList<MainPicBean> picBeans;

        public ViewPagerAdapter() {
            picBeans = new ArrayList<MainPicBean>();
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return picBeans.size();
        }

        public void setData(ArrayList<MainPicBean> beans) {
            if (beans != null && !beans.isEmpty()) {
                picBeans.clear();
                picBeans.addAll(beans);
            }
            notifyDataSetChanged();
        }


        /**
         * Create the page for the given position.  The adapter is responsible
         * for adding the view to the container given here, although it only
         * must ensure this is done by the time it returns from
         * {@link #finishUpdate(ViewGroup)}.
         *
         * @param container The containing View in which the page will be shown.
         * @param position  The page position to be instantiated.
         * @return Returns an Object representing the new page.  This does not
         * need to be a View, but can be some other container of the page.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MainPicBean picBean = picBeans.get(position);
            PhotoView photoView = new PhotoView(container.getContext());
            mBitmapUtils.display(photoView, picBean.getPicUrl());
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        /**
         * Remove a page for the given position.  The adapter is responsible
         * for removing the view from its container, although it only must ensure
         * this is done by the time it returns from {@link #finishUpdate(ViewGroup)}.
         *
         * @param container The containing View from which the page will be removed.
         * @param position  The page position to be removed.
         * @param object    The same object that was returned by
         *                  {@link #instantiateItem(View, int)}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view   Page View to check for association with <code>object</code>
         * @param object Object to check for association with <code>view</code>
         * @return true if <code>view</code> is associated with the key object <code>object</code>
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
