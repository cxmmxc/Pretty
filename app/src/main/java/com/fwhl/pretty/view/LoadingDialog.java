package com.fwhl.pretty.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fwhl.pretty.R;


/**
 * Created by Terry.Chen on 2015/5/4 17:13.
 * Description:旋转加载Loading
 * Email:cxm_lmz@163.com
 */
public class LoadingDialog extends Dialog {

    private ProgressBar progress;
    private Context mContext;
    private TextView text_dec;
    public LoadingDialog(Context context) {
        super(context, R.style.style_loadingdialog);
        mContext = context;
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.loading);

        text_dec = (TextView) findViewById(R.id.text_dec);
        progress = (ProgressBar) findViewById(R.id.progress);
    }
    
    public void setText_dec(String src) {
        text_dec.setText(src);
    }

    private void initData() {
//        progress.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim));
    }

}
