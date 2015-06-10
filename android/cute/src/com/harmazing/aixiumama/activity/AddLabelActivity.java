package com.harmazing.aixiumama.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.API.HttpHead;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.db.DBHelper;
import com.harmazing.aixiumama.fragment.AttentionFragment;
import com.harmazing.aixiumama.model.Label;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.LabelTextView;
import com.harmazing.aixiumama.model.sina.AccessTokenKeeper;
import com.harmazing.aixiumama.model.sina.Constants;
import com.harmazing.aixiumama.model.sina.LoginButton;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.wxapi.WXEntryActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class AddLabelActivity extends BaseActivity {

    private static String TAG = "AddLabelActivity";
    private float lastX=0;
    private float lastY=0;
    private int screenWidth=0;
    private int screenHeight=0;
    public static QQAuth mQQAuth;
    private Tencent mTencent;
    private static int  STATE, CLICK = 1, LONGCLICK = 2;
    QQShare mQQShare;
    IWXAPI api;
    //  public static String APP_ID = "222222";
//    public static String APP_ID="1103396138";
    public static LoginButton weibo;
    IUiListener listener;
    public  AuthListener mLoginListener = new AuthListener();
    int stickerID;
    FrameLayout frame;
    LinkedList<LabelTextView> viewList ;
    LinkedList<String> itemsList = new LinkedList<String>();
    ImageView qq;
    ImageView weichat;
    Label label;
    int labelNum;
    EditText cuteTxt;
    ImageView labelImageBG,cuteLable;
    Bitmap bitmap;
    int cuteId;
    String imageUrl;
    TextView txtPrivate;
    Boolean isQQSshare,isWeiboShare,isWeChatShare,isPrivate;
    ProgressDialog progressDialog;
    ArrayAdapter<String> labelResultAdapter;
    JSONArray nextArray;
    ListView labelList;
    LabelTextView labelTextView;
    Timer timer;
    TimerTask timerTask;
    DBHelper dbHelper;
    private Animation shake;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label);
        isQQSshare = false;
        isWeChatShare = false;
        isWeiboShare = false;
        isPrivate = false;
        api = WXAPIFactory.createWXAPI(this, CuteApplication.APP_ID);
        stickerID = getIntent().getIntExtra("stickerID", 0);
//        byte[] bitmap_byte = getIntent().getByteArrayExtra("bitmap_byte");
        viewList = new LinkedList<LabelTextView>();
        progressDialog = new ProgressDialog(AddLabelActivity.this);

        if(dbHelper == null) {
            dbHelper = new DBHelper(getApplicationContext());
        }
        try {
            dbHelper.CreateDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        screenWidth =display.getWidth();
        screenHeight=display.getHeight();

        frame = (FrameLayout)findViewById(R.id.frame);
        frame.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CuteApplication.getScreenHW(getApplicationContext())[0]));

        labelImageBG = (ImageView)findViewById(R.id.add_label_image);
//        bitmap = BitmapFactory.decodeByteArray(bitmap_byte, 0, bitmap_byte.length);
//        labelImageBG.setImageBitmap(bitmap);
        bitmap = ActivityGallery.bitmap;
        labelImageBG.setImageBitmap(bitmap);
        cuteLable = (ImageView) findViewById(R.id.pic_cute);

        cuteTxt = (EditText)findViewById(R.id.cute_text);

        cuteTxt.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                temp = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = editable.toString();
                LogUtil.v("length",editable.length());
                if (content.length() > 140) {
                    content = content.substring(0, 140);
                    cuteTxt.setText(content);
                    cuteTxt.setSelection(140);//设置光标在最后

                    ToastUtil.show(getApplicationContext(), "最多140个字!");
                    }
            }
        });


        findViewById(R.id.label_pic).setOnClickListener(new LabelPicOnClick());
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtPrivate = (TextView) findViewById(R.id.share).findViewById(R.id.only);
        qq = (ImageView) findViewById(R.id.qq_share);
        weichat = (ImageView)findViewById(R.id.weixin_share);
        weibo = (LoginButton) findViewById(R.id.weibo_share);
        qqInit();
        sinaInit();
        /**
         * 判断是否有过绑定微信，QQ，微博
         */
        View.OnClickListener onQQShareListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppSharedPref.newInstance(getApplicationContext()).getQQUserId().equals("") && (!isQQSshare)){
                    qq.setBackgroundResource(R.drawable.qq_red);
                    isQQSshare = true;
                }
               else  if (AppSharedPref.newInstance(getApplicationContext()).getQQUserId().equals("") && (!isQQSshare)){
//                    qqInit();
                    qqLogin();

                }
              else  if (!AppSharedPref.newInstance(getApplicationContext()).getQQUserId().equals("") && isQQSshare){
                    qq.setBackgroundResource(R.drawable.qq_gray);
                    isQQSshare = false;
                }
            }
        };
        View.OnClickListener onWeiboListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppSharedPref.newInstance(getApplicationContext()).getWeiboToken().equals("") && !isWeiboShare){
                            weibo.setBackgroundResource(R.drawable.weibo_red);
                            isWeiboShare = true;
                }
              else  if (AppSharedPref.newInstance(getApplicationContext()).getWeiboToken().equals("") && !isWeiboShare){
//                    sinaInit();
                       weibo.loginWeiBo();
                }
              else  if (!AppSharedPref.newInstance(getApplicationContext()).getWeiboToken().equals("") && isWeiboShare){
                    weibo.setBackgroundResource(R.drawable.weibo_gray);
                    isWeiboShare = false;
                }
            }
        };
         View.OnClickListener onWeChatListener = new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (AppSharedPref.newInstance(getApplicationContext()).getWeichatToken() != null && !isWeChatShare){
                     weichat.setBackgroundResource(R.drawable.wechat_red);
                     isWeChatShare = true;
                 }
                 else  if (AppSharedPref.newInstance(getApplicationContext()).getWeichatToken() == null && !isWeChatShare){
//                    sinaInit();
                     bindWeChat();
                 }
                 else  if (AppSharedPref.newInstance(getApplicationContext()).getWeichatToken() != null && isWeChatShare){
                     weichat.setBackgroundResource(R.drawable.weichat_gray);
                     isWeChatShare = false;
                 }
             }
         };
        txtPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPrivate) {
                    isPrivate = true;
                    txtPrivate.setTextColor(getResources().getColor(R.color.pink));
                }
                else{
                    isPrivate = false;
                    txtPrivate.setTextColor(getResources().getColor(R.color.light_grey));
                }
            }
        });
        weichat.setOnClickListener(onWeChatListener);
        qq.setOnClickListener(onQQShareListener);
        weibo.setOnClickListener(onWeiboListener);
        /**
         *  上传所有信息：
         */
        findViewById(R.id.submit).setEnabled(true);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("正在发布");
                progressDialog.setCancelable(false);
                findViewById(R.id.submit).setEnabled(false);
                if(labelNum == 0){
                    AlertDialog builder = new AlertDialog.Builder(AddLabelActivity.this)
                            .setMessage("至少添加1个标签")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    builder.show();
                    findViewById(R.id.submit).setEnabled(true);
                    return;

                }
                progressDialog.show();
           //  拼 label json串

                JSONArray labelArray = new JSONArray();
                for(int i=0;i<labelNum;i++) {
                    Label label = viewList.get(i).getLabel();
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("name", label.getName());
                        obj.put("direction", label.getDirection());
                        /**
                         * 我这里存label的坐标是 frame里的绝对坐标，需要转化成百分比再上传服务器
                         */
                        if(label.getDirection() == 1) {
                            obj.put("x", (label.getX() + viewList.get(i).getLabelLength() -  viewList.get(i).getBallHeight() / 2 ) / CuteApplication.getScreenHW(getApplicationContext())[0]);
                        }
                        else {
                            obj.put("x", (label.getX() + viewList.get(i).getBallHeight() / 2) / CuteApplication.getScreenHW(getApplicationContext())[0]);
                        }
                        obj.put("y",(label.getY() + viewList.get(i).getBallHeight() / 2)  / CuteApplication.getScreenHW(getApplicationContext())[0]);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    labelArray.put(obj);
                }
                LogUtil.v("发布cute的label JSON",labelArray.toString());
                //bitmap = ActivityGallery.mGPUImage.getBitmapWithFilterApplied();

                RequestParams params = new RequestParams();
                params.put("text",cuteTxt.getText().toString());
                try {
                    bitmap = ActivityGallery.bitmap;
                    bitmap = BitmapUtil.compress(bitmap, 800, 800);
                    params.put("image",BitmapUtil.saveBitmap2File(getApplication(),bitmap));
                    LogUtil.v("压缩之后的尺寸", BitmapUtil.getBitmapSize(bitmap)/1024);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                params.put("user", AppSharedPref.newInstance(getApplicationContext()).getUserId());

                //  用户没有贴标签
                if(stickerID != 0) {
                    params.put("sticker", stickerID);
                }
                params.put("label_names",labelArray.toString());
                if (isPrivate)
                    params.put("is_private",1);
                else
                    params.put("is_private",0);
                LogUtil.v("发照片",API.GET_CUTES+params.toString());
                HttpUtil.addClientHeader(getApplicationContext());
                HttpUtil.post(API.GET_CUTES, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        if (statusCode == 200) {
                            LogUtil.v(TAG, response.toString());
                            ToastUtil.showLongTime(getApplicationContext(), "发布成功");
                            //  关闭上一个activity
                            for(int i=0;i<CuteApplication.activityList.size();i++) {
                                LogUtil.v("class name3",CuteApplication.activityList.get(i).getLocalClassName());
                            }

                            CuteApplication.activityList.get(CuteApplication.activityList.size() - 2).finish();

                            //保存图片
                            new Thread(){
                                @Override
                                public void run() {
                                    if (CuteApplication.getIsSave())
                                        handler.sendEmptyMessage(100);
                                    super.run();
                                }
                            }.start();
                            try {
                                cuteId = response.getInt("id");
                                imageUrl = response.getString("image");

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (isQQSshare){
                               onQQShare(cuteId,imageUrl);
                            }
                            if (isWeiboShare){
                              onWeiboShare(cuteId);
                            }
                            if (isWeChatShare){
                                wechatShare("FriendsCircle",cuteId);
                            }
                            progressDialog.dismiss();
                            ToastUtil.showLongTime(getApplicationContext(), "发布成功");
                            //  关闭现在这个
                            finish();

//                            ActivityGallery.bitmap.recycle();
                            ActivityGallery.imageView = null;
                            ActivityGallery.mGPUImage = null;
                            ActivityGallery.sticker  = null;
                            ActivityGallery.bitmap = null;

                        } else {
                            progressDialog.dismiss();
                            ToastUtil.showLongTime(getApplicationContext(), "世界上最遥远的距离就是没网");
                            finish();
                        }

                        try {
                            //  切换到关注页看我发布的内容
                            AttentionFragment.refreshList();
                        }catch (Exception e){e.printStackTrace();}



                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        LogUtil.v(TAG,errorResponse.toString());
                        ToastUtil.showLongTime(getApplicationContext(), "世界上最遥远的距离就是没网");
                        progressDialog.dismiss();
                        LogUtil.v(TAG,"statusCode"+statusCode);
                        finish();
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFinish() {
                        LogUtil.v(TAG,"f");
                        super.onFinish();
                    }
                });
            }
        });

        findViewById(R.id.frame).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    view.setVisibility(View.INVISIBLE);
                    findViewById(R.id.dot).setVisibility(View.INVISIBLE);
                }
            }
        });

        /**
         * 打标签
         */
        findViewById(R.id.frame).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, final MotionEvent event) {
//                ToastUtil.show(getApplicationContext(),event.getRawX() + "  " + event.getRawY());

                if(event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (findViewById(R.id.label_pic).getVisibility() == View.VISIBLE && findViewById(R.id.dot).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.label_pic).setVisibility(View.INVISIBLE);
                        findViewById(R.id.dot).setVisibility(View.INVISIBLE);

                    } else {
                        label = new Label(getApplicationContext());
                        int[] loca = new int[2];
                        view.getLocationOnScreen(loca);
                        //          loca[1] 为标题栏高度：219
                        // Label 的点坐标出现！！！
                        double x = (int)event.getRawX() - CuteApplication.getWidth(findViewById(R.id.dot)) / 2 ;
                        double y = event.getRawY() - loca[1];

                        setLayout(findViewById(R.id.dot),(int)x,(int)y);

                        label.setXByAdd(x);
                        label.setYByAdd(y);


                        if (labelNum == 0) {
                            findViewById(R.id.label_pic).setVisibility(View.VISIBLE);
                            findViewById(R.id.dot).setVisibility(View.VISIBLE);
                        }else {
                            findViewById(R.id.dot).setVisibility(View.VISIBLE);
                            if (labelNum == 5) {
                                AlertDialog builder = new AlertDialog.Builder(AddLabelActivity.this)
                                        .setMessage("最多添加5个标签")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                findViewById(R.id.dot).setVisibility(View.INVISIBLE);
                                                dialog.dismiss();
                                            }
                                        }).create();
                                builder.show();

                            }else
                                addLabelDialog();
                        }
                    }
                }
                return true;
            }
        });


    }


    public void  showLabel() {

        int x = (new Double(label.getX())).intValue();
        int y = (new Double(label.getY())).intValue();

        findViewById(R.id.dot).setVisibility(View.INVISIBLE);

        labelTextView = new LabelTextView(getApplicationContext());

        labelTextView.setLabelText(label.getName());
        //判断左边界越界(左标签)
        if((x - CuteApplication.getWidth(labelTextView) < 0) && label.getDirection() == 1)
        {
            label.setDirection(2);
            labelTextView.setRightLabel();
        }
        //判断右边界越界
        if((x + CuteApplication.getWidth(labelTextView) - labelTextView.getBallHeight() / 2 > screenWidth))
        {
            labelTextView.setLeftLabel();
            label.setDirection(1);
            x -= CuteApplication.getWidth(labelTextView);
            label.setXByAdd(label.getX() - CuteApplication.getWidth(labelTextView));
        }

        //(右标签)
        if(x < 0){
            x = 0 ;
            label.setX(0d);
        }
        //判断下边界越界
        if(y + BitmapUtil.dip2px(getApplicationContext(),40) >= screenWidth){
            y = screenWidth - BitmapUtil.dip2px(getApplicationContext(),40);
            label.setYByAdd(y + 0d);
        }
        LogUtil.v(TAG,x+"       "+screenWidth);
        LogUtil.v(TAG,y+"       "+screenWidth);


        labelNum ++;
        labelTextView.setId(labelNum);
        frame.addView(labelTextView);
        setLayout(labelTextView,x,y);
        labelTextView.setOnTouchListener(new LabelTouchListener());
        labelTextView.setOnClickListener(new LabelOnClickListener(labelTextView));
        labelTextView.setOnLongClickListener( new LabelLongPressListener(labelTextView));
        viewList.add(labelTextView);
        labelTextView.setLabel(label);
    }

    public  void setLayout(View v,int x,int y)
    {
        FrameLayout.MarginLayoutParams margin = new FrameLayout.MarginLayoutParams(v.getLayoutParams());
        margin.width = CuteApplication.getWidth(v);
        margin.height = CuteApplication.getHeight(v);
        margin.setMargins(x,y, 0, 0);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(margin);
        v.setLayoutParams(layoutParams);
    }

    class LabelOnClickListener implements View.OnClickListener {
        LabelTextView labelTV;
        public LabelOnClickListener(LabelTextView label)
        {
            labelTV = label;
        }
        @Override
        public void onClick(View view) {

            if(STATE == CLICK) {
                LogUtil.v(" left", labelTV.isLabelLeft());
                LogUtil.v("right  ", labelTV.isLabelRight());
                if (labelTV.isLabelLeft()) {
                    labelTV.setRightLabel();
                    // 圆点的坐标也改变了
//                    label.setXByAdd(label.getX() - CuteApplication.getWidth(labelTV));
                } else {
                    labelTV.setLeftLabel();
//                    label.setXByAdd(label.getX() + CuteApplication.getWidth(labelTV));
                }
            }
        }
    }

    class LabelLongPressListener implements View.OnLongClickListener{

        LabelTextView labelTV;
        public LabelLongPressListener(LabelTextView label)
        {
            labelTV = label;
        }
        @Override
        public boolean onLongClick(View view) {
            if(STATE == LONGCLICK) {
                AlertDialog builder = new AlertDialog.Builder(AddLabelActivity.this)

                        .setMessage("您要删除当前标签吗？").
                                setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        labelNum--;
                                        viewList.remove(labelTV.getId() - 1);//labelNum 是从1计数的
                                        frame.removeView(labelTV);
                                    }
                                }).
                                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                                create();
                builder.show();
            }
            return true;
        }
    }

    class  LabelTouchListener implements View.OnTouchListener {
        long lastTime;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action=event.getAction();

            //Toast.makeText(DraftTest.this, "λ�ã�"+x+","+y, Toast.LENGTH_SHORT).show();
            switch(action){
                case MotionEvent.ACTION_DOWN:


                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    lastTime = System.currentTimeMillis();
                    break;
                /**
                 * layout(l,t,r,b)
                 * l  Left position, relative to parent
                 t  Top position, relative to parent
                 r  Right position, relative to parent
                 b  Bottom position, relative to parent
                 * */
                case MotionEvent.ACTION_MOVE:

                    int dx =(int)(event.getRawX() - lastX);
                    int dy =(int)(event.getRawY() - lastY);
                    LogUtil.v("xy",dx+"    "+dy);
                    if(Math.abs(dx) < 1 && Math.abs(dy) < 1)
                        STATE = LONGCLICK;
                    else
                        STATE = 0;

//                    LogUtil.v("ltrb",v.getLeft() +"  "+v.getTop() +"  "+v.getRight() +" "+v.getBottom() );
                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    LogUtil.v("ltrb",left+"  "+top+"  "+right+" "+bottom);
                    if(left < 0){
                        left = 0;
                        right = left + v.getWidth();
                    }
                    if(right > screenWidth){
                        right = screenWidth;
                        left = right - v.getWidth();
                    }
                    if(top < 0){
                        top = 0;
                        bottom = top + v.getHeight();
                    }
                    if(bottom > CuteApplication.getScreenHW(getApplicationContext())[0]){
                        bottom = CuteApplication.getScreenHW(getApplicationContext())[0];
                        top = bottom - v.getHeight();
                    }

                    setLayout(v,left,top);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.v("time",System.currentTimeMillis() - lastTime);
                    if(System.currentTimeMillis() - lastTime < 90)
                        STATE = CLICK;
                    else
                        STATE = 0;
//                    else if(System.currentTimeMillis() - lastTime > 2000)
//                        STATE = LONGCLICK;

                    lastX = event.getRawX();// - event.getX();
                    lastY = event.getRawY();// - event.getY();

                    int[] loca = new int[2];
                    v.getLocationOnScreen(loca);

                    double x = loca[0];
                    double y = loca[1] -getTopViewHeight() - BitmapUtil.dip2px(getApplicationContext(),50);

                    if(y <= 0)
                        y = 0;

                    if(x <= 0)
                        x = 0;

//                    label.setXByAdd(x);
//                    label.setYByAdd(y);
                    if(v instanceof LabelTextView) {
                        ((LabelTextView) v).getLabel().setXByAdd(x);
                        ((LabelTextView) v).getLabel().setYByAdd(y);
                        LogUtil.v("强转了","强转了强转了强转了");
                    }
                    break;
            }
            return false;
        }
    }

    public int getTopViewHeight(){
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return  frame.top;
    }
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
         final    Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            /*
                    授权成功
             */
            if (accessToken != null && accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                        new java.util.Date(accessToken.getExpiresTime()));
                String format = getString(R.string.weibosdk_demo_token_to_string_format_1);

                AccessTokenKeeper.writeAccessToken(getApplicationContext(), accessToken);

                LogUtil.v(TAG,"微博登陆成功"+String.format(format, accessToken.getToken(), date) );

                /*
                           登陆成功给和美服务器
                           1.weibo_id：   uid
                           2.weibo_token  access_token
                     */
                RequestParams params = new RequestParams();
                try {
                    params.put("weibo_id", accessToken.getUid());
                    params.put("weibo_token", accessToken.getToken());
                    LogUtil.v("weiboToken", accessToken.getToken());
//                    AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(accessToken.getToken());
//                    AppSharedPref.newInstance(getApplicationContext()).setWeiboUserId(accessToken.getUid());
//                        params.put("qq_sig","yCdpibyLyps+wrZMat/4vqkC3cc=");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpUtil.post(API.BIND_WEIBO,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        LogUtil.v(TAG,"和美服务器发送请求成功");
                        LogUtil.v("startAtyRespon",response.toString());
                        try {
                            weibo.setBackgroundResource(R.drawable.weibo_red);
                            Toast.makeText(getApplicationContext(),"微博绑定成功",Toast.LENGTH_LONG).show();
                            AppSharedPref.newInstance(getApplicationContext()).setToken(response.getString("token"));
                            AppSharedPref.newInstance(getApplicationContext()).setUserId(response.getString("id"));
                            AppSharedPref.newInstance(getApplicationContext()).setUserName(response.getString("username"));
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(accessToken.getToken());
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboUserId(accessToken.getUid());
                            weibo.setBackgroundResource(R.drawable.weibo_red);
                            isWeiboShare = true;
                            LogUtil.v("token",response.getString("token")+"");
                            LogUtil.v("id",response.getString("id")+"");
                            LogUtil.v("username",response.getString("username")+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplication(),"该账户已与另一个账户绑定！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplication(),"该账户已与另一个账户绑定！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplication(),"该账户已与另一个账户绑定！",Toast.LENGTH_LONG).show();
                    }
                });

            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(AddLabelActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(AddLabelActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_SHORT).show();
        }
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onError(UiError e) {
            LogUtil.i(TAG, "登录失败=>" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            LogUtil.i(TAG, "取消登录");
        }
    }
    private void qqLogin() {
        if (!mQQAuth.isSessionValid()) {
            listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    ToastUtil.show(AddLabelActivity.this, "QQ 登录成功");
                    LogUtil.i(TAG, "登录成功=>" + values.toString());
                    LogUtil.i(TAG, "isSessionValid=>" + mQQAuth.isSessionValid());
                    LogUtil.i(TAG, "AccessToken=>" + mTencent.getAccessToken());
                    LogUtil.i(TAG, "QQToken=>" + mQQAuth.getQQToken());



                    /*
                           登陆成功给和美服务器
                           1.qq_openid： qq openid
                            2.qq_openkey: qq openkey
                            3.qq_sig: qq sig（需使用非URL编码）
                     */
                    RequestParams params = new RequestParams();
                    try {
                        params.put("qq_openid",values.getString("openid"));
                        params.put("qq_openkey",mTencent.getAccessToken());
                        AppSharedPref.newInstance(getApplicationContext()).setQQToken(mTencent.getAccessToken());
//                        params.put("qq_sig","yCdpibyLyps+wrZMat/4vqkC3cc=");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUtil.post(API.POST_QQ,params,new JsonHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            LogUtil.v(TAG,"和美服务器发送请求成功");
                            try {
                                qq.setBackgroundResource(R.drawable.qq_red);
                                Toast.makeText(getApplicationContext(),"QQ绑定成功！",Toast.LENGTH_LONG).show();
                                AppSharedPref.newInstance(getApplicationContext()).setToken(response.getString("token"));
                                AppSharedPref.newInstance(getApplicationContext()).setUserId(response.getString("id"));
                                AppSharedPref.newInstance(getApplicationContext()).setUserName(response.getString("username"));
                                AppSharedPref.newInstance(getApplicationContext()).setQQUserId(response.getString("qq_openid"));
                                LogUtil.v("token",response.getString("token")+"");
                                LogUtil.v("id",response.getString("id")+"");
                                LogUtil.v("username",response.getString("username")+"");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            super.onSuccess(statusCode, headers, response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            ToastUtil.show(AddLabelActivity.this,"和美服务器发送请求失败");
                            LogUtil.e(TAG,responseString);
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });


                }
            };
            //mQQAuth.login(this, "all", listener);
            mTencent.loginWithOEM(this, "all", listener, "10000144", "10000144", "xxxx");
        } else {

            //mQQAuth.logout(this);
        }
    }
    private void qqInit() {
        mQQAuth = QQAuth.createInstance(StartActivity.APP_ID, this.getApplicationContext());
        mTencent = Tencent.createInstance(StartActivity.APP_ID, this.getApplicationContext());
        LogUtil.i(TAG, "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
        LogUtil.i(TAG, "isSessionValid=>" + mQQAuth.isSessionValid());
        mQQShare = new QQShare(getApplicationContext(), mQQAuth.getQQToken());
//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }
    }
    public void sinaInit() {
        // 创建授权认证信息
        WeiboAuth.AuthInfo authInfo = new WeiboAuth.AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        weibo = (LoginButton) findViewById(R.id.weibo_share);
        weibo.setWeiboAuthInfo(authInfo, mLoginListener);
        LogUtil.v("&&&&",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken()+"@");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mTencent != null)
            mTencent.onActivityResult(requestCode,resultCode,data);
        if (weibo != null) {
            weibo.onActivityResult(requestCode, resultCode, data);
        }
    }
    public String saveBitmap(Bitmap bitmap) throws IOException
    {
        if(bitmap == null)
        {
            LogUtil.v("截屏图片为null","");
            return null;
        }
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if  (sdCardExist)
        {

            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            String path=sdDir.getPath()+"/Pictures/cute";
            File path1 = new File(path);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();}
            File file = new File(sdDir,"/Pictures/cute/"+ System.currentTimeMillis()+".jpg");
            FileOutputStream out;
            try{
                out = new FileOutputStream(file);
                if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
                {
                    out.flush();
                    out.close();
                }
                LogUtil.v("存入内存卡",file.getAbsolutePath());
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), System.currentTimeMillis()+".jpg", "description");
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
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
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100){
                cuteLable.setVisibility(View.VISIBLE);
                int w = CuteApplication.getScreenHW(getApplicationContext())[0];
                int h = CuteApplication.getScreenHW(getApplicationContext())[1];
                Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565 );
//获取屏幕
                View decorview = getWindow().getDecorView();
//             decorview.setDrawingCacheEnabled(true);
//             decorview.buildDrawingCache();
//             Bmp = decorview.getDrawingCache();
                Canvas c = new Canvas(Bmp);
                decorview.draw(c);
                // 剪切
                Bmp = Bitmap.createBitmap(Bmp,0,BitmapUtil.dip2px(getApplicationContext(),50)+ getTopViewHeight() ,w,w);
                decorview.destroyDrawingCache();
                decorview.setDrawingCacheEnabled(false);
                try {
                    saveBitmap(Bmp);
                    cuteLable.setVisibility(View.INVISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }
//                return Bmp;
            }
            super.handleMessage(msg);
        }
    };

    private Bitmap getAndSaveCurrentImage() {
//构建Bitmap
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
        int w = CuteApplication.getScreenHW(getApplicationContext())[0];
        int h = CuteApplication.getScreenHW(getApplicationContext())[1];
        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565 );
//获取屏幕
        View decorview = this.getWindow().getDecorView();
//        decorview.setDrawingCacheEnabled(true);
//        decorview.buildDrawingCache();
//        Bmp = decorview.getDrawingCache();
        Canvas c = new Canvas(Bmp);
        decorview.draw(c);
        // 剪切
        Bmp = Bitmap.createBitmap(Bmp,0,BitmapUtil.dip2px(getApplicationContext(),50)+ getTopViewHeight() ,w,w);

        decorview.destroyDrawingCache();
        decorview.setDrawingCacheEnabled(false);
        return Bmp;
    }

    @Override
    protected void onDestroy() {
        System.out.println("释放资源2");

        if(bitmap != null) {
            bitmap.recycle();
        }

        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        if(timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if(ActivityGallery.bitmap != null) {
            ActivityGallery.bitmap.recycle();
        }

        label = null;
        viewList.clear();


        super.onDestroy();
    }
    public void onQQShare(int cuteId, String imageUrl){
        final Bundle params = new Bundle();
        String content = label.getName();
        String title = "分享了一张我的照片, ...";
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(HttpHead.head+imageUrl);
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,content);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, API.SHARE_PIC_TO+cuteId+"/");
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        doShareToQzone(params);
    }
    private void doShareToQzone(final Bundle params) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mTencent.shareToQzone(AddLabelActivity.this, params, new IUiListener() {

                    @Override
                    public void onCancel() {
                        LogUtil.v("activity", "onCancel: ");
                    }

                    @Override
                    public void onError(UiError e) {
                        // TODO Auto-generated method stub
                        LogUtil.v("onError: " + e.errorMessage, "e");
                    }

                    @Override
                    public void onComplete(Object response) {
                        // TODO Auto-generated method stub
                        LogUtil.v("onComplete: ", response.toString());
                        UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.TENCENT_QQ, AppSharedPref.newInstance(AddLabelActivity.this).getUserId());
                        MobclickAgent.onSocialEvent(AddLabelActivity.this, platform);
                        Toast.makeText(AddLabelActivity.this,"分享成功！",Toast.LENGTH_LONG).show();
                    }

                });
            }
        }).start();
    }
    private void onWeiboShare(final int cuteId){
        RequestParams params = new RequestParams();
            LogUtil.v("AppSharedPref.newInstance(ctx).getWeiboToken()",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
            params.put("access_token", AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
            HttpUtil.post(API.ACCESS_TOKEN_IS_VALID, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    LogUtil.v("access_token_valid", response.toString());
                    try {
                        int time = response.getInt("expire_in");
                        if (time >= 0) {
                            LogUtil.v("timeSHow", time + "");
                            onClickShareWeibo(cuteId);
                        } else {
                            Toast.makeText(getApplicationContext(), "微博认证过期，请用微博重新登录或重新绑定微博！", Toast.LENGTH_LONG).show();
                            weibo.loginWeiBo();
                        }
                        LogUtil.v("time", time + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

//              onClickShareWeibo(holder, url);
    }
    private void onClickShareWeibo(int cuteId){
        String status = null;
//            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
        try {
            status = URLEncoder.encode("分享了一张我的照片,连接地址："+API.SHARE_PIC_TO+cuteId+"/");
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestParams params = new RequestParams();
        params.put("access_token",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
        params.put("status",status);
        params.put("pic", new ByteArrayInputStream(getImageByte(getAndSaveCurrentImage())), "title.JPEG");
        HttpUtil.post(API.SEND_MESSAGE_TO_WEIBO_FRIENDS, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("response", response.toString());
                UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.SINA_WEIBO, AppSharedPref.newInstance(AddLabelActivity.this).getUserId());
                MobclickAgent.onSocialEvent(AddLabelActivity.this, platform);
                Toast.makeText(getApplicationContext(), "微博分享成功！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("errer", responseString);
            }
        });
    }
    private byte []  getImageByte(Bitmap imageId){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageId.compress(Bitmap.CompressFormat.PNG,100,baos);
        LogUtil.v("byte",baos.toByteArray().length+"");
        return baos.toByteArray();
    }

    class LabelPicOnClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            addLabelDialog();
        }
    }

    private void addLabelDialog(){
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewTxt = inflater.inflate(R.layout.label_dialog, null);
        final EditText edittext = (EditText) viewTxt.findViewById(R.id.auto);
        final TextView addedLabelTV = (TextView) viewTxt.findViewById(R.id.added_label_tv);

        /**
         * 500毫秒后自动弹出软键盘
         */
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)edittext.getContext().getSystemService(INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edittext, 0);
            }
        };

        timer.schedule(timerTask, 300);

        if(label == null){
            label = new Label(getApplicationContext());
            label.setXByAdd(500d);
            label.setYByAdd(500d);
        }



        findViewById(R.id.label_pic).setVisibility(View.INVISIBLE);


        //   没有输入之前显示历史使用标签
        itemsList = dbHelper.queryData();
        labelResultAdapter = new ArrayAdapter<String>(
                AddLabelActivity.this,
                R.layout.autotxt_item,
                (String[]) itemsList.toArray(new String[itemsList.size()]));
        labelList = (ListView)
                viewTxt.findViewById(R.id.labels_list);
        labelList.setAdapter(labelResultAdapter);

        labelList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int i) {
                // 判断是否滚动到底部
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    //加载更多功能的代码
                    LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                    if (CuteApplication.nextPageUrl.length() > 4) {

                        HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    nextArray = new JSONArray(response.getString("results"));

                                    JSONObject obj;
                                    for(int i=0;i<nextArray.length();i++){
                                        obj = new JSONObject(nextArray.get(i).toString());
                                        itemsList.add(obj.getString("name"));
                                    }
                                    labelResultAdapter = new ArrayAdapter<String>(
                                            AddLabelActivity.this,
                                            R.layout.autotxt_item,
                                            (String[]) itemsList.toArray(new String[itemsList.size()]));
                                    labelList.setAdapter(labelResultAdapter);
                                    labelList.scrollTo(0, labelList.getHeight()/2);

//                                        LogUtil.v("labelList.getHeight()/2", labelList.getHeight()/2);
                                    if (response.getString("next").length() > 10) {
                                        CuteApplication.nextPageUrl = response.getString("next");
                                    } else {
                                        LogUtil.v("next", "没有更多数据");
                                        CuteApplication.nextPageUrl = "";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                super.onSuccess(statusCode, headers, response);
                            }
                        });
                    }

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }



        });

        edittext.addTextChangedListener(new TextWatcher() {
            private String temp;

            SpannableStringBuilder ssb;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                temp = charSequence.toString();

                ssb = new SpannableStringBuilder ("添加标签："+temp);
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(calculateLength(temp) > 15) {
                    shake = AnimationUtils.loadAnimation(AddLabelActivity.this, R.anim.shake);
                    edittext.startAnimation(shake);

                    toast = Toast.makeText(getApplicationContext(),
                            "请最多输入15个汉字或30个英文", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    //用颜色标记文本
                    ssb.setSpan(new ForegroundColorSpan(Color.rgb(255, 145, 185)), 20, temp.length()+5,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

//                    System.out.println("gyw111" + temp.length());
                    addedLabelTV.setText(ssb);
                    //addedLabelTV.setMovementMethod(LinkMovementMethod.getInstance());

                } else {
                    addedLabelTV.setText("添加标签："+temp);
                }

                addedLabelTV.setText("添加一个标签：" + editable.toString());
                HttpUtil.cancelRequest(getApplicationContext());
                performSearch(editable.toString());

                System.out.println("edittext : " + edittext.getText().toString().length());
            }
        });



        final Dialog builder = new AlertDialog.Builder(AddLabelActivity.this).create();
        builder.show();

        //  软键盘回车响应
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(textView.getText().toString());
                }
                return false;
            }
        });
        //  添加标签响应
        viewTxt.findViewById(R.id.added_label_linear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("edittext2 : " + edittext.getText().toString().length());
                if(edittext.getText().toString().length() < 1){
                    return;
                } else if(calculateLength(edittext.getText().toString()) > 15) {
                    shake = AnimationUtils.loadAnimation(AddLabelActivity.this, R.anim.shake);
                    edittext.startAnimation(shake);

                    toast = Toast.makeText(getApplicationContext(),
                            "请最多输入15个汉字或30个英文", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    return;
                }


                builder.dismiss();
                dbHelper.insertLabelItems(edittext.getText().toString());
                label.setName(edittext.getText().toString());
                showLabel();

            }
        });

        labelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                builder.dismiss();

                label.setName(itemsList.get(i));
                showLabel();
            }
        });


        builder.getWindow().setContentView(viewTxt);
        builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ImageView cancel = (ImageView) builder.getWindow().findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                findViewById(R.id.dot).setVisibility(View.INVISIBLE);
            }
        });

    }


    /**
     * 计算输入的标签的字符串的长度
     * @param c  输入的字符串
     * @return   返回的字符串的长度
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    private void performSearch(String content){
        try {
            content = URLEncoder.encode(content,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtil.get(API.GET_LABELS + "?search=" + content , new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                itemsList.clear();

                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    if(array.length() > 0){
                        CuteApplication.nextPageUrl = response.getString("next");
                        JSONObject obj;
                        for(int i=0;i<array.length();i++){
                            obj = new JSONObject(array.get(i).toString());
                            itemsList.add(obj.getString("name"));
                        }
                    }
                    labelResultAdapter = new ArrayAdapter<String>(
                            AddLabelActivity.this,
                            R.layout.autotxt_item,
                            (String[]) itemsList.toArray(new String[itemsList.size()]));
                    labelList.setAdapter(labelResultAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }
    private void bindWeChat(){
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "cute";
        api.sendReq(req);
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    private void wechatShare(String type,int cuteId){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl =API.SHARE_PIC_TO+cuteId+"/";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "分享了一张我的照片, ...";
        msg.description = "想说的都在照片里，小伙伴们快来吧！@CUTE";
        BitmapFactory.Options opts = new BitmapFactory.Options();
//        Bitmap thumb = BitmapUtil.compress(bitmap, 10, 10);
        opts.inSampleSize =  12;
        byte [] bytes = getImageByte(getAndSaveCurrentImage());
        Bitmap thumb = BitmapFactory.decodeByteArray(bytes,0,bytes.length,opts);
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
        msg.thumbData = com.tencent.mm.sdk.platformtools.Util.bmpToByteArray(thumb, true);
//        msg.thumbData = getImageByte(getAndSaveCurrentImage());
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(type);
        WXEntryActivity.youMengShareTag = req.transaction;
        req.message = msg;
        req.scene = type.equals("FriendsCircle") ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
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
