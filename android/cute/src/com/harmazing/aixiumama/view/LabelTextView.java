package com.harmazing.aixiumama.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.model.Label;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.LogUtil;


/**
 * Created by Lyn on 2014/11/10.
 */
public class LabelTextView extends RelativeLayout {

    Context mContext;
    String text;
    TextView textRightTV,textLeftTV;
    Label label;
    View ball1, ball2,ball3;
    FrameLayout ball_framelayout;
    boolean isMove;
    int screenWidth ;
    float lastX,lastY;



    public LabelTextView(Context context  ){
        super(context);
        this.mContext = context;


        initView(context);
        label = new Label(context);
        screenWidth = CuteApplication.getScreenHW(mContext)[0];
        setAnimation();
    }

    private void initView(Context context) {
//        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.label_tv,this);
//        textRightTV =  (TextView)findViewById(R.id.text);
//        textLeftTV =  (TextView)findViewById(R.id.text_left);
//        ball_framelayout = (FrameLayout)findViewById(R.id.ball_framelayout);

        textLeftTV = new TextView(context);
        textLeftTV.setId(1);
        textLeftTV.setTextColor(context.getResources().getColor(R.color.white));
        textLeftTV.setBackgroundResource(R.drawable.label_bg2_right);
        textLeftTV.setGravity(CENTER_VERTICAL);
        textLeftTV.setVisibility(GONE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,BitmapUtil.dip2px(context,25)) ;
        params.addRule(ALIGN_PARENT_LEFT);
        params.addRule(CENTER_VERTICAL);
        params.setMargins(0,0,0,0);
        this.addView(textLeftTV, params);

        ball_framelayout  = new FrameLayout(context);
        ball_framelayout.setId(2);
        RelativeLayout.LayoutParams paramsFL = new RelativeLayout.LayoutParams(BitmapUtil.dip2px(context,40),BitmapUtil.dip2px(context,40)) ;
        paramsFL.addRule(RIGHT_OF,textLeftTV.getId());
//        paramsFL.addRule(CENTER_VERTICAL);
        this.addView(ball_framelayout,paramsFL);

        int flWidth = BitmapUtil.dip2px(context,16);// 40/2 -8/2

        ball2 = new View(context);
        ball2.setBackgroundResource(R.drawable.black_border);
        FrameLayout.LayoutParams paramsBall2 = new FrameLayout.LayoutParams(BitmapUtil.dip2px(context,6),BitmapUtil.dip2px(context,6));
        paramsBall2.setMargins(flWidth,flWidth,0,0);
        ball_framelayout.addView(ball2,paramsBall2);

        ball3 = new View(context);
        ball3.setBackgroundResource(R.drawable.black_border);
        FrameLayout.LayoutParams paramsBall3 = new FrameLayout.LayoutParams(BitmapUtil.dip2px(context,6),BitmapUtil.dip2px(context,6));
        paramsBall3.setMargins(flWidth,flWidth,0,0);
        ball_framelayout.addView(ball3,paramsBall3);

        ball1 = new View(context);
        ball1.setBackgroundResource(R.drawable.white_border);
        FrameLayout.LayoutParams paramsBall1 =  new FrameLayout.LayoutParams(BitmapUtil.dip2px(context,8),BitmapUtil.dip2px(context,8));
        paramsBall1.setMargins(flWidth,flWidth,0,0);
        ball_framelayout.addView(ball1,paramsBall1);


        textRightTV = new TextView(context);
        textRightTV.setTextColor(context.getResources().getColor(R.color.white));
        textRightTV.setBackgroundResource(R.drawable.label_bg2);
        textRightTV.setGravity(CENTER_VERTICAL);
        RelativeLayout.LayoutParams paramsRight = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,BitmapUtil.dip2px(context,25)) ;
        paramsRight.addRule(CENTER_VERTICAL);
        paramsRight.addRule(RIGHT_OF,ball_framelayout.getId());
        paramsRight.setMargins(-BitmapUtil.dip2px(context,5),0,0,0);
        this.addView(textRightTV,paramsRight);

        this.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public LabelTextView(Context context, AttributeSet attrs){
        super(context,attrs);
     
    }

    private void setAnimation(){
//        ball1 = (View) findViewById(R.id.ball1);
//        ball2 = (View) findViewById(R.id.ball2);
//        ball3 = (View) findViewById(R.id.ball3);
        // black1

//        Animation black1_alpha =
//                AnimationUtils.loadAnimation(mContext, R.anim.black1_alpha);
//
//        Animation black1_1 =
//                AnimationUtils.loadAnimation(mContext, R.anim.black1_1);
//
//
//        //  black2
//        Animation black2_1=
//                AnimationUtils.loadAnimation(mContext, R.anim.black2_1);
//
//        Animation black2_alpha=
//                AnimationUtils.loadAnimation(mContext, R.anim.black2_alpha);
//        //  white
//        Animation white_anim1 =
//                AnimationUtils.loadAnimation(mContext,	 R.anim.white_anim1);
//        Animation white_anim2 =
//                AnimationUtils.loadAnimation(mContext,
//                        R.anim.white_anim2);
//
//        Animation white_anim3 =
//                AnimationUtils.loadAnimation(mContext,
//                        R.anim.white_anim3);


        final AnimationSet set = new AnimationSet(true);


        set.addAnimation(CuteApplication.white_anim1);
        set.addAnimation(CuteApplication.white_anim2);
        set.addAnimation(CuteApplication.white_anim3);

        set.setRepeatCount(Animation.INFINITE);
        set.setRepeatMode(Animation.RESTART);


        ball1.setAnimation(set);

        final AnimationSet set2 = new AnimationSet(true);


        set2.addAnimation(CuteApplication.black1_1);
        set2.addAnimation(CuteApplication.black1_alpha);
        set2.setRepeatCount(Animation.INFINITE);
        set2.setRepeatMode(Animation.RESTART);
        ball2.setAnimation(set2);

        final AnimationSet set3 = new AnimationSet(true);

        set3.addAnimation(CuteApplication.black2_1);
        set3.addAnimation(CuteApplication.black2_alpha);
        set3.setRepeatCount(Animation.INFINITE);
        set3.setRepeatMode(Animation.RESTART);
        ball3.setAnimation(set3);


        ball1.startAnimation(set);
        ball2.startAnimation(set2);
        ball3.startAnimation(set3);
        CuteApplication.black2_alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ball1.startAnimation(set);
                ball2.startAnimation(set2);
                ball3.startAnimation(set3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void initViews(AttributeSet attrs) {
        //TypedArray是一个数组容器
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.LabeltextView);
        text = array.getString(R.styleable.LabeltextView_text);
        textRightTV.setText(text);
        textLeftTV.setText(text);
        label.setName(text);
        array.recycle();
    }

    public Label getLabel(){
        return label;
    }

    public int getLabelLength(){
        int length = 0;
        if(isLabelRight())
            length = BitmapUtil.dip2px(mContext,30) + CuteApplication.getWidth(textRightTV);

        else if(isLabelLeft())
            length = BitmapUtil.dip2px(mContext,30) + CuteApplication.getWidth(textLeftTV);

        LogUtil.v("textLeftTV",CuteApplication.getWidth(textRightTV) + textRightTV.getText().toString());
        LogUtil.v("textLeftTV",CuteApplication.getWidth(textLeftTV) + textLeftTV.getText().toString());
        return length;
    }
    public void setLabel(Label label){
        this.label = label;

    }

    public int getBallHeight(){
        return CuteApplication.getHeight(ball_framelayout);
    }

    public void setLabelText(String text)
    {
        ball_framelayout.setVisibility(VISIBLE);
        textRightTV.setText(text);
        textLeftTV.setText(text);
        label.setName(text);
    }

    public void setLeftLabel(){
        textRightTV.setVisibility(GONE);
        textLeftTV.setVisibility(VISIBLE);
        label.setDirection(1);
    }

    public void setRightLabel(){
        textRightTV.setVisibility(VISIBLE);
        textLeftTV.setVisibility(GONE);
        label.setDirection(2);
    }

    public boolean isLabelRight(){
        if(textRightTV.getVisibility() == VISIBLE)
            return true;
        else
            return false;
    }

    public boolean isLabelLeft(){
        if(textLeftTV.getVisibility() == VISIBLE)
            return true;
        else
            return false;
    }

    public void clear(){
        setLabelText("");
        textRightTV.setVisibility(GONE);
        textLeftTV.setVisibility(GONE);
        ball_framelayout.setVisibility(GONE);
    }


    @Override
    protected void finalize() throws Throwable {
        LogUtil.v("LabelTextView 释放",this.getLabel().getName());
        super.finalize();
    }
}
