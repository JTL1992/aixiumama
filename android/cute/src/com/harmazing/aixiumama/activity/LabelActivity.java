package com.harmazing.aixiumama.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.adapter.LabelImageAdapter;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.GridViewUtility;
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


/**
 *  标签详情页
 *  参数：  labelID (String)
 */
public class LabelActivity extends FragmentActivity  {

    boolean isLogo,likedState;
    String labelID;
    static String TAG = "LabelActivty";
    ImageView iconIV;
    TextView labelDesTV,labelNameTV;
    Button fansNum,attLabelBtn,mostBtn,newBtn;
    GridView labelGridView;
    View bottomLine;
    int funNum;
    JSONArray nextArray;
    BorderScrollView borderScrollView;
 
    LabelImageAdapter labelImageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

    
        labelGridView = (GridView)findViewById(R.id.gv_label_container);
        bottomLine = (View)findViewById(R.id.v_label_bottomline);
        borderScrollView = (BorderScrollView)findViewById(R.id.borderScrollView);
        iconIV = (ImageView)findViewById(R.id.label_icon);
        labelDesTV = (TextView)findViewById(R.id.label_text);
        labelNameTV = (TextView)findViewById(R.id.label_name);
        fansNum = (Button)findViewById(R.id.fans_num);
        attLabelBtn = (Button)findViewById(R.id.att_label);

        mostBtn = (Button)findViewById(R.id.the_most);
        newBtn = (Button)findViewById(R.id.the_new);

        labelID = getIntent().getStringExtra("labelID");

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.label_icon).setFocusable(true);
        findViewById(R.id.label_icon).setFocusableInTouchMode(true);
        findViewById(R.id.label_icon).requestFocus();

        getLastLabel();
        /**
         *  验证这个Label 是不是 认证商标
         */
        HttpUtil.get(API.GET_LABELS + labelID +"/" ,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                LogUtil.v("lable Url",API.GET_LABELS + labelID +"/");
                try {

                    final String name = response.getString("name");
                    labelNameTV.setText(name);

                    if(response.getBoolean("certificate")){
                        isLogo = true;
                         /*
                            获取商标基本信息
                         */
                        try {
                            CuteApplication.downloadCornImage(API.STICKERS + response.getString("image"), iconIV);
                            labelDesTV.setText(response.getString("description"));

                            fansNum.setText("已有" + response.getInt("follow_count") + "粉丝");
                            funNum = response.getInt("follow_count");
                            /**
                             *  进入粉丝页面
                             */
                            fansNum.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(LabelActivity.this,LabelFansActivity.class);
                                    intent.putExtra("labelID",Integer.parseInt(labelID));
                                    intent.putExtra("labelName",name);
                                    startActivity(intent);
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else
                        isLogo = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  注册商标
                if(isLogo) {
                    findViewById(R.id.famous_label).setVisibility(View.VISIBLE);
                    /**
                     *  获取用户关注商标状态
                     */
                    HttpUtil.get(API.FOLLOW_LABELS +"?follow=" + labelID + "&user=" + AppSharedPref.newInstance(getApplicationContext()).getUserId(),new JsonHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                if(response.getInt("count") == 1){
                                    //  关注状态
                                    attLabelBtn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_grey_corn));
                                    attLabelBtn.setText("已关注");
                                    likedState =true;
                                }else{

                                    attLabelBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_pink));
                                    attLabelBtn.setText("关注");
                                    likedState =false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            /**
                             *  点击关注按钮响应
                             */
                            findViewById(R.id.att_label).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!likedState) {
                                        ToastUtil.show(getApplicationContext(), "关注！");
                                        funNum++;
                                        fansNum.setText("已有" + funNum + "粉丝");
                                        // 变红
                                        attLabelBtn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_grey_corn));
                                        attLabelBtn.setText("已关注");
                                        likedState = true;
                                    } else {
                                        funNum--;
                                        fansNum.setText("已有" + funNum + "粉丝");
                                        attLabelBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_pink));
                                        attLabelBtn.setText("关注");
                                        ToastUtil.show(getApplicationContext(), "取消关注");
                                        likedState = false;
                                    }

                                    RequestParams params = new RequestParams();
                                    params.put("user", Integer.parseInt(AppSharedPref.newInstance(getApplicationContext()).getUserId()));
                                    params.put("follow",Integer.parseInt(labelID));
                                    HttpUtil.post(API.FOLLOW_LABELS, params, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            if (statusCode == 201) {
                                                LogUtil.v(TAG, "关注成功");

                                            } else if (statusCode == 204) {
                                                LogUtil.v(TAG, "取消关注");
                                            }
                                            super.onSuccess(statusCode, headers, response);
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                            ToastUtil.show(LabelActivity.this, "网络异常");
                                            super.onFailure(statusCode, headers, responseString, throwable);
                                        }
                                    });

                                }
                            });
                            super.onSuccess(statusCode, headers, response);
                        }
                    });



                }else   //  非注册商标
                    findViewById(R.id.famous_label).setVisibility(View.GONE);


                mostBtn.setTextColor(getResources().getColor(R.color.pink));
                mostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mostBtn.setTextColor(getResources().getColor(R.color.pink));
                        newBtn.setTextColor(getResources().getColor(R.color.black));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bottomLine.getLayoutParams());
                        params.setMargins(CuteApplication.getScreenHW(getApplicationContext())[0]/4 - BitmapUtil.dip2px(getApplicationContext(),20),0,0,0);
                        bottomLine.setLayoutParams(params);


                        findViewById(R.id.label_icon).setFocusable(true);
                        findViewById(R.id.label_icon).setFocusableInTouchMode(true);
                        findViewById(R.id.label_icon).requestFocus();

                        getLastLabel();
                    }
                });

                newBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mostBtn.setTextColor(getResources().getColor(R.color.black));
                        newBtn.setTextColor(getResources().getColor(R.color.pink));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bottomLine.getLayoutParams());
                        params.setMargins(CuteApplication.getScreenHW(getApplicationContext())[0] * 3/4 - BitmapUtil.dip2px(getApplicationContext(),20),0,0,0);
                        bottomLine.setLayoutParams(params);
                        
                        findViewById(R.id.label_icon).setFocusable(true);
                        findViewById(R.id.label_icon).setFocusableInTouchMode(true);
                        findViewById(R.id.label_icon).requestFocus();

                        getCuteHot();
                    }
                });
                super.onSuccess(statusCode, headers, response);
            }
        });



        borderScrollView.setOnBorderListener(new BorderScrollView.OnBorderListener() {
            @Override
            public void onBottom() {
 
                    LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                    if (CuteApplication.nextPageUrl.length() > 4) {
                        HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {

                            @Override
                            public void onStart() {
                                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                                findViewById(R.id.no_more).setVisibility(View.GONE);
                                super.onStart();
                            }

                            @Override
                            public void onFinish() {
                                findViewById(R.id.loading).setVisibility(View.GONE);
                                super.onFinish();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    nextArray = new JSONArray(response.getString("results"));
                                    labelImageAdapter.addKingdArray(nextArray);
                                    GridViewUtility.setGridViewHeightByMySelf(labelGridView, BitmapUtil.dip2px(getApplicationContext(), 80));
                                    labelImageAdapter.notifyDataSetChanged();

                                    LogUtil.v("next", response.getString("next"));
                                    if (response.getString("next").length() > 10) {
                                        CuteApplication.nextPageUrl = response.getString("next");
                                    } else {
                                        LogUtil.v("next", "没有更多数据");
                                        CuteApplication.nextPageUrl = "";
                                        findViewById(R.id.no_more).setVisibility(View.VISIBLE);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                super.onSuccess(statusCode, headers, response);
                            }
                        });
                    } else {
                        LogUtil.v("next", "没有更多数据");
                        findViewById(R.id.no_more).setVisibility(View.VISIBLE);
                    }
            }

            @Override
            public void onTop() {
            }
        });
                }

    public void getCuteHot(){
        HttpUtil.get(API.CUTE_LABELS + "?label=" + labelID + "&key=hot", new JsonHttpResponseHandler() {
            ProgressDialog progressDialog = new ProgressDialog(LabelActivity.this);

            @Override
            public void onStart() {
                progressDialog.setMessage("正在加载");
                progressDialog.setCancelable(false);
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    CuteApplication.nextPageUrl = response.getString("next");
                    if (array.length() > 0) {
                        labelImageAdapter = new LabelImageAdapter(LabelActivity.this, array);
                        labelGridView.setAdapter(labelImageAdapter);
                        GridViewUtility.setGridViewHeightByMySelf(labelGridView, BitmapUtil.dip2px(getApplicationContext(), 80));
                        // 80 是item高度，50是loading , no_more 显示的区域
//                                        getApplicationContext().viewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, GridViewUtility.getGridViewHeight(gridViewNew, BitmapUtil.dip2px(getApplicationContext(), 80)) + BitmapUtil.dip2px(getApplicationContext(), 50)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    public void getLastLabel(){
        HttpUtil.get(API.CUTE_LABELS + "?label=" + labelID + "&key=last", new JsonHttpResponseHandler() {

            ProgressDialog progressDialog = new ProgressDialog(LabelActivity.this);

            @Override
            public void onStart() {
                progressDialog.setMessage("正在加载");
                progressDialog.setCancelable(false);
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    CuteApplication.nextPageUrl = response.getString("next");
                    if (array.length() > 0) {
                        labelImageAdapter = new LabelImageAdapter(LabelActivity.this, array);
                        labelGridView.setAdapter(labelImageAdapter);
                        GridViewUtility.setGridViewHeightByMySelf(labelGridView, BitmapUtil.dip2px(getApplicationContext(), 80));
                        // 80 是item高度，50是loading , no_more 显示的区域

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.label, menu);
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
