package com.harmazing.aixiumama.model;

import java.util.List;

/**
 * Created by JTL on 2014/11/27.
 * 实现好友列表
 */
public class Group
{
    //分组名称
    private String mGroupName;
    //分组项目
    private List<GroupItem> mGroupItems;
    //分组类型  0:微博，1手机联系人
    private int type;
    public Group(String GroupName, List<GroupItem> GroupItems, int type)
    {
        this.mGroupName = GroupName;
        this.mGroupItems = GroupItems;
        this.type = type;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String mGroupName) {
        this.mGroupName = mGroupName;
    }

    public List<GroupItem> getGroupItems() {
        return mGroupItems;
    }

    public void setGroupItems(List<GroupItem> mItems) {
        this.mGroupItems = mItems;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
