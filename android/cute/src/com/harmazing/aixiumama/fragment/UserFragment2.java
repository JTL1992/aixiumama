package com.harmazing.aixiumama.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.AttentionActivity;
import com.harmazing.aixiumama.activity.InviteFriendsAty;
import com.harmazing.aixiumama.activity.UserFansActivity;
import com.harmazing.aixiumama.activity.VisitHistoryActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.base.BaseFragment;
import com.harmazing.aixiumama.model.User;
import com.harmazing.aixiumama.parser.UserParser;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.CuteRankActivity;
import com.harmazing.aixiumama.activity.PresentActivity;
import com.harmazing.aixiumama.activity.UserSettingAty;
import com.harmazing.aixiumama.utils.RequestVo;
import com.pullrefreshview.PullToRefreshListView;

/**
 * Created by gyw
 *
 */
public class UserFragment2 extends BaseFragment {

    private String userName, userID, userBirthday;
    public static String userGender;
    public static String userLocation;
    public static String userDes;
    public static String userBabiesGender;
    private TextView follows_tv,fans_tv,name_tv,des_tv,cute_tv,favorites_tv,cute_num;
    public static TextView photosNumTV;
    private ImageView mumIcon,nineIV,threeIV,labelIV,presentIV;
    private Button addFriend;
    private String nameImage;
    private View nine,three,lable;
    private ImageButton settings;

    private LinearLayout photo_layout;
    private LinearLayout att_layout;
    private LinearLayout fans_layout;
    private LinearLayout visit_layout;

    private FrameLayout fl_container_user;

    private PullToRefreshListView lv_body_user2;
    private View handerView;

    private RequestVo userRequestVo;
    private DataCallback userCallback;

    private android.support.v4.app.FragmentTransaction ft;
    private FragmentManager fm;
    private BaseFragment userLeftFragment2;
    private BaseFragment userCenterFragment2;
    private BaseFragment userRightFragment2;

    @Override
    protected int setMyContentView() {
        return R.layout.fragment_header_user2;
    }

    @Override
    protected void initView() {
        follows_tv = (TextView)view.findViewById(R.id.follows);
        fans_tv = (TextView)view.findViewById(R.id.fans);
        name_tv = (TextView)view.findViewById(R.id.mum_name);
        des_tv = (TextView)view.findViewById(R.id.des);
        favorites_tv = (TextView)view.findViewById(R.id.favorites);
        cute_tv = (TextView)view.findViewById(R.id.cute_info);
        mumIcon = (ImageView)view.findViewById(R.id.user_icon);
        photosNumTV = (TextView)view.findViewById(R.id.photos_num);
        photosNumTV.setText(AppSharedPref.newInstance(getActivity()).getDataInt("UserPhotoNum")+"");
        addFriend = (Button)view.findViewById(R.id.add_friend);
        nineIV = (ImageView)view.findViewById(R.id.nine);
        threeIV = (ImageView)view.findViewById(R.id.three);
        labelIV = (ImageView)view.findViewById(R.id.label);
        nine = view.findViewById(R.id.nine_line);
        three = view.findViewById(R.id.three_line);
        lable = view.findViewById(R.id.label_line);
        cute_num = (TextView) view.findViewById(R.id.cute_num);
         //点击设置，进入个人中心
        settings = (ImageButton) view.findViewById(R.id.settings);

        //让页面顶部的设置页面获取焦点,不让页面下滑
        settings.requestFocus();

        // 点击照片
        photo_layout = (LinearLayout) view.findViewById(R.id.photo_layout);

         //点击 关注
        att_layout = (LinearLayout) view.findViewById(R.id.att_layout);
        //  点击粉丝
        fans_layout = (LinearLayout) view.findViewById(R.id.fans_layout);
        //点击来访
        visit_layout = (LinearLayout) view.findViewById(R.id.visit_layout);

        fl_container_user = (FrameLayout) view.findViewById(R.id.fl_container_user);

        presentIV = (ImageView) view.findViewById(R.id.present);
        fm = getFragmentManager();
    }

    @Override
    protected void initRequestVo() {

        String requestUrl = API.GET_USER + AppSharedPref.newInstance(getActivity()).getUserId() + "/?format=json";
        UserParser userParser = new UserParser();
        userRequestVo = new RequestVo(requestUrl, context, null, userParser);
    }

    @Override
    protected void initCallBall() {
        userCallback = new DataCallback<User>() {
            @Override
            public void processData(User data) {
                if(data != null) {
                    initHeaderView(data);
                }
                //关闭提示框
                closeProgressDialog();
            }
        };
    }

    private void initHeaderView(User user) {
        String gender = null;
//        addFriend.setVisibility(View.GONE);
        view.findViewById(R.id.back).setVisibility(View.GONE);

        fans_tv.setText(user.fans_count);
        follows_tv.setText(user.follows_count);
        name_tv.setText(user.name);
        userName = user.name;
        userBirthday = AppSharedPref.newInstance(getActivity()).getBirthday();
        if (!user.babies.isEmpty()) {
            userBabiesGender = user.babies.get(0).gender;
            if (userBabiesGender.equals("1"))
                gender = "男";
            else if (userBabiesGender.equals("0"))
                gender = "未知";
            else
                gender = "女";
        }
        userGender = user.gender;
        userLocation = user.location;
        userDes = user.description;

        des_tv.setText(CuteApplication.date2Age(userBirthday)+" "+gender+"\n"+userDes);
        favorites_tv.setText(user.visited_count);
        cute_num.setText(user.liked_count);
        cute_tv.setText(String.format(context.getResources().getString(R.string.cute_info), user.rank));
        nameImage =user.image;
        CuteApplication.downloadCornImage(API.STICKERS +nameImage,mumIcon);

    }

    @Override
    protected void initData() {
        userID = AppSharedPref.newInstance(getActivity()).getUserId();
        getDataFromServer(userRequestVo, userCallback);
        showFirstView();
    }

    @Override
    protected void setListeners() {
        nineIV.setOnClickListener(this);
        threeIV.setOnClickListener(this);
        labelIV.setOnClickListener(this);

        //设置
        settings.setOnClickListener(this);

        //照片
        photo_layout.setOnClickListener(this);
        //关注
        att_layout.setOnClickListener(this);
        //粉丝
        fans_layout.setOnClickListener(this);
        //来访
        visit_layout.setOnClickListener(this);
        //礼品
        presentIV.setOnClickListener(this);
        // cute排名
        cute_num.setOnClickListener(this);
        cute_tv.setOnClickListener(this);
        //邀请好友
        addFriend.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        ft = fm.beginTransaction();

        nineIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico3));
        threeIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico2));
        labelIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico1));


        Intent intent = null;

        switch (view.getId()) {
            case R.id.nine:
                nineIV.setImageResource(R.drawable.gr_ico3_hover);
                userLeftFragment2 = new UserLeftFragment2(userID);
                ft.replace(R.id.fl_container_user, userLeftFragment2);
                nine.setVisibility(View.VISIBLE);
                three.setVisibility(View.INVISIBLE);
                lable.setVisibility(View.INVISIBLE);
                break;
            case R.id.three:
                threeIV.setImageResource(R.drawable.gr_ico2_hover);
//                userCenterFragment2 = new UserCenterFragment2(userID);
                Fragment userCenterFragment2 = new userCenterFragment(userID);
                ft.replace(R.id.fl_container_user,userCenterFragment2);
                nine.setVisibility(View.INVISIBLE);
                three.setVisibility(View.VISIBLE);
                lable.setVisibility(View.INVISIBLE);
                break;
            case R.id.label:
                labelIV.setImageResource(R.drawable.gr_ico1_hover);
                userRightFragment2 = new UserRightFragment2(userID);
                ft.replace(R.id.fl_container_user,userRightFragment2);
                nine.setVisibility(View.INVISIBLE);
                three.setVisibility(View.INVISIBLE);
                lable.setVisibility(View.VISIBLE);
                break;
            case R.id.settings: //进入个人中心
                intent = new Intent(getActivity(), UserSettingAty.class);
                intent.putExtra("birthday",userBirthday);
                intent.putExtra("name",userName);
                intent.putExtra("image",nameImage);
                startActivity(intent);
                break;
            case R.id.att_layout:   //关注
                intent = new Intent(getActivity(), AttentionActivity.class);
                intent.putExtra("userID",Integer.parseInt(AppSharedPref.newInstance(getActivity()).getUserId()));
                intent.putExtra("userName",userName);
                startActivity(intent);
                break;
            case R.id.fans_layout:      //粉丝
                intent = new Intent(getActivity(), UserFansActivity.class);
                intent.putExtra("userID",Integer.parseInt(AppSharedPref.newInstance(getActivity()).getUserId()));
                intent.putExtra("userName",userName);
                startActivity(intent);
                break;
            case R.id.visit_layout:      //来访
                intent = new Intent(getActivity(),VisitHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.present:
                intent = new Intent(getActivity(),PresentActivity.class);
                startActivity(intent);
                break;
            case R.id.cute_num:
                intent = new Intent(getActivity(), CuteRankActivity.class);
                startActivity(intent);
                break;
            case R.id.cute_info:
                intent = new Intent(getActivity(), CuteRankActivity.class);
                startActivity(intent);
                break;
            case  R.id.add_friend:
                intent = new Intent(getActivity(), InviteFriendsAty.class);
                startActivity(intent);
                break;
        }
        ft.commit();
    }

    /**
     * 默认加载第一个页面
     */
    private void showFirstView() {
        nineIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico3_hover));
        threeIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico2));
        labelIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico1));
        nine.setVisibility(View.VISIBLE);
        three.setVisibility(View.INVISIBLE);
        lable.setVisibility(View.INVISIBLE);
        ft = fm.beginTransaction();
        userLeftFragment2 = new UserLeftFragment2(userID);
        ft.replace(R.id.fl_container_user, userLeftFragment2);
        ft.commit();
    }
}