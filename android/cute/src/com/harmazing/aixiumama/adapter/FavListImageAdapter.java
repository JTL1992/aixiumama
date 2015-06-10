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
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.application.CuteApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/26.
 */
public class FavListImageAdapter extends BaseAdapter{

        private Context mContext;
        private LayoutInflater inflater;
        JSONArray kindArray;

        public FavListImageAdapter(Context c,JSONArray array) {
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

    public JSONArray getArray(){
        return kindArray;
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
            convertView = inflater.inflate(R.layout.fav_listview_item_gridview_item, parent,false);
            temp = new GirdTemp();
            temp.cuteImage = (ImageView) convertView.findViewById(R.id.cute_image);
            temp.mumIcon = (ImageView)convertView.findViewById(R.id.mum_icon);
            temp.mumName = (TextView)convertView.findViewById(R.id.mum_name);
            temp.cuteText = (TextView)convertView.findViewById(R.id.cute_text);
            temp.dateText = (TextView)convertView.findViewById(R.id.date_text);
            convertView.setTag(temp);
        } else {
            temp = (GirdTemp) convertView.getTag();
        }

          JSONObject cute_detailObj,publish_userObj ;
        try {
            temp.date = ((JSONObject) getItem(position)).getString("create_date").substring(0,10);
            if(position - 1 >= 0 &&temp.date.equals(((JSONObject) getItem(position - 1)).getString("create_date").substring(0,10)))
            {
                temp.dateText.setVisibility(View.INVISIBLE);
            }else{
                temp.dateText.setVisibility(View.VISIBLE);
                temp.dateText.setText(temp.date + "");
            }
            temp.cuteID = ((JSONObject) getItem(position)).getInt("cute");
            cute_detailObj = new JSONObject(((JSONObject) getItem(position)).getString("cute_detail"));
            // cute 大图
            CuteApplication.downloadIamge(API.STICKERS + cute_detailObj.getString("image_small"), temp.cuteImage);
            //  cute 描述
            temp.cuteText.setText(cute_detailObj.getString("text"));
            //********************************
            publish_userObj = new JSONObject(cute_detailObj.getString("publish_user"));
            //  发布者头像
            CuteApplication.downloadIamge(API.STICKERS + publish_userObj.getString("image"), temp.mumIcon);
            //发布者name
            temp.mumName.setText(publish_userObj.getString("name"));

            temp.userID = publish_userObj.getInt("auth_user");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        temp.mumIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PersonActivity.class);
                intent.putExtra("person_id",  temp.userID);
                mContext.startActivity(intent);
            }
        });

        temp.cuteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PhotoDetailActivity.class);
                intent.putExtra("id",  temp.cuteID);
                intent.putExtra("showAllComments",true);    //不弹键盘
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }


        private class GirdTemp {
            ImageView cuteImage,mumIcon;
            int cuteID,userID;
            TextView mumName,cuteText,dateText;
            String date;
        }

    }