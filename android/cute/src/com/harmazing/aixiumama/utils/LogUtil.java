package com.harmazing.aixiumama.utils;

import android.util.Log;

import com.harmazing.aixiumama.application.CuteApplication;

/**
 * 调试时用于日志打印
 */
public class LogUtil {


    public static void v(String tag, Object o){
        if(CuteApplication.isDebug){
            Log.v(tag, ""+o)  ;
        }
    }
    public static void d(String tag, Object o){
        if(CuteApplication.isDebug){
            Log.d(tag, ""+o)  ;
        }
    }
    public static void i(String tag, Object o){
        if(CuteApplication.isDebug){
            Log.i(tag, ""+o)  ;
        }
    }
    public static void w(String tag, Object o){
        if(CuteApplication.isDebug){
            Log.w(tag, ""+o)  ;
        }
    }
    public static void e(String tag, Object o){
        if(CuteApplication.isDebug){
            Log.e(tag, ""+o)  ;
        }
    }

}
