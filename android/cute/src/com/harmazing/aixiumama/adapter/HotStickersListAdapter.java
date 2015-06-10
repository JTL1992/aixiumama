package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.StickersActivity;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/25.
 */
public class HotStickersListAdapter  extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;
    String relyTo = "",reltContent,relyUser;

    public HotStickersListAdapter(JSONArray kindArray
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

            convertView = LayoutInflater.from(ctx).inflate(R.layout.hot_stickers_list_tiem, parent,false);
            holder.iconTV = (ImageView)convertView.findViewById(R.id.sticker_icon);
            holder.nameTV = (TextView)convertView.findViewById(R.id.sticker_name);
            holder.textTV = (TextView)convertView.findViewById(R.id.sticker_text);
            holder.layout = (RelativeLayout)convertView.findViewById(R.id.sticker_layout);
            holder.image1 = (ImageView)convertView.findViewById(R.id.image1);
            holder.image2 = (ImageView)convertView.findViewById(R.id.image2);
            holder.image3 = (ImageView)convertView.findViewById(R.id.image3);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        try
        {
            holder.nameTV.setText(((JSONObject) getItem(position)).getString("name"));
            holder.textTV.setText(((JSONObject) getItem(position)).getString("description"));
            CuteApplication.downloadIamge(API.STICKERS + ((JSONObject) getItem(position)).getString("icon"), holder.iconTV);
            holder.stickerID = ((JSONObject) getItem(position)).getString("id");
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx,StickersActivity.class);
                    intent.putExtra("stickerID",Integer.parseInt(holder.stickerID));
                    ctx.startActivity(intent);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /**
         *      三张图片
         */
        HttpUtil.get(API.GET_CUTES + "?sticker=" + holder.stickerID,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    for(int i=0;i<3;i++){
                        JSONObject obj = (JSONObject)array.get(i);
                        final int id = obj.getInt("id");

                        switch (i){
                            case 0:
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), holder.image1);
                                holder.image1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ctx,PhotoDetailActivity.class);
                                        intent.putExtra("id",id);
                                        ctx.startActivity(intent);
                                    }
                                });
                                break;
                            case 1:
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), holder.image2);
                                holder.image2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ctx,PhotoDetailActivity.class);
                                        intent.putExtra("id",id);
                                        ctx.startActivity(intent);
                                    }
                                });
                                break;
                            case 2:
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), holder.image3);
                                holder.image3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ctx,PhotoDetailActivity.class);
                                        intent.putExtra("id",id);
                                        ctx.startActivity(intent);
                                    }
                                });
                                break;

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                super.onSuccess(statusCode, headers, response);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView iconTV,image1,image2,image3;
        TextView nameTV,textTV;
        RelativeLayout layout;
        String stickerID;
    }
}
