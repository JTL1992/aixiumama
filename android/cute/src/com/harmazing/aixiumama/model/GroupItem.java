package com.harmazing.aixiumama.model;

/**
 * Created by JTL on 2014/11/27.
 * QQ好友列表组项目
 */

public class GroupItem
{
    private String Title;
    private String mImage;
    private int type;
    public GroupItem(String mTitle,String mContent)
    {
        this.Title = mTitle;
        this.mImage = mContent;
    }
    public GroupItem(String mTitle,String mContent,int type)
    {
        this.Title = mTitle;
        this.mImage = mContent;
        this.type = type;
    }

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    public String getmImage() {
        return mImage;
    }
    public void setmImage(String content) {
       mImage = content;
    }
    public int getType(){return type;}
}
