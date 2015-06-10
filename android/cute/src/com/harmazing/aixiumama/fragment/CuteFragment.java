package com.harmazing.aixiumama.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.CuteListViewAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pullrefreshview.PullToRefreshBase;
import com.pullrefreshview.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/12/18.
 */
public class CuteFragment extends Fragment {

    int followCount;
    View addView;
    JSONArray nextArray;
    CuteListViewAdapter cuteListViewAdapter;
    private  FragmentTabHost mTabHost;
    private TabWidget mTabWidget;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.cute_listview, container, false);
        View headerView = inflater.inflate(R.layout.cute_lv_header,null);
        final TextView cuteNum = (TextView)headerView.findViewById(R.id.cute_num);
        final TextView  cuteInfo = (TextView)headerView.findViewById(R.id.cute_info);
        final PullToRefreshListView cuteList = (PullToRefreshListView)v.findViewById(R.id.cute_listview);
        final TextView  tipInfo = (TextView)headerView.findViewById(R.id.textView);
        final String label = DateUtils. formatDateTime(
                getActivity(),
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL
        ) ;
        cuteList.getRefreshableView().addHeaderView(headerView);
        cuteList.setPullLoadEnabled(true);
        cuteList.setScrollLoadEnabled(true);
        cuteList.setLastUpdatedLabel(label);
        HttpUtil.get(API.ADD_FRIENDS + "?user=" + AppSharedPref.newInstance(getActivity()).getUserId() + "&format=json", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    followCount = response.getInt("count");

                    //      好友数大于5 才显示排名
                    HttpUtil.get(API.GET_USER + AppSharedPref.newInstance(getActivity()).getUserId() + "/?format=json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            try {
                                if (followCount > 5) {
                                    cuteNum.setText(response.getInt("liked_count") + "");
                                    cuteInfo.setText(String.format(getActivity().getResources().getString(R.string.msg_cute_info), response.getString("rank")));
                                } else {
                                    tipInfo.setText("好友数超过5才会显示赞数哦");
                                    cuteInfo.setText("相互关注好友数量太少，快去邀请好友!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            super.onSuccess(statusCode, headers, response);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }
        });

        HttpUtil.get(API.NOTIFICATION + "?type=Like",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray array = null;
                try {
                    array = new JSONArray(response.getString("results"));
                    CuteApplication.nextPageUrl = response.getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(array.length() > 0) {
                    cuteListViewAdapter = new CuteListViewAdapter(array,getActivity());
                    cuteList.getRefreshableView().setAdapter(cuteListViewAdapter);
                }

                super.onSuccess(statusCode, headers, response);
            }
        });
        cuteList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                HttpUtil.get(API.NOTIFICATION+ "?type=Like",new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.v("刷新", response.toString());
                        try {
                            JSONArray array = new JSONArray(response.getString("results"));
                            if (array.length()>0){
                                if (!cuteListViewAdapter.isRefresh(array.getJSONObject(0).getString("create_date"))){
                                    newMessageResp();
                                    cuteListViewAdapter.refresh(array);
                                    cuteList.getRefreshableView().setAdapter(cuteListViewAdapter);
                                }
                                else{
                                    cuteList.getHeaderLoadingLayout().setReleaseLabel("暂无刷新");
                                }
                            }
                            cuteList.onPullDownRefreshComplete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (CuteApplication.nextPageUrl.length() > 4) {
                    HttpUtil.addClientHeader(getActivity());
                    HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                nextArray = new JSONArray(response.getString("results"));
                                cuteListViewAdapter.addKingdArray(nextArray);
                                cuteListViewAdapter.notifyDataSetChanged();

                                LogUtil.v("next", response.getString("next"));
                                if (response.getString("next").length() > 10) {
                                    CuteApplication.nextPageUrl = response.getString("next");
                                    LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                                } else {
                                    LogUtil.v("next", "没有更多数据");
                                    CuteApplication.nextPageUrl = "";
                                    cuteList.onPullUpRefreshComplete();
                                    Toast.makeText(getActivity(),"没有更多了",Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            super.onSuccess(statusCode, headers, response);
                            cuteList.onPullUpRefreshComplete();
                        }
                    });
                }
                else{
                    cuteList.onPullUpRefreshComplete();
                }
            }
        });

//        cuteList.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(final AbsListView view, int scrollState) {
//
//                // 当不滚动时
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//
//                    // 判断是否滚动到底部
//                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
//                        //加载更多功能的代码
//                        addView = inflater.inflate(R.layout.loading, view, false);
//                        LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
//                        if (CuteApplication.nextPageUrl.length() > 4) {
//                            HttpUtil.addClientHeader(getActivity());
//                            HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {
//                                @Override
//                                public void onStart() {
//                                    if (cuteList.getRefreshableView().getFooterViewsCount() == 0)
//                                        cuteList.getRefreshableView().addFooterView(addView);
//                                    super.onStart();
//                                }
//
//                                @Override
//                                public void onFinish() {
//                                    try {
//                                        cuteList.getRefreshableView().removeFooterView(addView);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//
//                                        LogUtil.e("cute推荐", "三星note3");
//                                    }
//                                    super.onFinish();
//                                }
//
//                                @Override
//                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                    try {
//                                        nextArray = new JSONArray(response.getString("results"));
//                                        cuteListViewAdapter.addKingdArray(nextArray);
//                                        cuteListViewAdapter.notifyDataSetChanged();
//
//                                        LogUtil.v("next", response.getString("next"));
//                                        if (response.getString("next").length() > 10) {
//                                            CuteApplication.nextPageUrl = response.getString("next");
//                                        } else {
//                                            LogUtil.v("next", "没有更多数据");
//                                            CuteApplication.nextPageUrl = "";
//                                            addView = inflater.inflate(R.layout.no_more, view, false);
//                                            cuteList.getRefreshableView().addFooterView(addView);
//                                        }
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    super.onSuccess(statusCode, headers, response);
//                                }
//                            });
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
        return v;
    }
    public void newMessageResp(){
//        CuteService.isResetMsg = true;
        mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
        mTabWidget = mTabHost.getTabWidget();
        mTabWidget.getChildTabViewAt(3).findViewById(R.id.red_button).setVisibility(View.INVISIBLE);

    }
}
