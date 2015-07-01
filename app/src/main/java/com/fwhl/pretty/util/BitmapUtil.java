package com.fwhl.pretty.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Terry.Chen on 2015/6/29 18:50.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class BitmapUtil {
    private static BitmapFactory.Options options;

    public static Bitmap picToBitmap(Context context, int resourse) {
        if(options == null) {
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inInputShareable = true;
            options.inPurgeable = true;
        }
        InputStream inputStream = context.getResources().openRawResource(resourse);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
