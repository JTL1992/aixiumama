package com.harmazing.aixiumama.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.PhotoDetailCommentsListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.model.Cute;
import com.harmazing.aixiumama.model.Label;
import com.harmazing.aixiumama.model.sina.AccessTokenKeeper;
import com.harmazing.aixiumama.model.sina.Constants;
import com.harmazing.aixiumama.model.sina.LoginButton;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.BaseMyListView;
import com.harmazing.aixiumama.view.LabelImageView;
import com.harmazing.aixiumama.view.LikeThumbNail;
import com.harmazing.aixiumama.view.StartCustomTextView;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
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
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 *      cute详情页
 *      参数 id(cute的id)int
 */
public class PhotoDetailActivity extends FragmentActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{
    private static int FOLLOW_STATE;
    private IWXAPI api;
    private Tencent mTencent;
    private QQAuth mQQAuth;
    private QQShare mQQShare = null;
    String next = null;
    int emojiNum = 0;
    //  public static String APP_ID = "222222";
//    public static String APP_ID="1103396138";
    BaseMyListView commentsList;
    TextView descriptionTV;
    StartCustomTextView mumHeadTV;
    ImageView followState;
    TextView likedTV,commentsBtn,babyInfoTV,deleteTV,jubaoTV;
    JSONArray array;
    SlidingDrawer mSlidingDrawer;
//    public static EditText rely;
    ImageView mumHeadIV,likedIcon;
    LabelImageView photoIV;
    public static String relyToPerson;
    String LZID,tmpContent;
    LikeThumbNail hListView;
    boolean likedState;
    LinearLayout likedLayout;
    Button moreBtn;
    public static String relyToName;
    int cuteID;
    Cute cute;
    String urlImage;
    ScrollView scrollView;
    PhotoDetailCommentsListAdapter commentsListAdapter;
    private AuthListener mLoginListener = new AuthListener();
    ImageView QQ, weixin, friendsCircle,collection,Qzone,delete,emoji,jubao;
    JsonHttpResponseHandler commentJsonResponseHandler;
    LoginButton weibo;
    FrameLayout emojicons;
    InputMethodManager imm;
    public static EmojiconEditText rely;
    public String comment_person;
    public boolean is_from_home;
    public String commnet_person_id;
    int curUserIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        api = WXAPIFactory.createWXAPI(this, CuteApplication.APP_ID);
        initView();
        qqInit();
        cuteID = getIntent().getIntExtra("id", 0);
        is_from_home = getIntent().getBooleanExtra("is_from_home", false);
        comment_person = getIntent().getStringExtra("commnet_person");
        commnet_person_id = getIntent().getStringExtra("commnet_person_id");

//        System.out.println("gyw123  " + comment_person + "   " + is_from_home);

        if (getIntent().getDataString() != null){
             String data = getIntent().getDataString();
             String cuteId = data.substring(15,20);
             Log.v("parse data",cuteId);
             Log.v("data",data);
             cuteID = Integer.parseInt(cuteId);
        }
        descriptionTV = (TextView) findViewById(R.id.pic_text);
        mumHeadTV = (StartCustomTextView) findViewById(R.id.mum_name);
        babyInfoTV = (TextView) findViewById(R.id.baby_info);
        likedIcon = (ImageView) findViewById(R.id.liked_icon);
        emoji = (ImageView) findViewById(R.id.emoji_icon);
        moreBtn = (Button) findViewById(R.id.more);
        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.sliding);
        likedLayout = (LinearLayout) findViewById(R.id.liked_layout);
        followState = (ImageView)findViewById(R.id.imageView2);
        emojicons = (FrameLayout)findViewById(R.id.emojicons);
        jubaoTV = (TextView) findViewById(R.id.jubao_tv);
        jubao = (ImageView) findViewById(R.id.jubao);
//        rely = (EditText) findViewById(R.id.rely);
        //  emoji 控件
        rely = (EmojiconEditText) findViewById(R.id.rely);
        setEmojiconFragment(false);
        imm = (InputMethodManager) rely.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        likedTV = (TextView) findViewById(R.id.liked);
        commentsBtn = (TextView) findViewById(R.id.coments);
        photoIV = (LabelImageView) findViewById(R.id.label_image);
        // 设置大小
        photoIV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CuteApplication.getScreenHW(getApplicationContext())[0]));

        mumHeadIV = (ImageView) findViewById(R.id.user_icon);

        commentsList = (BaseMyListView) findViewById(R.id.comments_listview);
        commentJsonResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    next = response.getString("next");
                    Log.v("成功",next);
                    array = new JSONArray(response.getString("results"));
                    if (array.length() > 0) {
//                        commentsBtn.setText(response.getString("count"));
                        commentsListAdapter.addCommentList(array);
//                        commentsListAdapter = new PhotoDetailCommentsListAdapter(array, PhotoDetailActivity.this,commentsList);
//                        commentsList.setAdapter(commentsListAdapter);
//                                ListViewUtility.setListViewHeightBasedOnChildren(commentsList);
                        commentsListAdapter.notifyDataSetChanged();
                    }
//                    }else
//                        commentsList.setAdapter(null);

                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    rely.setFocusable(true);
                    rely.setFocusableInTouchMode(true);
                    rely.requestFocus();
                    if (next != null)
                      HttpUtil.get(next,commentJsonResponseHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };
        if (AppSharedPref.newInstance(getApplicationContext()).getToken()!= null && AppSharedPref.newInstance(getApplicationContext()).getWeiboToken() != null ){
            sinaInit();
        }
        //  判断软键盘显示隐藏
        if(getIntent().getBooleanExtra("showAllComments",false))
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        else
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojicons.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(rely.getWindowToken(), 0);
            }
        });

        rely.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emojicons.getVisibility() == View.VISIBLE)
                    emojicons.setVisibility(View.GONE);
                imm.showSoftInput(rely, InputMethodManager.RESULT_SHOWN);
            }
        });
       
        /**
         *  滑出分享、删除
         */
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingDrawer.animateOpen();
                weixin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                           wechatShare("person",cute,urlImage);
                    }
                });
                friendsCircle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                          wechatShare("FriendsCircle",cute,urlImage);
                    }
                });
                QQ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickShare(cute, urlImage);
                        mSlidingDrawer.animateClose();
                    }
                });
                collection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFavoriteCute(cute);
                        mSlidingDrawer.animateClose();
                    }
                });
                Qzone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onShare2Qzone(cute,urlImage);
                        mSlidingDrawer.animateClose();
                    }
                });
                weibo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onWeiboShare(cute,urlImage);
                        mSlidingDrawer.animateClose();
                    }
                });

                if(String.valueOf(LZID).equals(AppSharedPref.newInstance(getApplicationContext()).getUserId())) {
                    delete.setVisibility(View.VISIBLE);
                    deleteTV.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            HttpUtil.delete(API.GET_CUTES + cuteID + "/", new JsonHttpResponseHandler(){
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    ToastUtil.show(getApplicationContext(), "删除失败");
                                    mSlidingDrawer.animateClose();
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    ToastUtil.show(getApplicationContext(),"删除失败");
                                    mSlidingDrawer.animateClose();
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                    if(statusCode == 200) {
                                        ToastUtil.show(getApplicationContext(), "删除成功");

//                                    }
//                                    else
//                                        ToastUtil.show(getApplicationContext(),"删除失败400");

                                    mSlidingDrawer.animateClose();
                                    finish();
                                    super.onSuccess(statusCode, headers, response);
                                }
                            });
                        }
                    });
                }else{
                    delete.setVisibility(View.INVISIBLE);
                    deleteTV.setVisibility(View.INVISIBLE);
                    jubao.setVisibility(View.VISIBLE);
                    jubaoTV.setVisibility(View.VISIBLE);
                    jubao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           HttpUtil.addPatchClientHeader(getApplicationContext());
                            HttpUtil.post(API.GET_CUTES+cuteID+"/",new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    if(statusCode == 200) {
                                        ToastUtil.show(getApplicationContext(), "举报成功");
                                        HttpUtil.removePatchHeader();
                                        finish();
                                    }
                                    else
                                        ToastUtil.show(getApplicationContext(),"举报失败400");
                                    HttpUtil.removePatchHeader();
                                    mSlidingDrawer.animateClose();
                                    super.onSuccess(statusCode, headers, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Log.v("stateCode",statusCode+"");
                                    HttpUtil.removePatchHeader();
                                }
                            });
                        }
                    });
                }


//                AlertDialog alert = new AlertDialog.Builder(PhotoDetailActivity.this).create();
//                alert.show();
//                alert.getWindow().setGravity(Gravity.BOTTOM);
//                WindowManager m = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
//
//
//                alert.getWindow().setLayout(
//                        WindowManager.LayoutParams.MATCH_PARENT, (int) (CuteApplication.getScreenHW(getApplicationContext())[1] * 0.25));
//                alert.getWindow().setContentView(R.layout.bottom_view);
//
//                alert.getWindow().findViewById(R.id.qq).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ToastUtil.show(getApplicationContext(), "QQ");
//                    }
//                });
            }
        });


        /********点击标签效果***********/
//        photoIV.setLabelInfo();
        photoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoIV.setLabelVisbility();
                if (mSlidingDrawer.isOpened())
                  mSlidingDrawer.animateClose();
            }
        });

        /********点击发送评论***********/
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                clearEmoji();

                tmpContent = rely.getText().toString();
                if(tmpContent.length() == 0){
                    ToastUtil.show(getApplicationContext(), "评论内容不能为空！");
                    return;
                }


                commentsBtn.setText(Integer.parseInt(commentsBtn.getText().toString()) + 1 + "");

//                tmpContent += rely.getUnicode();
                LogUtil.v("tmpContent1", tmpContent);
//                tmpContent = tmpContent.substring(0,tmpContent.length() - 6);
//                LogUtil.v("tmpContent2",tmpContent);
                rely.setText("");
                findViewById(R.id.emojicons).setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(rely.getWindowToken(), 0);

//                ToastUtil.show(getApplicationContext(), "评论成功");

                RequestParams params = new RequestParams();
                params.put("user", AppSharedPref.newInstance(getApplicationContext()).getUserId());
                //如果是首页传入评论的id
                if(is_from_home) {
                    params.put("reply_to_user",  commnet_person_id);
                    if(commnet_person_id.equals(LZID)) {
                        params.put("type", 1);
                    } else {
                        params.put("type", 2);
                    }
                    is_from_home = false;
                } else {
                    params.put("reply_to_user",  relyToPerson);
                    if(relyToPerson.equals(LZID)) {
                        params.put("type", 1);
                    } else {
                        params.put("type", 2);
                    }
                }

//                params.put("reply_to_user", is_from_home==true? commnet_person_id :relyToPerson);

                params.put("text",tmpContent);
                params.put("cute", getIntent().getIntExtra("id", 0));
                //  type 回复楼主1  回复某人2

                HttpUtil.addClientHeader(getApplicationContext());
                HttpUtil.post(API.COMMENTS_CUTE_ID + "?format=json", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        LogUtil.v("comment statusCode", statusCode);
                        if (statusCode == 200) {
                            ToastUtil.show(getApplicationContext(), "评论成功");
                            LogUtil.v("评论成功","");
                            emojiNum = 0;
                            HttpUtil.get(API.COMMENTS_CUTE_ID + "?cute=" + getIntent().getIntExtra("id", 0) + "&format=json", new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    LogUtil.v("comment cute id", API.COMMENTS_CUTE_ID + "?cute=" + getIntent().getIntExtra("id", 0) + "&format=json");
                                    try {
                                        next = response.getString("next");
                                        array = new JSONArray(response.getString("results"));
                                        if (array.length() > 0) {
                                            commentsListAdapter = new PhotoDetailCommentsListAdapter(array, PhotoDetailActivity.this,commentsList);
                                            commentsList.setAdapter(commentsListAdapter);

                                            commentsListAdapter.notifyDataSetChanged();
                                        }else
                                            commentsList.setAdapter(null);
                                        if (next != null)
                                           HttpUtil.get(next,commentJsonResponseHandler);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    super.onSuccess(statusCode, headers, response);
                                }
                            });
                        } else
                            ToastUtil.show(getApplicationContext(), "评论失败");

                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        ToastUtil.show(getApplicationContext(), "评论失败");
                        LogUtil.v("commit error",responseString);
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show(getApplicationContext(), "评论失败");
                        LogUtil.v("commit error",errorResponse.toString());
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        ToastUtil.show(getApplicationContext(), "评论失败");
                        LogUtil.v("commit error",errorResponse.toString());
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });

            }
        });



        /********点击返回***********/
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /********加载cute***********/
        HttpUtil.get(API.GET_CUTES + cuteID + "/?format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.getString("text").length() > 0) {
                        descriptionTV.setText(response.getString("text"));
                    }else
                        descriptionTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,10));

                    LZID = response.getString("user");    //要回复的对象（发布该cute1的人）
                    relyToPerson = LZID;
                    relyToName = new JSONObject(response.getString("publish_user")).getString("name");
                    mumHeadTV.setText(relyToName);
                    if(is_from_home) {
                        rely.setHint("回复" + comment_person);
                    } else {
                        rely.setHint("评论" + relyToName);
                    }


                    /********点击评论按钮弹出键盘***********/
                    findViewById(R.id.commemt).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(is_from_home) {
                                rely.setHint("回复" + comment_person);
                            } else {
                                rely.setHint("评论" + relyToName);
                            }
                            relyToPerson = LZID;
                            imm.showSoftInput(rely, InputMethodManager.SHOW_FORCED);
                        }
                    });
                    /**
                     * 为cute对象加标签
                     */
                    JSONArray jsonArray = new JSONArray(response.getString("labels"));
                    cute = new Cute(getApplicationContext());
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = new JSONObject(jsonArray.get(i).toString());

                            Label label = new Label(getApplicationContext());
                            label.setName(obj.getString("name"));
                            label.setDirection(obj.getInt("direction"));
                            label.setX(obj.getDouble("x"));
                            label.setY(obj.getDouble("y"));
                            label.setId(obj.getInt("id"));
                            label.setDirection(obj.getInt("direction"));
                            cute.addLabel(label);
                        }
                    }

                    photoIV.setCute(cute);
                    urlImage = API.STICKERS + response.getString("image_small");
                    CuteApplication.downloadIamge(urlImage, photoIV.getImageView(), new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String s, View view) {
                            photoIV.getImageView().setImageBitmap(null);
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            photoIV.clearGif();
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            photoIV.clearGif();
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });

                    CuteApplication.downloadCornImage(API.STICKERS + new JSONObject(response.getString("publish_user")).getString("image"), mumHeadIV);
                    /**
                     * 点击发布cute用户头像的跳转
                     */
                    mumHeadIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(PhotoDetailActivity.this, PersonActivity.class);
                            intent.putExtra("person_id", Integer.parseInt(LZID));
                            startActivity(intent);
                        }
                    });

                    likedTV.setText(response.getInt("liked_count") + "");


                    JSONArray array = new JSONArray(new JSONObject(response.getString("publish_user")).getString("babies"));
                    JSONObject obj = new JSONObject(array.get(0).toString());
                    if (obj.getInt("gender") == 1) {
                        babyInfoTV.setText(CuteApplication.date2Age(obj.getString("birthday")) + "  " + "男");
                    } else if (obj.getInt("gender") == 2) {
                        babyInfoTV.setText(CuteApplication.date2Age(obj.getString("birthday")) + "  " + "女");
                    } else {
                        babyInfoTV.setText(CuteApplication.date2Age(obj.getString("birthday")));
                    }

                    /**
                     * 点赞状态
                     */
                    likedState = response.getBoolean("is_liked");
                    if (likedState)
                        likedIcon.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.pl_ico1_red));
                    else
                        likedIcon.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.pl_ico1));

                    /**
                     * 读取点赞头像
                     */
                    hListView = (LikeThumbNail) findViewById(R.id.cute_head);
                    final JSONArray likeArray = new JSONArray(response.getString("last_likes"));
                    if(likeArray.length() > 0) {

                        for (int i = 0; i < likeArray.length(); i++) {
                            JSONObject likeObj = (JSONObject) likeArray.get(i);
                            String url = new JSONObject(likeObj.getString("user_detail")).getString("image_small");
                            int id = new JSONObject(likeObj.getString("user_detail")).getInt("auth_user");
                            hListView.addThumbnail(url, response.getInt("liked_count") , cuteID,id);
                            if(id == Integer.parseInt(AppSharedPref.newInstance(getApplicationContext()).getUserId()))
                                curUserIcon = i;
                        }


                    }else
                    {
                        hListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,10));
                    }
                    /**
                     * 点赞响应
                     */
                    likedLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int num = Integer.parseInt(likedTV.getText().toString()) ;
                            if(likedState){
                                num--;
                                if(num ==0 || num < 0) {
                                    hListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BitmapUtil.dip2px(getApplicationContext(), 10)));
                                    num = 0;
                                }

                                likedTV.setText(num  + "");
                                hListView.removeUserHead(curUserIcon + 1); //因为LikeThumbNail 里第0个是 user_icon
                                likedState = false;
                                likedIcon.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.pl_ico1));


                            }else{
                                num ++;
                                likedIcon.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.pl_ico1_red));
                                likedTV.setText(num  + "");
                                if(num > 0)
                                    hListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BitmapUtil.dip2px(getApplicationContext(),40)));
                                likedState = true;
                                hListView.addUserHead();
                            }


                            hListView.clear();
                            RequestParams params = new RequestParams();
                            params.put("user", Integer.parseInt(AppSharedPref.newInstance(getApplicationContext()).getUserId()));
                            params.put("cute", cuteID);
                            HttpUtil.post(API.LIKE, params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                    if (statusCode == 201) {
                                        ToastUtil.show(PhotoDetailActivity.this, "赞一下");
                                    } else if (statusCode == 204) {
                                        ToastUtil.show(PhotoDetailActivity.this, "好桑心");
                                    }
                                    //  更新点赞头像list
//                                    HttpUtil.get(API.LIKE + "?cute=" + cuteID,new JsonHttpResponseHandler(){
//                                        @Override
//                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                            JSONArray  updatelikeArray = null;
//                                            try {
//                                                 updatelikeArray   = new JSONArray(response.getString("results"));
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            if(updatelikeArray.length() == 0)
//                                                hListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,10));
//                                            else if (updatelikeArray.length() > 0)
//                                                hListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BitmapUtil.dip2px(getApplicationContext(),40)));
//
//                                            for (int i = 0; i < updatelikeArray .length(); i++) {
//                                                JSONObject likeObj = null;
//                                                try {
//                                                    likeObj = (JSONObject) updatelikeArray .get(i);
//                                                    String url = new JSONObject(likeObj.getString("user_detail")).getString("image_small");
//                                                    int id = new JSONObject(likeObj.getString("user_detail")).getInt("auth_user");
//                                                    hListView.addThumbnail(url, response.getInt("liked_count") , cuteID,id);
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//
//                                            super.onSuccess(statusCode, headers, response);
//                                        }
//                                    });
                                    super.onSuccess(statusCode, headers, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    ToastUtil.show(PhotoDetailActivity.this, "网络异常");
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                }
                            });
                        }
                    });   
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HttpUtil.get(API.COMMENTS_CUTE_ID + "?cute=" + getIntent().getIntExtra("id", 0) + "&format=json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        try {
                            next = response.getString("next");
                            LogUtil.v("next",next);
                            array = new JSONArray(response.getString("results"));
                            if (array.length() > 0) {
                                commentsBtn.setText(response.getString("count"));
                                commentsListAdapter = new PhotoDetailCommentsListAdapter(array, PhotoDetailActivity.this,commentsList);
                                commentsList.setAdapter(commentsListAdapter);
//                                ListViewUtility.setListViewHeightBasedOnChildren(commentsList);
                                commentsListAdapter.notifyDataSetChanged();
                            }else
                                commentsList.setAdapter(null);

                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            rely.setFocusable(true);
                            rely.setFocusableInTouchMode(true);
                            rely.requestFocus();
                            if (next != null)
                            HttpUtil.get(next,commentJsonResponseHandler);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                });
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                rely.setFocusable(true);
                rely.setFocusableInTouchMode(true);
                rely.requestFocus();

                /*
                    判断 当前用户！与这人  相互关注关系
            */
//                if (!AppSharedPref.newInstance(getApplicationContext()).getUserId().equals(LZID)){
//                    HttpUtil.get(API.ADD_FRIENDS + "?user=" + AppSharedPref.newInstance(getApplicationContext()).getUserId() + "&follow=" + LZID, new JsonHttpResponseHandler() {
//
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            try {
//                                if (response.getInt("count") == 1) {
//                                    JSONArray array = new JSONArray(response.getString("results"));
//                                    JSONObject obj = (JSONObject) array.get(0);
//                                    if (obj.getBoolean("repeat")) {
//                                        followState.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.qh_pic));
//                                        FOLLOW_STATE = 3;
//                                    } else {
//                                        followState.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.reply_pic));
//                                        FOLLOW_STATE = 2;
//                                    }
//                                } else {
//                                    followState.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.jh_pic));
//                                    FOLLOW_STATE = 1;
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            followState.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    switch (FOLLOW_STATE) {
//                                        case 1:
//                                            followState.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.reply_pic));
//                                            FOLLOW_STATE = 2;
//                                            ToastUtil.show(getApplication(), "关注成功");
//                                            break;
//                                        case 2:
//                                            followState.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.jh_pic));
//                                            FOLLOW_STATE = 1;
//                                            ToastUtil.show(getApplication(), "取消关注");
//                                            break;
//                                        case 3:
//                                            followState.setImageDrawable(getApplication().getResources().getDrawable(R.drawable.jh_pic));
//                                            FOLLOW_STATE = 1;
//                                            ToastUtil.show(getApplication(), "取消关注");
//                                            break;
//                                    }
//
//                                    RequestParams params = new RequestParams();
//                                    params.put("user", Integer.parseInt(AppSharedPref.newInstance(getApplicationContext()).getUserId()));
//                                    params.put("follow", LZID);
//                                    HttpUtil.post(API.ADD_FRIENDS, params, new JsonHttpResponseHandler() {
//                                        @Override
//                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                            if (statusCode == 201) {
//                                                LogUtil.v("关注成功", "");
//
//                                            } else if (statusCode == 204) {
//                                                LogUtil.v("取消关注", "");
//                                            }
//
//                                            super.onSuccess(statusCode, headers, response);
//                                        }
//
//                                        @Override
//                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                            ToastUtil.show(getApplicationContext(), "网络异常");
//                                            super.onFailure(statusCode, headers, responseString, throwable);
//                                        }
//                                    });
//                                }
//                            });
//                            super.onSuccess(statusCode, headers, response);
//                        }
//                    });
//            }else
//                    followState.setVisibility(View.GONE);

                super.onSuccess(statusCode, headers, response);
            }



            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSlidingDrawer.isOpened()){
                 mSlidingDrawer.animateClose();
            return false;}
        }
        else
           return super.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }
    private void sinaInit() {
        // 创建授权认证信息
        WeiboAuth.AuthInfo authInfo = new WeiboAuth.AuthInfo(getApplicationContext(), Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        weibo.setWeiboAuthInfo(authInfo, mLoginListener);
    }

    @Override
    public void onEmojiconBackspaceClicked(View view) {
        emojiNum--;
        EmojiconsFragment.backspace(rely);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(rely, emojicon);
        emojiNum ++;
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
       final  Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            /*
                    授权成功
             */
            if (accessToken != null && accessToken.isSessionValid()) {
//                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
//                        new java.util.Date(accessToken.getExpiresTime()));
//                String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
//                mTokenView.setText(String.format(format, accessToken.getToken(), date));
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), accessToken);
               Toast.makeText(getApplicationContext(),"微博登录成功！",Toast.LENGTH_LONG).show();
                LogUtil.v("","微博登陆成功");

                /*
                           登陆成功给和美服务器
                           1.weibo_id：   uid
                           2.weibo_token  access_token
                     */
                RequestParams params = new RequestParams();
                try {
                    params.put("weibo_id", accessToken.getUid());
                    params.put("weibo_token", accessToken.getToken());
                    LogUtil.v("weibo_id+weibo_token", accessToken.getUid() + "++" + accessToken.getToken());
//                    AppSharedPref.newInstance(getApplicationContext()).setWeiboUserId(accessToken.getUid());
//                    AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(accessToken.getToken());
//                        params.put("qq_sig","yCdpibyLyps+wrZMat/4vqkC3cc=");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpUtil.addClientHeader(getApplicationContext());
                HttpUtil.post(API.BIND_WEIBO, params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        LogUtil.v("","和美服务器发送请求成功");
                        try {

                            Toast.makeText(getApplicationContext(), "绑定成功!!!!!!!", Toast.LENGTH_LONG).show();
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboUserId(accessToken.getUid());
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(accessToken.getToken());
//                            AppSharedPref.newInstance(getApplicationContext()).setUserName(response.getString("username"));

                            LogUtil.v("token",response.getString("token")+"");
                            LogUtil.v("id",response.getString("id")+"");
//                            mSlidingDrawer.close();
//                            Toast.makeText(getApplicationContext(),"绑定成功!!!!!!!",Toast.LENGTH_LONG).show();
//                            LogUtil.v("username",response.getString("username")+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(),"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplicationContext(),"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplicationContext(),"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LogUtil.v("@@@@@@","!!!!!!!");
                    }
                });

                /**
                 *  获取个人信息 检查babies ??
                 */
//                HttpUtil.addWeiboClientHeader(getApplicationContext());

                LogUtil.v("获取个人信息Url",API.GET_USER + AppSharedPref.newInstance(getApplicationContext()).getUserId() +"/?format=json");
//                HttpUtil.get(API.GET_USER + AppSharedPref.newInstance(getApplicationContext()).getUserId() + "/?format=json", new JsonHttpResponseHandler() {
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                            /**
//                             * 存储个人信息
//                             */
////                            User.setUserNmae(getApplicationContext(), response.getString("name"));
//
//                            JSONArray jsonArray = new JSONArray(response.getString("babies"));
//                            // 没有baby
//                            if(jsonArray.length() < 1){
//                                startActivity(new Intent(UserSettingAty.this, UserProfileStart.class));
//                                finish();
//                            }else{
//                                startActivity(new Intent(UserSettingAty.this, TabHostActivity.class));
//                                finish();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        super.onSuccess(statusCode, headers, response);
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        ToastUtil.show(UserSettingAty.this, "获取个人信息失败");
//                        LogUtil.e(TAG, responseString);
//
//                        super.onFailure(statusCode, headers, responseString, throwable);
//                    }
//                });




            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(),
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_SHORT).show();
        }
    }
    private void initView(){
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.sliding);
        QQ = (ImageView) findViewById(R.id.qq);
        weixin = (ImageView) findViewById(R.id.weixin);
        friendsCircle = (ImageView) findViewById(R.id.pengyouquan);
        weibo = (LoginButton) findViewById(R.id.weibo);
        collection = (ImageView) findViewById(R.id.collection);
        Qzone = (ImageView) findViewById(R.id.image_QQkongjian);
        delete = (ImageView) findViewById(R.id.delete);
        deleteTV = (TextView) findViewById(R.id.delete_tv);
    }
    private void qqInit() {

        mQQAuth = QQAuth.createInstance(StartActivity.APP_ID, getApplicationContext());
        mTencent = Tencent.createInstance(StartActivity.APP_ID, getApplicationContext());
        LogUtil.i("mQQAuth", "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
        LogUtil.i("mQQAuth", "isSessionValid=>" + mQQAuth.isSessionValid());

        mQQShare = new QQShare(getApplicationContext(), mQQAuth.getQQToken());
//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }
    }
    private void onClickShare(Cute cute, String url) {
        final Bundle params = new Bundle();
        String content = "";
        for (int i = 0; i < cute.getLabelListLength(); i++){
            content += "#"+cute.getLabel(i).getName()+"# ";
        }
//        Uri uri = Uri.parse("android.resource://com.harmazing.cute/res/");
//        String path = getAbsoluteImagePath(uri);
//        Log.v("path",path);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, getString(R.string.title_share_to_friends));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getString(R.string.summary_share_to_friends));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, API.SHARE_PIC_TO + cuteID+"/");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "返回CUTE");
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mQQShare.shareToQQ(PhotoDetailActivity.this, params, new IUiListener(){
                    @Override
                    public void onComplete(Object o) {
                        Log.v("o", o.toString());
                        UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.TENCENT_QQ, AppSharedPref.newInstance(PhotoDetailActivity.this).getUserId());
                        MobclickAgent.onSocialEvent(PhotoDetailActivity.this, platform);
                        Toast.makeText(getApplicationContext(), "分享成功！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.v("uiError",uiError.errorMessage);
                    }
                });
            }
        }).start();
    }

    private void onFavoriteCute (Cute cute){
        final RequestParams params = new RequestParams();
        params.put("cute",cuteID);
        params.put("user",AppSharedPref.newInstance(getApplicationContext()).getUserId());
        HttpUtil.addClientHeader(getApplicationContext());
        HttpUtil.post(API.FAVOURITE_CUTES, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.v("post favorite", response.toString());
                Log.v("post statusCode", ""+statusCode);
                Toast.makeText(getApplicationContext(),"收藏成功！",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.v("post favorite,fail",responseString);
                Log.v("fail statusCode", ""+statusCode);
                Toast.makeText(getApplicationContext(),"已收藏！",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.v("fail statusCode", ""+statusCode);
                Log.v("post favorite,fail",errorResponse.toString());
                Toast.makeText(getApplicationContext(),"已收藏！",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.v("fail statusCode", ""+statusCode); Toast.makeText(getApplicationContext(),"已收藏！",Toast.LENGTH_LONG).show();
            }
        });

    }
    private void onShare2Qzone(Cute cute, String url){
        final Bundle params = new Bundle();
        String content = "";
        for (int i = 0; i < cute.getLabelListLength(); i++){
            content += "#"+cute.getLabel(i).getName()+"# ";
        }
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(url);
        String title = getString(R.string.title_share_to_circle);
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,getString(R.string.summary_share_to_circle));
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, API.SHARE_PIC_TO+cuteID+"/");
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,imageUrls);
        doShareToQzone(params);
    }
    private void doShareToQzone(final Bundle params) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mTencent.shareToQzone(PhotoDetailActivity.this, params, new IUiListener() {

                    @Override
                    public void onCancel() {
                        Log.v("activity", "onCancel: ");
                    }

                    @Override
                    public void onError(UiError e) {
                        // TODO Auto-generated method stub
                        Log.v("onError: " + e.errorMessage, "e");
                    }

                    @Override
                    public void onComplete(Object response) {
                        // TODO Auto-generated method stub
                        Log.v("onComplete: ", response.toString());
                        UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.TENCENT_QZONE, AppSharedPref.newInstance(PhotoDetailActivity.this).getUserId());
                        MobclickAgent.onSocialEvent(PhotoDetailActivity.this, platform);
                        Toast.makeText(PhotoDetailActivity.this,"分享成功！",Toast.LENGTH_LONG).show();
                    }

                });
            }
        }).start();
    }
    private void onWeiboShare(final Cute cute,final String url){
        RequestParams params = new RequestParams();
        if (AppSharedPref.newInstance(this).getWeiboToken().equals("")){
            Toast.makeText(this,"您的微博没有绑定，请先微博登录或绑定!",Toast.LENGTH_LONG).show();
            weibo.loginWeiBo();
        }
        else {
            params.put("access_token", AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
            HttpUtil.post(API.ACCESS_TOKEN_IS_VALID, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.v("access_token_valid", response.toString());
                    try {
                        int time = response.getInt("expire_in");
                        if (time >= 0)
                            onClickShareWeibo(cute, url);
                        else{
                            Toast.makeText(getApplicationContext(), "微博认证过期，请用微博重新登录或重新绑定微博！", Toast.LENGTH_LONG).show();
                             weibo.loginWeiBo();}
                        Log.v("time", time + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
//              onClickShareWeibo(holder, url);
    }
    private void onClickShareWeibo(Cute cute,String url){
        final RequestParams params = new RequestParams();
        String status = null;
        String content = getString(R.string.title_share_to_circle)+getString(R.string.summary_share_to_circle);
//        for (int i = 0; i < cute.getLabelListLength(); i++){
//            content += "#"+cute.getLabel(i).getName()+"# ";
//        }
        try {
            status = URLEncoder.encode(content + "链接地址：" + API.SHARE_PIC_TO + cuteID + "/");
        }catch (Exception e){
            e.printStackTrace();
        }
        params.put("access_token",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
        Log.v("access_token",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
        params.put("status",status);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Log.v("url",url);
//
//        ByteArrayInputStream byteImage = new ByteArrayInputStream(baos.toByteArray());
//        Log.v("bbbb",baos.toByteArray().length+"");
//        params.put("pic",byteImage,"share.jpg");
        HttpUtil.removePatchHeader();
        HttpUtil.get(url,new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.v("bytes",bytes.length+"");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
                ByteArrayInputStream byteImage = new ByteArrayInputStream(baos.toByteArray());
                Log.v("bytes!",""+baos.toByteArray().length);
                params.put("pic",byteImage,"share.jpg");
                HttpUtil.post(API.SEND_MESSAGE_TO_WEIBO_FRIENDS, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.v("response", response.toString());
                        UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.SINA_WEIBO, AppSharedPref.newInstance(PhotoDetailActivity.this).getUserId());
                        MobclickAgent.onSocialEvent(PhotoDetailActivity.this, platform);
                        Toast.makeText(getApplicationContext(),"分享成功！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.v("errer",responseString);
                    }
                });

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"失败！",Toast.LENGTH_LONG).show();
            }
        });
//            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());


//        params.put("access_token",AppSharedPref.newInstance(ctx).getWeiboToken());
//        Log.v("access_token",AppSharedPref.newInstance(ctx).getWeiboToken());
//        params.put("status",status);
//        params.put("pic",new ByteArrayInputStream(),"title.png");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (weibo != null) {
            weibo.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void clearEmoji(){
        for(int i=0;i>emojiNum;i++){
            EmojiconsFragment.backspace(rely);
        }
    }
    private void wechatShare(final String type,Cute cute,String urlImage){
        String content = "";
        for (int i = 0; i < cute.getLabelListLength(); i++){
            content += "#"+cute.getLabel(i).getName()+"# ";
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = API.SHARE_PIC_TO + cuteID+"/";
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        if (type.equals("FriendsCircle")){
            msg.title = getResources().getString(R.string.title_share_to_circle);
            msg.description = getResources().getString(R.string.summary_share_to_circle);
        }
        else{
            msg.title = getString(R.string.title_share_to_friends);
            msg.description = getString(R.string.summary_share_to_friends);
        }
        HttpUtil.removePatchHeader();
        HttpUtil.get(urlImage,  new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,opts);
                msg.thumbData = Util.bmpToByteArray(bitmap, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction(type);
                req.message = msg;
                req.scene = type.equals("FriendsCircle") ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                api.sendReq(req);
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.v("网速很慢，请您稍后重试",throwable.toString());
                Toast.makeText(PhotoDetailActivity.this,"网速很慢，请您稍后重试。",Toast.LENGTH_LONG).show();
            }
        });
//        HttpUtil.addClientHeader(ctx);
//
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
