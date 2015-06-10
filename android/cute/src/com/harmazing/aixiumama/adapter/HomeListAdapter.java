package com.harmazing.aixiumama.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.activity.StartActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.fragment.AttentionFragment;
import com.harmazing.aixiumama.model.Cute;
import com.harmazing.aixiumama.model.Label;
import com.harmazing.aixiumama.model.sina.AccessTokenKeeper;
import com.harmazing.aixiumama.model.sina.Constants;
import com.harmazing.aixiumama.model.sina.LoginButton;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.ImageUtils2;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.BaseMyListView;
import com.harmazing.aixiumama.view.CutePersonThumbNail;
import com.harmazing.aixiumama.view.LabelImageView;
import com.harmazing.aixiumama.wxapi.WXEntryActivity;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;

//import android.app.AlertDialog;

/**
 * Created by Lyn on 2014/11/6.
 */
public class HomeListAdapter extends BaseAdapter {
    QQShare mQQShare;
    Tencent mTencent;
    QQAuth mQQAuth;
    private IWXAPI api;
//    public static String APP_ID="1103396138";
    public static JSONArray kindArray;
    private Context ctx;
    private Activity activity = null;
    private static int FOLLOW_STATE;
    private AuthListener mLoginListener = new AuthListener();
    SlidingDrawer mSlidingDrawer;
    ImageView QQ, weixin, friendsCircle,collection,Qzone,delete,jubao;
    TextView deleteTV,jubaoTV;
    LoginButton weibo;
    BaseMyListView commentsListView;

    public String pulisherName;

    public void addKingdArray(JSONArray array){
        int len = kindArray.length();
        for(int i=0;i<array.length();i++)
            try {
                kindArray.put( len + i,array.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public Boolean isRefresh(String date){
                try {
                    Log.v("传入isrefresh", date);
                    Log.v("isrefresharray",kindArray.getJSONObject(0).getString("create_date"));
                    return (kindArray.getJSONObject(0).getString("create_date").substring(0,19).equals(date));
                }catch (Exception e){
                    Log.v("isrefresh","falure");
                    return false;
        }

    }

    public void refresh(JSONArray jsonArray){

        try {
            Log.v("refresh前",this.kindArray.getJSONObject(0).getString("create_date"));
            this.kindArray = jsonArray;
            Log.v("refresh后",this.kindArray.getJSONObject(0).getString("create_date"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    static public void setJsonArray(JSONArray array){
        kindArray = array;
    }

    public HomeListAdapter(JSONArray kindArray
            , Context ctx) {
        api = WXAPIFactory.createWXAPI(ctx, CuteApplication.APP_ID);
        this.kindArray = kindArray;
        this.ctx = ctx;
        this.activity = (Activity)ctx;
        initView();
        qqInit();
        Log.v("kindArray","");
    }
    public HomeListAdapter(JSONArray kindArray
            ) {
        this.kindArray = kindArray;
    }

    @Override
    public int getCount() {

        return kindArray.length();
    }

    @Override
    public Object getItem(int position) {

        return kindArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {

        return -1;
    }

    ImageUtils2.ImageCallback callback=new ImageUtils2.ImageCallback() {
        @Override
        public void loadImage(Bitmap bitmap, String imagePath) {
            notifyDataSetChanged();
        }
    };

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {

        final ViewHolder holder ;
        initView();
        if (AppSharedPref.newInstance(ctx).getToken()!= null && AppSharedPref.newInstance(ctx).getWeiboToken() != null ){
            sinaInit();
        }
        if (convertView == null ) {
            holder = new ViewHolder();
            if (ctx != null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.home_listview_item, parent,false);
            holder.picTextTV = (TextView)convertView.findViewById(R.id.pic_text);
            holder.pulisherTV = (TextView)convertView.findViewById(R.id.pulisher);
            holder.baby_infoTV = (TextView)convertView.findViewById(R.id.baby_info);
            holder.comentsTV = (TextView)convertView.findViewById(R.id.coments);
            holder.likedTV = (TextView)convertView.findViewById(R.id.liked);

            holder.labelImageView = (LabelImageView) convertView.findViewById(R.id.label_image);
            holder.cuteHead = (CutePersonThumbNail)convertView.findViewById(R.id.cute_head);
            holder.mumHead = (ImageView)convertView.findViewById(R.id.user_icon);
            holder.commentLayout = (LinearLayout)convertView.findViewById(R.id.comment_layout);
            holder.stateIV = (ImageView)convertView.findViewById(R.id.follow_state);
            holder.moreBtn = (Button)convertView.findViewById(R.id.more);
            holder.likedIcon = (ImageView)convertView.findViewById(R.id.liked_icon);
            holder.likedLayout = (LinearLayout)convertView.findViewById(R.id.liked_layout);
            holder.cute = new Cute(ctx);
            convertView.setTag(holder);
            }
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        commentsListView = (BaseMyListView)convertView.findViewById(R.id.comments_listview);


        try
        {
            holder.commentsNum = ((JSONObject) getItem(position)).getInt("comments_count");
//            LogUtil.v("holder.commentsNum", holder.commentsNum);

            /**
             *  评论 List
             */
            View view = convertView.findViewById(R.id.v_all_comments);
            view.setVisibility(View.GONE);
            holder.commentsArray = new JSONArray(((JSONObject) getItem(position)).getString("last_comments"));

            holder.comentsTV.setText(holder.commentsNum + "");

            //commentsListView.setAdapter(new CommentsListAdapter(holder.commentsArray, ctx, holder.cuteId, pulisherName));

            /*
               显示 查看全部……条评论
           */
            if(holder.commentsNum > 3) {
                TextView num = (TextView) view.findViewById(R.id.num);
                num.setText(String.format(ctx.getResources().getString(R.string.replt_format), holder.commentsNum + ""));
    //            commentsListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ListViewUtility.getListViewHeight(commentsListView)));
                view.setVisibility(View.VISIBLE);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                        intent.putExtra("id", holder.cuteId);
                        intent.putExtra("showAllComments", true);
                        ctx.startActivity(intent);
                    }
                });
            }


            holder.labelImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CuteApplication.getScreenHW(ctx)[0]));
            /**
             * 为cute对象加标签
             */
            JSONArray jsonArray = new  JSONArray(((JSONObject)getItem(position)).getString("labels"));
                holder.cute.clear();
                holder.labelImageView.clear();
            double x, y;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = new JSONObject(jsonArray.get(i).toString());
                    x =  Double.valueOf(obj.get("x").toString());
                    y = Double.valueOf(obj.get("y").toString());
                    LogUtil.v("第"+position+"的 xy",obj.get("x").toString()+"    "+obj.get("y").toString());
                    if(y > 1 )
                        y = 0.95d;
                    if(y < 0)
                        y = 0.5d;

                    if(x > 1)
                        x = 0.95d;
                    if(x < 0)
                        x = 0.5d;

                    Label label = new Label(ctx);
                    label.setName(obj.getString("name"));
                    label.setDirection(obj.getInt("direction"));
                    label.setX(x);
                    label.setY(y) ;

                    LogUtil.v(" 显示的xy", x+"     "+y);
//                    LogUtil.v("label xy",label.getX()+"    "+ label.getY());

                    label.setId(obj.getInt("id"));
                    label.setDirection(obj.getInt("direction"));
                    holder.cute.addLabel(label);
                }


            holder.labelImageView.setCute(holder.cute);

            final String urlImage = API.STICKERS +((JSONObject) getItem(position)).getString("image_small");

            CuteApplication.downloadIamge(urlImage, holder.labelImageView.getImageView(), new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    holder.labelImageView.clearGif();
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    holder.labelImageView.clearGif();
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });

            holder.labelImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**
                     *      所有标签点击闪烁、再点消失
                     */
                    holder.labelImageView.setLabelVisbility();
                    if (mSlidingDrawer.isOpened()) {
                        mSlidingDrawer.animateClose();
                    }
                }
            });
            /**
             * 获得该 item 的 发布者id、cuteid，与当前用户关系
             */
            holder.publishUserID = new JSONObject(((JSONObject) getItem(position)).getString("publish_user")).getInt("auth_user");
            holder.cuteId = ((JSONObject) getItem(position)).getInt("id");

            holder.likedState = ((JSONObject)getItem(position)).getBoolean("is_liked");
            holder.likedTV.setText(((JSONObject) getItem(position)).getString("liked_count"));
            pulisherName = new JSONObject(((JSONObject) getItem(position)).getString("publish_user")).getString("name");
            holder.pulisherTV.setText(pulisherName);

            CuteApplication.downloadCornImage( API.STICKERS + new JSONObject(((JSONObject) getItem(position)).getString("publish_user")).getString("image"),holder.mumHead);
            holder.mumHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, PersonActivity.class);
                    intent.putExtra("person_id",holder.publishUserID);
                    ctx.startActivity(intent);
                }
            });
            if(((JSONObject) getItem(position)).getString("text").length() > 1) {
                holder.picTextTV.setText(((JSONObject) getItem(position)).getString("text"));
                holder.picTextTV.setVisibility(View.VISIBLE);
            }
            else
                holder.picTextTV.setVisibility(View.GONE);

            JSONArray array = new JSONArray(new JSONObject(((JSONObject)getItem(position)).getString("publish_user")).getString("babies"));
            final JSONObject obj = (JSONObject)array.get(0);
            holder.baby_infoTV.setText(CuteApplication.calculateBabyInfo(obj));


            //首页评论
            commentsListView.setAdapter(new CommentsListAdapter(holder.commentsArray, ctx, holder.cuteId, pulisherName));

            /**
             *  点击评论按钮
             */
            holder.commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                    intent.putExtra("id", holder.cuteId);
//                    intent.putExtra("position", position);
                    ctx.startActivity(intent);
                }
            });



            /**
             *  点赞头像LIst
             */

            holder.likeArray = new JSONArray(((JSONObject)getItem(position)).getString("last_likes"));
                if(holder.likeArray.length() > 0) {
                    holder.cuteHead.clear();
                    LinkedList<String> list = new LinkedList<String>();
                    LinkedList<Integer> listUserID = new LinkedList<Integer>();
                    for (int i = 0; i < holder.likeArray.length(); i++) {

                        JSONObject likeObj = (JSONObject) holder.likeArray.get(i);
                        if (i < 9) {
                            list.add(new JSONObject(likeObj.getString("user_detail")).getString("image_small"));
                            listUserID.add(new JSONObject(likeObj.getString("user_detail")).getInt("auth_user"));
                        } else
                            break;
                    }
                    holder.cuteHead.setThumbnail(list, ((JSONObject) getItem(position)).getInt("liked_count") ,holder.cuteId);
                    holder.cuteHead.setOnClick(listUserID);
                    holder.cuteHead.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,BitmapUtil.dip2px(ctx,40)));
                }else {
                    holder.cuteHead.clear();
                    holder.cuteHead.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                }
            /**
             *  滑出分享、删除
             */
            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSlidingDrawer.animateOpen();
                    friendsCircle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            wechatShare("FriendsCircle",holder,urlImage);
                            mSlidingDrawer.animateClose();
                        }
                    });
                    weixin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            wechatShare("person", holder, urlImage);
                            mSlidingDrawer.animateClose();
                        }
                    });
                    QQ.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onClickShare(holder, urlImage);
                            mSlidingDrawer.animateClose();
                        }
                    });
                    collection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onFavoriteCute(holder);
                            mSlidingDrawer.animateClose();
                        }
                    });
                   Qzone.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                            onShare2Qzone(holder,urlImage);
                            mSlidingDrawer.animateClose();
                       }
                   });
                   weibo.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           onWeiboShare(holder,urlImage);
                           mSlidingDrawer.animateClose();
                       }
                   });
                    if(String.valueOf(holder.publishUserID).equals(AppSharedPref.newInstance(ctx).getUserId())) {
                        jubao.setVisibility(View.INVISIBLE);
                        jubaoTV.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.VISIBLE);
                        deleteTV.setVisibility(View.VISIBLE);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HttpUtil.addClientHeader(ctx);
                                HttpUtil.delete(API.GET_CUTES + holder.cuteId + "/", new JsonHttpResponseHandler(){
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        ToastUtil.show(ctx,"已删除");
                                        mSlidingDrawer.animateClose();
                                        super.onFailure(statusCode, headers, responseString, throwable);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        ToastUtil.show(ctx,errorResponse.toString());
                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                    }

                                    @TargetApi(Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        if(statusCode == 204) {
                                            ToastUtil.show(ctx, "删除成功");
//                                            try {
//                                                kindArray.remove(position);
//                                            }catch (Exception e){e.printStackTrace();}
//                                            HomeListAdapter.this.notifyDataSetChanged();
                                            AttentionFragment.refreshList();
                                        }
                                        else
                                            ToastUtil.show(ctx,"删除失败400");

                                        mSlidingDrawer.animateClose();
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
                                HttpUtil.addPatchClientHeader(ctx);
                                HttpUtil.post(API.GET_CUTES+ holder.cuteId +"/",new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        if(statusCode == 200) {
                                            ToastUtil.show(ctx, "举报成功");
                                            HttpUtil.removePatchHeader();
                                        }
                                        else
                                            ToastUtil.show(ctx,"举报失败400");
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
                }
            });

            /**
             * 点赞状态
             */
            if(holder.likedState)
                holder.likedIcon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.pl_ico1_red));
            else
                holder.likedIcon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.pl_ico1));

            /**
             * 点赞
             */
            holder.likedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  初始化
                    final LinkedList<String> list = new LinkedList<String>();
                    final LinkedList<Integer> listUserID = new LinkedList<Integer>();
                    holder.cuteHead.clear();

                    RequestParams params = new RequestParams();
                    params.put("user", Integer.parseInt(AppSharedPref.newInstance(ctx).getUserId()));
                    params.put("cute", holder.cuteId);
                    HttpUtil.post(API.LIKE, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            if (statusCode == 201) {
                                ToastUtil.show(ctx, "赞一下");
                                // 变红
                                holder.likedIcon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.pl_ico1_red));
                                holder.likedTV.setText(Integer.parseInt(holder.likedTV.getText().toString()) + 1 + "");
                                holder.likedState = true;
                            } else if (statusCode == 204) {
                                holder.likedState = false;
                                holder.likedIcon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.pl_ico1));
                                if(!holder.likedTV.getText().toString().equals("0")) {
                                    holder.likedTV.setText(Integer.parseInt(holder.likedTV.getText().toString()) - 1 + "");
                                }
                                ToastUtil.show(ctx, "好桑心");
                            }

                            new AsyncTask(){
                                @Override
                                protected void onPreExecute() {
                                    holder.likedLayout.setEnabled(false);
                                }

                                @Override
                                protected Object doInBackground(Object[] objects) {
                                    SystemClock.sleep(2000);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object o) {
                                    holder.likedLayout.setEnabled(true);
                                }
                            }.execute();


                            //  更新点赞头像list
                            HttpUtil.get(API.LIKE + "/?cute=" + holder.cuteId,new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                    try {
                                        holder.likeArray   = new JSONArray(response.getString("results"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    //  自动调整点赞头像list高度
                                    if(holder.likeArray.length() >= 1){
                                        holder.cuteHead.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,BitmapUtil.dip2px(ctx,40)));
                                    }else if(holder.likeArray.length() == 0)
                                        holder.cuteHead.setLayoutParams(new LinearLayout.LayoutParams(0,0));

                                    for (int i = 0; i < holder.likeArray .length(); i++) {
                                        JSONObject likeObj = null;
                                        try {
                                            likeObj = (JSONObject) holder.likeArray .get(i);

                                            if (i < 9) {
                                                list.add(new JSONObject(likeObj.getString("user_detail")).getString("image_small"));
                                                listUserID.add(new JSONObject(likeObj.getString("user_detail")).getInt("auth_user"));
                                            } else
                                                break;

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    holder.cuteHead.setThumbnail(list, Integer.parseInt(holder.likedTV.getText().toString()),holder.cuteId);
                                    holder.cuteHead.setOnClick(listUserID);

                                    /**
                                     *  修改数据源，防止listview下滑再回来的数据不同步现象
                                     */
                                    try {
                                        JSONObject object = (JSONObject)kindArray.get(position);
                                        object.put("last_likes",holder.likeArray);
                                        object.put("is_liked",holder.likedState);
                                        object.put("liked_count",holder.likeArray .length());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    super.onSuccess(statusCode, headers, response);
                                }
                            });
                            super.onSuccess(statusCode, headers, response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            ToastUtil.show(ctx, "网络异常");
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        /*
                    判断 当前用户！与这人关注的人的 相互关注关系
            */

//        if(!AppSharedPref.newInstance(ctx).getUserId().equals(holder.publishUserID+"")) {
//            HttpUtil.get(API.ADD_FRIENDS + "?user=" + AppSharedPref.newInstance(ctx).getUserId() + "&follow=" + holder.publishUserID, new JsonHttpResponseHandler() {
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    try {
//                        if (response.getInt("count") == 1) {
//                            JSONArray array = new JSONArray(response.getString("results"));
//                            JSONObject obj = (JSONObject) array.get(0);
//                            if (obj.getBoolean("repeat")) {
//                                holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.qh_pic));
//                                FOLLOW_STATE = 3;
//                            } else {
//                                holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.reply_pic));
//                                FOLLOW_STATE = 2;
//                            }
//                        } else {
//                            holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.jh_pic));
//                            FOLLOW_STATE = 1;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    holder.stateIV.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            switch (FOLLOW_STATE) {
//                                case 1:
//                                    holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.reply_pic));
//                                    FOLLOW_STATE = 2;
//                                    ToastUtil.show(ctx, "关注成功");
//                                    break;
//                                case 2:
//                                    holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.jh_pic));
//                                    FOLLOW_STATE = 1;
//                                    ToastUtil.show(ctx, "取消关注");
//                                    break;
//                                case 3:
//                                    holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.jh_pic));
//                                    FOLLOW_STATE = 1;
//                                    ToastUtil.show(ctx, "取消关注");
//                                    break;
//                            }
//
//                            RequestParams params = new RequestParams();
//                            params.put("user", Integer.parseInt(AppSharedPref.newInstance(ctx).getUserId()));
//                            params.put("follow", holder.publishUserID);
//                            HttpUtil.post(API.ADD_FRIENDS, params, new JsonHttpResponseHandler() {
//                                @Override
//                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                    if (statusCode == 201) {
//                                        LogUtil.v("关注成功", "");
//
//                                    } else if (statusCode == 204) {
//                                        LogUtil.v("取消关注", "");
//                                    }
//
//                                    super.onSuccess(statusCode, headers, response);
//                                }
//
//                                @Override
//                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                    ToastUtil.show(ctx, "网络异常");
//                                    super.onFailure(statusCode, headers, responseString, throwable);
//                                }
//                            });
//                        }
//                    });
//                    super.onSuccess(statusCode, headers, response);
//                }
//            });
//        }else
//            holder.stateIV.setVisibility(View.GONE);
        return convertView;
    }

    static class ViewHolder {
        TextView picTextTV,pulisherTV,comentsTV,baby_infoTV;
        TextView likedTV;

        LabelImageView labelImageView;
        CutePersonThumbNail cuteHead;
        LinearLayout commentLayout,likedLayout;
        ImageView mumHead,likedIcon,stateIV;
        Button moreBtn;
        boolean likedState;
        public int publishUserID,cuteId,commentsNum;
        JSONArray commentsArray,likeArray;
        Cute cute;


    }
    private void initView(){
         if (activity != null){
         mSlidingDrawer = (SlidingDrawer)activity.findViewById(R.id.sliding);
         QQ = (ImageView) activity.findViewById(R.id.qq);
         weixin = (ImageView) activity.findViewById(R.id.weixin);
         friendsCircle = (ImageView) activity.findViewById(R.id.pengyouquan);
         weibo = (LoginButton) activity.findViewById(R.id.weibo);
         collection = (ImageView) activity.findViewById(R.id.collection);
         Qzone = (ImageView) activity.findViewById(R.id.image_QQkongjian);
         delete = (ImageView) activity.findViewById(R.id.delete);
         deleteTV = (TextView) activity.findViewById(R.id.delete_tv);
         jubao = (ImageView) activity.findViewById(R.id.jubao);
         jubaoTV = (TextView) activity.findViewById(R.id.jubao_tv);
         }
    }
    private void qqInit() {
        if(ctx != null){
            mQQAuth = QQAuth.createInstance(StartActivity.APP_ID, ctx);
            mTencent = Tencent.createInstance(StartActivity.APP_ID, ctx);
            mQQShare = new QQShare(ctx, mQQAuth.getQQToken());
            LogUtil.i("mQQAuth", "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
            LogUtil.i("mQQAuth", "isSessionValid=>" + mQQAuth.isSessionValid());
        }




//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }
    }
    private void onClickShare(ViewHolder holder, String url) {
        final Bundle params = new Bundle();
        String content = "";
        for (int i = 0; i < holder.cute.getLabelListLength(); i++){
            content += "#"+holder.cute.getLabel(i).getName()+"# ";
        }
//        Uri uri = Uri.parse("android.resource://com.harmazing.cute/res/");
//        String path = getAbsoluteImagePath(uri);
//        Log.v("path",path);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, ctx.getString(R.string.title_share_to_friends));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, ctx.getString(R.string.summary_share_to_friends));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, API.SHARE_PIC_TO+holder.cuteId+"/");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "返回CUTE");

//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mQQShare.shareToQQ(activity, params, new IUiListener(){
                    @Override
                    public void onComplete(Object o) {
                        Log.v("o", o.toString());
                        UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.TENCENT_QQ, AppSharedPref.newInstance(ctx).getUserId());
                        MobclickAgent.onSocialEvent(ctx, platform);
                        Toast.makeText(ctx, "分享成功！", Toast.LENGTH_LONG).show();
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
   private void onFavoriteCute (ViewHolder holder){
       final RequestParams params = new RequestParams();
         params.put("cute",holder.cuteId);
         params.put("user",AppSharedPref.newInstance(ctx).getUserId());
       HttpUtil.addClientHeader(ctx);
       HttpUtil.post(API.FAVOURITE_CUTES, params, new JsonHttpResponseHandler(){
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               super.onSuccess(statusCode, headers, response);
               Log.v("post favorite", response.toString());
               Log.v("post statusCode", ""+statusCode);
               Toast.makeText(ctx,"收藏成功！",Toast.LENGTH_LONG).show();
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
               super.onFailure(statusCode, headers, responseString, throwable);
               Log.v("post favorite,fail",responseString);
               Log.v("fail statusCode", ""+statusCode);
               Toast.makeText(ctx,"已收藏！",Toast.LENGTH_LONG).show();
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               super.onFailure(statusCode, headers, throwable, errorResponse);
               Log.v("fail statusCode", ""+statusCode);
               Toast.makeText(ctx,"已收藏！",Toast.LENGTH_LONG).show();
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
               super.onFailure(statusCode, headers, throwable, errorResponse);
               Log.v("fail statusCode", ""+statusCode); Toast.makeText(ctx,"已收藏！",Toast.LENGTH_LONG).show();
           }
       });

   }
    private void onShare2Qzone(ViewHolder holder, String url){
        final Bundle params = new Bundle();
        String content = "";
        for (int i = 0; i < holder.cute.getLabelListLength(); i++){
            content += "#"+holder.cute.getLabel(i).getName()+"# ";
        }
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(url);
        String title = ctx.getString(R.string.title_share_to_circle);
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,ctx.getString(R.string.summary_share_to_circle));
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, API.SHARE_PIC_TO+holder.cuteId+"/");
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,imageUrls);
        doShareToQzone(params);
    }
    private void doShareToQzone(final Bundle params) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mTencent.shareToQzone(activity, params, new IUiListener() {

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
                        UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.TENCENT_QZONE, AppSharedPref.newInstance(ctx).getUserId());
                        MobclickAgent.onSocialEvent(ctx, platform);
                        Toast.makeText(ctx,"分享成功！",Toast.LENGTH_LONG).show();
                    }

                });
            }
        }).start();
    }
    private void onWeiboShare(final ViewHolder holder,final String url){
    RequestParams params = new RequestParams();
        if (AppSharedPref.newInstance(ctx).getWeiboToken().equals("")){
            Toast.makeText(ctx,"微博没有绑定，请先微博登录或绑定微博！",Toast.LENGTH_LONG).show();
            weibo.loginWeiBo();}
        else {
            Log.v("AppSharedPref.newInstance(ctx).getWeiboToken()",AppSharedPref.newInstance(ctx).getWeiboToken());
            params.put("access_token", AppSharedPref.newInstance(ctx).getWeiboToken());
            HttpUtil.post(API.ACCESS_TOKEN_IS_VALID, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.v("access_token_valid", response.toString());
                    try {
                        int time = response.getInt("expire_in");
                        if (time >= 0){
                            Log.v("timeSHow",time+"");
                            onClickShareWeibo(holder, url);}
                        else{
                            Toast.makeText(ctx, "微博认证过期，请用微博重新登录或重新绑定微博！", Toast.LENGTH_LONG).show();
                        weibo.loginWeiBo();
                        }
                        Log.v("time", time + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
//              onClickShareWeibo(holder, url);
    }
    private void sinaInit() {
        if (ctx != null){
        // 创建授权认证信息
        WeiboAuth.AuthInfo authInfo = new WeiboAuth.AuthInfo(ctx, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        if (weibo != null)
        weibo.setWeiboAuthInfo(authInfo, mLoginListener);
        }
    }
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
        final   Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            /*
                    授权成功
             */
            if (accessToken != null && accessToken.isSessionValid()) {
//                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
//                        new java.util.Date(accessToken.getExpiresTime()));
//                String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
//                mTokenView.setText(String.format(format, accessToken.getToken(), date));
                Toast.makeText(ctx,"微博登录成功！",Toast.LENGTH_LONG).show();
                AccessTokenKeeper.writeAccessToken(ctx, accessToken);

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
                    Log.v("weibo_id+weibo_token",accessToken.getUid()+"++"+accessToken.getToken());
//                    AppSharedPref.newInstance(ctx).setWeiboUserId(accessToken.getUid());
//                    AppSharedPref.newInstance(ctx).setWeiboToken(accessToken.getToken());
//                        params.put("qq_sig","yCdpibyLyps+wrZMat/4vqkC3cc=");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpUtil.addClientHeader(ctx);
                HttpUtil.post(API.BIND_WEIBO, params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        LogUtil.v("","和美服务器发送请求成功");
                        try {

                            Toast.makeText(ctx,"绑定成功!!!!!!!",Toast.LENGTH_LONG).show();
                            AppSharedPref.newInstance(ctx).setWeiboToken(accessToken.getToken());
                            AppSharedPref.newInstance(ctx).setWeiboUserId(accessToken.getUid());
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
                        Toast.makeText(ctx,"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(ctx,"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(ctx,"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Log.v("@@@@@@","!!!!!!!");
                    }
                });
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(ctx,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_SHORT).show();
        }
    }
    private void onClickShareWeibo(final ViewHolder holder,String url){
        final RequestParams params = new RequestParams();
        String status = null;
        final String content = ctx.getString(R.string.title_share_to_circle)+ctx.getString(R.string.summary_share_to_circle);
//        for (int i = 0; i < holder.cute.getLabelListLength(); i++){
//            content += "#"+holder.cute.getLabel(i).getName()+"# ";
//        }
        try {
            status = URLEncoder.encode(content+"链接地址：" + API.SHARE_PIC_TO+holder.cuteId+"/");
        }catch (Exception e){
            e.printStackTrace();
        }
        params.put("access_token",AppSharedPref.newInstance(ctx).getWeiboToken());
        Log.v("access_token",AppSharedPref.newInstance(ctx).getWeiboToken());
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
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                Bitmap bmp =BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
//                ByteArrayInputStream byteImage = new ByteArrayInputStream(baos.toByteArray());
//                Log.v("bytes!",""+baos.toByteArray().length);
                params.put("pic",new ByteArrayInputStream(bytes),"share.JPEG");
                HttpUtil.post(API.SEND_MESSAGE_TO_WEIBO_FRIENDS, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.v("response", response.toString());
                        UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.SINA_WEIBO,AppSharedPref.newInstance(ctx).getUserId());
                        platform.setName(holder.pulisherTV.getText().toString());
                        MobclickAgent.onSocialEvent(ctx, platform);
                        Toast.makeText(ctx,"分享成功！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.v("errer",responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(ctx,"由于网络原因，分享失败！",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(ctx,"网络不稳定，分享失败！",Toast.LENGTH_LONG).show();
            }
        });
//            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());


//        params.put("access_token",AppSharedPref.newInstance(ctx).getWeiboToken());
//        Log.v("access_token",AppSharedPref.newInstance(ctx).getWeiboToken());
//        params.put("status",status);
//        params.put("pic",new ByteArrayInputStream(),"title.png");

    }



    private void wechatShare(final String type,ViewHolder holder,String urlImage){
        String content = "";
        for (int i = 0; i < holder.cute.getLabelListLength(); i++){
            content += "#"+holder.cute.getLabel(i).getName()+"# ";
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl =  API.SHARE_PIC_TO+holder.cuteId+"/";
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        if (type.equals("FriendsCircle")){
            msg.title = ctx.getResources().getString(R.string.title_share_to_circle);
            msg.description = ctx.getResources().getString(R.string.summary_share_to_circle);
        }
        else{
            msg.title = ctx.getString(R.string.title_share_to_friends);
            msg.description = ctx.getString(R.string.summary_share_to_friends);
        }
        Log.v("bitmap1", "112312312312332");
        HttpUtil.removePatchHeader();
        HttpUtil.get(urlImage,  new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,opts);
                msg.thumbData = Util.bmpToByteArray(bitmap,true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction(type);
                WXEntryActivity.youMengShareTag = req.transaction;
                req.message = msg;
                req.scene = type.equals("FriendsCircle") ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                api.sendReq(req);
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.v("网速很慢，请您稍后重试",throwable.toString());
                          Toast.makeText(ctx,"网速很慢，请您稍后重试。",Toast.LENGTH_LONG).show();
            }
        });
//        HttpUtil.addClientHeader(ctx);
//
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}