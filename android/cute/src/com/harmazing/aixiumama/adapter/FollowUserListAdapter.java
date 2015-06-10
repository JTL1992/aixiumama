package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.UserFansActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.AttentionActivity;
import com.harmazing.aixiumama.activity.LabelFansActivity;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/21.
 */
public class FollowUserListAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;
    private static int FOLLOW_STATE;
    private static int ATT_FANS;

    public FollowUserListAdapter(JSONArray kindArray
            , Context ctx,int kind) {
        this.kindArray = kindArray;
        this.ctx = ctx;
        ATT_FANS = kind;
    }

    public void addKingdArray(JSONArray array){
        int len = kindArray.length();
        for(int i=0;i<array.length();i++)
            try {
                kindArray.put( len + i,array.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {

        final ViewHolder holder ;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.follow_user_list_item, parent,false);
            holder.userIconIV = (ImageView)convertView.findViewById(R.id.user_icon);
            holder.stateIV = (ImageView)convertView.findViewById(R.id.follow_state);
            holder.userNmaeTV = (TextView)convertView.findViewById(R.id.visit_layout);
            holder.infoTV = (TextView)convertView.findViewById(R.id.info);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            JSONObject obj;

            if(ATT_FANS == AttentionActivity.USER) {
                holder.userID = ((JSONObject) getItem(position)).getInt("follow");
            }else if(ATT_FANS == UserFansActivity.FANS){
                holder.userID = ((JSONObject) getItem(position)).getInt("user");
            }else if(ATT_FANS == LabelFansActivity.LABEL){
                holder.userID = ((JSONObject) getItem(position)).getInt("user");
            }

            if (ATT_FANS == AttentionActivity.USER)
                obj = new JSONObject(((JSONObject) getItem(position)).getString("follow_detail"));
            else {
                obj = new JSONObject(((JSONObject) getItem(position)).getString("user_detail"));
            }
            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), holder.userIconIV);

            holder.userNmaeTV.setText(obj.getString("name"));
            /**
             * baby信息
             */
            JSONArray array = new JSONArray(obj.getString("babies"));
            JSONObject babyObj = (JSONObject) array.get(0);
            if (babyObj.getInt("gender") == 1) {
                holder.infoTV.setText(
                        CuteApplication.date2Age(babyObj.getString("birthday")) + "  " + "男");
            } else if (babyObj.getInt("gender") == 2) {
                holder.infoTV.setText(CuteApplication.date2Age(babyObj.getString("birthday")) + "  " + "女");
            } else {
                holder.infoTV.setText(CuteApplication.date2Age(babyObj.getString("birthday")));
            }

            /*
                    判断 当前用户！与这人关注的人的 相互关注关系
            */
            LogUtil.v("me",AppSharedPref.newInstance(ctx).getUserId());
            LogUtil.v("you",holder.userID);
            if (!AppSharedPref.newInstance(ctx).getUserId().equals(holder.userID+"")) {
                HttpUtil.get(API.ADD_FRIENDS + "?user=" + AppSharedPref.newInstance(ctx).getUserId() + "&follow=" + holder.userID, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getInt("count") == 1) {
                                JSONArray array = new JSONArray(response.getString("results"));
                                JSONObject obj = (JSONObject) array.get(0);
                                if (obj.getBoolean("repeat")) {
                                    holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.qh_pic));
                                    FOLLOW_STATE = 3;
                                } else {
                                    holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.reply_pic));
                                    FOLLOW_STATE = 2;
                                }
                            } else {
                                holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.jh_pic));
                                FOLLOW_STATE = 1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        holder.stateIV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                switch (FOLLOW_STATE) {
                                    case 1:
                                        if(ATT_FANS == UserFansActivity.FANS)
                                            holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.qh_pic));
                                            else
                                            holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.reply_pic));
                                        FOLLOW_STATE = 2;
                                        ToastUtil.show(ctx, "关注成功");
                                        break;
                                    case 2:
                                        holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.jh_pic));
                                        FOLLOW_STATE = 1;
                                        ToastUtil.show(ctx, "取消关注");
                                        break;
                                    case 3:
                                        holder.stateIV.setImageDrawable(ctx.getResources().getDrawable(R.drawable.jh_pic));
                                        FOLLOW_STATE = 1;
                                        ToastUtil.show(ctx, "取消关注");
                                        break;
                                }

                                RequestParams params = new RequestParams();
                                params.put("user", Integer.parseInt(AppSharedPref.newInstance(ctx).getUserId()));
                                params.put("follow", holder.userID);
                                LogUtil.v("user:,follow:",Integer.parseInt(AppSharedPref.newInstance(ctx).getUserId())+"@"+holder.userID);
                                HttpUtil.post(API.ADD_FRIENDS, params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        if (statusCode == 201) {
                                            LogUtil.v("关注成功", "");

                                        } else if (statusCode == 204) {
                                            LogUtil.v("取消关注", "");
                                        }

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
                        super.onSuccess(statusCode, headers, response);
                    }
                });
            }else{
                //  查看的该用户与当前用户是同一个人
                holder.stateIV.setVisibility(View.GONE);
            }
            }catch(JSONException e){
                e.printStackTrace();
            }

        /**
         * 点击进入该用户详情
         */

        holder.userIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, PersonActivity.class);
                intent.putExtra("person_id",holder.userID);
                ctx.startActivity(intent);
            }
        });


        return convertView;
    }

    private class ViewHolder {
        ImageView userIconIV,stateIV;
        TextView userNmaeTV,infoTV;
        int userID;
    }
}
