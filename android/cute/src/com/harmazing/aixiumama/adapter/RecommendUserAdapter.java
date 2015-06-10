package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/12/4.
 */
public class RecommendUserAdapter  extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;

    public RecommendUserAdapter(JSONArray kindArray
            , Context ctx) {
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
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final ViewHolder holder ;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(ctx).inflate(R.layout.recommenduser_item, parent,false);
            holder.iconIV = (ImageView)convertView.findViewById(R.id.icon);
            holder.nameTV = (TextView)convertView.findViewById(R.id.name);
            holder.textTV = (TextView)convertView.findViewById(R.id.babyinfo);

            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            CuteApplication.downloadIamge(API.STICKERS + ((JSONObject) getItem(position)).getString("image"), holder.iconIV);
            holder.nameTV.setText(((JSONObject) getItem(position)).getString("name"));
            holder.userID = ((JSONObject) getItem(position)).getInt("auth_user");
            JSONArray array = new JSONArray(((JSONObject) getItem(position)).getString("babies"));
            JSONObject obj = (JSONObject)array.get(0);
            LogUtil.v("宝宝生日：",obj.toString());
            holder.textTV.setText(CuteApplication.calculateBabyInfo(obj));
            LogUtil.v("宝宝生日转换",CuteApplication.calculateBabyInfo(obj));

            /**
             *   item 事件
             */
            holder.iconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, PersonActivity.class);
                    intent.putExtra("person_id", holder.userID);
                    ctx.startActivity(intent);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }


    private class ViewHolder {
        ImageView iconIV;
        TextView nameTV,textTV;
        int userID;
    }
}
