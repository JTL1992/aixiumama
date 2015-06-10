package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.utils.LogUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;

/**
 * Created by Lyn on 2014/11/6.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    JSONArray kindArray;

    public ImageAdapter(Context c,JSONArray array) {
        mContext = c;
        kindArray  = array;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return kindArray.length();
    }

    @Override
    public Object getItem(int position) {
        return  kindArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {

        return -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GirdTemp temp;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_item, parent,false);
            temp = new GirdTemp();
            temp.phone_function_pic = (ImageView) convertView.findViewById(R.id.gridview_item_view);

            try {
                temp.cuteID = ((JSONObject) getItem(position)).getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            convertView.setTag(temp);
        } else {
            temp = (GirdTemp) convertView.getTag();
        }

        try {
            CuteApplication.imageLoader.loadImage(API.STICKERS + ((JSONObject) getItem(position)).getString("image"),new ImageLoadingListener() {
                    @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    SoftReference<Bitmap> bitmapSoftReference = new SoftReference<Bitmap>(bitmap);
                    if(bitmap != null)
                        temp.phone_function_pic.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
       } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.v("adapter",""+  temp.phone_function_pic.getHeight());

        temp.phone_function_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PhotoDetailActivity.class);
                intent.putExtra("id",  temp.cuteID);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }




    private class GirdTemp {
        ImageView phone_function_pic;
        int cuteID;
    }

}
