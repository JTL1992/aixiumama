package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.GroupItemAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.model.Group;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.adapter.FriendViewAdapter;
import com.harmazing.aixiumama.model.GroupItem;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.ContactsUtils;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2014/11/27.
 */
public class InviteFriendsAty extends Activity {
    QQShare mQQShare;
    Tencent mTencent;
    QQAuth mQQAuth;
    IWXAPI api;
    private String downloadUrl = "http://www.pgyer.com/app/view/35d1da753cd0b7d727ab54beeb9187d7";
    String title = "宝妈亲，我今天发现有个都是跟你家宝宝同龄的交流平台";
    String summary = "我参考好多同龄妈妈们的经验，以后买东西再也不纠结了！我也晒了这么多，发你看看";
//    public static String APP_ID="1103396138";
    FriendViewAdapter mFriendViewAdapter;
    List<Group> groups ;
    List<GroupItem> contactsGroupItems;
    List<GroupItem> weiboGroupItems;
    List<GroupItem> contacters;
    ListView list,listSearch;
    EditText search;
    TextView result;
    HashMap<String,String> contacts;
    Boolean isShowGroupItem[] ={false,false,false,false,false};
    JsonHttpResponseHandler parseJsonDataHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_invite);
        qqInit();
        api = WXAPIFactory.createWXAPI(this, CuteApplication.APP_ID);
//        mQQShare = new QQShare(this, mQQAuth.getQQToken());
        list = (ListView) findViewById(R.id.listView);
        listSearch = (ListView) findViewById(R.id.list);
        listSearch.setVisibility(View.INVISIBLE);
        search = (EditText) findViewById(R.id.edit_search);
        result = (TextView) findViewById(R.id.text_search);
        contactsGroupItems = new LinkedList<GroupItem>();
        weiboGroupItems = new LinkedList<GroupItem>();
        groups = new LinkedList<Group>();
        contacters = new LinkedList<GroupItem>();
        ImageView btnBack = (ImageView) findViewById(R.id.titleBar).findViewById(R.id.left_view);
//        list = (ListView) findViewById(R.id.list);
        btnBack.setOnClickListener(onBackListener);
        search.addTextChangedListener(onSearchChangeListener);
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search.getText().toString().equals(""))
                Toast.makeText(InviteFriendsAty.this,"输入为空！！！！",Toast.LENGTH_LONG).show();
            }
        });
        contacts = (HashMap<String, String>)ContactsUtils.getAllCallRecords(this);
//        for (String s : contacts.keySet())
//            Log.v(s,contacts.get(s));
         parseJsonDataHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("getDataFrom",response.toString());
                try {
                    JSONArray mJSONArray = response.getJSONArray("users");
                     for (int i = 0; i < mJSONArray.length(); i++)
                         weiboGroupItems.add(new GroupItem(mJSONArray.getJSONObject(i).getString("name"), mJSONArray.getJSONObject(i).getString("profile_image_url"),0));
                }catch (Exception e){
                    LogUtil.v("@@@@@","");
                }

            }
        };
        initGroup();
//        contacts = (HashMap<String,String>)ContactsUtils.getAllCallRecords(getApplicationContext());
//        for (String s : contacts.keySet())
//            Log.v("@@@",s);

    }
    TextWatcher onSearchChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            if (charSequence.equals(""))
//                list.setVisibility(View.VISIBLE);
//            else
//                list.setVisibility(View.INVISIBLE);
        }
        @Override
        public void afterTextChanged(Editable editable) {
                contacters.clear();
            if (editable.toString().equals("")){
                list.setVisibility(View.VISIBLE);
                listSearch.setVisibility(View.INVISIBLE);
            }
            else {
//                list.setVisibility(View.INVISIBLE);
                  for(GroupItem groupItem : weiboGroupItems ){
                      if (groupItem.getTitle().contains(editable.toString())) {
                          contacters.add(groupItem);
                      }
                  }
                  for (GroupItem groupItem : contactsGroupItems){
                      if (groupItem.getTitle().contains(editable.toString())){
                          contacters.add(groupItem);
                      }
                  }
                  GroupItemAdapter groupItemAdapter = new GroupItemAdapter(getApplicationContext(),contacters,3,InviteFriendsAty.this);
                  listSearch.setAdapter(groupItemAdapter);
                  list.setVisibility(View.INVISIBLE);
                  listSearch.setVisibility(View.VISIBLE);
            }
        }
    };
    View.OnClickListener onBackListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
    private void initGroup(){
        RequestParams params = new RequestParams();
        if (AppSharedPref.newInstance(this).getWeiboToken().equals(""))
            Toast.makeText(this,"您的微博没有绑定，绑定后才能邀请微博好友！",Toast.LENGTH_LONG).show();
        else{
           params.put("access_token", AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
           params.put("uid",AppSharedPref.newInstance(getApplicationContext()).getWeiboUserId());
        HttpUtil.get(API.INVITE_WEIBO_FRIENDS, params , parseJsonDataHandler);}
        for (String name : contacts.keySet())
            contactsGroupItems.add(new GroupItem(name,"",1));
//        for (int i = 0; i < 5; i++)
         groups.add(new Group("微博好友",weiboGroupItems,0));
         groups.add(new Group("手机通讯录",contactsGroupItems,1));
         groups.add(new Group("QQ好友",null,2));
         groups.add(new Group("微信好友",null,3));
         mFriendViewAdapter = new FriendViewAdapter(getApplicationContext(), groups,InviteFriendsAty.this);
         list.setAdapter(mFriendViewAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RelativeLayout GroupLayout = (RelativeLayout) view.findViewById(R.id.group_line);
                ListView ItemsList = (ListView) view.findViewById(R.id.list);
                if (i == 2){
                    onClickShare();
//                    Toast.makeText(getApplicationContext(),"@",Toast.LENGTH_LONG).show();
                }
                if (i == 3){
                    wechatShare("person");
                }
                else if (i == 0 || i == 1){
                    if (!isShowGroupItem[i]) {
                        ItemsList.setVisibility(View.VISIBLE);
                        GroupLayout.findViewById(R.id.image_jiantou).setBackgroundResource(R.drawable.icon_up);
                        isShowGroupItem[i] = true;
                    } else {
                        ItemsList.setVisibility(View.GONE);
                        GroupLayout.findViewById(R.id.image_jiantou).setBackgroundResource(R.drawable.icon_down);
                        isShowGroupItem[i] = false;
                    }
                }
            }
        });


    }
    private void qqInit() {
        mQQAuth = QQAuth.createInstance(StartActivity.APP_ID, this);
        mTencent = Tencent.createInstance(StartActivity.APP_ID, this);
        LogUtil.i("mQQAuth", "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
        LogUtil.i("mQQAuth", "isSessionValid=>" + mQQAuth.isSessionValid());
        mQQShare = new QQShare(this, mQQAuth.getQQToken());
//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }
    }
    private void onClickShare() {
        final Bundle params = new Bundle();
//        Uri uri = Uri.parse("android.resource://com.harmazing.cute/res/");
//        String path = getAbsoluteImagePath(uri);
//        Log.v("path",path);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  API.SHARE_TO_FRIENDS+AppSharedPref.newInstance(this).getUserId()+"/");
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  downloadUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, getPicUrl());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "返回CUTE");
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mQQShare.shareToQQ(InviteFriendsAty.this, params, new IUiListener(){
                    @Override
                    public void onComplete(Object o) {
                        LogUtil.v("o",o.toString());
                        Toast.makeText(getApplicationContext(), "分享成功！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(UiError uiError) {
                       LogUtil.v("uiError",uiError.errorMessage);
                    }
                });
            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTencent.onActivityResult(requestCode, resultCode, data);
    }
    private String getPicUrl(){
        Resources res= getApplicationContext().getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.ic_launcher);

        try {
            return saveBitmap(bmp);
//            Log.v("绝对路径",file.getPath());

        }catch (Exception e){
            return null;
        }

    }
    public String saveBitmap(Bitmap bitmap) throws IOException
    {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            File file = new File(sdDir,"Pictures/image.png");
            FileOutputStream out;
            try{
                out = new FileOutputStream(file);
                if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
                {
                    out.flush();
                    out.close();
                }
                LogUtil.v("存入内存卡",file.getAbsolutePath());
                return file.getAbsolutePath();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"当前无内存卡，分享图片等功能无效！",Toast.LENGTH_LONG).show();
            return null;
        }

    }

         private void wechatShare(String type){
             WXWebpageObject webpage = new WXWebpageObject();
//             webpage.webpageUrl = downloadUrl;
             webpage.webpageUrl = API.SHARE_TO_FRIENDS+AppSharedPref.newInstance(this).getUserId()+"/";
             WXMediaMessage msg = new WXMediaMessage(webpage);
             msg.title = title;
             msg.description = summary;
             Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
             msg.thumbData = com.tencent.mm.sdk.platformtools.Util.bmpToByteArray(thumb, true);
             SendMessageToWX.Req req = new SendMessageToWX.Req();
             req.transaction = buildTransaction("webpage");
             req.message = msg;
             req.scene = type.equals("FriendsCircle") ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
             api.sendReq(req);
         }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}