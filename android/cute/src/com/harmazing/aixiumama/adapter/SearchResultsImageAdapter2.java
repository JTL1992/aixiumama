package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/23.
 */
public class SearchResultsImageAdapter2 extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    JSONArray kindArray;

    public SearchResultsImageAdapter2(Context c, JSONArray array) {
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

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GirdTemp temp;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.horizontal_list_item, parent,false);
            temp = new GirdTemp();
            temp.iconIV = (ImageView) convertView.findViewById(R.id.user_icon);
            temp.nameTV = (TextView)convertView.findViewById(R.id.mum_name);
            temp.textTV  = (TextView)convertView.findViewById(R.id.baby_age);

            convertView.setTag(temp);
        } else {
            temp = (GirdTemp) convertView.getTag();
        }

        try {
            //Log.i("SearchResultsImageAdapter", API.STICKERS + kindArray.optJSONObject(position).getString("image_small"));

            temp.ID = kindArray.optJSONObject(position).getInt("auth_user");

            CuteApplication.downloadIamge(API.STICKERS + kindArray.optJSONObject(position).getString("image_small"), temp.iconIV);
            temp.nameTV.setText(((JSONObject) getItem(position)).getString("name"));
            JSONArray array = new JSONArray(((JSONObject) getItem(position)).getString("babies"));
            JSONObject obj = (JSONObject) array.get(0);
            temp.textTV.setText(CuteApplication.date2Age(obj.getString("birthday")));


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            Toast.makeText(mContext, "搜索出现错误", Toast.LENGTH_SHORT);
        }

        temp.iconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PersonActivity.class);
                intent.putExtra("person_id", temp.ID);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }


    private class GirdTemp {
        ImageView iconIV;
        TextView nameTV,textTV;
        int ID;
    }

}
