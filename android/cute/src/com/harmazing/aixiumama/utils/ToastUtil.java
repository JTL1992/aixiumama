package com.harmazing.aixiumama.utils;

import android.content.Context;
import android.widget.Toast;

import com.harmazing.aixiumama.application.CuteApplication;

public class ToastUtil {

    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, int messageRid) {
        Toast.makeText(context, messageRid, Toast.LENGTH_LONG).show();
    }


    public static void showLongTime(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static void showForDebug(Context context, String message) {
        if (CuteApplication.isDebug) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

}
