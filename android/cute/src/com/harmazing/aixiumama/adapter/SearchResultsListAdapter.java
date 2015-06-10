package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.activity.LabelActivity;
import com.harmazing.aixiumama.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/23.
 */
public class SearchResultsListAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;
    String relyTo = "",reltContent,relyUser;

    public SearchResultsListAdapter(JSONArray kindArray
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

            convertView = LayoutInflater.from(ctx).inflate(R.layout.search_result_label_list_item, parent,false);
            holder.nameTV = (TextView)convertView.findViewById(R.id.name);
            holder.textTV = (TextView)convertView.findViewById(R.id.text);
            holder.layout = (RelativeLayout)convertView.findViewById(R.id.layout);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();

        }
        try
        {
            holder.labelID  = ((JSONObject) getItem(position)).getInt("id");
            holder.nameTV.setText(((JSONObject) getItem(position)).getString("name"));
            holder.textTV.setText("已有" + ((JSONObject) getItem(position)).getString("usage_count") + "张图片");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, LabelActivity.class);
                intent.putExtra("labelID", holder.labelID+"");
                ctx.startActivity(intent);
            }
        });


        return convertView;
    }

    private class ViewHolder {
        RelativeLayout layout;
        TextView nameTV,textTV;
        int labelID;
    }
}