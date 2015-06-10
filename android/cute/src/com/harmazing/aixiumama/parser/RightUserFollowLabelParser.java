package com.harmazing.aixiumama.parser;

import android.util.Log;

import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.utils.GsonUtil;
import com.harmazing.aixiumama.model.RightUserFollowLabel;

/**
 * Created by gyw on 2014/12/21.
 */
public class RightUserFollowLabelParser extends BaseParser {
    private static final String TAG = "RightUserFollowLabelParser";
    public Object parseJSON(String str) {
        try {
            RightUserFollowLabel data = GsonUtil.json2Bean(str, RightUserFollowLabel.class);
//            Log.i(TAG, "解析的bean是..." + data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "解析数据失败...");
        }
        return null;
    }
}
