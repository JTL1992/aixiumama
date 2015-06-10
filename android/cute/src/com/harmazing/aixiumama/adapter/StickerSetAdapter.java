package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.service.CuteService;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.ActivityGallery;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Lyn on 2014/12/4.
 */
public class StickerSetAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    JSONArray kindArray;

    public StickerSetAdapter(Context c,JSONArray array) {
        mContext = c;
        kindArray  = array;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return kindArray.length();
    }

    @Override
    public Object getItem(int position) {
        return  kindArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {

        return -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GirdTemp temp;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sticker_set_item, parent,false);
            temp = new GirdTemp();
            temp.sticker = (ImageView) convertView.findViewById(R.id.sticker);
            temp.getLayout = (RelativeLayout)convertView.findViewById(R.id.get);

            convertView.setTag(temp);
        } else {
            temp = (GirdTemp) convertView.getTag();
        }

        try {
            temp.icon = ((JSONObject) getItem(position)).getString("icon");
            temp.image = ((JSONObject) getItem(position)).getString("image");
            temp.id = ((JSONObject) getItem(position)).getInt("id");


            CuteApplication.imageLoader.loadImage(API.STICKERS + temp.icon , new ImageLoadingListener() {
                String stickerId = CuteService.stickersList.get(position + 1).getId() + "";
                String dir = Environment.getExternalStorageDirectory().getPath();

                @Override
                public void onLoadingStarted(String s, View view) {
//                                    ActivityGallery.sticker.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    if (BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".jpg") != null) {
                        temp.sticker.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".jpg"));
                    }else
                        LogUtil.v("贴纸没缓存", dir + "/Pictures/cute/" + stickerId + ".jpg");
                }

                @Override
                public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                    //  判断以前是否存过这个图
                    if (!BitmapUtil.isExist(dir + "/Pictures/cute/" + stickerId + ".jpg")) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    BitmapUtil.saveBitmap2sd(bitmap, stickerId);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                super.run();
                            }
                        }.start();
                    }
                    temp.sticker.setImageBitmap(bitmap);

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        temp.getLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CuteApplication.imageLoader.loadImage(API.STICKERS +  temp.image, new ImageLoadingListener() {
                    String stickerId = temp.id+"";
                    String dir = Environment.getExternalStorageDirectory().getPath();
                    @Override
                    public void onLoadingStarted(String s, View view) {
        //                                    ActivityGallery.sticker.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        if(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId +".jpg") != null) {
                            if(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".jpg").getHeight() > 320 || BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".jpg").getWidth() > 320) {
                                ActivityGallery.sticker.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".jpg"), new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 0.4f);

                            } else {
                                ActivityGallery.sticker.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".jpg"), new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 1f);
                            }
                            ActivityGallery.sticker.setVisibility(View.VISIBLE);
                            ActivityGallery.sticker.clearBorder(false);//    画出边框
                            ActivityGallery.stickerID = temp.id;
                            CuteApplication.activityList.get(CuteApplication.activityList.size() - 1).finish();
                        }
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                        //  判断以前是否存过这个图
                        if(!BitmapUtil.isExist(dir+"/Pictures/cute/"+stickerId+".jpg")) {
                            new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        BitmapUtil.saveBitmap2sd(bitmap, stickerId);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    super.run();
                                }
                            }.start();
                        }
                        if(bitmap.getHeight() > 320 || bitmap.getWidth() > 320) {
                            ActivityGallery.sticker.setImageBitmap(bitmap, new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 0.4f);
                        } else {
                            ActivityGallery.sticker.setImageBitmap(bitmap, new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 1f);
                        }
                        ActivityGallery.sticker.setVisibility(View.VISIBLE);
                        ActivityGallery.sticker.clearBorder(false);//    画出边框
                        ActivityGallery.stickerID = temp.id;
                        CuteApplication.activityList.get(CuteApplication.activityList.size() - 1).finish();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                        }
                    });
                    }
        });
        return convertView;
    }




    private class GirdTemp {
        ImageView sticker;
        RelativeLayout getLayout;
        String icon,image;
        int id;
    }

}
