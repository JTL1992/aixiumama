package com.harmazing.aixiumama.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.UserFansActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;

import java.util.LinkedList;

/**
 * Created by Lyn on 2014/11/19.
 */
public class CutePersonThumbNail extends LinearLayout{

    Context mContext;
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9;
    LinkedList<ImageView> viewList = new LinkedList<ImageView>();
    Button num;

    public CutePersonThumbNail(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setFocusable(true);
        this.setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.cute_person_thumbnail, this);
        viewList.clear();
        findViews();
    }

    private void findViews() {
        num = (Button)findViewById(R.id.num);
        imageView1 = (ImageView)findViewById(R.id.imageview1);
        imageView2 = (ImageView)findViewById(R.id.imageview2);
        imageView3 = (ImageView)findViewById(R.id.imageview3);
        imageView4 = (ImageView)findViewById(R.id.imageview4);
        imageView5 = (ImageView)findViewById(R.id.imageview5);
        imageView6 = (ImageView)findViewById(R.id.imageview6);
        imageView7 = (ImageView)findViewById(R.id.imageview7);
        imageView8 = (ImageView)findViewById(R.id.imageview8);
        imageView9 = (ImageView)findViewById(R.id.imageview9);

        viewList.add(imageView1);
        viewList.add(imageView2);
        viewList.add(imageView3);
        viewList.add(imageView4);
        viewList.add(imageView5);
        viewList.add(imageView6);
        viewList.add(imageView7);
        viewList.add(imageView8);
        viewList.add(imageView9);


    }

    /**
     *  显示出 thumbnail
     * @param list
     * @param cuteNum
     */
    public void setThumbnail(LinkedList<String> list,int cuteNum, final int cuteID)
    {
        for(int i=0;i<list.size();i++)
        {
            CuteApplication.downloadLittleCornImage(API.STICKERS + list.get(i), viewList.get(i));
            viewList.get(i).setVisibility(VISIBLE);
            //  imageview 都用完了要现实button
            if(i == 8){
                num.setVisibility(VISIBLE);
                num.setText(cuteNum + "");
                num.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, UserFansActivity.class);
                        intent.putExtra("cuteID",cuteID);
                        intent.putExtra("userName","赞过的用户");
                        intent.putExtra("CutePersonThumbNail",false);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

    /**
     * 清空
     */
    public void clear(){
        for(int i=0;i<viewList.size();i++)
        {
            viewList.get(i).setOnClickListener(null);
            viewList.get(i).setImageDrawable(null);
            viewList.get(i).setVisibility(GONE);
            num.setVisibility(GONE);
        }
        viewList.clear();
        findViews();
    }



    /**
     * 为每个点赞thumbnail 设置点击事件
     * @param listID
     */
    public void setOnClick(LinkedList<Integer> listID){
        for(int i=0;i<listID.size();i++)
        {
            viewList.get(i).setOnClickListener(new ImageViewOnClickListener(viewList.get(i),listID.get(i)));
        }
    }


    class ImageViewOnClickListener implements View.OnClickListener {
        ImageView labelTV;
        int userID;
        public ImageViewOnClickListener(ImageView label,int id)
        {
            labelTV = label;
            userID = id;
        }
        @Override
        public void onClick(View view) {
            /**
             *      跳转到个人主页
             */
            Intent intent = new Intent(mContext, PersonActivity.class);
            intent.putExtra("person_id",userID);
            mContext.startActivity(intent);
        }
    }
}
