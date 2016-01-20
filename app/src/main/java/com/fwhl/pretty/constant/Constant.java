package com.fwhl.pretty.constant;

import android.os.Environment;

/**
 * Created by Terry.Chen on 2015/6/29 16:33.
 * Description:常量类
 * Email:cxm_lmz@163.com
 */
public class Constant {
    public final static String SHAREPREFENCE_NAME = "sp_text";
    public final static String JSOUP_URL = "http://www.mnsfz.com/";
    public final static String JSOUP_SIMEI_URL = "http://www.henha.com/";

    public final static String HOME_FRAGMENT = "home_fragment";
    public final static String CATEGORY_FRAGMENT = "category_fragment";


    //美女分类图床链接,Json形式存
    public final static String SIWA = "http://imgchr.com/images/siwa.jpg";
    public final static String COSPLAY = "http://imgchr.com/images/cosplay.jpg";
    public final static String SHOWGIRL = "http://imgchr.com/images/showgirl.jpg";
    public final static String XINGGAN = "http://imgchr.com/images/xinggan.jpg";
    public final static String WANGLUO = "http://imgchr.com/images/wangluo.jpg";

    public final static String Category_Josn = "{\"list\":[{\"title\":\"丝袜美腿\",\"img_url\":\"http://imgchr.com/images/siwa.jpg\",\"href_url\":\"http://www.simei8.com/html/siwameitui/\",\"type\":0},{\"title\":\"Cosplay\",\"img_url\":\"http://imgchr.com/images/cosplay.jpg\",\"href_url\":\"http://www.simei8.com/html/cosplay/\",\"type\":1},{\"title\":\"ShowGirl\",\"img_url\":\"http://imgchr.com/images/showgirl.jpg\",\"href_url\":\"http://www.simei8.com/html/showgirl/\",\"type\":2},{\"title\":\"性感美女\",\"img_url\":\"http://imgchr.com/images/xinggan.jpg\",\"href_url\":\"http://www.simei8.com/html/xinggan/\",\"type\":3},{\"title\":\"网络美女\",\"img_url\":\"http://imgchr.com/images/wangluo.jpg\",\"href_url\":\"http://www.simei8.com/html/wangluo/\",\"type\":4}]}";
    
    private static String base_dir = Environment.getExternalStorageDirectory().getPath();
    
    public final static String CaceFileDir = base_dir+"/DCIM/1024MM";
    public final static String UPDATE_DIR = base_dir+"/1024MM";
    
    public final static String YOUMI_APPID = "0f193c8620155be1";
    public final static String YOUMI_APPSECRET = "8104d1e7e244857b";
    public final static String UMENG_APPID = "55a5e99567e58edc7f002e51";
}
