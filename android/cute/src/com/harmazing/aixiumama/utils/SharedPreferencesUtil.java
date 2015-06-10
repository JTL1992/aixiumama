package com.harmazing.aixiumama.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by guoyongwei on 2015/2/14.
 */
public class SharedPreferencesUtil {

    public static SharedPreferences sp;
    public static final String CONFIG = "config";

    public static void SaveStringData(Context conext, String key, String value) {
        if(sp == null) {
            sp = conext.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }

       sp.edit().putString(key, value).commit();
    }

    public static String getStringData(Context conext, String key, String defValue) {
        if(sp == null) {
            sp = conext.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }
}
