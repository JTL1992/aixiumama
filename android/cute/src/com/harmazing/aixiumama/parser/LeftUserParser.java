package com.harmazing.aixiumama.parser;

import android.util.Log;

import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.model.LeftUser;
import com.harmazing.aixiumama.utils.GsonUtil;

/**
 * Created by dell on 2014/12/19.
 */
public class LeftUserParser extends BaseParser {
    private static final String TAG = "LeftParser";
    public Object parseJSON(String str) {
        try {
            LeftUser data = GsonUtil.json2Bean(str, LeftUser.class);
            //Log.i(TAG, "解析的bean是..." + data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "解析数据失败...");
        }
        return null;
    }
}
