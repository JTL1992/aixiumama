package com.harmazing.aixiumama.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.UserFansActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.AttentionActivity;
import com.harmazing.aixiumama.activity.UserSettingAty;
import com.harmazing.aixiumama.activity.VisitHistoryActivity;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.BorderScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liujinghui on 11/2/14.
 */
public class UserFragment extends Fragment {

    public static ViewPager mViewPager;
    String userName,userID;
    TextView follows_tv,fans_tv,name_tv,des_tv,cute_tv,favorites_tv;
    public static TextView photosNumTV;
    ImageView mumIcon,nineIV,threeIV,labelIV;
    Button addFriend;
    String nameImage;
    public static BorderScrollView scrollview;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userID = AppSharedPref.newInstance(getActivity()).getUserId();
        Fragment msgFragment = new userLeftFragment(userID);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container_user,msgFragment);
        transaction.commit();
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scrollview = (BorderScrollView)getView().findViewById(R.id.scrollview);
        follows_tv = (TextView)getView().findViewById(R.id.follows);
        fans_tv = (TextView)getView().findViewById(R.id.fans);
        name_tv = (TextView)getView().findViewById(R.id.mum_name);
        des_tv = (TextView)getView().findViewById(R.id.des);
        favorites_tv = (TextView)getView().findViewById(R.id.favorites);
        cute_tv = (TextView)getView().findViewById(R.id.cute_info);
        mumIcon = (ImageView)getView().findViewById(R.id.user_icon);
        photosNumTV = (TextView)getView().findViewById(R.id.photos_num);
        photosNumTV.setText(AppSharedPref.newInstance(getActivity()).getDataInt("UserPhotoNum")+"");
        addFriend = (Button)getView().findViewById(R.id.add_friend);
        nineIV = (ImageView)getView().findViewById(R.id.nine);
        threeIV = (ImageView)getView().findViewById(R.id.three);
        labelIV = (ImageView)getView().findViewById(R.id.label);

        //  因为是自己查看个人主页 不显示加好友按钮
        addFriend.setVisibility(View.GONE);
        getView().findViewById(R.id.back).setVisibility(View.GONE);

        HttpUtil.get(API.GET_USER + AppSharedPref.newInstance(getActivity()).getUserId() + "/?format=json", new JsonHttpResponseHandler() {

            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            @Override
            public void onStart() {
                progressDialog.setMessage("正在加载");
                progressDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    fans_tv.setText(response.getInt("fans_count")+"");
                    follows_tv.setText(response.getInt("follows_count")+"");
                    name_tv.setText(response.getString("name"));
                    userName = response.getString("name");
                    des_tv.setText(response.getString("description"));
                    favorites_tv.setText(response.getString("visited_count"));
                    cute_tv.setText(String.format(getActivity().getResources().getString(R.string.cute_info),response.getInt("liked_count")+"",response.getInt("rank")+""));
                    nameImage = response.getString("image");
                    CuteApplication.downloadCornImage(API.STICKERS + response.getString("image"), mumIcon);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtil.show(getActivity(),"获取资料失败");
                super.onFailure(statusCode, headers, responseString, throwable);
            }
});

        getView().findViewById(R.id.nine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nineIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico3_hover));
                threeIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico2));
                labelIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico1));
                Fragment msgFragment = new userLeftFragment(userID);
                replaceFragment(msgFragment);
            }
        });

        getView().findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nineIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico3));
                threeIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico2_hover));
                labelIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico1));
                Fragment msgFragment = new userCenterFragment(userID);
                replaceFragment(msgFragment);

            }
        });

        getView().findViewById(R.id.label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nineIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico3));
                threeIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico2));
                labelIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico1_hover));
                Fragment msgFragment = new userRightFragment(userID);
                replaceFragment(msgFragment);
            }
        });


        /**
         * 点击照片
         */
        getView().findViewById(R.id.photo_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /**
         * 点击 关注
         */
        getView().findViewById(R.id.att_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AttentionActivity.class);
                intent.putExtra("userID",Integer.parseInt(AppSharedPref.newInstance(getActivity()).getUserId()));
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        /**
         *  点击粉丝
         */
        getView().findViewById(R.id.fans_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserFansActivity.class);
                intent.putExtra("userID",Integer.parseInt(AppSharedPref.newInstance(getActivity()).getUserId()));
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        /**
         * 点击收草
         */
        getView().findViewById(R.id.visit_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),VisitHistoryActivity.class);
                startActivity(intent);

            }
        });
        /**
         *  点击设置，进入个人中心
         */
        getView().findViewById(R.id.settings).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           Intent intent = new Intent(getActivity(), UserSettingAty.class);
                intent.putExtra("name",userName);
                intent.putExtra("image",nameImage);
           startActivity(intent);
//                Toast.makeText(getActivity(),"正在努力coding。。。",Toast.LENGTH_LONG).show();


            }
        });
    }

    private void replaceFragment(Fragment newFragment) {

        FragmentTransaction trasection =
                getFragmentManager().beginTransaction();
        if(!newFragment.isAdded()){
            try{
                //FragmentTransaction trasection =
                getFragmentManager().beginTransaction();
                trasection.replace(R.id.fl_container_user, newFragment);
                trasection.addToBackStack(null);
                trasection.commit();

            }catch (Exception e) {
                e.printStackTrace();
            }
        }else
            trasection.show(newFragment);
    }

}