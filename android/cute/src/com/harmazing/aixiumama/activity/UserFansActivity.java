package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.adapter.FollowUserListAdapter;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 *  显示该用户的粉丝list
 *  参数  userID  userName
 */
public class UserFansActivity extends Activity {
    String userName;
    int userID;
    ListView followUserList;
    TextView titleTV;
    JSONArray nextArray;
    View addView;
    FollowUserListAdapter followUserListAdapter;
    
    public static int FANS = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fans);
        followUserList = (ListView)findViewById(R.id.fans_list);
        titleTV = (TextView)findViewById(R.id.top_title);

        userID = getIntent().getIntExtra("userID", 0);
        userName = getIntent().getStringExtra("userName");
        titleTV.setText(userName + "的粉丝");

        final LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(this.LAYOUT_INFLATER_SERVICE);

        //  为false时候表示是 CutePersonThumbNail 传过来的
        if(getIntent().getBooleanExtra("CutePersonThumbNail",true)) {
            /**
             *  关注的其他用户
             */
            HttpUtil.get(API.ADD_FRIENDS + "?follow=" + userID, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray array = null;
                    try {
                        array = new JSONArray(response.getString("results"));
                        if (array.length() > 0) {
                            followUserListAdapter = new FollowUserListAdapter(array, UserFansActivity.this, FANS);
                            followUserList.setAdapter(followUserListAdapter);
                            CuteApplication.nextPageUrl = response.getString("next");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    super.onSuccess(statusCode, headers, response);
                }
            });


        }else{
            /**
             *  为cute点赞的人们
             */
            HttpUtil.get(API.LIKE + "?cute=" + getIntent().getIntExtra("cuteID",0), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray array = null;
                    try {
                        array = new JSONArray(response.getString("results"));
                        if (array.length() > 0) {
                            followUserListAdapter = new FollowUserListAdapter(array, UserFansActivity.this, FANS);
                            followUserList.setAdapter(followUserListAdapter);
                            CuteApplication.nextPageUrl = response.getString("next");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    super.onSuccess(statusCode, headers, response);
                }
            });


        }

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        followUserList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        addView = inflater.inflate(R.layout.loading, view, false);
                        LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                        if (!CuteApplication.nextPageUrl.equals("null") ) {
                            HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    if (followUserList.getFooterViewsCount() < 1)
                                        followUserList.addFooterView(addView);
                                    super.onStart();
                                }



                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    try {
//                                        ToastUtil.show(UserFansActivity.this,"没有更多了");
//                                        followUserList.requestFocus();
                                      if (followUserList.getFooterViewsCount() == 1)
                                        followUserList.removeFooterView(addView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        LogUtil.e("cute推荐", "三星note3");
                                    }
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                }


                                @Override
                                public void onFinish() {
                                    try {
                                        followUserList.removeFooterView(addView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        LogUtil.e("cute推荐", "三星note3");
                                    }
                                    super.onFinish();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        nextArray = new JSONArray(response.getString("results"));

                                        followUserListAdapter.addKingdArray(nextArray);
                                        followUserListAdapter.notifyDataSetChanged();

                                        LogUtil.v("next", response.getString("next"));
                                        if (response.getString("next").length() > 10) {
                                            CuteApplication.nextPageUrl = response.getString("next");
                                        } else {
                                            LogUtil.v("next", "没有更多数据");
                                            CuteApplication.nextPageUrl = "";
                                            followUserList.removeFooterView(addView);
                                            addView = inflater.inflate(R.layout.no_more, view, false);
                                            followUserList.addFooterView(addView);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    super.onSuccess(statusCode, headers, response);
                                }
                            });
                        }
                        else{
                            if(followUserList.getFooterViewsCount() == 0){
                            addView = inflater.inflate(R.layout.no_more, view, false);
                            followUserList.addFooterView(addView);
                            }
                        }

                    }
                }

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_fans, menu);
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
