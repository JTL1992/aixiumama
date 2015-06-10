package com.harmazing.aixiumama.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.FavListAdapter;
import com.harmazing.aixiumama.adapter.FavListImageAdapter;
import com.harmazing.aixiumama.R;

import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.BorderScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lyn on 2014/12/18.
 */
public class FavFragment extends Fragment {

    JSONArray nextArray;
    BorderScrollView borderScrollView;
    FavListImageAdapter favListImageAdapter;
    ArrayList<FavListImageAdapter> favListImageAdapters;
    FavListAdapter favListAdapter;
    String nextCuteItemPage= null;
    boolean isExce = false;
    View addView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.favorities, container, false);
        final GridView favGridView = (GridView)v.findViewById(R.id.gd_fav);
        final ListView favList = (ListView)v.findViewById(R.id.fav_list);
//        borderScrollView = (BorderScrollView)v.findViewById(R.id.borderScrollView);
        HttpUtil.get(API.FAVORITE + "&user=" + AppSharedPref.newInstance(getActivity()).getUserId(), new JsonHttpResponseHandler() {
            //     HttpUtil.get(API.FAVORITE + "&user=4344",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray array = null;
                JSONArray jsonArray = new JSONArray();
                JSONObject cach = new JSONObject();
                 favListImageAdapters = new ArrayList<FavListImageAdapter>();
                try {
                    array = new JSONArray(response.getString("results"));
                    Log.v("jsonArray",array.length()+"");
                    nextCuteItemPage = response.getString("next");
                    if (array.length() > 0) {
                        for (int i = 0; i < array.length(); i++){
                            if (i == 0){
                                jsonArray.put(array.getJSONObject(i));
                                cach = array.getJSONObject(i);
                            }
                            else{
                                if (array.getJSONObject(i).getString("create_date").substring(0, 10).equals(cach.getString("create_date").substring(0, 10))){
                                    jsonArray.put(array.getJSONObject(i));
                                    cach = array.getJSONObject(i);
                                    Log.v("jsonArray",jsonArray.toString());
                                }
                                else{
                                    favListImageAdapter = new FavListImageAdapter(getActivity(),jsonArray);
                                    favListImageAdapters.add(favListImageAdapter);
                                    jsonArray = new JSONArray();
                                    jsonArray.put(array.getJSONObject(i));
                                    cach = array.getJSONObject(i);
                                }
                                 if (i == array.length()-1){
                                    favListImageAdapter = new FavListImageAdapter(getActivity(),jsonArray);
                                    favListImageAdapters.add(favListImageAdapter);
                                }
                            }
                        }
                        favListAdapter = new FavListAdapter(getActivity(),favListImageAdapters);
                        favList.setAdapter(favListAdapter);
//                        favListImageAdapter = new FavListImageAdapter( getActivity(),array);
//                        favGridView.setAdapter(favListImageAdapter);
//                        setGridViewHeight(favGridView, array.length());
                    } else
                        ToastUtil.show(getActivity(), "暂无收藏");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
//        favList.setScrollLoadEnabled(false);
//        favList.setPullLoadEnabled(true);
//        favList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//            }
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
//                if (CuteApplication.nextPageUrl.length() > 4){
//                    HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler(){
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            super.onSuccess(statusCode, headers, response);
//                            try {
//                                JSONArray jsonArray = new JSONArray();
//                                JSONObject cach = new JSONObject();
//                                JSONArray array = new JSONArray(response.getString("results"));
//                                Log.v("jsonArray",array.length()+"");
//                                CuteApplication.nextPageUrl = response.getString("next");
//                                if (array.length() > 0) {
//                                    for (int i = 0; i < array.length(); i++){
//                                        if (i == 0){
//                                            jsonArray.put(array.getJSONObject(i));
//                                            cach = array.getJSONObject(i);
//                                        }
//                                        else{
//                                            if (array.getJSONObject(i).getString("create_date").substring(0, 10).equals(cach.getString("create_date").substring(0, 10))){
//                                                jsonArray.put(array.getJSONObject(i));
//                                                cach = array.getJSONObject(i);
//                                                Log.v("jsonArray",jsonArray.toString());
//                                            }
//                                            else{
//                                                favListImageAdapter = new FavListImageAdapter(getActivity(),jsonArray);
//                                                favListImageAdapters.add(favListImageAdapter);
//                                                jsonArray = new JSONArray();
//                                                jsonArray.put(array.getJSONObject(i));
//                                                cach = array.getJSONObject(i);
//                                            }
//                                            if (i == array.length()-1){
//                                                favListImageAdapter = new FavListImageAdapter(getActivity(),jsonArray);
//                                                favListImageAdapters.add(favListImageAdapter);
//                                            }
//                                        }
//                                    }
//                                    favListAdapter.addFavItemList(favListImageAdapters);
//                                    favListAdapter.notifyDataSetChanged();
//                                }
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//        });
//        borderScrollView.setOnBorderListener(new BorderScrollView.OnBorderListener() {
//            @Override
//            public void onBottom() {
//                //OnBotton 方法容易多次执行，防止多次请求
//                if(!isExce) {
//                    isExce = true;
//                    LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
//                    if (CuteApplication.nextPageUrl.length() > 4) {
//                        HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {
//                            @Override
//                            public void onStart() {
//                                v.findViewById(R.id.loading).setVisibility(View.VISIBLE);
//                                v.findViewById(R.id.no_more).setVisibility(View.GONE);
//                                super.onStart();
//                            }
//                            @Override
//                            public void onFinish() {
//                                v.findViewById(R.id.loading).setVisibility(View.GONE);
//                                super.onFinish();
//                            }
//
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
////                                try {
////                                    isExce = false;
////                                    nextArray = new JSONArray(response.getString("results"));
////                                    favListImageAdapter.addKingdArray(nextArray);
////                                    favListImageAdapter.notifyDataSetChanged();
////
////                                    setGridViewHeight(favGridView, favListImageAdapter.getArray().length());
//////                                GridViewUtility.setGridViewHeightByMySelf(favGridView, BitmapUtil.dip2px(getActivity(), 270));
////                                    LogUtil.v("next", response.getString("next"));
////                                    if (response.getString("next").length() > 10) {
////                                        CuteApplication.nextPageUrl = response.getString("next");
////                                    } else {
////                                        LogUtil.v("next", "没有更多数据");
////                                        CuteApplication.nextPageUrl = "";
////                                        v.findViewById(R.id.no_more).setVisibility(View.VISIBLE);
////                                    }
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
//                                super.onSuccess(statusCode, headers, response);
//                            }
//                        });
//                    } else {
//                        LogUtil.v("next", "没有更多数据");
//                        v.findViewById(R.id.no_more).setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//            @Override
//            public void onTop() {
//            }
//        });
        favList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    if(absListView.getLastVisiblePosition() == absListView.getCount() - 1){
                        Log.v("nextCutePage",nextCuteItemPage);
                        if (nextCuteItemPage.equals("null")){
                            Log.v("viewNum",""+favList.getFooterViewsCount());
                          addView = inflater.inflate(R.layout.no_more, absListView, false);
                            if (favList.getFooterViewsCount() == 1){
                                favList.removeFooterView(addView);
                            }
                            Log.v("viewNum",""+favList.getFooterViewsCount());
                            addView = inflater.inflate(R.layout.no_more, absListView, false);
                            if (favList.getFooterViewsCount() == 0)
                                favList.addFooterView(addView);
                            Log.v("nextCutePage","&&&&&&&");
                            Log.v("viewNum",""+favList.getFooterViewsCount());
                        }
                        if (!nextCuteItemPage.equals("null")){
                            addView = inflater.inflate(R.layout.loading, absListView, false);
                            HttpUtil.get(nextCuteItemPage,new JsonHttpResponseHandler(){
                                @Override
                                public void onStart() {
                                    Log.v("请求开始","");
                                    if (favList.getFooterViewsCount() == 0)
                                        favList.addFooterView(addView);
                                    super.onStart();
                                }

                                @Override
                                public void onFinish() {
                                    super.onFinish();
                                    Log.v("请求结束","");
                                    favList.removeFooterView(addView);
                                }
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    JSONArray array = null;
                                    JSONArray jsonArray = new JSONArray();
                                    JSONObject cach = new JSONObject();
                                    favListImageAdapters = new ArrayList<FavListImageAdapter>();
                                    try {
                                        array = new JSONArray(response.getString("results"));
                                        Log.v("jsonArray",array.length()+"");
                                        nextCuteItemPage = response.getString("next");
                                        if (array.length() > 0) {
                                            for (int i = 0; i < array.length(); i++){
                                                if (i == 0){
                                                    jsonArray.put(array.getJSONObject(i));
                                                    cach = array.getJSONObject(i);
                                                }
                                                else{
                                                    if (array.getJSONObject(i).getString("create_date").substring(0, 10).equals(cach.getString("create_date").substring(0, 10))){
                                                        jsonArray.put(array.getJSONObject(i));
                                                        cach = array.getJSONObject(i);
                                                        Log.v("jsonArray",jsonArray.toString());
                                                    }
                                                    else{
                                                        favListImageAdapter = new FavListImageAdapter(getActivity(),jsonArray);
                                                        favListImageAdapters.add(favListImageAdapter);
                                                        jsonArray = new JSONArray();
                                                        jsonArray.put(array.getJSONObject(i));
                                                        cach = array.getJSONObject(i);
                                                    }
                                                    if (i == array.length()-1){
                                                        favListImageAdapter = new FavListImageAdapter(getActivity(),jsonArray);
                                                        favListImageAdapters.add(favListImageAdapter);
                                                    }
                                                }
                                            }
                                            favListAdapter.addFavItemList(favListImageAdapters);
                                            favListAdapter.notifyDataSetChanged();
//                        favListImageAdapter = new FavListImageAdapter( getActivity(),array);
//                        favGridView.setAdapter(favListImageAdapter);
//                        setGridViewHeight(favGridView, array.length());
                                        } else
                                            ToastUtil.show(getActivity(), "暂无收藏");
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
        return v;
    }
    private void setGridViewHeight(GridView gridView,int arrayLen){
        int row = 0;
        if(arrayLen % 2 == 0){
            row = arrayLen / 2;
        }else
            row = arrayLen / 2 + 1;

        int height = row * BitmapUtil.dip2px(getActivity(), 290);
        gridView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
    }
}
