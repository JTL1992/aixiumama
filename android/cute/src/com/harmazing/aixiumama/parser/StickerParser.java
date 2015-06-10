package com.harmazing.aixiumama.parser;

import android.util.Log;

import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.model.Sticker;
import com.harmazing.aixiumama.utils.GsonUtil;

/**
 * Created by guoyongwei on 2015/1/4.
 */

public class StickerParser extends BaseParser<Sticker> {

    private static final String TAG = "StickerParser";

    @Override
    public Sticker parseJSON(String str){
        try{
            Sticker data = GsonUtil.json2Bean(str, Sticker.class);
//            Log.i(TAG,"解析的bean是..." + data);
            return data;
        }catch(Exception e){
            e.printStackTrace();
            Log.i(TAG, "解析数据失败...");
        }
        return null;
    }
}
