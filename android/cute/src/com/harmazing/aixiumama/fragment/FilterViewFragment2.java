package com.harmazing.aixiumama.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.ActivityGallery;
import com.harmazing.aixiumama.activity.StickerSetActivity;
import com.harmazing.aixiumama.adapter.FilterListViewAdapter;
import com.harmazing.aixiumama.adapter.StickerListViewAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.filter.AmaroFilter;
import com.harmazing.aixiumama.filter.BrannanFilter;
import com.harmazing.aixiumama.filter.Cute1977Filter;
import com.harmazing.aixiumama.filter.EarlybirdFilter;
import com.harmazing.aixiumama.filter.HudsonFilter;
import com.harmazing.aixiumama.filter.InkwellFilter;
import com.harmazing.aixiumama.filter.LomoFilter;
import com.harmazing.aixiumama.filter.LordKelvinFilter;
import com.harmazing.aixiumama.filter.NashvilleFilter;
import com.harmazing.aixiumama.filter.RiseFilter;
import com.harmazing.aixiumama.filter.ToasterFilter;
import com.harmazing.aixiumama.filter.ValenciaFilter;
import com.harmazing.aixiumama.filter.WaldenFilter;
import com.harmazing.aixiumama.filter.XprollFilter;
import com.harmazing.aixiumama.service.CuteService;
import com.harmazing.aixiumama.view.HorizontalListView;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.filter.HefeFilter;
import com.harmazing.aixiumama.filter.SierraFilter;
import com.harmazing.aixiumama.filter.SutroFilter;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Lyn on 2014/11/12.
 */
public class FilterViewFragment2 extends Fragment {
    View v = null;
    int mNum;

    HorizontalListView hListView;
    static FilterViewFragment2 newInstance(int num ) {
        FilterViewFragment2 array = new FilterViewFragment2();

        Bundle args = new Bundle();
        args.putInt("num", num);

        array.setArguments(args);

        return array;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;

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

                    switch (i){
                        case 0:
                            //adapterView.getChildAt(0).setBackgroundColor(Color.RED);
                            CuteApplication.setFilterCLear(ActivityGallery.mGPUImage);
                            break;
                        case 1:
                            //adapterView.getChildAt(0).setBackgroundColor(Color.RED);
                            ActivityGallery.mGPUImage.setFilter(new EarlybirdFilter(getActivity()));
                            break;
                        case 2:
                            ActivityGallery.mGPUImage.setFilter(new NashvilleFilter(getActivity()));
                            break;
                        case 3:
                            ActivityGallery.mGPUImage.setFilter(new WaldenFilter(getActivity()));
                            break;
                        case 4:
                            ActivityGallery.mGPUImage.setFilter(new HudsonFilter(getActivity()));
                            break;
                        case 5:
                            ActivityGallery.mGPUImage.setFilter(new SierraFilter(getActivity()));
                            break;
                        case 6:
                            ActivityGallery.mGPUImage.setFilter(new ValenciaFilter(getActivity()));
                            break;
                        case 7:
                            ActivityGallery.mGPUImage.setFilter(new RiseFilter(getActivity()));
                            break;
                        case 8:
                            ActivityGallery.mGPUImage.setFilter(new XprollFilter(getActivity()));
                            break;
                        case 9:
                            ActivityGallery.mGPUImage.setFilter(new AmaroFilter(getActivity()));
                            break;
                        case 10:
                            ActivityGallery.mGPUImage.setFilter(new LomoFilter(getActivity()));
                            break;
                        case 11:
                            ActivityGallery.mGPUImage.setFilter(new SutroFilter(getActivity()));
                            break;
                        case 12:
                            ActivityGallery.mGPUImage.setFilter(new BrannanFilter(getActivity()));
                            break;
                        case 13:
                            ActivityGallery.mGPUImage.setFilter(new HefeFilter(getActivity()));
                            break;
                        case 14:
                            ActivityGallery.mGPUImage.setFilter(new LordKelvinFilter(getActivity()));
                            break;
                        case 15:
                            ActivityGallery.mGPUImage.setFilter(new Cute1977Filter(getActivity()));
                            break;
                        case 16:
                            ActivityGallery.mGPUImage.setFilter(new ToasterFilter(getActivity()));
                            break;
                        case 17:
                            ActivityGallery.mGPUImage.setFilter(new InkwellFilter(getActivity()));
                            break;

                    }

                    ActivityGallery.imageView.setImageBitmap(ActivityGallery.mGPUImage.getBitmapWithFilterApplied());
                }
            });

        }else if(mNum == 1) {
            //  贴纸
            v = inflater.inflate(R.layout.sticker, container, false);
            HorizontalListView    stickersListView = (HorizontalListView) v.findViewById(R.id.horizon_listview);
            stickersListView.getParent().requestDisallowInterceptTouchEvent(false);
            if(CuteService.stickersJSONarray.length() > 0)
                stickersListView.setAdapter(new StickerListViewAdapter(CuteService.stickersJSONarray,getActivity()));

            stickersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {
                    if(i==0){
                        Intent intent = new Intent(getActivity(),StickerSetActivity.class);
                        startActivity(intent);
                    }
                    CuteApplication.imageLoader.loadImage(API.STICKERS + CuteService.stickersList.get(i).getUrl(),new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            ActivityGallery.sticker.setImageBitmap( bitmap,new Point(200, 200), 30, 0.5f);
                            ActivityGallery.sticker.clearBorder(false);//    画出边框
                            ActivityGallery.sticker.setVisibility(View.VISIBLE);
                            ActivityGallery.stickerID = CuteService.stickersList.get(i).getId();
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });

                }
            });
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
}
