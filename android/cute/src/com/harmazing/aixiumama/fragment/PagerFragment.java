package com.harmazing.aixiumama.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.View;
import android.widget.AdapterView;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.base.BaseFragment;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.ActivityGallery;
import com.harmazing.aixiumama.activity.StickerSetActivity;
import com.harmazing.aixiumama.adapter.PaperAdapter;
import com.harmazing.aixiumama.model.Sticker;
import com.harmazing.aixiumama.view.HorizontalListView;
import com.harmazing.aixiumama.utils.RequestVo;
import com.harmazing.aixiumama.parser.StickerParser;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoyongwei on 2015/1/19.
 */
public class PagerFragment extends BaseFragment {

    private RequestVo stickerRequestVo;
    private DataCallback stickerCallback;

    private List<Bitmap> peperBitmapList = new ArrayList<Bitmap>();
    private List<String> peperNameList = new ArrayList<String>();

    private HorizontalListView stickersListView;

    private PaperAdapter paperAdapter;

    private Sticker mSticker;

    @Override
    protected int setMyContentView() {
        return R.layout.sticker;
    }

    @Override
    protected void initRequestVo() {
        String requestUrl = API.ALL_STICKERS;
        StickerParser stickerParser = new StickerParser();
        stickerRequestVo = new RequestVo(requestUrl, context, null, stickerParser);

    }

    @Override
    protected void initCallBall() {
        //占位, 显示贴纸库
        peperBitmapList.add(new BitmapFactory().decodeResource(getResources(), R.drawable.blank));

        stickerCallback = new DataCallback<Sticker>() {
            @Override
            public void processData(final Sticker data) {
                if(data != null) {
                    mSticker = data;
                    System.out.println("gyw1"+data);
                    //请求网络,放在子线程中
                    new Thread() {
                        @Override
                        public void run() {
                            for(int i = 0; i < data.results.size(); i++) {

                                peperNameList.add(data.results.get(i).name);

                                CuteApplication.imageLoader.loadImage(API.STICKERS + data.results.get(i).image, new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String s, View view) {}

                                    @Override
                                    public void onLoadingFailed(String s, View view, FailReason failReason) {}

                                    @Override
                                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                        peperBitmapList.add(bitmap);
                                    }

                                    @Override
                                    public void onLoadingCancelled(String s, View view) {}

                                });
                            }
                        }
                    }.start();
                }
                //关闭提示框
                closeProgressDialog();
            }
        };

    }

    @Override
    protected void initView() {
        stickersListView = (HorizontalListView) view.findViewById(R.id.horizon_listview);
    }

    @Override
    protected void initData() {
        getDataFromServer(stickerRequestVo, stickerCallback);
        //关闭提示框
        closeProgressDialog();


        stickersListView.getParent().requestDisallowInterceptTouchEvent(false);
        stickersListView.setAdapter(new PaperAdapter(context, peperNameList, peperBitmapList));

        stickersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                if(i==0){
                    Intent intent = new Intent(context,StickerSetActivity.class);
                    startActivity(intent);
                } else {
                    ActivityGallery.sticker.setImageBitmap(peperBitmapList.get(i), new Point(500, 500), 0, 1f);
                    ActivityGallery.sticker.setVisibility(View.VISIBLE);
                    ActivityGallery.sticker.clearBorder(false);//    画出边框

                    ActivityGallery.stickerID = Integer.parseInt(mSticker.results.get(i).id);
                }
            }
        });
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void onClick(View view) {

    }
}
