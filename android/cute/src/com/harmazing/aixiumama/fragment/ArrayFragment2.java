package com.harmazing.aixiumama.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TabWidget;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.HomeListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pullrefreshview.PullToRefreshBase;
import com.pullrefreshview.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public  class ArrayFragment2 extends Fragment {
    View v = null,addView;
    int mNum,per;
    PullToRefreshListView pullToRefreshListView;
    PullToRefreshListView pullToRefreshListViewAttention;
    ListView homeListView;
    static ListView homeListViewAttention;
    static JSONArray jsonArray;
    JSONArray nextArray;
    static HomeListAdapter homeListAdapter,homeListAdapterAttention;
    private FragmentTabHost mTabHost;
    private TabWidget mTabWidget;
    public static boolean isRefresh = true,isMove0=true,isMove1=true;
    public static ArrayFragment2 newInstance(int num) {
        ArrayFragment2 array= new ArrayFragment2();
        Bundle args = new Bundle();
        args.putInt("num", num);
        array.setArguments(args);
        return array;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;

    }
    View view_zhanwei_recommend;
    View view_zhanwei_attention;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
        mTabWidget = mTabHost.getTabWidget();
            if(mNum == 0) {
                v = inflater.inflate(R.layout.recommend2, container, false);

                }else{
                v = inflater.inflate(R.layout.attentiong3, container, false);

                pullToRefreshListViewAttention = (PullToRefreshListView) v.findViewById(R.id.home_listview);

                view_zhanwei_attention = v.findViewById(R.id.view_zhanwei_attention);

                pullToRefreshListViewAttention.setPullLoadEnabled(false);
                //设置listview下拉刷新
                pullToRefreshListViewAttention.setPullRefreshEnabled(true);

                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                pullToRefreshListViewAttention.setLastUpdatedLabel(label);

                homeListViewAttention = pullToRefreshListViewAttention.getRefreshableView();

                pullToRefreshListViewAttention.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                        refreshList();
//                        pullToRefreshListViewAttention.onPullDownRefreshComplete();
                        HttpUtil.get(API.GET_CUTES + "?key=follow&format=json", new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        try {
                                            JSONArray array =  response.getJSONArray("results");
//                                            if (!homeListAdapter.isRefresh(array.getJSONObject(0).getString("create_date"))){
                                            homeListAdapterAttention.refresh(array);
                                            homeListViewAttention.setAdapter(homeListAdapterAttention);
                                            mTabWidget.getChildAt(0).findViewById(R.id.red_point).setVisibility(View.INVISIBLE);
//                                            }
                                            pullToRefreshListViewAttention.onPullDownRefreshComplete();
                                        }catch (Exception e ){
                                            LogUtil.v("recommend","失败");
                                        }
                                    }
                                }
                        );
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                    }
                });

                pullToRefreshListViewAttention.onPullDownRefreshComplete();

                homeListViewAttention.addHeaderView(inflater.inflate(R.layout.blank_header, null));
                homeListViewAttention.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(final AbsListView view, int scrollState) {
                        // 当不滚动时
                        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                            CuteApplication.imageLoader.resume();
                            // 判断滚动到顶部
                            if (view.getFirstVisiblePosition() == 0) {

                                if (!isRefresh) {
                                    if(!isMove1) {
                                        startAnimaDown(HomeFragment.topView);
                                        view_zhanwei_attention.setVisibility(View.INVISIBLE);
                                        isMove1 = true;
                                        isMove0 = true;
                                        isRefresh = true;
                                    }
                                }
                            }


                            // 判断是否滚动到底部
                            if (view.getLastVisiblePosition() == view.getCount() - 1) {
                                //加载更多功能的代码
                                addView = inflater.inflate(R.layout.loading, view, false);
                                LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                                if (CuteApplication.nextPageUrl.length() > 4) {
                                    HttpUtil.addClientHeader(getActivity());
                                    HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {

                                        @Override
                                        public void onStart() {
                                            if (homeListViewAttention.getFooterViewsCount() < 1)
                                                homeListViewAttention.addFooterView(addView);
                                            super.onStart();
                                        }

                                        @Override
                                        public void onFinish() {
                                            try {
                                                homeListViewAttention.removeFooterView(addView);
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


                                                homeListAdapterAttention.addKingdArray(nextArray);
                                                homeListAdapterAttention.notifyDataSetChanged();

                                                LogUtil.v("next", response.getString("next"));
                                                if (response.getString("next").length() > 10) {
                                                    CuteApplication.nextPageUrl = response.getString("next");
                                                } else {
                                                    LogUtil.v("next", "没有更多数据");
                                                    CuteApplication.nextPageUrl = "";
                                                    addView = inflater.inflate(R.layout.no_more, view, false);
                                                    homeListViewAttention.addFooterView(addView);
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

                        //  滑动时候
                        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ) {
                            CuteApplication.imageLoader.pause();
                            if(isMove1 ) {
                                startAnimaUp(HomeFragment.topView);
                                view_zhanwei_attention.setVisibility(View.GONE);
                                isMove1 = false;
                                isRefresh = false;
                            }
                        }
                        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ) {
                            CuteApplication.imageLoader.pause();
                        }

                    }

                    @Override
                    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

                    }
                });


                HttpUtil.get(API.GET_CUTES + "?key=follow&format=json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        try {
                            jsonArray = new JSONArray(response.getString("results"));
                            homeListAdapterAttention = new HomeListAdapter(jsonArray, getActivity());
                            homeListViewAttention.setAdapter(homeListAdapterAttention);
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
                        LogUtil.v("ArrayFragment2", "关注 : onFinish");
                        super.onFinish();
                    }
                });

            }



        return v;
    }

    public void startAnimaUp(final View view){

        Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -1 * BitmapUtil.dip2px(getActivity(),50));
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(view.getLayoutParams());
                params.setMargins(0,-1 * BitmapUtil.dip2px(getActivity(),50),0,0);
                view.setLayoutParams(params);
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);

    }

    public void startAnimaDown(final View view){

        Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f,-1 * BitmapUtil.dip2px(getActivity(),50),0.0f );
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(view.getLayoutParams());
                params.setMargins(0,0,0,0);
                view.setLayoutParams(params);
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);

    }

    public static void refreshList(){
        HttpUtil.get(API.GET_CUTES + "?key=follow&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    jsonArray = new JSONArray(response.getString("results"));
                    homeListAdapterAttention.setJsonArray(jsonArray);
                    homeListViewAttention.setAdapter(homeListAdapterAttention);
                    CuteApplication.nextPageUrl = response.getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }

        });
    }



}