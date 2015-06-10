package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SlidingDrawer;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.HomeListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CuteMostActivity extends Activity {

    ListView homeListView;
    View addView;
    JSONArray jsonArray,nextArray;
    HomeListAdapter homeListAdapter;
    SlidingDrawer mSlidingDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cute_most);
        final LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(this.LAYOUT_INFLATER_SERVICE);
        homeListView = (ListView) findViewById(R.id.home_listview);
        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.sliding);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        homeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, int scrollState) {

                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        addView =  inflater.inflate(R.layout.loading, view, false);
                        LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                        if (CuteApplication.nextPageUrl.length() > 0) {

                            HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    if (homeListView.getFooterViewsCount() < 1)
                                        homeListView.addFooterView(addView);
                                    super.onStart();
                                }

                                @Override
                                public void onFinish() {
                                    try {
                                        homeListView.removeFooterView(addView);
                                    }catch (Exception e){
                                        e.printStackTrace();

                                        LogUtil.e("cute推荐","三星note3");
                                    }
                                    super.onFinish();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        nextArray = new JSONArray(response.getString("results"));
//

                                        homeListAdapter.addKingdArray(nextArray);
                                        homeListAdapter.notifyDataSetChanged();

                                        LogUtil.v("next", response.getString("next"));
                                        if (response.getString("next").length() > 10) {
                                            CuteApplication.nextPageUrl = response.getString("next");
                                        } else {
                                            LogUtil.v("next", "没有更多数据");
                                            CuteApplication.nextPageUrl = "";
                                            addView = inflater.inflate(R.layout.no_more, view, false);
                                            homeListView.addFooterView(addView);
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
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        HttpUtil.get(API.GET_CUTES + "?key=hot&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    jsonArray = new JSONArray(response.getString("results"));
                    homeListAdapter = new HomeListAdapter(jsonArray, CuteMostActivity.this);
                    homeListView.setAdapter(homeListAdapter);
                    CuteApplication.nextPageUrl = response.getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFinish() {
                LogUtil.v("超市了？", "ff");
                super.onFinish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cute_most, menu);
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
