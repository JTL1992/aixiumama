package com.harmazing.aixiumama.parser;

import android.util.Log;

import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.utils.GsonUtil;
import com.harmazing.aixiumama.model.RightUserlabels;

/**
 * Created by dell on 2014/12/19.
 */
public class RightUserParser extends BaseParser {
    private static final String TAG = "RightUserParser";
    public Object parseJSON(String str) {
        try {
            RightUserlabels data = GsonUtil.json2Bean(str, RightUserlabels.class);
            //Log.i(TAG, "解析的bean是..." + data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "解析数据失败...");
        }
        return null;
    }
}
