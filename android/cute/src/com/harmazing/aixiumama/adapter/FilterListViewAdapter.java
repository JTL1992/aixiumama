package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harmazing.aixiumama.utils.GPUImageUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by Lyn on 2014/11/12.
 */
public class FilterListViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<GPUImageFilter> list;
    String []filterName = {"原图","萌萌哒","惹人爱","细嫩","生机勃勃","稚气","纯真","温润","无邪","冰雪聪明","嬉戏","顽皮","水汪汪"
    ,"活泼","烂漫","粉嘟嘟","喜洋洋","灰太狼"
    };
    int[] imageArr = {R.drawable.c0 ,R.drawable.c1,R.drawable.c2 ,R.drawable.c3 ,
            R.drawable.c4 ,R.drawable.c5 ,R.drawable.c6 ,R.drawable.c7 ,
            R.drawable.c8 ,R.drawable.c9 ,R.drawable.c10 ,R.drawable.c11 ,
            R.drawable.c12 ,R.drawable.c13 ,R.drawable.c14 ,R.drawable.c15 ,
            R.drawable.c16 ,R.drawable.c17 };

    public FilterListViewAdapter(Context context){

        mContext = context;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
        list = (List<GPUImageFilter>) GPUImageUtil.getImageFilter(mContext);

    }
    @Override
    public int getCount() {
        return filterName.length;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

                ViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.beautify_horizon_list_items2, null);
            holder = new ViewHolder();
            holder.imageview_item_listview = (ImageView) convertView.findViewById(R.id.gpuimageview_item_listview);
            holder.filter_name = (TextView) convertView.findViewById(R.id.filter_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        convertView = View.inflate(mContext, R.layout.beautify_horizon_list_items2, null);
//        GPUImageView gpuimageview_item_listview = (GPUImageView) convertView.findViewById(R.id.gpuimageview_item_listview);
//        TextView filter_name = (TextView) convertView.findViewById(R.id.filter_name);
//
//        gpuimageview_item_listview.setImage(ActivityGallery.bitmap);
//        gpuimageview_item_listview.setFilter(list.get(position));
//        LogUtil.v("position", position + "");
//        if ( position == 11){
//            gpuimageview_item_listview.saveToPictures("XXX", "c" + position + ".jpg", new GPUImage.OnPictureSavedListener() {
//                @Override
//                public void onPictureSaved(Uri uri) {
//                    LogUtil.v("filterName[position]", filterName[position]);
//                }
//            });
//    }
        holder.imageview_item_listview.setImageDrawable(BitmapUtil.readBitMapToDrawable(mContext, imageArr[position]));
        holder.filter_name.setText(filterName[position]);

        return convertView;
    }


    class ViewHolder {
        TextView filter_name;
        ImageView imageview_item_listview;
    }
}

