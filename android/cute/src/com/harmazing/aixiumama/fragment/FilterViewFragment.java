package com.harmazing.aixiumama.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable ;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.harmazing.aixiumama.activity.ActivityGallery;
import com.harmazing.aixiumama.activity.StickerSetActivity;
import com.harmazing.aixiumama.adapter.FilterListViewAdapter;
import com.harmazing.aixiumama.adapter.StickerListViewAdapter;
import com.harmazing.aixiumama.service.CuteService;
import com.harmazing.aixiumama.utils.GPUImageUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.view.HorizontalListView;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by Lyn on 2014/11/12.
 */
public class FilterViewFragment extends Fragment {
    View v = null;
    int mNum;
    private List<GPUImageFilter> gpuImageFilterList;
    HorizontalListView hListView;
    public static FilterViewFragment newInstance(int num) {
        FilterViewFragment array = new FilterViewFragment();

        Bundle args = new Bundle();
        args.putInt("num", num);

        array.setArguments(args);

        return array;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        gpuImageFilterList = (List<GPUImageFilter>) GPUImageUtil.getImageFilter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mNum == 0) {
            //  美化
            v = inflater.inflate(R.layout.beautify, container, false);
            hListView = (HorizontalListView) v.findViewById(R.id.horizon_listview);
            hListView.getParent().requestDisallowInterceptTouchEvent(false);
            hListView.setAdapter(new FilterListViewAdapter(getActivity()));

            hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(ActivityGallery.mGPUImage != null) {
                        ActivityGallery.mGPUImage.setFilter(gpuImageFilterList.get(i));
                        new GetFilterImage().execute();
                    }
                }
            });

        }else if(mNum == 1) {
            //  贴纸
            v = inflater.inflate(R.layout.sticker, container, false);
            HorizontalListView stickersListView = (HorizontalListView) v.findViewById(R.id.horizon_listview);
            stickersListView.getParent().requestDisallowInterceptTouchEvent(true);
            if(CuteService.stickersJSONarray.length() > 0) {
                stickersListView.setAdapter(new StickerListViewAdapter(CuteService.stickersJSONarray, getActivity()));
                stickersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        LogUtil.v("onItemClick", i + "   " + l);
                        if (i == 0) {
                            Intent intent = new Intent(getActivity(), StickerSetActivity.class);
                            startActivity(intent);
                        } else{
                            String stickerId = CuteService.stickersList.get(i).getId()+"";
                            ActivityGallery.stickerID = CuteService.stickersList.get(i).getId();
                            final String stickerName = CuteService.stickersList.get(i).getName();
                                String dir = Environment.getExternalStorageDirectory().getPath();
                            if(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId +stickerName+".png") != null) {
                                        if(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId +stickerName+ ".png").getHeight() > 320 || BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId +stickerName+ ".png").getWidth() > 320) {
                                            ActivityGallery.sticker.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + stickerName+".png"), new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 0.4f);

                                        } else {
                                            ActivityGallery.sticker.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId +stickerName+ ".png"), new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 1f);
                                        }
                                        ActivityGallery.sticker.setVisibility(View.VISIBLE);
                                        ActivityGallery.sticker.clearBorder(false);//    画出边框
//                                        ActivityGallery.stickerID = CuteService.stickersList.get(i + 1).getId();
                                    }
                                }
//                            CuteApplication.imageLoader.loadImage(API.STICKERS + CuteService.stickersList.get(i).getUrl(), new ImageLoadingListener() {
//                                String stickerId = CuteService.stickersList.get(i).getId()+"";
//                                String dir = Environment.getExternalStorageDirectory().getPath();
//                                @Override
//                                public void onLoadingStarted(String s, View view) {
////                                    ActivityGallery.sticker.setVisibility(View.VISIBLE);
//                                }
//
//                                @Override
//                                public void onLoadingFailed(String s, View view, FailReason failReason) {
//                                    if(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId +".png") != null) {
//                                        if(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".png").getHeight() > 320 || BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".png").getWidth() > 320) {
//                                            ActivityGallery.sticker.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".png"), new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 0.4f);
//
//                                        } else {
//                                            ActivityGallery.sticker.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + stickerId + ".png"), new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 1f);
//                                        }
//                                        ActivityGallery.sticker.setVisibility(View.VISIBLE);
//                                        ActivityGallery.sticker.clearBorder(false);//    画出边框
//                                        ActivityGallery.stickerID = CuteService.stickersList.get(i + 1).getId();
//                                    }
//                                }
//
//                                @Override
//                                public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
//                                    //  判断以前是否存过这个图
//                                    if(!BitmapUtil.isExist(dir+"/Pictures/cute/"+stickerId+".png")) {
//                                            new Thread(){
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        BitmapUtil.saveBitmap2sd(bitmap, stickerId);
//                                                    } catch (IOException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                    super.run();
//                                                }
//                                            }.start();
//                                    }
//                                    if(bitmap.getHeight() > 320 || bitmap.getWidth() > 320) {
//                                        ActivityGallery.sticker.setImageBitmap(bitmap, new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 0.4f);
//                                    } else {
//                                        ActivityGallery.sticker.setImageBitmap(bitmap, new Point(ActivityGallery.margin, ActivityGallery.margin), 0, 1f);
//                                    }
//                                    ActivityGallery.sticker.setVisibility(View.VISIBLE);
//                                    ActivityGallery.sticker.clearBorder(false);//    画出边框
//
//                                    ActivityGallery.stickerID = CuteService.stickersList.get(i + 1).getId();
//                                }
//
//                                @Override
//                                public void onLoadingCancelled(String s, View view) {
//
//                                }
//                            });
//                    }
                    }
                });
            }
        }


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    class GetFilterImage extends AsyncTask<Object, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Object[] objects) {
            Bitmap bitmap = ActivityGallery.mGPUImage.getBitmapWithFilterApplied();
            return BitmapUtil.compress(bitmap, 1000, 1000);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ActivityGallery.imageView.setImageBitmap(bitmap);
        }
    }

}
