package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.fragment.MessageArrayFragment;
import com.harmazing.aixiumama.utils.BitmapUtil;

import org.json.JSONArray;

/**
 * Created by Lyn on 2014/11/6.
 */
public class FavListViewAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;


    public FavListViewAdapter(JSONArray kindArray
            , Context ctx) {
        this.kindArray = kindArray;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {

        return MessageArrayFragment.mouthArray.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return -1;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        final ViewHolder holder ;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater flater = LayoutInflater.from(ctx);
//            convertView = flater.inflate(R.layout.fav_listview_item, parent,false);
            convertView = flater.inflate(R.layout.fav_listview_item_gridview_item, parent,false);
            holder.favTime = (TextView)convertView.findViewById(R.id.fav_time);
            holder.gridView  =(GridView)convertView.findViewById(R.id.fav_item_gridview);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

            holder.mouth = Integer.parseInt(MessageArrayFragment.keyOfArray.get(position));
            holder.favTime.setText(holder.mouth + "月收藏");

            holder.gridView.setAdapter(new FavListImageAdapter(ctx,MessageArrayFragment.mouthArray.get(holder.mouth)));
//            GridViewUtility.setGridViewHeightByMySelf(holder.gridView, BitmapUtil.dip2px(ctx,180));
            int height = MessageArrayFragment.mouthArray.get(holder.mouth).length()/2 * BitmapUtil.dip2px(ctx,260);
//        LogUtil.v("length",MessageArrayFragment.mouthArray.get(holder.mouth).length());
//        LogUtil.v("180",BitmapUtil.dip2px(ctx,180));
            holder.gridView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));


        return convertView;
    }


    private class ViewHolder {
        TextView favTime;
        GridView gridView;
        int mouth;
    }
}

