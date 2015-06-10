package com.harmazing.aixiumama.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.List;
/**
 * Created by gyw on 2014/12/21.
 */
public abstract class BaseMyAdapter<T> extends BaseAdapter {

    public Context context;
    public List<T> list;

    public BaseMyAdapter(Context context) {
        this.context = context;
    }

    public BaseMyAdapter(Context context, List<T> list){
        this.context = context;
        this.list = list;
    }
    public void addDataList(List<T> list){
        int len = this.list.size();
        for (int i = 0; i < list.size(); i++ ){
            this.list.add(len+i,list.get(i));
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public abstract View getView(int i, View view, ViewGroup viewGroup);
}
