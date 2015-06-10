package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harmazing.aixiumama.activity.ActivityGallery;
import com.harmazing.aixiumama.filter.AmaroFilter;
import com.harmazing.aixiumama.filter.BrannanFilter;
import com.harmazing.aixiumama.filter.Cute1977Filter;
import com.harmazing.aixiumama.filter.EarlybirdFilter;
import com.harmazing.aixiumama.filter.HefeFilter;
import com.harmazing.aixiumama.filter.HudsonFilter;
import com.harmazing.aixiumama.filter.InkwellFilter;
import com.harmazing.aixiumama.filter.LomoFilter;
import com.harmazing.aixiumama.filter.LordKelvinFilter;
import com.harmazing.aixiumama.filter.NashvilleFilter;
import com.harmazing.aixiumama.filter.RiseFilter;
import com.harmazing.aixiumama.filter.SierraFilter;
import com.harmazing.aixiumama.filter.SutroFilter;
import com.harmazing.aixiumama.filter.ToasterFilter;
import com.harmazing.aixiumama.filter.ValenciaFilter;
import com.harmazing.aixiumama.filter.WaldenFilter;
import com.harmazing.aixiumama.filter.XprollFilter;
import com.harmazing.aixiumama.R;

import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;

/**
 * Created by Lyn on 2014/11/12.
 */
public class FilterListViewAdapter2 extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    String []filterName = {"原图","萌萌哒","惹人爱","细嫩","生机勃勃","稚气","纯真","温润","无邪","冰雪聪明","嬉戏","顽皮","水汪汪"
    ,"活泼","烂漫","粉嘟嘟","喜洋洋","灰太狼"
    };

    public FilterListViewAdapter2(Context context){

        mContext = context;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
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
    public View getView(int position, View convertView, ViewGroup parent) {

        final GirdTemp temp;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.beautify_horizon_list_items, parent,false);
            temp = new GirdTemp();
            temp.nameTV=  (TextView)convertView.findViewById(R.id.filter_name);
            temp.mGPUImageView = (ImageView) convertView.findViewById(R.id.gpuimage);

            convertView.setTag(temp);
        } else {
            temp = (GirdTemp) convertView.getTag();
        }

        //temp.nameTV.setBackgroundColor(0xffdbdbdb);

        switch (position){
            case 0:
                GPUImageToneCurveFilter gpuImageToneCurveFilter = new GPUImageToneCurveFilter();
                ActivityGallery.mGPUImage.setFilter(gpuImageToneCurveFilter);
                break;
            case 1:
                ActivityGallery.mGPUImage.setFilter(new EarlybirdFilter(mContext));
                break;
            case 2:
                ActivityGallery.mGPUImage.setFilter(new NashvilleFilter(mContext));
                break;
            case 3:
                ActivityGallery.mGPUImage.setFilter(new WaldenFilter(mContext));
                break;
            case 4:
                ActivityGallery.mGPUImage.setFilter(new HudsonFilter(mContext));
                break;
            case 5:
                ActivityGallery.mGPUImage.setFilter(new SierraFilter(mContext));
                break;
            case 6:
                ActivityGallery.mGPUImage.setFilter(new ValenciaFilter(mContext));
                break;
            case 7:
                ActivityGallery.mGPUImage.setFilter(new RiseFilter(mContext));
                break;
            case 8:
                ActivityGallery.mGPUImage.setFilter(new XprollFilter(mContext));
                break;
            case 9:
                ActivityGallery.mGPUImage.setFilter(new AmaroFilter(mContext));
                break;
            case 10:
                ActivityGallery.mGPUImage.setFilter(new LomoFilter(mContext));
                break;
            case 11:
                ActivityGallery.mGPUImage.setFilter(new SutroFilter(mContext));
                break;
            case 12:
                ActivityGallery.mGPUImage.setFilter(new BrannanFilter(mContext));
                break;
            case 13:
                ActivityGallery.mGPUImage.setFilter(new HefeFilter(mContext));
                break;
            case 14:
                ActivityGallery.mGPUImage.setFilter(new LordKelvinFilter(mContext));
                break;
            case 15:
                ActivityGallery.mGPUImage.setFilter(new Cute1977Filter(mContext));
                break;
            case 16:
                ActivityGallery.mGPUImage.setFilter(new ToasterFilter(mContext));
                break;
            case 17:
                ActivityGallery.mGPUImage.setFilter(new InkwellFilter(mContext));
                break;
        }

        if(temp.image == null){
         //   temp.image = BitmapUtil.compress(ActivityGallery.mGPUImage.getBitmapWithFilterApplied(),BitmapUtil.dip2px(mContext,80),BitmapUtil.dip2px(mContext,80));
            temp.image = ActivityGallery.mGPUImage.getBitmapWithFilterApplied();
            temp.mGPUImageView.setImageBitmap(temp.image);
            temp.mGPUImageView.setTag(temp.image);
        }else{
            temp.mGPUImageView.setImageBitmap((Bitmap)temp.mGPUImageView.getTag());
        }


        temp.nameTV.setText(filterName[position]);

        return convertView;
    }

    private class GirdTemp {
        TextView nameTV;
        ImageView mGPUImageView;
        Bitmap image;
    }
}

