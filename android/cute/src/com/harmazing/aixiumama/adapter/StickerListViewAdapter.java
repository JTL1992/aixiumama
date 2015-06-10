package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;


/**
 * Created by Lyn on 2014/11/16.
 */
public class StickerListViewAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;


    public StickerListViewAdapter(JSONArray kindArray
            , Context ctx) {
        this.kindArray = kindArray;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {

        return (kindArray.length()+1);
    }

    @Override
    public Object getItem(int position) {

        return kindArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {

        return -1;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {

        final ViewHolder holder ;
        Log.v("kindArray",kindArray.length()+"个");
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.sticker_horizon_list_items, parent,false);
            holder.stickerView = (ImageView)convertView.findViewById(R.id.sticker_image);
            holder.sitckerName = (TextView)convertView.findViewById(R.id.sticker_name);
            holder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.rl_sticker);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position == 0){
            holder.stickerView.setImageResource(R.drawable.stickers_set);
            holder.sitckerName.setText("贴纸库");
//            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(ctx,StickerSetActivity.class);
//                    ctx.startActivity(intent);
//                }
//            });
        }else {
            try {
                LogUtil.v("position",""+position);
               final String stickerId =kindArray.getJSONObject(position-1).getInt("id")+ "";
               final String stickerName = kindArray.getJSONObject(position-1).getString("name");
               final String dir = Environment.getExternalStorageDirectory().getPath();
                if (BitmapUtil.isExist(dir + "/Pictures/cute/" + stickerId + stickerName + ".png")){
                    LogUtil.v("有dir缓存贴纸地址",dir + "/Pictures/cute/" + stickerId + stickerName+ ".png");
                    holder.stickerView.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId +  stickerName + ".png"));
            }
                else
                CuteApplication.imageLoader.loadImage(API.STICKERS + kindArray.getJSONObject(position-1).getString("image"), new ImageLoadingListener() {
//                    String stickerId =kindArray.getJSONObject(position-1).getInt("id")+ "";
//                    String dir = Environment.getExternalStorageDirectory().getPath();

                    @Override
                    public void onLoadingStarted(String s, View view) {
//                                    ActivityGallery.sticker.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        if (BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + stickerName +".png") != null) {
                                holder.stickerView.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId +stickerName + ".png"));
                            LogUtil.v("dir缓存贴纸地址",dir + "/Pictures/cute/" + stickerId +stickerName + ".png");
                        }else
                            LogUtil.v("贴纸没缓存",dir + "/Pictures/cute/" + stickerId + stickerName +".png");
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                        //  判断以前是否存过这个图
                        if (!BitmapUtil.isExist(dir + "/Pictures/cute/" + stickerId + stickerName +".png")) {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        BitmapUtil.saveBitmap2sd(bitmap, stickerId+stickerName);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    super.run();
                                }
                            }.start();
                        }
                        holder.stickerView.setImageBitmap(bitmap);

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
                holder.sitckerName.setText(kindArray.getJSONObject(position-1).getString("name"));



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView stickerView;
        TextView sitckerName;
        RelativeLayout relativeLayout;
    }
}