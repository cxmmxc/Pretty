package com.fwhl.pretty.util;

/**
 * Created by Terry.Chen on 2015/7/10 13:58.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class StrUtil {
    
    public static String getBeanStrType(String url) {
        if(url.isEmpty()){
            return "";
        }
        String[] strings = url.split("/");
        if("".equals(strings[0])) {
            return strings[2];
        }else {
            return strings[1];
        }
    }

    public static String getTypeToStr(String type) {
        if("siwameitui".equalsIgnoreCase(type)) {
            return "丝袜美腿";
        }else if("cosplay".equalsIgnoreCase(type)) {
            return "Cosplay";
        }else if("showgirl".equalsIgnoreCase(type)) {
            return "Showgirl";
        }else if("xinggan".equalsIgnoreCase(type)){
            return "性感美女";
        }else if("wangluo".equalsIgnoreCase(type)){
            return "网络美女";
        }
        return null;
    }
}
