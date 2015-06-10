package com.harmazing.aixiumama.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.LogUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


/**
 * Created by guoyongwei on 2015/2/14.
 */
public abstract class BaseMyFragment extends Fragment{

    public View view;
    public Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = initView(inflater);
        return view;
    }

    /**
     * 从网络获取json数据
     */
    public void getData(HttpRequest.HttpMethod method, String url, RequestParams params, RequestCallBack<String> callBack) {
        //创建HttpUtils对象
        HttpUtils utils = new HttpUtils();

        if(AppSharedPref.newInstance(context).getToken() != null) {
            try {
                params.addHeader("Authorization", "token " + AppSharedPref.newInstance(context).getToken());
                LogUtil.v("发送的Header", "token " + AppSharedPref.newInstance(context).getToken());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //请求网络,获取json数据
        utils.send(method, url, params, callBack);
    }

    public abstract View initView(LayoutInflater inflater);
    public abstract void initData();
}
