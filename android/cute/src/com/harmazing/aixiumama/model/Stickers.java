package com.harmazing.aixiumama.model;

/**
 * Created by Lyn on 2014/11/25.
 */
public class Stickers {
    int id;
    String name,url,banner,icon;

    public Stickers(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBanner() {
        return banner;
    }

    public String getIcon() {
        return icon;
    }
}
