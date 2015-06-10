package com.harmazing.aixiumama.utils;

/**
 * Created by JingHuiLiu on 7-16-2014.
 */
public class AppTextUtil {

    public static Boolean isEmpty(String text) {
        Boolean isEmpty = true;
        if(text == null) {
            isEmpty = true;
        } else if(text != null) {
            if(!text.trim().isEmpty()) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

}
