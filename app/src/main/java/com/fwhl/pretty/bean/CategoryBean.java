package com.fwhl.pretty.bean;

import java.io.Serializable;

/**
 * Created by Terry.Chen on 2015/7/8 14:56.
 * Description:
 * Email:cxm_lmz@163.com
 */
public class CategoryBean implements Serializable {
    private String title;
    private String img_url;
    private String href_url;
    private int type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getHref_url() {
        return href_url;
    }

    public void setHref_url(String href_url) {
        this.href_url = href_url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CategoryBean{" +
                "title='" + title + '\'' +
                ", img_url='" + img_url + '\'' +
                ", href_url='" + href_url + '\'' +
                ", type=" + type +
                '}';
    }
}
