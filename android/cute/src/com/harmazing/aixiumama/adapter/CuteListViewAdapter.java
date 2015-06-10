package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.TabHostActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.fragment.MessageFragment;
import com.harmazing.aixiumama.view.RoundedImageView;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;

import com.harmazing.aixiumama.utils.UserInfoUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/6.
 */
public class CuteListViewAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;


    public CuteListViewAdapter(JSONArray kindArray
            , Context ctx) {
        this.kindArray = kindArray;
        this.ctx = ctx;
    }
    public Boolean isRefresh(String date){
        try {
            Log.v("isrefresh", date);
            return (kindArray.getJSONObject(0).getString("create_date").equals(date));
        }catch (Exception e){
            Log.v("isrefresh","falure");
            return false;
        }

    }
    public void refresh(JSONArray jsonArray){
        this.kindArray = jsonArray;
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
            LayoutInflater flater = LayoutInflater.from(ctx);
            convertView = flater.inflate(R.layout.cute_listview_item, parent,false);
            holder.userIcon = (RoundedImageView)convertView.findViewById(R.id.user_icon);
            holder.cutedIcon = (ImageView)convertView.findViewById(R.id.cuted_icon);
            holder.userName = (TextView)convertView.findViewById(R.id.visit_layout);
            holder.dateTxt = (TextView)convertView.findViewById(R.id.date_text);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
            holder.redPoint = (ImageView) convertView.findViewById(R.id.redpoint);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject cute_detailObj = null;

        // 当前用户信息就从本地读取了
        holder.userName.setText(UserInfoUtils.getUserName(ctx));
        if (position < MessageFragment.newCuteNum){
         holder.redPoint.setVisibility(View.VISIBLE);
        }
        try {
            holder.dateTxt.setText(CuteApplication.calculateDays(((JSONObject) getItem(position)).getString("create_date")));

            JSONObject contentObj = new JSONObject(((JSONObject) getItem(position)).getString("content"));

            JSONObject userObj = new JSONObject(contentObj.getString("user_detail"));
            CuteApplication.downloadIamge(API.STICKERS + userObj.getString("image"),holder.userIcon);
            holder.userName.setText(userObj.getString("name"));
            holder.cutedID = contentObj.getString("cute");
            final String userId = contentObj.getString("user");
            cute_detailObj = new JSONObject(contentObj.getString("cute_detail"));
            CuteApplication.downloadIamge(API.STICKERS + cute_detailObj.getString("image"),holder.cutedIcon);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                    intent.putExtra("showAllComments",true);    //不弹键盘
                    holder.redPoint.setVisibility(View.INVISIBLE);
                    MessageFragment.newCuteNum -= 1;
                    TabHostActivity.cuteHaveRead++;
                    Intent broadcastIntent = new Intent("com.cute.broadcast");
                    broadcastIntent.putExtra("action",4);//cutehaveread
                    ctx.sendBroadcast(broadcastIntent);
//                    intent.putExtra("showAllComments",false);
                    intent.putExtra("id",Integer.parseInt(holder.cutedID ));
                    ctx.startActivity(intent);
                }
            });
            holder.userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, PersonActivity.class);
                    intent.putExtra("person_id",Integer.parseInt(userId));
                    ctx.startActivity(intent);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }





        return convertView;
    }


    private class ViewHolder {
        ImageView cutedIcon,redPoint;
        String cutedID,time;
        TextView userName,dateTxt;
        RelativeLayout layout;
        RoundedImageView userIcon;
    }
}
