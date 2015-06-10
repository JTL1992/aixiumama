package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.LabelActivity;
import com.harmazing.aixiumama.base.BaseMyAdapter;
import com.harmazing.aixiumama.model.RightUserFollowLabel;
import com.harmazing.aixiumama.R;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by gyw on 2014/12/21.
 */
public class UserRightFollowLabelAdapter extends BaseMyAdapter<RightUserFollowLabel.RightUserFollowLabelResults> {

    private BitmapUtils bitmapUtils;

    public UserRightFollowLabelAdapter(Context context, List<RightUserFollowLabel.RightUserFollowLabelResults> list) {
        super(context, list);
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }
//    @Override
//    public int getCount() {
//        return list.size();
//    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.user_right2_lv_followlabel_item, null);
            holder.iv_followlabel_item = (ImageView)view.findViewById(R.id.iv_followlabel_item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(list.size() > 0) {
            bitmapUtils.display(holder.iv_followlabel_item, API.STICKERS + list.get(i).follow_detail.image_small);
        }
        holder.iv_followlabel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LabelActivity.class);
                intent.putExtra("labelID",list.get(i).id);
                context.startActivity(intent);
            }
        });

        return view;
    }
    class ViewHolder{
        ImageView iv_followlabel_item;
    }
}
