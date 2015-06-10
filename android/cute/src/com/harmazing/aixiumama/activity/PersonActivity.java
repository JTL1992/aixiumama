package com.harmazing.aixiumama.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.base.BaseFragment;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.fragment.UserLeftFragment2;
import com.harmazing.aixiumama.fragment.UserRightFragment2;
import com.harmazing.aixiumama.fragment.userCenterFragment;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.BorderScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *  个人主页
 *  参数  person_id
 */
public class PersonActivity extends FragmentActivity implements View.OnClickListener {
    private BaseFragment userLeftFragment2;
    private BaseFragment userRightFragment2;
    private FrameLayout fl_container_user;
    private FragmentManager fm;
    private android.support.v4.app.FragmentTransaction ft;
    private View nine_line,three_line,label_line;
    boolean isFriend;
    TextView follows_tv,fans_tv,name_tv,des_tv,fav_tv,cute_tv,favorite_tv,cute_num;
    public static TextView photosNumTV;
    ImageView mumIcon,nineIV,threeIV,labelIV;
    Button addFriend;
    int userID;
    HashMap<Integer,Integer> block;
    String userName;
    RelativeLayout share,userRight;
    SlidingDrawer mSlidingDrawer;
    public static BorderScrollView scrollview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user);
        block = new HashMap<Integer, Integer>();
        follows_tv = (TextView) findViewById(R.id.follows);
        fans_tv = (TextView) findViewById(R.id.fans);
        name_tv = (TextView) findViewById(R.id.mum_name);
        des_tv = (TextView) findViewById(R.id.des);
        fav_tv = (TextView) findViewById(R.id.favorites);
        favorite_tv = (TextView) findViewById(R.id.favorite_visited);
        cute_tv = (TextView) findViewById(R.id.cute_info);
        mumIcon = (ImageView) findViewById(R.id.user_icon);
        photosNumTV = (TextView) findViewById(R.id.photos_num);
        addFriend = (Button)findViewById(R.id.add_friend);
        nineIV = (ImageView) findViewById(R.id.nine);
        threeIV = (ImageView) findViewById(R.id.three);
        labelIV = (ImageView) findViewById(R.id.label);
        nine_line = findViewById(R.id.nine_line);
        three_line = findViewById(R.id.three_line);
        label_line = findViewById(R.id.label_line);
        scrollview = (BorderScrollView) findViewById(R.id.scrollview);
        userID = getIntent().getIntExtra("person_id",0);
        cute_num = (TextView) findViewById(R.id.cute_num);
        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.sliding);
        share = (RelativeLayout) findViewById(R.id.sliding).findViewById(R.id.share);
        userRight = (RelativeLayout) findViewById(R.id.sliding).findViewById(R.id.user_right);
        findViewById(R.id.visit_layout).setVisibility(View.GONE);
        findViewById(R.id.view_ver_line).setVisibility(View.GONE);


        fl_container_user = (FrameLayout) findViewById(R.id.fl_container_user);
        fm = getSupportFragmentManager();
        setListeners();
        showFirstView();

        if(AppSharedPref.newInstance(getApplicationContext()).getUserId().equals(userID+"")){
            photosNumTV.setText(AppSharedPref.newInstance(getApplicationContext()).getDataInt("UserPhotoNum")+"");
        }

        LogUtil.v("userID",userID+"");
        RequestParams params = new RequestParams();
        params.put("block_user",userID);
        HttpUtil.get(API.BLOCK+"?",params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("user_right",response.toString());
                try {
                    JSONArray result = response.getJSONArray("results");
                    for (int i = 0; i < result.length();i++){
                        JSONObject jsonObject = result.getJSONObject(i);
                     int t = jsonObject.getInt("type");
                     int j = jsonObject.getInt("id");
                       block.put(t,j);
                        LogUtil.v("map",block.toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /**
         * 判断好友状态
         */
        if(!AppSharedPref.newInstance(getApplicationContext()).getUserId().equals(userID+"")) {
            HttpUtil.get(API.ADD_FRIENDS + "?user=" + AppSharedPref.newInstance(getApplicationContext()).getUserId() + "&follow=" + userID, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        if (response.getInt("count") == 1) {
                            isFriend = true;
                            addFriend.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_grey_corn));
                            addFriend.setText("已关注");
                        } else {
                            isFriend = false;
                            addFriend.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_pink));
                            addFriend.setText("关注");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    super.onSuccess(statusCode, headers, response);
                }
            });

        /**
         *   关注
         */
        addFriend.setVisibility(View.VISIBLE);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(! isFriend){
                    ToastUtil.show(getApplicationContext(), "关注成功");
                    // 变红
                    addFriend.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_grey_corn));
                    addFriend.setText("已关注");
                    isFriend =true;
                }else{
                    addFriend.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_pink));
                    addFriend.setText("关注");
                    ToastUtil.show(getApplicationContext(), "取消关注");
                    isFriend =false;
                }

                RequestParams params = new RequestParams();
                params.put("user",Integer.parseInt(AppSharedPref.newInstance(getApplicationContext()).getUserId()));
                params.put("follow",userID);
                LogUtil.v("关注",userID+"");
                HttpUtil.post(API.ADD_FRIENDS,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        LogUtil.v("关注接口",response.toString());
                        if(statusCode == 201) {
                            LogUtil.v("关注成功", "");
                            Toast.makeText(getApplicationContext(),"关注成功",Toast.LENGTH_LONG).show();

                        }else if(statusCode == 204){
                            LogUtil.v("取消关注","");
                            Toast.makeText(getApplicationContext(),"取消关注",Toast.LENGTH_LONG).show();
                        }

                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        LogUtil.v("error code",""+statusCode);
                    }
                });
            }
        });
            /**
             * 对查看用户的设置
             */

            findViewById(R.id.person_settings).setVisibility(View.VISIBLE);
            findViewById(R.id.person_settings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final TextView avoid = (TextView)mSlidingDrawer.findViewById(R.id.avoid);
                    final TextView notShow = (TextView) mSlidingDrawer.findViewById(R.id.noshare);
                    if (block.isEmpty()) {
                        avoid.setText("不看他的分享");
                        notShow.setText("不让他看我的分享");
                    }
                    else{
                        for (int i : block.keySet()){
                            if (i == 1){
                                avoid.setText("取消不看他的分享");
                            }
                             if (i == 2){
                                 notShow.setText("取消不让他看我的分享");
                             }

                        }

                    }
                    share.setVisibility(View.INVISIBLE);
                    userRight.setVisibility(View.VISIBLE);
                    mSlidingDrawer.animateOpen();
//                    final AlertDialog alert = new AlertDialog.Builder(PersonActivity.this).create();
//
//                    alert.show();
//                    alert.getWindow().setGravity(Gravity.CENTER|Gravity.TOP);
//                    alert.getWindow().setLayout(
//                            android.view.WindowManager.LayoutParams.WRAP_CONTENT,
//                            android.view.WindowManager.LayoutParams.WRAP_CONTENT);
//                    alert.getWindow().setContentView(R.layout.peron_settings_dialog);
                    /**
                     * 不看他的分享(取消)
                     */
                    userRight.findViewById(R.id.avoid).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!block.keySet().contains(1)){
                                RequestParams params = new RequestParams();
                            params.put("user", AppSharedPref.newInstance(getApplicationContext()).getUserId());
                            params.put("type", 1);
                            params.put("block_user", userID);
                            HttpUtil.post(API.BLOCK, params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    LogUtil.v("not show cute to me", response.toString() + statusCode);
                                    if (statusCode == 201) {
                                        ToastUtil.show(getApplicationContext(), "操作成功");
                                        mSlidingDrawer.animateClose();
                                        share.setVisibility(View.VISIBLE);
                                        userRight.setVisibility(View.INVISIBLE);
                                        avoid.setText("取消不看他的分享");
                                        try {
                                            block.put(1,response.getInt("id"));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    } else {
                                        ToastUtil.show(getApplicationContext(), "操作失败");
                                        mSlidingDrawer.animateClose();
                                        share.setVisibility(View.VISIBLE);
                                        userRight.setVisibility(View.INVISIBLE);
                                    }

                                    super.onSuccess(statusCode, headers, response);
                                }


                            });


                        }
                            else{
                                LogUtil.v("block.get(1)",block.get(1)+"");
                                HttpUtil.delete(API.BLOCK+block.get(1)+"/",new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        LogUtil.v("1111statusCode+ response",response.toString()+statusCode);
                                        Toast.makeText(getApplicationContext(),"取消成功！",Toast.LENGTH_LONG).show();
                                        avoid.setText("不看他的分享");
                                        block.remove(1);
                                        mSlidingDrawer.animateClose();
                                        share.setVisibility(View.VISIBLE);
                                        userRight.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                    }
                    });
                    /**
                     * 不让他看我的分享（取消）
                     */
                    mSlidingDrawer.findViewById(R.id.noshare).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!block.keySet().contains(2)) {
                                RequestParams params = new RequestParams();
                                params.put("user", AppSharedPref.newInstance(getApplicationContext()).getUserId());
                                params.put("type", 2);
                                params.put("block_user", userID);
                                HttpUtil.post(API.BLOCK, params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        LogUtil.v("not show cute to him", response.toString());
                                        if (statusCode == 201) {
                                            ToastUtil.show(getApplicationContext(), "操作成功");
                                            mSlidingDrawer.animateClose();
                                            share.setVisibility(View.VISIBLE);
                                            userRight.setVisibility(View.INVISIBLE);
                                            notShow.setText("取消不让他看我的分享");
                                            try {
                                                block.put(2,response.getInt("id"));
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        } else {
                                            ToastUtil.show(getApplicationContext(), "操作失败");
                                            mSlidingDrawer.animateClose();
                                            share.setVisibility(View.VISIBLE);
                                            userRight.setVisibility(View.INVISIBLE);
                                        }

                                        super.onSuccess(statusCode, headers, response);
                                    }
                                });

                            }
                            else{
                                HttpUtil.delete(API.BLOCK+block.get(2)+"/",new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        LogUtil.v("222statusCoderesponse",response.toString()+statusCode);
                                        Toast.makeText(getApplicationContext(),"取消成功！",Toast.LENGTH_LONG).show();
                                        mSlidingDrawer.animateClose();
                                        notShow.setText("不让他看我的分享");
                                        block.remove(2);
                                        share.setVisibility(View.VISIBLE);
                                        userRight.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }

                    });
                    /**
                     * 举报
                     */
                    mSlidingDrawer.findViewById(R.id.report).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RequestParams params = new RequestParams();
                            params.put("user",AppSharedPref.newInstance(getApplicationContext()).getUserId());
                            params.put("report_user",userID);
                            HttpUtil.post(API.REPORT,params,new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                    if(statusCode == 200){
                                        ToastUtil.show(getApplicationContext(),"操作成功");
                                        mSlidingDrawer.animateClose();
                                        share.setVisibility(View.VISIBLE);
                                        userRight.setVisibility(View.INVISIBLE);
                                    }else{
                                        ToastUtil.show(getApplicationContext(),"操作失败");
                                        mSlidingDrawer.animateClose();
                                        share.setVisibility(View.VISIBLE);
                                        userRight.setVisibility(View.INVISIBLE);
                                    }

                                    super.onSuccess(statusCode, headers, response);
                                }
                            });

                        }

                    });
                    /**
                     * 取消
                     */
                    mSlidingDrawer.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSlidingDrawer.animateClose();
                            share.setVisibility(View.VISIBLE);
                            userRight.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });
        }else {
            addFriend.setVisibility(View.GONE);

        }
        //  获取要展示用户的id

        HttpUtil.get(API.GET_USER + userID + "/", new JsonHttpResponseHandler() {

            ProgressDialog progressDialog = new ProgressDialog(PersonActivity.this);
            @Override
            public void onStart() {
                progressDialog.setMessage("正在加载");
                progressDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray baby = response.getJSONArray("babies");

                    //photosNumTV.setText("");
                    fans_tv.setText(response.getInt("fans_count") + "");
                    follows_tv.setText(response.getInt("follows_count") + "");
                    name_tv.setText(response.getString("name"));
                    userName = response.getString("name");
                    des_tv.setText(CuteApplication.calculateBabyInfo(baby.getJSONObject(0))+"\n"+response.getString("description"));
                    fav_tv.setText(response.getInt("favorite_cute_count") + "");
                    cute_num.setText(response.getInt("liked_count")+"");
                    cute_tv.setText(String.format(getApplication().getResources().getString(R.string.cute_info), response.getInt("rank") + ""));
                    CuteApplication.downloadCornImage(API.STICKERS + response.getString("image"), mumIcon);

                    /**
                     * isFriend = ???
                     */
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtil.show(getApplicationContext(), "获取资料失败");
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });



        /**
         *  点击关注
         */
        findViewById(R.id.att_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, AttentionActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        /**
         *  点击粉丝
         */
        findViewById(R.id.fans_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, UserFansActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        /**
         * 点击收藏
         */
        findViewById(R.id.visit_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        CuteApplication.leftHeight = 0;
        CuteApplication.centerHeight = 0;
        CuteApplication.rightHeight = 0;

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        CuteApplication.leftHeight = 0;
        CuteApplication.centerHeight = 0;
        CuteApplication.rightHeight = 0;
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (mSlidingDrawer.isOpened()){
                mSlidingDrawer.animateClose();
                return false;
            }
            else
             return super.onKeyDown(keyCode, event);
        }
        else
        return super.onKeyDown(keyCode, event);
    }


    private void setListeners() {
        nineIV.setOnClickListener(this);
        threeIV.setOnClickListener(this);
        labelIV.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        ft = fm.beginTransaction();

        nineIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico3));
        threeIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico2));
        labelIV.setImageDrawable(getResources().getDrawable(R.drawable.gr_ico1));

        switch (view.getId()) {
            case R.id.nine:
                nineIV.setImageResource(R.drawable.gr_ico3_hover);
                userLeftFragment2 = new UserLeftFragment2(userID+"");
                ft.replace(R.id.fl_container_user, userLeftFragment2);
                nine_line.setVisibility(View.VISIBLE);
                three_line.setVisibility(View.INVISIBLE);
                label_line.setVisibility(View.INVISIBLE);
                break;
            case R.id.three:
                threeIV.setImageResource(R.drawable.gr_ico2_hover);
                //userCenterFragment2 = new UserCenterFragment2(userID);
                Fragment userCenterFragment2 = new userCenterFragment(userID+"");
                ft.replace(R.id.fl_container_user, userCenterFragment2);
                nine_line.setVisibility(View.INVISIBLE);
                three_line.setVisibility(View.VISIBLE);
                label_line.setVisibility(View.INVISIBLE);
                break;
            case R.id.label:
                labelIV.setImageResource(R.drawable.gr_ico1_hover);
                userRightFragment2 = new UserRightFragment2(userID+"");
                ft.replace(R.id.fl_container_user, userRightFragment2);
                nine_line.setVisibility(View.INVISIBLE);
                three_line.setVisibility(View.INVISIBLE);
                label_line.setVisibility(View.VISIBLE);
                break;
        }
        ft.commit();
    }

    /**
     * 默认加载第一个页面
     */
    private void showFirstView() {
        ft = fm.beginTransaction();
        userLeftFragment2 = new UserLeftFragment2(userID+"");
        ft.replace(R.id.fl_container_user, userLeftFragment2);
        ft.commit();
    }


    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
