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

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.FollowUserListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  关注该 标签的粉丝
 *  参数 labelID      labelName
 */
public class LabelFansActivity extends Activity {

    String labelName;
    int labelID;
    ListView followUserList;
    TextView titleTV;
    public static int LABEL = 7;
    JSONArray nextArray;
    View addView;
    FollowUserListAdapter followUserListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fans);

        followUserList = (ListView)findViewById(R.id.fans_list);
        titleTV = (TextView)findViewById(R.id.top_title);

        labelID = getIntent().getIntExtra("labelID", 0);
        labelName = getIntent().getStringExtra("labelName");
        titleTV.setText(labelName + "的粉丝");
        final LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(this.LAYOUT_INFLATER_SERVICE);
        /**
         *  关注改品牌的用户
         */
        HttpUtil.get(API.FOLLOW_LABELS + "?follow=" + labelID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray array = null;
                try {
                    array = new JSONArray(response.getString("results"));
                    if (array.length() > 0) {

                        followUserListAdapter = new FollowUserListAdapter(array, LabelFansActivity.this, LABEL);
                        followUserList.setAdapter(followUserListAdapter);
                        CuteApplication.nextPageUrl = response.getString("next");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
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
                        if (CuteApplication.nextPageUrl.length() > 0) {
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
                                        ToastUtil.show(LabelFansActivity.this, "没有更多了");
                                        followUserList.removeFooterView(addView);
                                        followUserList.requestFocus();
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


                    }
                }

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.label_fans, menu);
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
