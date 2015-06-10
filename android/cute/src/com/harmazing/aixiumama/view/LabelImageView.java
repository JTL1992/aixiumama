package com.harmazing.aixiumama.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.harmazing.aixiumama.activity.LabelActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.model.Cute;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;

import java.util.LinkedList;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by Lyn on 2014/11/10.
 */
public class LabelImageView extends FrameLayout {

    Context mContext;
    Cute cute;
    String labelText;
   
    LinkedList<LabelTextView> viewList ;
    LabelTextView labelTextView;
    GifImageView gifImageView;
    ImageView labelImage;
    FrameLayout frame;

    boolean clickFlag = false;
    public LabelImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context != mContext){
        this.setFocusable(true);
        this.setClickable(true);
        cute = new Cute(context);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.label_imageview, this);
        findViews();
        }
    }

    public void setCute(Cute cute) {
        this.cute = cute;
        findViews();
        setLabelLocation(cute);
        setLabelOnClickListener(cute);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setLabelVisbility();
            }
        },2000);
    }

    public void clearGif(){
//        CuteApplication.gifFromResource.recycle();
        gifImageView.setImageBitmap(null);
    }

    public Cute getCute() {
        return cute;
    }

    public int getLabelNum(){
        return cute.getLabelListLength();
    }

    public ImageView getImageView(){
        return labelImage;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelVisbility(){

            if(clickFlag) {
                for(int i=0;i<getLabelNum();i++) {
                    viewList.get(i).setVisibility(VISIBLE);
                }
                clickFlag =false;
            }
            else {
                for(int i=0;i<getLabelNum();i++) {
                    viewList.get(i).setVisibility(GONE);
                }
                clickFlag = true;
        }
    }

    private void findViews() {
       gifImageView = (GifImageView)findViewById(R.id.iv);

        gifImageView.setImageDrawable(CuteApplication.gifFromResource);

        frame = (FrameLayout) findViewById(R.id.frame);

        labelImage =    (ImageView) findViewById(R.id.image_bg);

        viewList = new LinkedList<LabelTextView>();

        for(LabelTextView view : viewList){
            frame.removeView(view);
        }

        viewList.clear();

    }



    public static void setLayout(View view,int x,int y)
    {
        FrameLayout.MarginLayoutParams margin=new FrameLayout.MarginLayoutParams(view.getLayoutParams());
        margin.width = CuteApplication.getWidth(view);
        margin.height = CuteApplication.getHeight(view);
        margin.setMargins(x,y, 0, 0);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    public void setLabelLocation(Cute cute){


        int x,y;
        //  改变xy因为要计算LabelText 占的大小
        for(int i=0;i<cute.getLabelListLength();i++) {
            labelTextView = new LabelTextView(mContext);
            labelTextView.setLabelText(cute.getLabel(i).getName());
            labelTextView.setLabel(cute.getLabel(i));

            if(labelTextView.getLabel().getDirection() == 1){
                labelTextView.setLeftLabel();
                x = new Double(labelTextView.getLabel().getX()).intValue() - CuteApplication.getWidth(labelTextView) + labelTextView.getBallHeight() / 2;
                if(x < 0) {
//                    viewList.get(i).setRightLabel();
                    x = 0;
                }

            }else {
                labelTextView.setRightLabel();
                x = new Double(labelTextView.getLabel().getX() - labelTextView.getBallHeight() / 2).intValue();
            }

            y = new Double(labelTextView.getLabel().getY() - labelTextView.getBallHeight() / 2).intValue()  ;
            //判断下边界越界
            if(y + BitmapUtil.dip2px(mContext,40) >= CuteApplication.screenWH[0]){
                y = CuteApplication.screenWH[0] - BitmapUtil.dip2px(mContext,40);
            }

            frame.addView(labelTextView);
            setLayout(labelTextView, x, y);
            viewList.add(labelTextView);

            System.out.println("labelout :  " + "x :  " + x + "  y :  " + y + "     "+labelTextView.getLabel().getName());
        }
    }

    /**
     * 为每个Label 添加响应事件
     * @param _cute
     */
    public void setLabelOnClickListener(Cute _cute) {
        this.cute = _cute;
        for(int i=0;i<cute.getLabelListLength();i++){
            viewList.get(i).setOnClickListener(new LabelOnClickListener(viewList.get(i),cute.getLabel(i).getId()+""));
        }
    }

    public void clear(){
        for(int i=0;i<viewList.size();i++){
             viewList.get(i).clear();
             this.frame.removeView(viewList.get(i));
        }
        viewList.clear();
        this.cute.clear();
    }

    class LabelOnClickListener implements View.OnClickListener {
        LabelTextView labelTV;
        String labelID;
        public LabelOnClickListener(LabelTextView label,String id)
        {
            labelTV = label;
            labelID = id;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, LabelActivity.class);

            intent.putExtra("labelID",labelID);
            mContext.startActivity(intent);
        }
    }
}
