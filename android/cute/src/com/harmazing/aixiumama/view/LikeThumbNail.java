package com.harmazing.aixiumama.view;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.UserFansActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;

/**
 * TODO: document your custom view class.
 */
public class LikeThumbNail extends LinearLayout {

    Button numBtn;
    Context mContext;
    LinearLayout ll;
    boolean matchScreenFlag = true;
    ImageView userImage;
    public LikeThumbNail(Context context) {
        super(context);
    }

    public LikeThumbNail(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setFocusable(true);
        this.setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.like_thumb_nail, this);
        init();
    }

    private void init() {
        ll = (LinearLayout)findViewById(R.id.ll);
        userImage = (ImageView)findViewById(R.id.user_icon);
        numBtn = new Button(mContext);
        numBtn.setBackground(mContext.getResources().getDrawable(R.drawable.white_border));
        numBtn.setTextColor(mContext.getResources().getColor(R.color.middle_grey));
        numBtn.setGravity(Gravity.CENTER);
        numBtn.setPadding(0,0,0,0);
        numBtn.setTextSize(13);
        numBtn.setText("0");
    }

    /**
     *  add thumbnail
     * @param url
     * @param cuteNum
     */
    public void addThumbnail(String url,int cuteNum, final int cuteID,int listID) {
        numBtn.setText(cuteNum+"");
        if (matchScreenFlag) {

                ImageView iconView = new ImageView(mContext);
                CuteApplication.downloadLittleCornImage(API.STICKERS + url, iconView);
                iconView.setOnClickListener(new ImageViewOnClickListener(iconView, listID));

                ll.addView(iconView);
                LinearLayout.LayoutParams params = new LayoutParams(iconView.getLayoutParams());
                params.height = BitmapUtil.dip2px(mContext,30);
                params.width = BitmapUtil.dip2px(mContext,30);
                params.setMargins(BitmapUtil.dip2px(mContext,5),0,0,0);
                iconView.setLayoutParams(params);
                matchScreenFlag = CuteApplication.getWidth(ll) + BitmapUtil.dip2px(mContext,50) < CuteApplication.screenWH[0];
                LogUtil.v("ll", CuteApplication.getWidth(ll) + BitmapUtil.dip2px(mContext, 50));

        }else{
            ll.addView(numBtn);
            LinearLayout.LayoutParams params = new LayoutParams(numBtn.getLayoutParams());
            params.height = BitmapUtil.dip2px(mContext,30);
            params.width = BitmapUtil.dip2px(mContext,30);
            params.setMargins(BitmapUtil.dip2px(mContext,5),0,0,0);
            numBtn.setLayoutParams(params);
            numBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, UserFansActivity.class);
                    intent.putExtra("cuteID", cuteID);
                    intent.putExtra("userName", "赞过的用户");
                    intent.putExtra("CutePersonThumbNail", false);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void addUserHead(){
        String dir = Environment.getExternalStorageDirectory().getPath();

        if(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + AppSharedPref.newInstance(mContext).getUserId() + ".png") != null) {
            CuteApplication.downloadLittleCornImage("file:///" + dir + "/Pictures/cute/" + AppSharedPref.newInstance(mContext).getUserId() + ".png", userImage);
//            userImage.setImageBitmap(BitmapUtil.getBitmap(dir + "/Pictures/cute/" + AppSharedPref.newInstance(mContext).getUserId() + ".png"));
            userImage.setVisibility(VISIBLE);
            numBtn.setText((Integer.parseInt(numBtn.getText().toString()) + 1) + "");
        }else{
            CuteApplication.downloadLittleCornImage(AppSharedPref.newInstance(mContext).getPicDir(),userImage);
            LogUtil.v("LikeThumbNail", "本地user头像读取失败");
        }
    }

    public void removeUserHead(int user){
        if(userImage.getVisibility() == GONE) {
            ll.removeView(ll.getChildAt(user));
        }else{
            userImage.setVisibility(GONE);
        }
        numBtn.setText((Integer.parseInt(numBtn.getText().toString()) - 1) + "");
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

    public void clear(){
//        ll.removeAllViews();
    }


}
