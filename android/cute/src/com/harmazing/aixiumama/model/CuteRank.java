package com.harmazing.aixiumama.model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JTL on 2015/1/5.
 */
public class CuteRank {
    String name;
    String age;
    String gender;
    String icon;
    int userId;
    public CuteRank(String name, String age, int gender,String icon,int userId){
        this.name = name;
//        String a[] = age.split("-");
//        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        final Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//        String str = formatter.format(curDate);
//        final String year = str.substring(0,4);
//        final String month = str.substring(5,7);
//        int y = Integer.parseInt(year) - Integer.parseInt(a[0]);
//        int m = Integer.parseInt(month) - Integer.parseInt(a[1]);
//        Log.v("this.year"+"@this.month",year+"@"+month);
//        Log.v("birthday.year"+"@birthday.month",a[0]+"@"+a[1]);
//        if (y == 0)
//            this.age = m+"个月";
//        else
//            this.age = y+"年"+" "+m+"个月";
        this.age = getBabyAge(age);
        if (gender == 1)
          this.gender = "男";
        else
          this.gender = "女";
        this.icon = icon;
        this.userId = userId;
        Log.v("name"+"@age"+"@gender",this.name+"@"+this.age+"@"+this.gender);
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public int getUserId() {
        return userId;
    }
    public static String getBabyAge(String gender){
        String babyAge ="";
        String a[] = gender.split("-");
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        final String year = str.substring(0,4);
        final String month = str.substring(5,7);
        int y = Integer.parseInt(year) - Integer.parseInt(a[0]);
        int m = Integer.parseInt(month) - Integer.parseInt(a[1]);
        String day = a[2];
        Log.v("this.year"+"@this.month",year+"@"+month);
        Log.v("birthday.year"+"@birthday.month",a[0]+"@"+a[1]);
        if (y == 0)
            if (m>0)
            babyAge = m+"个月";
            else
            babyAge = "预产期"+" "+a[1]+"月"+a[2]+"日";
        if (y < 0)
            babyAge = "预产期"+" "+a[1]+"月"+a[2]+"日";
        if (y > 0)
            babyAge = y+"年"+" "+m+"个月";
        return babyAge;
    }
}
