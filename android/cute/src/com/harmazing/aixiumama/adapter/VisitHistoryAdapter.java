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
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/12/9.
 */
public class VisitHistoryAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;

    public VisitHistoryAdapter(JSONArray kindArray
            , Context ctx) {
        this.kindArray = kindArray;
        this.ctx = ctx;
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
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final ViewHolder holder ;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.follow_user_list_item, parent,false);
            holder.babyInfo = (TextView)convertView.findViewById(R.id.info);
            holder.userName = (TextView)convertView.findViewById(R.id.visit_layout);
            holder.tv_date = (TextView)convertView.findViewById(R.id.tv_date);

            holder.userIcon = (ImageView)convertView.findViewById(R.id.user_icon);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //  clear
        holder.tv_date.setText("");
        holder.tv_date.setVisibility(View.GONE);

        try
        {
            holder.date = ((JSONObject) getItem(position)).getString("create_date").substring(0,10);
            JSONObject visitor = new JSONObject(((JSONObject) getItem(position)).getString("visitor"));
            holder.userID = visitor.getInt("auth_user");
            CuteApplication.downloadIamge(API.STICKERS + visitor.getString("image"), holder.userIcon);
            holder.userName.setText(visitor.getString("name"));
            JSONArray babyArray = new JSONArray(visitor.getString("babies"));
            JSONObject babyObj = (JSONObject) babyArray.get(0);
            holder.babyInfo.setText(CuteApplication.calculateBabyInfo(babyObj));
            holder.userIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx,PersonActivity.class);
                    intent.putExtra("person_id",holder.userID);
                    ctx.startActivity(intent);
                }
            });
            if(position > 0) {
                if (!holder.date.equals(((JSONObject) getItem(position - 1)).getString("create_date").substring(0,10))) {
                    holder.tv_date.setText(holder.date);
                    holder.tv_date.setVisibility(View.VISIBLE);
                }
            }else if(position == 0)
//                holder.tv_date.setText("今日来访：");
                holder.tv_date.setText(((JSONObject) getItem(position)).getString("create_date").substring(0,10));
                holder.tv_date.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        String date;
        int userID;
        TextView babyInfo,userName,tv_date;
        ImageView userIcon;
    }
}
