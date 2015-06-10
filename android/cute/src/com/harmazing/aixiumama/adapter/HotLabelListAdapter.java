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
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/22.
 */
public class HotLabelListAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;

    public HotLabelListAdapter(JSONArray kindArray
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

            try {

                holder.labelID= ((JSONObject) getItem(position)).getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            convertView = LayoutInflater.from(ctx).inflate(R.layout.hot_label_listview_item, parent,false);
            holder.iconIV = (ImageView)convertView.findViewById(R.id.icon);
            holder.imageIV1 = (ImageView)convertView.findViewById(R.id.image1);
            holder.imageIV2 = (ImageView)convertView.findViewById(R.id.image2);
            holder.imageIV3 = (ImageView)convertView.findViewById(R.id.image3);
            holder.imageIV4 = (ImageView)convertView.findViewById(R.id.image4);
            holder.item = (LinearLayout)convertView.findViewById(R.id.hot_label_item_layout);
            holder.nameTV = (TextView)convertView.findViewById(R.id.hot_label_name);
            holder.textTV = (TextView)convertView.findViewById(R.id.text);
            holder.numTV = (TextView)convertView.findViewById(R.id.pic_num);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            CuteApplication.downloadIamge(API.STICKERS + ((JSONObject) getItem(position)).getString("image"), holder.iconIV);
            holder.nameTV.setText(((JSONObject) getItem(position)).getString("name"));
            holder.textTV.setText(((JSONObject) getItem(position)).getString("description"));
            holder.numTV.setText(((JSONObject) getItem(position)).getInt("follow_count") + "粉丝");

            /**
             *  HotLabelListView 的item 事件
             */
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, LabelActivity.class);
                    try {
                        intent.putExtra("labelID", ((JSONObject) getItem(position)).getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ctx.startActivity(intent);
                }
            });
            /**
             *   请求该标签所有的cute图
             */
            HttpUtil.get(API.CUTE_LABELS + "?label=" + ((JSONObject) getItem(position)).getInt("id"),new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    int[]cuteID = new int[9];
                    String[]cuteImageID = new String[9];
                    try {
                        JSONArray array = new JSONArray(response.getString("results"));
                        //  只显示4张图
                        if (array.length() > 4)
                           for(int i=0;i<4;i++){
                               JSONObject obj = (JSONObject)array.get(i);
                               obj = new JSONObject(obj.getString("cute_detail"));
                               cuteID[i] = obj.getInt("id");
                               cuteImageID[i] = obj.getString("image_small");
                           }
                        else{
                            for(int i=0;i<array.length();i++){
                                JSONObject obj = (JSONObject)array.get(i);
                                obj = new JSONObject(obj.getString("cute_detail"));
                                cuteID[i] = obj.getInt("id");
                                cuteImageID[i] = obj.getString("image_small");
                            }
                        }
                        /**
                         *      设置4张个imageview 的最大尺寸
                         */
                        int maxWidth = (CuteApplication.screenWH[0] - BitmapUtil.dip2px(ctx,40)) / 4;
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(maxWidth,maxWidth);
                        params.setMargins(BitmapUtil.dip2px(ctx, 5), 0, BitmapUtil.dip2px(ctx, 5), 0);
                        holder.imageIV1.setLayoutParams(params);
                        holder.imageIV2.setLayoutParams(params);
                        holder.imageIV3.setLayoutParams(params);
                        holder.imageIV4.setLayoutParams(params);

                        CuteApplication.downloadIamge(API.STICKERS + cuteImageID[0],holder.imageIV1);
                        CuteApplication.downloadIamge(API.STICKERS + cuteImageID[1],holder.imageIV2);
                        CuteApplication.downloadIamge(API.STICKERS + cuteImageID[2],holder.imageIV3);
                        CuteApplication.downloadIamge(API.STICKERS + cuteImageID[3],holder.imageIV4);

                        holder.imageIV1.setOnClickListener(new CuteImageOnClickListener(cuteID[0]));
                        holder.imageIV2.setOnClickListener(new CuteImageOnClickListener(cuteID[1]));
                        holder.imageIV3.setOnClickListener(new CuteImageOnClickListener(cuteID[2]));
                        holder.imageIV4.setOnClickListener(new CuteImageOnClickListener(cuteID[3]));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    super.onSuccess(statusCode, headers, response);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class CuteImageOnClickListener implements View.OnClickListener{
        int cuteID;
        public CuteImageOnClickListener(int id){
            cuteID = id;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ctx, PhotoDetailActivity.class);
            intent.putExtra("id", cuteID);
            ctx.startActivity(intent);
        }
    }

    private class ViewHolder {
        ImageView iconIV,imageIV1,imageIV2,imageIV3,imageIV4;
        TextView nameTV,textTV,numTV;
        int labelID;
        LinearLayout item;
    }
}
