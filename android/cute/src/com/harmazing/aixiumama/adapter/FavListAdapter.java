package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.base.BaseMyGridView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/1/14.
 */
public class FavListAdapter extends BaseAdapter {
    ArrayList<FavListImageAdapter> favListImageAdapters;
    Context ctx;
    public FavListAdapter(Context context,ArrayList<FavListImageAdapter> favListImageAdapters){
        ctx = context;
        this.favListImageAdapters = favListImageAdapters;
        Log.v("favListImageAdapters",""+favListImageAdapters.size());
    }
    public void addFavItemList(ArrayList<FavListImageAdapter> list){
        int len = favListImageAdapters.size();
        for(int i=0;i<list.size();i++)
            favListImageAdapters.add(len + i,list.get(i));

    }
    @Override
    public int getCount() {
        return favListImageAdapters.size();
    }

    @Override
    public Object getItem(int i) {
        return favListImageAdapters.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(ctx).inflate(R.layout.cuted_item_gridview_layout,null);
            viewHolder.mGridView = (BaseMyGridView) view.findViewById(R.id.gridview);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)view.getTag();
        }
        Log.v("mGridView",""+i);
        viewHolder.mGridView.setAdapter(favListImageAdapters.get(i));
        return view;
    }
    class ViewHolder{
        BaseMyGridView mGridView;
    }

}
