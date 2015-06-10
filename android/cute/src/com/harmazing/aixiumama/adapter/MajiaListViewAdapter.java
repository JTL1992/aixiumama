package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.AppSharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/12/19.
 */
public class MajiaListViewAdapter extends BaseAdapter{
    private JSONArray kindArray;
    private Context ctx;


    public MajiaListViewAdapter(JSONArray kindArray
            , Context ctx ) {
        this.kindArray = kindArray;
        this.ctx = ctx;

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

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.follow_user_list_item, parent, false);
            holder.userIconIV = (ImageView) convertView.findViewById(R.id.user_icon);
            holder.stateIV = (ImageView) convertView.findViewById(R.id.follow_state);
            holder.userNmaeTV = (TextView) convertView.findViewById(R.id.visit_layout);
            holder.infoTV = (TextView) convertView.findViewById(R.id.info);
            holder.rl_user = (RelativeLayout)convertView.findViewById(R.id.rl_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            holder.userID = ((JSONObject) getItem(position)).getString("auth_user");
            holder.token = ((JSONObject) getItem(position)).getString("token");
            holder.userName = ((JSONObject) getItem(position)).getString("name");
            holder.weiboToken = ((JSONObject) getItem(position)).getString("weibo_token");
            holder.QQToken = ((JSONObject) getItem(position)).getString("qq_openkey");
            holder.QQId = ((JSONObject) getItem(position)).getString("qq_openid");
            CuteApplication.downloadIamge(API.STICKERS + ((JSONObject) getItem(position)).getString("image"), holder.userIconIV);
            holder.userNmaeTV.setText(holder.userName);
            holder.birthday =  ((JSONObject) getItem(position)).getJSONArray("babies").getJSONObject(0).getString("birthday");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.rl_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSharedPref.newInstance(ctx).setUserId(holder.userID);
                AppSharedPref.newInstance(ctx).setToken(holder.token);
                AppSharedPref.newInstance(ctx).setUserName(holder.userName);
                AppSharedPref.newInstance(ctx).setWeiboToken(holder.weiboToken);
                AppSharedPref.newInstance(ctx).setQQToken(holder.QQToken);
                AppSharedPref.newInstance(ctx).setQQUserId(holder.QQId);
                AppSharedPref.newInstance(ctx).setBirthday(holder.birthday);
                CuteApplication.activityList.get( CuteApplication.activityList.size() -2).finish();
                CuteApplication.activityList.get( CuteApplication.activityList.size() -1).finish();
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView userIconIV,stateIV;
        TextView userNmaeTV,infoTV;
        String userID,token,userName,weiboToken,QQToken,QQId,birthday;
        RelativeLayout rl_user;

    }
}
