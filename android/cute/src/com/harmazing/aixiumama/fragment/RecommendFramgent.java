package com.harmazing.aixiumama.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.adapter.HomeListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
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

/**
 * Created by Lyn on 2015/1/30.
 */
public class RecommendFramgent extends Fragment {

    View addView;
    View view_zhanwei_recommend;
    PullToRefreshListView pullToRefreshListView;
    ListView homeListView;
    static JSONArray jsonArray;
    JSONArray nextArray;
    static HomeListAdapter homeListAdapter;
    private TabWidget mTabWidget;
    private FragmentTabHost mTabHost;
    public static boolean isRefresh = true,isMove0=true,isMove1=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend2, container, false);
        mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
        mTabWidget = mTabHost.getTabWidget();
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.home_listview);
        view_zhanwei_recommend = view.findViewById(R.id.view_zhanwei_recommend);
        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        pullToRefreshListView.setLastUpdatedLabel(label);
        /**
         * 屏蔽下拉加载操作,
         */
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setPullRefreshEnabled(true);
        homeListView = pullToRefreshListView.getRefreshableView();

        //设置listview下拉刷新
        //pullToRefreshListView.setScrollLoadEnabled(true);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                        new GetDataTask().execute();
                HttpUtil.get(API.GET_CUTES + "?key=recommend&format=json", new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    JSONArray array = response.getJSONArray("results");
//                                    if (!homeListAdapter.isRefresh(array.getJSONObject(0).getString("create_date"))){
                                    homeListAdapter.refresh(array);
                                    homeListView.setAdapter(homeListAdapter);
                                    mTabWidget.getChildTabViewAt(0).findViewById(R.id.red_point).setVisibility(View.INVISIBLE);
//                                    }
                                    pullToRefreshListView.onPullDownRefreshComplete();
                                } catch (Exception e) {
                                    LogUtil.v("recommend", "失败");
                                }
                            }
                        }
                );


            }

            @Override
            public void onPullUpToRefresh (PullToRefreshBase< ListView > refreshView) {

            }
        });


        homeListView.addHeaderView(inflater.inflate(R.layout.blank_header, null));

        homeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                             @Override
                                             public void onScrollStateChanged ( final AbsListView view, int scrollState){

                                                 // 当不滚动时
                                                 if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                                                     CuteApplication.imageLoader.resume();
                                                     // 判断滚动到顶部
                                                     if (view.getFirstVisiblePosition() == 0) {
                                                         if (!isRefresh) {
                                                             if (!isMove0) {
                                                                 startAnimaDown(HomeFragment.topView);
                                                                 view_zhanwei_recommend.setVisibility(View.INVISIBLE);
                                                                 isMove0 = true;
                                                                 isMove1 = true;
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
                                                                     if (homeListView.getFooterViewsCount() < 1)
                                                                         homeListView.addFooterView(addView);
                                                                     super.onStart();
                                                                 }

                                                                 @Override
                                                                 public void onFinish() {
                                                                     try {
                                                                         homeListView.removeFooterView(addView);
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

                                                 //  滑动时候
                                                 if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                                     CuteApplication.imageLoader.pause();
                                                     if (isMove0) {
                                                         startAnimaUp(HomeFragment.topView);
                                                         view_zhanwei_recommend.setVisibility(View.GONE);
                                                         isMove0 = false;
                                                         isRefresh = false;
                                                     }
                                                 }

                                                 if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                                                     CuteApplication.imageLoader.pause();
                                                 }
                                             }

                                             @Override
                                             public void onScroll (AbsListView absListView,int firstVisibleItem,
                                                                   int visibleItemCount, int totalItemCount){

                                             }
                                         }

        );


        HttpUtil.addClientHeader(getActivity());
        HttpUtil.get(API.GET_CUTES + "?key=recommend&format=json", new

                        JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject
                                    response) {

                                try {
                                    jsonArray = new JSONArray(response.getString("results"));
                                    homeListAdapter = new HomeListAdapter(jsonArray, getActivity());
                                    homeListView.setAdapter(homeListAdapter);

                                    CuteApplication.nextPageUrl = response.getString("next");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                super.onSuccess(statusCode, headers, response);
                            }


                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable
                                    throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String
                                    responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable
                                    throwable, JSONArray errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }

                            @Override
                            public void onFinish() {
                                LogUtil.v("ArrayFragment2", "推荐 : onFinish");
                                super.onFinish();
                            }
                        }
        );



        return view;
    }

    public void startAnimaUp(final View view){

        Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -1 * BitmapUtil.dip2px(getActivity(), 135));
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(view.getLayoutParams());
                params.setMargins(0,-1 * BitmapUtil.dip2px(getActivity(), 135),0,0);
                view.setLayoutParams(params);
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);

    }

    public void startAnimaDown(final View view){

        Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f,-1 * BitmapUtil.dip2px(getActivity(), 135),0.0f );
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
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);

    }
}
