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
import com.harmazing.aixiumama.activity.LabelActivity;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.SearchResultsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/23.
 */
public class SearchResultsImageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    JSONArray kindArray;
    private int WHO;

    public SearchResultsImageAdapter(Context c,JSONArray array,int who) {
        mContext = c;
        kindArray  = array;
        WHO = who;
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

            if(WHO == SearchResultsActivity.USER) {
                temp.ID = kindArray.optJSONObject(position).getInt("auth_user");
            }else if(WHO == SearchResultsActivity.BRAND){
                temp.ID = kindArray.optJSONObject(position).getInt("id");
            }

            if(WHO == SearchResultsActivity.USER) {
                CuteApplication.downloadIamge(API.STICKERS + kindArray.optJSONObject(position).getString("image_small"), temp.iconIV);
                temp.nameTV.setText(((JSONObject) getItem(position)).getString("name"));
                JSONArray array = new JSONArray(((JSONObject) getItem(position)).getString("babies"));
                JSONObject obj = (JSONObject) array.get(0);
                temp.textTV.setText(CuteApplication.date2Age(obj.getString("birthday")));
            }else if(WHO == SearchResultsActivity.BRAND) {
                CuteApplication.downloadIamge(API.STICKERS + kindArray.optJSONObject(position).getString("image_small"), temp.iconIV);
                temp.nameTV.setText(((JSONObject) getItem(position)).getString("name"));
                temp.textTV.setText(((JSONObject) getItem(position)).getString("description"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            Toast.makeText(mContext, "1qeqweqwe", Toast.LENGTH_SHORT);
        }


        if(WHO == SearchResultsActivity.USER) {
            temp.iconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PersonActivity.class);
                    intent.putExtra("person_id", temp.ID);
                    mContext.startActivity(intent);
                }
            });
        }else if(WHO == SearchResultsActivity.BRAND){
            temp.iconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LabelActivity.class);
                    intent.putExtra("labelID", temp.ID+"");
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    private class GirdTemp {
        ImageView iconIV;
        TextView nameTV,textTV;
        int ID;
    }

}
