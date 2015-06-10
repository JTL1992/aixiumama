package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.harmazing.aixiumama.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/6.
 */
public class HotLabelAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;
    String title, name, protrait, desc, time;


    public HotLabelAdapter(JSONArray kindArray
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
            return ((JSONObject) getItem(position)).getInt("birthday");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        LayoutInflater flater = LayoutInflater.from(ctx);
        View view = flater.inflate(R.layout.hot_label_listview_item, null);
//        LinearLayout linear = (LinearLayout)view.findViewById(R.id.linear);
//        TextView tv_name = (TextView)view.findViewById(R.id.name);
//        try
//        {
//            name = ((JSONObject)getItem(position)).getString("guardian");
//            tv_name.setText(name);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }


        return view;
    }
}
