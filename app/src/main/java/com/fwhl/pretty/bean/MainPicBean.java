package com.fwhl.pretty.bean;

import java.io.Serializable;

/**
 * Created by Terry.Chen on 2015/6/29 18:35.
 * Description:首页的图片Bean
 * Email:cxm_lmz@163.com
 */
public class MainPicBean implements Serializable{
    private String picUrl;
    private String title;
    private String hrefUrl;
    private String type;//丝袜美腿、showgirl....

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHrefUrl() {
        return hrefUrl;
    }

    public void setHrefUrl(String hrefUrl) {
        this.hrefUrl = hrefUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MainPicBean{" +
                "picUrl='" + picUrl + '\'' +
                ", title='" + title + '\'' +
                ", hrefUrl='" + hrefUrl + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
