package com.harmazing.aixiumama.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harmazing.aixiumama.adapter.HomeListAdapter;

import org.json.JSONArray;

/**
 * Created by Lyn on 2014/11/6.
 */
public class UserArrayFragment extends Fragment {
    View v = null;
    int mNum;
    int userID;


    HomeListAdapter homeListAdapter;
    View addView;
    JSONArray jsonArray,nextArray;
    boolean isBottom = true;
    
    LayoutInflater globalInflater;
    public static UserArrayFragment newInstance(int num, int id) {
        UserArrayFragment array= new UserArrayFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putInt("id", id);
        array.setArguments(args);

        return array;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        userID = getArguments() != null ? getArguments().getInt("id") : 1;


    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        globalInflater = inflater;
        switch (mNum){
            case 0:



                break;
            case 1:

                break;
            case 2:



                break;
        }

        return v;
    }

//    public class UserborderScrollViewListener implements BorderScrollView.OnBorderListener {
//        @Override
//        public void onBottom() {
//            if(isBottom) {
//                isBottom = false;
//                //加载更多功能的代码
//                addView = globalInflater.inflate(R.layout.loading, null);
//                LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
//                if (CuteApplication.nextPageUrl.length() > 4) {
//
//                    HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {
//
//                        @Override
//                        public void onStart() {
//                            if (homeListViewAttention.getFooterViewsCount() < 1)
//                                homeListViewAttention.addFooterView(addView);
//                            super.onStart();
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            try {
//                                homeListViewAttention.removeFooterView(addView);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                LogUtil.e("cute推荐", "三星note3");
//                            }
//                            super.onFinish();
//                        }
//
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            try {
//                                nextArray = new JSONArray(response.getString("results"));
//
//                                homeListAdapter.addKingdArray(nextArray);
//                                homeListAdapter.notifyDataSetChanged();
//                                ListViewUtility.setListViewHeightBasedOnChildren(homeListViewAttention);
//                                //  加上FootView 的高度
//                                CuteApplication.centerHeight = ListViewUtility.getListViewHright(homeListViewAttention) + BitmapUtil.dip2px(getActivity(),50) ;
//                                UserFragment.mViewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CuteApplication.centerHeight));
//                                LogUtil.v("next", response.getString("next"));
//                                if (response.getString("next").length() > 10) {
//                                    CuteApplication.nextPageUrl = response.getString("next");
//                                } else {
//                                    LogUtil.v("next", "没有更多数据");
//                                    CuteApplication.nextPageUrl = "";
//                                    addView = globalInflater.inflate(R.layout.no_more, null);
//                                    homeListViewAttention.addFooterView(addView);
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            isBottom = true;
//                            super.onSuccess(statusCode, headers, response);
//                        }
//                    });
//                } else {
//                    addView = globalInflater.inflate(R.layout.no_more, null);
//                    homeListViewAttention.addFooterView(addView);
//
//                }
//            }
//        }
//
//        @Override
//        public void onTop() {
//
//        }
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }


}