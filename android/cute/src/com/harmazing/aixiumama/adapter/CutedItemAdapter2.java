package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.model.CutedItem2;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/31.
 */
public class CutedItemAdapter2 extends BaseAdapter {
    Context context;
    ArrayList<CutedItem2> cuteds;
    int Num ;
    public CutedItemAdapter2(Context context,ArrayList<CutedItem2> cutedItem){
        this.context = context;
        this.cuteds = cutedItem;
        for (int i = 0; i < cuteds.size(); i++){
            Log.v("传进的cutes", cuteds.get(i).getUserName());
        }
    }
    @Override
    public int getCount() {
        return cuteds.size();
    }

    @Override
    public Object getItem(int i) {
        return cuteds.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.cuted_item_layout, null);
            viewHolder.name = (TextView) view.findViewById(R.id.visit_layout);
            viewHolder.day = (TextView)view.findViewById(R.id.day);
            viewHolder.description = (TextView)view.findViewById(R.id.textContent);
            viewHolder.image = (ImageView)view.findViewById(R.id.image_cute);
            viewHolder.icon = (ImageView) view.findViewById(R.id.image_person);
            viewHolder.layout = (RelativeLayout) view.findViewById(R.id.layout);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.name.setText(cuteds.get(i).getUserName());
        if (i == 0)
        viewHolder.day.setText(cuteds.get(i).getTitle());
        else
        viewHolder.day.setText("");

        viewHolder.description.setText(cuteds.get(i).getDescription());
        CuteApplication.downloadIamge(API.PIC_HEAD + cuteds.get(i).getIconUrl(), viewHolder.icon);
        CuteApplication.downloadIamge(API.PIC_HEAD+cuteds.get(i).getImageUrl(),viewHolder.image);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("showAllComments",true);    //不弹键盘
                intent.putExtra("id",cuteds.get(i).getCuteId());
                context.startActivity(intent);
            }
        });
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("id",cuteds.get(i).getCuteId());
                intent.putExtra("showAllComments",true);    //不弹键盘
                context.startActivity(intent);
            }
        });
        viewHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra("person_id",cuteds.get(i).getUserId());
                context.startActivity(intent);
            }
        });
        return view;
    }

    class ViewHolder{
        TextView name,day,description;
        ImageView image,icon;
        RelativeLayout layout;

    }
}
