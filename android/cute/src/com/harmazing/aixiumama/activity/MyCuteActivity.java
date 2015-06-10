package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.HomeListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pullrefreshview.PullToRefreshBase;
import com.pullrefreshview.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/1/28.
 */
public class MyCuteActivity extends Activity {
    ImageView backBtn;
    PullToRefreshListView mList;
    JSONArray jsonArray;
    HomeListAdapter homeListAdapter;
    String userID;
    View addView;
    PullToRefreshBase.OnRefreshListener<ListView> mRefreshListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycutelist_layout);
        parseIntent();
        backBtn = (ImageView) findViewById(R.id.back);
        mList = (PullToRefreshListView) findViewById(R.id.list);
        initScrollListner();
        httpGetData();
        final String label = DateUtils. formatDateTime(
                this,
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL
        ) ;
        mList.setPullLoadEnabled(true);
        //设置listview下拉刷新
        mList.setScrollLoadEnabled(false);
        mList.setLastUpdatedLabel(label);
        mList.setOnRefreshListener(mRefreshListener);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initScrollListner(){
       mRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
           @Override
           public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

           }

           @Override
           public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
               if (CuteApplication.nextPageUrl.length() > 4) {
                   HttpUtil.addClientHeader(MyCuteActivity.this);
                   HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {

                       @Override
                       public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                           try {
                              JSONArray nextArray = new JSONArray(response.getString("results"));
                               homeListAdapter.addKingdArray(nextArray);
                               homeListAdapter.notifyDataSetChanged();
                               LogUtil.v("next", response.getString("next"));
                               if (response.getString("next").length() > 10) {
                                   CuteApplication.nextPageUrl = response.getString("next");
                               } else {
                                   LogUtil.v("next", "没有更多数据");
                                   CuteApplication.nextPageUrl = "";
                               }

                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                           mList.onPullUpRefreshComplete();
                           super.onSuccess(statusCode, headers, response);
                       }
                   });
               }
           }
       };


    }
    private void parseIntent(){
        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("user_id");
    }
    private void httpGetData(){
        HttpUtil.get(API.GET_CUTES + "?user=" + userID, new JsonHttpResponseHandler() {
            ProgressDialog progressDialog = new ProgressDialog(MyCuteActivity.this);
            @Override
            public void onStart() {
                progressDialog.setMessage("正在加载");
                progressDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (this.progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                try {
                    jsonArray = new JSONArray(response.getString("results"));

                    homeListAdapter = new HomeListAdapter(jsonArray, MyCuteActivity.this);
                    mList.getRefreshableView().setAdapter(homeListAdapter);
                    CuteApplication.nextPageUrl = response.getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
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