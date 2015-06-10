package com.harmazing.aixiumama.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.HomeListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.MyCuteActivity;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.view.BaseMyListView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/12/18.
 */
@SuppressLint("ValidFragment")
public class userCenterFragment extends Fragment {
    Button bottomBtn;
    BaseMyListView homeListViewAttention;
    String userID;
    JSONArray jsonArray;
    HomeListAdapter homeListAdapter;
    Handler mHandler;
    public userCenterFragment(String id){
        userID = id;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.attention, container, false);
        bottomBtn = new Button(getActivity());
        bottomBtn.setText("点击查看更多");
        bottomBtn.setBackgroundColor(getResources().getColor(R.color.white));
//        addView = View.inflate(getActivity(),R.layout.loading,homeListViewAttention);
        homeListViewAttention = (BaseMyListView) v.findViewById(R.id.home_listview);
        final ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setBackgroundColor(getResources().getColor(R.color.white));
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0){
                    homeListViewAttention.removeFooterView(bottomBtn);
                    homeListViewAttention.addFooterView(progressBar);
                }
                else{
                    homeListViewAttention.removeFooterView(progressBar);
                    homeListViewAttention.addFooterView(bottomBtn);
                }
            }
        };
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyCuteActivity.class);
                intent.putExtra("user_id",userID);
                getActivity().startActivity(intent);
            }
        });
        HttpUtil.get(API.GET_CUTES + "?user=" + userID , new JsonHttpResponseHandler() {
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
                    jsonArray = new JSONArray(response.getString("results"));
                    JSONArray array = new JSONArray();
                    if(jsonArray.length() > 2)
                        for (int i = 0; i < 2; i++)
                          array.put(jsonArray.getJSONObject(i));
                    else
                       array = jsonArray;
                    homeListAdapter = new HomeListAdapter(array, getActivity());
                    homeListViewAttention.setAdapter(homeListAdapter);
                    if (jsonArray.length() > 2)
                    homeListViewAttention.addFooterView(bottomBtn);
//                    ListViewUtility.setListViewHeightBasedOnChildren(homeListViewAttention);
                    CuteApplication.nextPageUrl = response.getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (this.progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                super.onSuccess(statusCode, headers, response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
//
        return v;
    }

}
