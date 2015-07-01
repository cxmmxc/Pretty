package com.fwhl.pretty.inter;

import android.view.View;

/**
 * Created by Terry.Chen on 2015/6/29 18:49.
 * Description:
 * Email:cxm_lmz@163.com
 */
public interface OnItemListener {
    void OnItemClickLister(View view, int position);
    void OnLongPressListener(View view, int position);
}
