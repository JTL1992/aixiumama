package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.LabelActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/20.
 */
public class ShareLabelListAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;



    public ShareLabelListAdapter(JSONArray kindArray
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
        try {
            return ((JSONObject) getItem(position)).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.share_label_item, parent, false);

            holder.labelNumTV = (TextView)convertView.findViewById(R.id.label_num);
            holder.labelNameTV = (TextView)convertView.findViewById(R.id.label_name);
            holder.labelIcon1 = (ImageView)convertView.findViewById(R.id.share_label1);
            holder.labelIcon2 = (ImageView)convertView.findViewById(R.id.share_label2);
            holder.labelIcon3 = (ImageView)convertView.findViewById(R.id.share_label3);
            holder.item = (LinearLayout) convertView.findViewById(R.id.label_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        try
        {
            JSONObject obj = new JSONObject(((JSONObject)getItem(position)).getString("label_detail"));
            holder.labelNameTV.setText(obj.getString("name"));
            holder.labelID = ((JSONObject)getItem(position)).getString("label");

            //点击item
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, LabelActivity.class);
                    intent.putExtra("labelID",holder.labelID);
                    ctx.startActivity(intent);
                }
            });

            HttpUtil.get(API.CUTE_LABELS + "?label=" +((JSONObject)getItem(position)).getString("label"),new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        JSONArray array = new JSONArray(response.getString("results"));
                        //  为图片数量TextView赋值
                        holder.labelNumTV.setText(response.getString("count") + "张");

                        /**
                         * 只加载前三个图片
                         */
                        JSONObject obj;
                        for(int i=0;i<array.length();i++){
                            if(i == 0){
                                obj = (JSONObject)array.get(i);
                                obj = new JSONObject(obj.getString("cute_detail"));
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), holder.labelIcon1);
                                final int id1 = obj.getInt("id");
                                holder.labelIcon1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                                        intent.putExtra("id",id1);
                                        ctx.startActivity(intent);
                                    }
                                });
                            }else if(i == 1){
                                obj = (JSONObject)array.get(i);
                                obj = new JSONObject(obj.getString("cute_detail"));
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"),holder.labelIcon2);
                                final int id2 = obj.getInt("id");
                                holder.labelIcon2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                                        intent.putExtra("id",id2);
                                        ctx.startActivity(intent);
                                    }
                                });
                            }else if(i == 2){
                                obj = (JSONObject)array.get(i);
                                obj = new JSONObject(obj.getString("cute_detail"));
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"),holder.labelIcon3);
                                final int id3 = obj.getInt("id");
                                holder.labelIcon3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                                        intent.putExtra("id",id3);
                                        ctx.startActivity(intent);
                                    }
                                });
                            }else
                                break;
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    super.onSuccess(statusCode, headers, response);
                }
            });

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        return convertView;
    }


    class ViewHolder{
        TextView labelNameTV,labelNumTV;
        ImageView labelIcon1,labelIcon2,labelIcon3;
        String labelID;
        LinearLayout item;
    }
}

