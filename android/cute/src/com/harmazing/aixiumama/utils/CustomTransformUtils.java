package com.harmazing.aixiumama.utils;

import com.harmazing.aixiumama.model.LeftUser;

import java.util.Calendar;

/**
 * Created by dell on 2014/12/23.
 */
public class CustomTransformUtils {

    public static String data2BabyInfo(LeftUser.Babies babies) {
        String str = "";
        if(babies.gender.equals(1)){
            str = date2Age(babies.birthday)  + "  男";
        }else if(babies.gender.equals(2)){
            str = date2Age(babies.birthday)  + "  女";
        }else{
            str = date2Age(babies.birthday);
        }
        return str;
    }

    /**
     * 日期(2014-1-1)转年龄(1年1个月)
     * @param date
     * @return
     */
    public static String date2Age(String date){
        if(date.equals("null")){
            return "未输入baby出生日期";
        }
        int year = Integer.parseInt(date.split("-")[0]);
        int mouth = Integer.parseInt(date.split("-")[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        year = calendar.get(Calendar.YEAR) - year;
        mouth = Math.abs(mouth - calendar.get(Calendar.MONTH) - 1);
        String age;
        if(year == 0){
            age = mouth + "个月";
        }else if(year < 0){
            age = "预产期" + mouth + "个月";
        }
        else{
            age = year + "年" + mouth + "个月";
        }
        return age;
    }
}
