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
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.TabHostActivity;
import com.harmazing.aixiumama.adapter.MsgListViewAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;

import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
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
public class MsgFragment extends Fragment {

    PullToRefreshListView msgList;
    private FragmentTabHost mTabHost;
    private TabWidget mTabWidget;
    JSONArray nextArray;
    View addView;
    MsgListViewAdapter msgListViewAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.message_listview, container, false);
        msgList  = (PullToRefreshListView)v.findViewById(R.id.message_list);
        HttpUtil.addClientHeader(getActivity());
        HttpUtil.get(API.NOTIFICATION+"?action=exclude&type=like", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray array = null;
                JSONArray noLikeArray = new JSONArray();
                try {
                    array = new JSONArray(response.getString("results"));
                    CuteApplication.nextPageUrl = response.getString("next");
                    for (int i = 0; i < array.length(); i++ ){
                        if (!array.getJSONObject(i).getString("type").equals("Like"))
                             noLikeArray.put(array.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (noLikeArray.length() > 0) {
                    msgListViewAdapter = new MsgListViewAdapter(noLikeArray, getActivity());
                    msgList.getRefreshableView().setAdapter(msgListViewAdapter);
                }

                super.onSuccess(statusCode, headers, response);
            }
        });
        /**
         * 屏蔽下拉加载操作,
         */
        final String label = DateUtils. formatDateTime(
                getActivity(),
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL
        ) ;
        msgList.setPullLoadEnabled(true);
        //设置listview下拉刷新
        msgList.setScrollLoadEnabled(true);
        msgList.setLastUpdatedLabel(label);
        msgList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                HttpUtil.get(API.NOTIFICATION+"?action=exclude&type=like",new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.v("刷新",response.toString());
                        try {
                           JSONArray array = new JSONArray(response.getString("results"));
                            JSONArray noLikeArray = new JSONArray();
                           if (array.length()>0){
                               newMessageResp();
                               mTabWidget.getChildTabViewAt(3).findViewById(R.id.red_button).setVisibility(View.INVISIBLE);
                               for (int i = 0; i < array.length(); i++)
                                   if (!array.getJSONObject(i).getString("type").equals("Like"))
                                       noLikeArray.put(array.getJSONObject(i)) ;
//                               if (!msgListViewAdapter.isRefresh(array.getJSONObject(0).getString("create_date"))){
                               if (noLikeArray.length() > 0){
                                   msgListViewAdapter.refresh(noLikeArray);
                                   msgList.getRefreshableView().setAdapter(msgListViewAdapter);
                               }
                               else{
                                   msgList.getHeaderLoadingLayout().setReleaseLabel("暂无刷新");
                               }
                           }
                            msgList.onPullDownRefreshComplete();
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
                                CuteApplication.nextPageUrl = response.getString("next");
                                nextArray = new JSONArray(response.getString("results"));
                                JSONArray noLikeArray = new JSONArray();
                                for (int i = 0; i < nextArray.length(); i++)
                                    if (!nextArray.getJSONObject(i).getString("type").equals("Like"))
                                        noLikeArray.put(nextArray.getJSONObject(i));
                                if (noLikeArray.length() > 0){
                                msgListViewAdapter.addKingdArray(noLikeArray);
                                msgListViewAdapter.notifyDataSetChanged();
                                }

                                LogUtil.v("next", response.getString("next"));
                                if (response.getString("next").length() > 10) {
                                    CuteApplication.nextPageUrl = response.getString("next");
                                    LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                                } else {
                                    Toast.makeText(getActivity(),"没有更多了",Toast.LENGTH_LONG).show();
                                    LogUtil.v("next", "没有更多数据");
                                    CuteApplication.nextPageUrl = "";
                                    msgList.onPullUpRefreshComplete();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            super.onSuccess(statusCode, headers, response);
                            msgList.onPullUpRefreshComplete();
                        }
                    });
                }
                else{
                    msgList.onPullUpRefreshComplete();
                }
            }
        });


//        msgList.setOnScrollListener(new AbsListView.OnScrollListener() {
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
//
//                                @Override
//                                public void onStart() {
//                                    if (msgList.getRefreshableView().getFooterViewsCount() < 1)
//                                        msgList.getRefreshableView().addFooterView(addView);
//                                    super.onStart();
//                                }
//
//                                @Override
//                                public void onFinish() {
//                                    try {
//                                        msgList.getRefreshableView().removeFooterView(addView);
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//
//                                        LogUtil.e("cute推荐","三星note3");
//                                    }
//                                    super.onFinish();
//                                }
//
//                                @Override
//                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                    try {
//                                        nextArray = new JSONArray(response.getString("results"));
//                                        msgListViewAdapter.addKingdArray(nextArray);
//                                        msgListViewAdapter.notifyDataSetChanged();
//
//                                        LogUtil.v("next", response.getString("next"));
//                                        if (response.getString("next").length() > 10) {
//                                            CuteApplication.nextPageUrl = response.getString("next");
//                                        } else {
//                                            LogUtil.v("next", "没有更多数据");
//                                            CuteApplication.nextPageUrl = "";
//                                            addView = inflater.inflate(R.layout.no_more, view, false);
//                                            msgList.getRefreshableView().addFooterView(addView);
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
        if (TabHostActivity.newMessageNum == 0)
        mTabWidget.getChildTabViewAt(3).findViewById(R.id.red_button).setVisibility(View.INVISIBLE);

    }
}
