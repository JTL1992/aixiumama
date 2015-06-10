package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.VisitHistoryAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.AppSharedPref;
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

public class VisitHistoryActivity extends Activity {

    ListView historyList;
    LayoutInflater inflater;
    View addView;
    VisitHistoryAdapter visitHistoryAdapter;
    JSONArray nextArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_history);
        inflater= (LayoutInflater) this
                .getSystemService(this.LAYOUT_INFLATER_SERVICE);
        final View headerView =  inflater.inflate(R.layout.textview_header,null);
        historyList = (ListView)findViewById(R.id.visit_list);
        final TextView countTV = (TextView)headerView.findViewById(R.id.textview);
        historyList.addHeaderView(headerView);

        historyList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        addView = inflater.inflate(R.layout.loading, view, false);
                        LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                        if (CuteApplication.nextPageUrl.length() > 4) {

                            HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    if (historyList.getFooterViewsCount() < 1)
                                        historyList.addFooterView(addView);
                                    super.onStart();
                                }

                                @Override
                                public void onFinish() {
                                    try {
                                        historyList.removeFooterView(addView);
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

                                        visitHistoryAdapter.addKingdArray(nextArray);
                                        visitHistoryAdapter.notifyDataSetChanged();
                                        LogUtil.v("next", response.getString("next"));
                                        if (response.getString("next").length() > 10) {
                                            CuteApplication.nextPageUrl = response.getString("next");
                                        } else {
                                            LogUtil.v("next", "没有更多数据");
                                            CuteApplication.nextPageUrl = "";
                                            addView = inflater.inflate(R.layout.no_more, view, false);
                                            historyList.addFooterView(addView);
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

        HttpUtil.get(API.HISTORY + "?user=" + AppSharedPref.newInstance(getApplicationContext()).getUserId(),new JsonHttpResponseHandler(){
            ProgressDialog progressDialog = new ProgressDialog(VisitHistoryActivity.this);

            @Override
            public void onStart() {
                progressDialog.setMessage("正在加载");
                progressDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    if (array.length() > 0){
                        countTV.setText("总来访：" + response.getString("count"));
                        visitHistoryAdapter = new VisitHistoryAdapter(array, VisitHistoryActivity.this);
                        historyList.setAdapter(visitHistoryAdapter);
                    }else
                        ToastUtil.show(getApplicationContext(), "还没有人看过你哦");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //  请求今题来访
                HttpUtil.get(API.HISTORY + "?user=" + AppSharedPref.newInstance(getApplicationContext()).getUserId() + "&period=today",new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            countTV.setText(countTV.getText().toString() + "    今日来访：" + response.getString("count"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }
                });

                progressDialog.dismiss();
                super.onSuccess(statusCode, headers, response);
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
        getMenuInflater().inflate(R.menu.visit_history, menu);
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
