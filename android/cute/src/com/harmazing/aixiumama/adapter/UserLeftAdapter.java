package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.harmazing.aixiumama.base.BaseMyAdapter;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.model.LeftUser;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;


/**
 * Created by gyw on 2014/12/19.
 */
public class UserLeftAdapter extends BaseMyAdapter<LeftUser.LeftUserResults> {

    private BitmapUtils bitmapUtils;

    public UserLeftAdapter(Context context, List list) {
        super(context, list);
        bitmapUtils = new BitmapUtils(context);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.gridview_right_item, null);
            holder = new ViewHolder();
            holder.gridview_item_view = (ImageView) view.findViewById(R.id.gridview_right_item_view);
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }


        //holder.gridview_item_view.setImageResource(R.drawable.blank);
        //bitmapUtils.display(holder.gridview_item_view, API.STICKERS +list.get(i).image);

        CuteApplication.downloadIamge(API.STICKERS +list.get(i).image, holder.gridview_item_view);
        holder.cuteID = list.get(i).id;


        holder.gridview_item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("showAllComments",true);    //不弹键盘
                intent.putExtra("id", Integer.parseInt(holder.cuteID));
                context.startActivity(intent);
            }
        });

        return view;
    }

    static class ViewHolder {
        ImageView gridview_item_view;
        String cuteID;
    }
}
