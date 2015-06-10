package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.base.BaseMyAdapter;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.model.RightUserlabels;
import com.harmazing.aixiumama.activity.LabelActivity;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by gyw on 2014/12/21.
 */
public class UserRightAdapter extends BaseMyAdapter<RightUserlabels.RightUserlabelsResults> {

    public UserRightAdapter(Context context, List<RightUserlabels.RightUserlabelsResults> list) {
        super(context, list);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.share_label_item2, null);

            holder.labelNumTV = (TextView)view.findViewById(R.id.label_num);
            holder.labelNameTV = (TextView)view.findViewById(R.id.label_name);
            holder.labelIcon1 = (ImageView)view.findViewById(R.id.share_label1);
            holder.labelIcon2 = (ImageView)view.findViewById(R.id.share_label2);
            holder.labelIcon3 = (ImageView)view.findViewById(R.id.share_label3);
            holder.item = (LinearLayout) view.findViewById(R.id.label_item);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(list.size() > 0) {
            holder.labelNameTV.setText(list.get(i).label_detail.name);
            holder.labelID = list.get(i).label;
        }
        //点击item
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LabelActivity.class);
                intent.putExtra("labelID",holder.labelID);
                context.startActivity(intent);
            }
        });

        HttpUtil.get(API.CUTE_LABELS + "?label=" + list.get(i).label, new JsonHttpResponseHandler() {
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
                    for (int i = 0; i < array.length(); i++) {
                        if (i == 0) {
                            obj = (JSONObject) array.get(i);
                            obj = new JSONObject(obj.getString("cute_detail"));
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), holder.labelIcon1);
                            final int id1 = obj.getInt("id");
                            holder.labelIcon1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, PhotoDetailActivity.class);
                                    intent.putExtra("id", id1);
                                    context.startActivity(intent);
                                }
                            });
                        } else if (i == 1) {
                            obj = (JSONObject) array.get(i);
                            obj = new JSONObject(obj.getString("cute_detail"));
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), holder.labelIcon2);
                            final int id2 = obj.getInt("id");
                            holder.labelIcon2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, PhotoDetailActivity.class);
                                    intent.putExtra("id", id2);
                                    context.startActivity(intent);
                                }
                            });
                        } else if (i == 2) {
                            obj = (JSONObject) array.get(i);
                            obj = new JSONObject(obj.getString("cute_detail"));
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), holder.labelIcon3);
                            final int id3 = obj.getInt("id");
                            holder.labelIcon3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, PhotoDetailActivity.class);
                                    intent.putExtra("id", id3);
                                    context.startActivity(intent);
                                }
                            });
                        } else
                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }
        });


        return view;
    }
    class ViewHolder{
        TextView labelNameTV,labelNumTV;
        ImageView labelIcon1,labelIcon2,labelIcon3;
        String labelID;
        LinearLayout item;
    }
}
