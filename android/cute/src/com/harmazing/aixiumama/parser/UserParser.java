package com.harmazing.aixiumama.parser;

import android.util.Log;

import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.model.User;
import com.harmazing.aixiumama.utils.GsonUtil;

/**
 * Created by gyw on 2014/12/17.
 */
public class UserParser extends BaseParser<User> {
    private static final String TAG = "UserParser";

    @Override
    public User parseJSON(String str) {
        try {
            User data = GsonUtil.json2Bean(str, User.class);
            //Log.i(TAG,"解析的bean是..." + data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "解析数据失败...");
        }
        return null;
    }
}
