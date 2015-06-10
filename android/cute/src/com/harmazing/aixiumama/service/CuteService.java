package com.harmazing.aixiumama.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.HomeListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.model.Stickers;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class CuteService extends Service {
    public static final String ACTION = "com.harmazing.cute.service.CuteService";
    private static long FRESHTIME = 5000;
    public static final String dir = Environment.getExternalStorageDirectory().getPath();
    public static LinkedList<Stickers> stickersList = new LinkedList<Stickers>();
    public static LinkedList<Stickers> hotStickersList = new LinkedList<Stickers>();
    public static JSONArray stickersJSONarray = new JSONArray();
    public static Boolean isResetMsg = false;
    HomeListAdapter homeListAdapter1,homeListAdapter2;
    int recommendCuteId = 0;
    int attentionCuteId = 0;
    int messageCutedId = 0;
    int messageId = 0;
    public static int newMessageCount = 0;
    public static int newCutedCount = 0;
    String createdDate = null;
    JsonHttpResponseHandler recommnedJsonHttpResponseHandler,attentionJsonHttpResponseHandler,messageJsonHttpResponseHandler;
    JsonHttpResponseHandler msgJsonHttpResponseHandler,cuteJsonHttpResponseHandler;
    private final int NEW_RECOMMEND = 1;
    private final int NEW_MESSAGE = 2;
    private final int NEW_CUTE = 3;
    public CuteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {

        /**
         *  加载贴纸url
         */

        if(stickersList.size() > 0)
            stickersList.clear();

        if(hotStickersList.size() > 0)
            hotStickersList.clear();

        Stickers sticker = new Stickers();
        sticker.setName("贴纸库");
        stickersList.add(sticker);

        //  所有贴纸初始化
        HttpUtil.addClientHeader(getApplicationContext());
        HttpUtil.get(API.ALL_STICKERS,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    stickersJSONarray = array;

                    for(int i=0; i<array.length(); i++)
                    {
                        Stickers stickers = new Stickers();
                        JSONObject obj  = (JSONObject) array.get(i);
                        stickers.setId(obj.getInt("id"));
                        stickers.setBanner(obj.getString("banner_image"));
                        stickers.setIcon(obj.getString("icon"));
                        stickers.setUrl(obj.getString("image"));//把image换了
                        stickers.setName(obj.getString("name"));
                        stickersList.add(stickers);
                        saveStickers2SD(obj.getInt("id")+obj.getString("name"),obj.getString("image"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AppSharedPref.newInstance(getApplicationContext()).setData("stickers",stickersJSONarray.toString());

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("网络异常","读取本地stickersJSONarray");
                try {
                    stickersJSONarray = new JSONArray(AppSharedPref.newInstance(getApplicationContext()).getData("stickers"));
                    for(int i=0; i<stickersJSONarray.length(); i++)
                    {
                        Stickers stickers = new Stickers();
                        JSONObject obj  = (JSONObject) stickersJSONarray.get(i);
                        stickers.setId(obj.getInt("id"));
                        stickers.setBanner(obj.getString("banner_image"));
                        stickers.setIcon(obj.getString("icon"));
                        stickers.setUrl(obj.getString("image"));//把image换了
                        stickers.setName(obj.getString("name"));
                        stickersList.add(stickers);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v("stickersJSONarray",stickersJSONarray.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("网络异常","1");
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.v("网络异常","2");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


        // 热门贴纸 初始化
        HttpUtil.get(API.SINGLE_STICKER + "?key=hot",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));

                    for(int i=0; i<array.length(); i++)
                    {
                        Stickers stickers = new Stickers();
                        JSONObject obj  = (JSONObject) array.get(i);
                        stickers.setId(obj.getInt("id"));
                        stickers.setBanner(obj.getString("banner_image"));
                        stickers.setIcon(obj.getString("icon"));
                        stickers.setUrl(obj.getString("image"));//把image换了
                        stickers.setName(obj.getString("name"));
                        hotStickersList.add(stickers);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

        });
        /**
         * 消息页面cute消息
         */
        cuteJsonHttpResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array =  response.getJSONArray("results");
                    if (response.getInt("count") != 0){
                        if (newCutedCount != response.getInt("count")) {
//                        messageCutedId = array.getJSONObject(0).getInt("id");
                            newCutedCount = response.getInt("count");
                            Log.v("新的messageCutedId:", "" + messageCutedId);
                            Intent intent = new Intent("com.cute.broadcast");
                            intent.putExtra("action", NEW_CUTE);
                            intent.putExtra("count", newCutedCount);
                            sendBroadcast(intent);
                            Log.v("发送了一个cute广播", intent.getAction());
                        }
                    }
                    else{
                        Log.v("没有新的cute推送","!!!!!!!");
                    }
                }catch (Exception e ){
                    Log.v("cuteMessage","失败");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (messageCutedId != 0 )
                            HttpUtil.get(API.NOTIFICATION + "?type=Like&from="+messageCutedId, cuteJsonHttpResponseHandler);
                        else
                            HttpUtil.get(API.NOTIFICATION + "?type=Like", cuteJsonHttpResponseHandler);
                    }
                };
                Timer  mTimer = new Timer();
                mTimer.schedule(task,FRESHTIME);
            }
        };
        /**
         * 首页新消息提醒
         */
        recommnedJsonHttpResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array =  response.getJSONArray("results");
                    if (response.getInt("count") != 0){
                       recommendCuteId = array.getJSONObject(0).getInt("id");
                       Log.v("新的recommendCuteId:",""+recommendCuteId);
                       Intent intent = new Intent("com.cute.broadcast");
                       intent.putExtra("action",NEW_RECOMMEND);
                       sendBroadcast(intent);
                        Log.v("发送了一个推荐广播","?????????");
               }
                    else{
//                        Log.v("没有新的推荐推送","!!!!!!!");
                    }
                }catch (Exception e ){
                    Log.v("recommend","失败");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (recommendCuteId != 0 )
                            HttpUtil.get(API.GET_CUTES + "?key=recommend&format=json&from="+recommendCuteId, recommnedJsonHttpResponseHandler);
                        else
                            HttpUtil.get(API.GET_CUTES + "?key=recommend&format=json", recommnedJsonHttpResponseHandler);
                    }
                };
                Timer  mTimer = new Timer();
                mTimer.schedule(task,FRESHTIME);
            }
        };
        attentionJsonHttpResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    JSONArray array =  response.getJSONArray("results");
                    if (response.getInt("count") != 0){
                        attentionCuteId = array.getJSONObject(0).getInt("id");
                        Log.v("attentionCuteId:",""+attentionCuteId);
                        Intent intent = new Intent("com.cute.broadcast");
                        intent.putExtra("action",NEW_RECOMMEND);
                        sendBroadcast(intent);
                        Log.v("发送了一个关注广播","?????????");
                    }
                    else{
//                        Log.v("没有新的关注推送","!!!!!!!");
                    }
                }catch (Exception e ){
                    Log.v("attention","失败");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (attentionCuteId != 0)
                           HttpUtil.get(API.GET_CUTES + "?key=follow&format=json&from="+attentionCuteId, attentionJsonHttpResponseHandler);
                        else
                            HttpUtil.get(API.GET_CUTES + "?key=follow&format=json", attentionJsonHttpResponseHandler);
                    }
                };
                Timer  mTimer = new Timer();
                mTimer.schedule(task,FRESHTIME);
            }
        };
        HttpUtil.get(API.NOTIFICATION+"?type=Like",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    messageCutedId = array.getJSONObject(0).getInt("id");
                    Log.v("初始的messageCutedId:",""+messageCutedId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (messageCutedId != 0 )
                            HttpUtil.get(API.NOTIFICATION + "?type=Like&from="+messageCutedId, cuteJsonHttpResponseHandler);
                        else
                            HttpUtil.get(API.NOTIFICATION + "?type=Like", cuteJsonHttpResponseHandler);
                    }
                };
                Timer  mTimer = new Timer();
                mTimer.schedule(task,FRESHTIME);
            }
        });
        HttpUtil.get(API.GET_CUTES + "?key=follow&format=json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        try {
                           JSONArray array = new JSONArray(response.getString("results"));
                           attentionCuteId = array.getJSONObject(0).getInt("id");
                            Log.v("初始的attentionCuteId:",""+attentionCuteId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        super.onSuccess(statusCode, headers, response);
                    }

            @Override
            public void onFinish() {
                super.onFinish();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (attentionCuteId != 0)
                            HttpUtil.get(API.GET_CUTES + "?key=follow&format=json&from="+attentionCuteId, attentionJsonHttpResponseHandler);
                        else
                            HttpUtil.get(API.GET_CUTES + "?key=follow&format=json", attentionJsonHttpResponseHandler);
                    }
                };
                Timer  mTimer = new Timer();
                mTimer.schedule(task, FRESHTIME);
            }
        });
        HttpUtil.get(API.GET_CUTES + "?key=recommend&format=json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            JSONArray array =  response.getJSONArray("results");
                             recommendCuteId = array.getJSONObject(0).getInt("id");
                            Log.v("recommendCuteId:",""+recommendCuteId);
                        }catch (Exception e ){
                            Log.v("recommend","失败");
                        }
                    }
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                if (recommendCuteId != 0 )
                                 HttpUtil.get(API.GET_CUTES + "?key=recommend&format=json&from="+recommendCuteId, recommnedJsonHttpResponseHandler);
                                else
                                 HttpUtil.get(API.GET_CUTES + "?key=recommend&format=json", recommnedJsonHttpResponseHandler);
                            }
                        };
                      Timer  mTimer = new Timer();
                        mTimer.schedule(task, FRESHTIME);
                    }
                }

        );
        /**
         * 消息中心的通知
         */
        messageJsonHttpResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array =  response.getJSONArray("results");
                    Log.v("count",""+response.getInt("count"));
                    if (response.getInt("count") != 0){
//                        if (isResetMsg)
//                            messageId = array.getJSONObject(0).getInt("id");
                        if (newMessageCount != response.getInt("count")){
                             newMessageCount = response.getInt("count");
//                        messageId = array.getJSONObject(0).getInt("id");
                             Log.v("messageId:",""+messageId);
                             Intent intent = new Intent("com.cute.broadcast");
                             intent.putExtra("action",NEW_MESSAGE);
                             intent.putExtra("count",newMessageCount);
                             sendBroadcast(intent);
                             Log.v("发送了一个新消息广播","?新消息数量："+newMessageCount);
                        }
                    }
                    else{
                        Log.v("没有新消息推送","!!!!!!!");
                    }
                }catch (Exception e ){
                    Log.v("attention","失败");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (messageId != 0){
                            LogUtil.v("NOTIFICATION+\"?from=",messageId+"");
                            HttpUtil.get(API.NOTIFICATION+"?from="+messageId, messageJsonHttpResponseHandler);
                        }
                        else
                            HttpUtil.get(API.NOTIFICATION, messageJsonHttpResponseHandler);
                    }
                };
                Timer mTimer = new Timer();
                mTimer.schedule(task,FRESHTIME);
            }
        };
        HttpUtil.get(API.NOTIFICATION, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray array = null;
                try {
                    array = new JSONArray(response.getString("results"));
                    messageId = array.getJSONObject(0).getInt("id");
                    Log.v("messageId:",""+messageId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                if (array.length() > 0) {
//                    msgListViewAdapter = new MsgListViewAdapter(array, getActivity());
//                    msgList.getRefreshableView().setAdapter(msgListViewAdapter);
//                }

                super.onSuccess(statusCode, headers, response);

            }
            @Override
            public void onFinish() {
                super.onFinish();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (messageId != 0)
                        HttpUtil.get(API.NOTIFICATION+"?from="+messageId, messageJsonHttpResponseHandler);
                        else
                        HttpUtil.get(API.NOTIFICATION, messageJsonHttpResponseHandler);
                    }
                };
                Timer  mTimer = new Timer();
                mTimer.schedule(task, FRESHTIME);
            }
        });

        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(hotStickersList.size() > 0)
            hotStickersList.clear();
        // 热门贴纸 初始化
        HttpUtil.get(API.SINGLE_STICKER + "?key=hot",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));

                    for(int i=0; i<array.length(); i++)
                    {
                        Stickers stickers = new Stickers();
                        JSONObject obj  = (JSONObject) array.get(i);
                        stickers.setId(obj.getInt("id"));
                        stickers.setBanner(obj.getString("banner_image"));
                        stickers.setIcon(obj.getString("icon"));
                        stickers.setUrl(obj.getString("image"));//把image换了
                        stickers.setName(obj.getString("name"));
                        hotStickersList.add(stickers);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

        });
    }

    private void saveStickers2SD(final String id,final String url){
        if (!BitmapUtil.isExist(dir + "/Pictures/cute/" + id + ".png")) {
            new Thread() {
                @Override
                public void run() {

                        CuteApplication.imageLoader.loadImage(API.STICKERS + url, new ImageLoadingListener() {
                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                try {
                                      BitmapUtil.saveBitmap2sd(bitmap, id);
//                                    BitmapUtil.saveBitmap2sd(BitmapUtil.compress(bitmap, 800, 800), id);
                                    Log.v("贴纸缓存成功","第"+id+"个贴纸");
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                Log.v("贴纸缓存失败","");
                            }

                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }
                        });
//                        BitmapUtil.saveBitmap2sd(bitmap, id);

                    super.run();
                }
            }.start();
        }
    }
    public static void deleteStickersCach(){
        for (int i = 1; i < CuteService.stickersJSONarray.length()+1; i++)
            try {
                String name = CuteService.stickersJSONarray.getJSONObject(i-1).getString("name");
                File cachFile = new File(dir + "/Pictures/cute/"+
                        i+name +".png");
                if(! cachFile.exists()){
                    return;
                }
                else{
                   Boolean isDelete =  cachFile.delete();
                    if (isDelete)
                    Log.v("删除贴纸缓存","第"+i+name+"张");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

    }
}
