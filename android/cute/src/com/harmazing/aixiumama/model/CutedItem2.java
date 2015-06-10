package com.harmazing.aixiumama.model;

/**
 * Created by Administrator on 2014/12/31.
 */
public class CutedItem2 {
    private String userName;
    private String ImageUrl;
    private String description;
    private String IconUrl;
    private String date;
    private int cuteId;
    private int userId;
    private String title;
    public CutedItem2 (String userName,String description,String ImageUrl,String IconUrl,String date,int cuteId,int userId,String title){
        this.userName = userName;
        this.description = description;
        this.IconUrl = IconUrl;
        this.ImageUrl = ImageUrl;
        this.date = date;
        this.cuteId = cuteId;
        this.userId = userId;
        this.title = title;
    }
    public int getCuteId() {
        return cuteId;
    }

    public int getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }
}
