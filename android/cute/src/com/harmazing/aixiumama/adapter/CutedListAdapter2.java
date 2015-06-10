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
 * Created by Administrator on 2014/12/31.
 */
public class CutedListAdapter2 extends BaseAdapter{
    ArrayList<CutedItemAdapter2> cutedItemAdapter2ArrayList;
    int num;
    Context context;
    public CutedListAdapter2(Context context, ArrayList<CutedItemAdapter2> cutedItemAdapter2ArrayList){
        this.context = context;
        this.cutedItemAdapter2ArrayList = cutedItemAdapter2ArrayList;
    }
    public void addCuteItemList(ArrayList<CutedItemAdapter2> list){
        int len = cutedItemAdapter2ArrayList.size();
        for(int i=0;i<list.size();i++)
        cutedItemAdapter2ArrayList.add( len + i,list.get(i));

    }
    @Override
    public int getCount() {
        return cutedItemAdapter2ArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.cuted_item_gridview_layout,null);
            viewHolder.mGridView = (BaseMyGridView) view.findViewById(R.id.gridview);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        Log.v("MMMMM",""+i);
        viewHolder.mGridView.setAdapter(cutedItemAdapter2ArrayList.get(i));


        return view;
    }
    class ViewHolder{
        BaseMyGridView mGridView;
    }
}
