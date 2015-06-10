package com.harmazing.aixiumama.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2014/12/9.
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = MyPushMessageReceiver.class.getSimpleName();
    boolean D = true;
    /**
     * 调用PushManager.startWork后，sdk将对push server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String version = "";
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + "userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        if(D) {
            Log.v("onBind", responseString);
            Log.v("当前系统","Product Model: " + android.os.Build.MODEL + ","
                    + android.os.Build.VERSION.SDK + ","
                    + android.os.Build.VERSION.RELEASE);
        }
          try{
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                 version = info.versionName;

          }catch (Exception e){
              e.printStackTrace();
          }
        RequestParams params = new RequestParams();
        params.put("name",android.os.Build.MANUFACTURER);
        params.put("model",android.os.Build.MODEL);
        params.put("cute_version",version);
        params.put("type",3);
        params.put("os","android"+android.os.Build.VERSION.RELEASE);
        params.put("baidu_push_channel_id",channelId);
        params.put("baidu_push_user_id",userId);
        String log = "name:"+android.os.Build.MANUFACTURER+"model:"+android.os.Build.MODEL+"cute_version:"+version+"type:"+3+
                "os:"+ "android"+android.os.Build.VERSION.RELEASE+ "baidu_push_channel_id:"+channelId+"baidu_push_user_id:"+userId;
        HttpUtil.addClientHeader(context);
        Log.v("params",log);
        HttpUtil.post(API.BAIDU_PUSH, params , new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                 Log.v("#####",response.toString()+statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.v("statusCode",responseString+statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.v("statusCode",statusCode+"");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.v("statusCode",statusCode+"");
            }
        });

    }
    /**
     * 接收透传消息的函数。
     */
    @Override
    public void onMessage(Context context, String message, String customContentString) {
        String messageString = "透传消息message=" + message + " customContentString="
                + customContentString;
        Log.d("message", messageString);
// 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        if (customContentString != null & customContentString != "") {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     */
    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "通知点击title=" + title + " description="
                + description + " customContent=" + customContentString;
        Log.d("onNotifyClick", notifyString);
// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (customContentString != null & customContentString != "") {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);

                int type = customJson.getInt("type");
                int id = customJson.getInt("id");
                int userId = customJson.getInt("userId");

                if (type == 0){
                    Log.v("系统消息！","");
                Toast.makeText(context,"系统消息",Toast.LENGTH_LONG).show();
                }
                if (type == 1){
                    Intent intent = new Intent(context, PersonActivity.class);
                    intent.putExtra("userID",id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if (type == 2){
                    Intent intent =new Intent(context, PhotoDetailActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("commnet_person_id",String.valueOf(userId));
                    intent.putExtra("commnet_person",description.split(" ")[0]);
                    intent.putExtra("is_from_home",true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } catch (JSONException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * setTags() 的回调函数。
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode + " sucessTags="
                + sucessTags + " failTags=" + failTags + " requestId="
                + requestId;
    }
    /**
     * delTags() 的回调函数。
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode + " sucessTags="
                + sucessTags + " failTags=" + failTags + " requestId="
                + requestId;
    }

    /**
     * listTags() 的回调函数。
     */
    @Override
    public void onListTags(Context context, int errorCode,
                           List<String> tags, String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
    }
    /**
     * PushManager.stopWork() 的回调函数。
     */
    @Override
    public void onUnbind(Context context,int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
    }

}

