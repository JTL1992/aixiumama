package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.base.BaseMyAdapter;
import com.harmazing.aixiumama.R;

import java.util.List;

/**
 * Created by guoyongwei on 2014/12/29.
 */
public class PaperAdapter extends BaseMyAdapter {

    private List<Bitmap> bitmapList;
    private List<String> nameList;

    public PaperAdapter(Context context, List nameList, List<Bitmap> bitmapList) {
        super(context);
        this.bitmapList = bitmapList;
        this.nameList = nameList;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view == null) {
            view = View.inflate(context, R.layout.sticker_horizon_list_items2, null);
            holder = new ViewHolder();
            holder.rl_item_sticker = (RelativeLayout) view.findViewById(R.id.rl_item_sticker);
            holder.iv_item_sticker = (ImageView) view.findViewById(R.id.iv_item_sticker);
            holder.tv_item_sticker = (TextView) view.findViewById(R.id.tv_item_sticker);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if(i == 0){
            holder.iv_item_sticker.setImageResource(R.drawable.blank);
            holder.tv_item_sticker.setText("贴纸库");
        }else {
            //CuteApplication.downloadIamge(API.STICKERS + list.get(i).image, holder.iv_item_sticker);

            holder.iv_item_sticker.setImageBitmap(bitmapList.get(i));
            holder.tv_item_sticker.setText(nameList.get(i));
       }

       return view;
    }
    static class ViewHolder {
        RelativeLayout rl_item_sticker;
        ImageView iv_item_sticker;
        TextView tv_item_sticker;
    }
}
