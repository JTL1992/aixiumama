package com.harmazing.aixiumama.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.ActivityGallery;
import com.harmazing.aixiumama.activity.TabHostActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.utils.BitmapUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/25.
 */
public class StickersImageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    JSONArray kindArray;

    public StickersImageAdapter(Context c,JSONArray array) {
        mContext = c;
        kindArray  = array;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        return kindArray.length() + 1;
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

            convertView.setTag(temp);
        } else {
            temp = (GirdTemp) convertView.getTag();
        }

        try {
            JSONObject obj = new JSONObject();
            if(position == 0){
                temp.phone_function_pic.setImageDrawable(BitmapUtil.readBitMapToDrawable(mContext, R.drawable.dh_xj));
                temp.phone_function_pic.setPadding(10,10,10,10);
                temp.phone_function_pic.setBackgroundColor(mContext.getResources().getColor(R.color.light_grey));
                temp.phone_function_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(mContext,ActivityGallery.class);

                        Dialog alertDialog = new AlertDialog.Builder(mContext)

                                .setItems(new String[]{"相册","拍照"}, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            intent.putExtra("action", TabHostActivity.PHOTO);
                                        }else if(which == 1){
                                            intent.putExtra("action",TabHostActivity.CAMERA);
                                        }
                                        mContext.startActivity(intent);

                                    }
                                }).create();
                        alertDialog.show();
                    }
                });

            }else {
                temp.cuteID = ((JSONObject) getItem(position - 1)).getInt("id");
                CuteApplication.downloadIamge(API.STICKERS + ((JSONObject) getItem(position - 1)).getString("image"), temp.phone_function_pic);
                temp.phone_function_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, PhotoDetailActivity.class);
                        intent.putExtra("id",  temp.cuteID);
                        mContext.startActivity(intent);
                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }


    private class GirdTemp {
        ImageView phone_function_pic;
        int cuteID;
    }

}
