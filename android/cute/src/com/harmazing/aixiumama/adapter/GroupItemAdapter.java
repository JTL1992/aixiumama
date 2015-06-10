package com.harmazing.aixiumama.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.activity.StartActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.model.GroupItem;
import com.harmazing.aixiumama.utils.UserInfoUtils;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.ContactsUtils;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Administrator on 2014/11/27.
 */
public class GroupItemAdapter extends BaseAdapter {
    private Tencent mTencent;
    private QQAuth mQQAuth;
    private QQShare mQQShare = null;
    private String downloadUrl = "http://www.pgyer.com/app/view/35d1da753cd0b7d727ab54beeb9187d7";
    String title = "宝妈亲，我今天发现有个都是跟你家宝宝同龄的交流平台";
    String summary = "我参考好多同龄妈妈们的经验，以后买东西再也不纠结了！我也晒了这么多，发你看看";
    //  public static String APP_ID = "222222";
//    public static String APP_ID="1103396138";
    private Context mContext;
    private  Activity activity ;
    private List<GroupItem> mItems;
    private int type;
    private Boolean isWeiboTokenValid = false;
    private IUiListener mIuiListener;
    private JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.v("发送微博获取的返回消息",response.toString());
            Toast.makeText(mContext,"微博发送成功",Toast.LENGTH_LONG).show();

        }
    };

    public GroupItemAdapter(Context mContext, List<GroupItem> mItems, int type, Activity activity) {

        this.mContext = mContext;
        this.mItems = mItems;
        this.type = type;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        if (mItems != null)
         return mItems.size();
        else
         return 0;
    }

    @Override
    public Object getItem(int Index) {

        return mItems.get(Index);
    }

    @Override
    public long getItemId(int Index) {
        return Index;
    }

    @Override
    public View getView( final int Index, View mView, ViewGroup mParent) {
//        qqInit();
        if (mView == null)
          mView = LayoutInflater.from(mContext).inflate(R.layout.layout_group_item,null);
        //绑定好友结构中的Title
        ((TextView) mView.findViewById(R.id.group_item_name)).setText(mItems.get(Index).getTitle());
        if (!mItems.get(Index).getmImage().equals(""))
         CuteApplication.downloadIamge(mItems.get(Index).getmImage(), (ImageView) mView.findViewById(R.id.item_head));
         mView.findViewById(R.id.button_invite).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

           if (type == 1){
               sendSMS(title+"\n"+summary+"下载地址:"+downloadUrl,ContactsUtils.contacts.get(mItems.get(Index).getTitle()));
               Toast.makeText(mContext,"成功",Toast.LENGTH_LONG).show();
           }
           else  if (type == 0){
               isTokenValid(Index);
//               String status = null;
//               try {
//                   status = URLEncoder.encode("@"+mItems.get(Index).getTitle()+":"+"推荐应用CUTE给你，标记美好生活，你一定要看看，CUTE中搜"+User.getUserName(mContext)+"，加我！","UTF-8");
//               }catch (Exception e){
//                   Log.v("URLEncoder","error");
//               }
////                 MultipartHttpPost(AppSharedPref.newInstance(mContext).getWeiboToken(),status,getImageByte(R.drawable.icon_person_head));
//               RequestParams params = new RequestParams();
//               Log.v("ImageByte", getImageByte(R.drawable.ic_launcher).length+"");
//               params.put("access_token", AppSharedPref.newInstance(mContext).getWeiboToken());
//               params.put("status", status);
//               params.put("pic",new ByteArrayInputStream(getImageByte(R.drawable.ic_launcher)),"title.png");
//               HttpUtil.post(API.SEND_MESSAGE_TO_WEIBO_FRIENDS, params, jsonHttpResponseHandler);
           }
           else if (mItems.get(Index).getType() == 0){
               String status = null;
               try {
                   status = URLEncoder.encode(title+"\n"+summary+"下载地址："+downloadUrl,"UTF-8");
               }catch (Exception e){
                   Log.v("URLEncoder","error");
               }
//                 MultipartHttpPost(AppSharedPref.newInstance(mContext).getWeiboToken(),status,getImageByte(R.drawable.icon_person_head));
               RequestParams params = new RequestParams();
               params.put("access_token", AppSharedPref.newInstance(mContext).getWeiboToken());
               params.put("status", status);
               Log.v("ImageByte", getImageByte(R.drawable.ic_launcher).length+"");
               params.put("pic",new ByteArrayInputStream(getImageByte(R.drawable.ic_launcher)),"title.png");
               HttpUtil.post(API.SEND_MESSAGE_TO_WEIBO_FRIENDS, params, jsonHttpResponseHandler);
           }
           else if(mItems.get(Index).getType() == 1){
               sendSMS("我CUTE的美好时光，你一定要看！CUTE里搜“"+ UserInfoUtils.getUserName(mContext)+"”，加我！下载地址>>"+downloadUrl,ContactsUtils.contacts.get(mItems.get(Index).getTitle()));
               Toast.makeText(mContext,"成功",Toast.LENGTH_LONG).show();
           }


//            Toast.makeText(mContext,"成功",Toast.LENGTH_LONG).show();
        }





         });

//        View.OnClickListener onContactListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendSMS("!!!!!", ContactsUtils.getAllCallRecords(mContext).get(mItems.get(Index).getTitle()));
//            }
//        };
        //绑定好友结构中的Content
//        CuteApplication.downloadIamge(mItems.get(Index).getmImage(), (ImageView) mView.findViewById(R.id.item_head));
        return mView;
    }
//    View.OnClickListener onContactListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            sendSMS("!!!!!", ContactsUtils.getAllCallRecords(mContext).get(mItems.get(Index).getTitle()));
//        }
//    };
private void qqInit() {
    mQQAuth = QQAuth.createInstance(StartActivity.APP_ID, mContext);
    mTencent = Tencent.createInstance(StartActivity.APP_ID, mContext);
    LogUtil.i("mQQAuth", "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
    LogUtil.i("mQQAuth", "isSessionValid=>" + mQQAuth.isSessionValid());
    mQQShare = new QQShare(mContext, mQQAuth.getQQToken());
//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }
}
    private void sendSMS(String smsBody,String num)

    {

        Uri smsToUri = Uri.parse("smsto:"+num);

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sms_body", smsBody);

        mContext.startActivity(intent);

    }
    private byte []  getImageByte(int ImageId){
        Resources res= mContext.getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, ImageId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG,100,baos);
        ByteArrayBody byteArrayBody = new ByteArrayBody(baos.toByteArray(),"invite.png");

        Log.v("byte",baos.toByteArray().length+"");
        return baos.toByteArray();
    }
    private void MultipartHttpPost(String accessToken,String status,ByteArrayBody pic){
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost postRequest = new HttpPost(API.SEND_MESSAGE_TO_WEIBO_FRIENDS);
        postRequest.setHeader("enctype","multipart/form-data");

//        ContentType contentType = new ContentType("");
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.STRICT);
          Log.v("ContentType()",reqEntity.getContentType().toString());

        try{
            reqEntity.addPart("access_token",new StringBody(accessToken));
            reqEntity.addPart("status",new StringBody(status));
            reqEntity.addPart("pic",pic);
        }catch (Exception e){
            Log.v("无法转码","参数错误");
        }


        postRequest.setEntity(reqEntity);
        try{
            HttpResponse response = httpClient.execute(postRequest);
            HttpEntity httpEntity = response.getEntity();
            String content = EntityUtils.toString(httpEntity);

            Toast.makeText(mContext,"邀请已发出",Toast.LENGTH_LONG).show();
            Log.v("返回参数", content);
        }catch (Exception e){
            e.printStackTrace();
            Log.v("数据解析出现问题","@@@@@@@");
        }
//        HttpResponse response = httpClient.execute(postRequest);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//        String sResponse;
//        StringBuilder s = new StringBuilder();
//        while ((sResponse = reader.readLine()) != null) {
//            s = s.append(sResponse);
//        }

    }
    private void onClickShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "快来CUTE");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "我CUTE的美好时光，你一定要看！CUTE里搜"+ UserInfoUtils.getUserName(mContext)+"，加我！");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  API.SHARE_TO_FRIENDS+AppSharedPref.newInstance(mContext).getUserId()+"/");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, API.SHARE_TO_FRIENDS_IMAGE+AppSharedPref.newInstance(mContext).getUserId()+"/");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "返回CUTE");
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mQQShare.shareToQQ(activity, params, new IUiListener(){
                    @Override
                    public void onComplete(Object o) {
                        Toast.makeText(mContext,"分享成功！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(UiError uiError) {

                    }
                });
            }
        });

    }
    public void isTokenValid(final int index){

        RequestParams p = new RequestParams();
        if (AppSharedPref.newInstance(mContext).getWeiboToken().equals(""))
            Toast.makeText(mContext,"您的微博没有绑定，请先微博登录或绑定！",Toast.LENGTH_LONG).show();
        else {
            p.put("access_token", AppSharedPref.newInstance(mContext).getWeiboToken());
            HttpUtil.post(API.ACCESS_TOKEN_IS_VALID, p, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        int time = response.getInt("expire_in");
                        if (time > 0) {
//                        isWeiboTokenValid = true;
                            onWeiboShare(index);
                        } else
//                        isWeiboTokenValid = false;
//                    else
                            Toast.makeText(mContext, "您的微博认证已经过期，请重新微博登录或绑定！", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        isWeiboTokenValid = false;
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    public void onWeiboShare(int index){
        String status = null;
        try {
            status = URLEncoder.encode(title+"\n"+summary,"UTF-8");
        }catch (Exception e){
            Log.v("URLEncoder","error");
        }
//                 MultipartHttpPost(AppSharedPref.newInstance(mContext).getWeiboToken(),status,getImageByte(R.drawable.icon_person_head));
        RequestParams params = new RequestParams();
        Log.v("ImageByte", getImageByte(R.drawable.ic_launcher).length+"");
        params.put("access_token", AppSharedPref.newInstance(mContext).getWeiboToken());
        params.put("status", status);
        params.put("pic",new ByteArrayInputStream(getImageByte(R.drawable.ic_launcher)),"title.png");
        HttpUtil.post(API.SEND_MESSAGE_TO_WEIBO_FRIENDS, params, jsonHttpResponseHandler);
    }



}
