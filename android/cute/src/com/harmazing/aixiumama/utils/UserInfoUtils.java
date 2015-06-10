package com.harmazing.aixiumama.utils;

import android.content.Context;

/**
 * Created by Lyn on 2014/11/7.
 */
public class UserInfoUtils {

    /***************用户名**********************/
    public static void setUserNmae(Context context,String name){
        AppSharedPref.newInstance(context).setUserName(name);
    }

    /***************粉丝数**********************/
    public static void setFansCount(Context context,int count){
        AppSharedPref.newInstance(context).setDataInt("FansCount",count);
    }

    /***************关注数*********************/
    public static void setFollowsCount(Context context,int count){
        AppSharedPref.newInstance(context).setDataInt("FollowsCount",count);
    }

    /***************签名*********************/
    public static void setDescription(Context context,String name){
        AppSharedPref.newInstance(context).setData("description",name);
    }

    /***************被cute数*********************/
    public static void setLikedCount(Context context,int count){
        AppSharedPref.newInstance(context).setDataInt("liked",count);
    }


    /***************来访数*********************/
    public static void setVisitCount(Context context,int count){
        AppSharedPref.newInstance(context).setDataInt("visit",count);
    }

    /***************头像url*********************/
    public static void setIconUrl(Context context,String url){
        AppSharedPref.newInstance(context).setData("userIcon",url);
    }

    /***************头像url*********************/
    public static void setBabyInfo(Context context,String babyinfo){
        AppSharedPref.newInstance(context).setData("babyinfo",babyinfo);

    }






    public static String getBabyInfo(Context context){
        return AppSharedPref.newInstance(context).getData("babyinfo");
    }


    public static String getIconUrl(Context context){
        return AppSharedPref.newInstance(context).getData("userIcon");
    }

    public static int getVisitCount(Context context){
        return AppSharedPref.newInstance(context).getDataInt("visit");
    }

    public static int getLikedCount(Context context){
        return AppSharedPref.newInstance(context).getDataInt("liked");
    }


    public static String getDescription(Context context){
        return AppSharedPref.newInstance(context).getData("description");
    }
    public static String getUserName(Context context){
        return AppSharedPref.newInstance(context).getUserName();
    }

    public static int getFansCount(Context context){
        return AppSharedPref.newInstance(context).getDataInt("FansCount");
    }

    public static int getFollowsCount(Context context){
        return AppSharedPref.newInstance(context).getDataInt("FollowsCount");
    }
     public static void setBirthday(Context context,String birth){
        AppSharedPref.newInstance(context).setBirthday(birth);
     }
    public static void setSex(Context context, int sex){
        AppSharedPref.newInstance(context).setSexBaby(sex);
    }
    public static void setUserSex(Context context, int sex){
        AppSharedPref.newInstance(context).setUserSex(sex);
    }
    public static int getUserSex(Context context){
        return AppSharedPref.newInstance(context).getUserSex();
    }
    public static String getDir(Context context){
        return AppSharedPref.newInstance(context).getDir();
    }
    public static void setDir(Context context ,String dir){
       AppSharedPref.newInstance(context).setDir(dir);
    }
}
