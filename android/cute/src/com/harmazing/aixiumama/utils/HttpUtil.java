package com.harmazing.aixiumama.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.params.ClientPNames;

public class HttpUtil {
    private static AsyncHttpClient client =new AsyncHttpClient();    //实例话对象
    static
    {
        // 允许重定向循环请求url
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,true);
        client.setTimeout(11000);   //设置链接超时，如果不设置，默认为10s
    }
    public static void get(String urlString,AsyncHttpResponseHandler res)    //用一个完整url获取一个string对象
    {
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,AsyncHttpResponseHandler res)   //url里面带参数
    {
        client.get(urlString, params,res);
    }
    public static void get(String urlString,JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
        client.get(urlString, params,res);
    }
    public static void get(String uString, BinaryHttpResponseHandler bHandler)   //下载数据使用，会返回byte数据
    {
        client.get(uString, bHandler);
    }

    public static void addClientHeader(Context context)
    {
        if(AppSharedPref.newInstance(context).getToken() != null) {
                try {
                    client.addHeader("Authorization", "token " + AppSharedPref.newInstance(context).getToken());
                    LogUtil.v("发送的Header", "token " + AppSharedPref.newInstance(context).getToken());
                } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void removePatchHeader(){
//        client.removeHeader("X-HTTP-Method-Override");
        client.removeAllHeaders();
    }

    public static void addPatchClientHeader(Context context)
    {
        HttpUtil.getClient().addHeader("X-HTTP-Method-Override","PATCH");
    }
    public static void post(String urlString,AsyncHttpResponseHandler res){
        client.post(urlString, res);
    }
    public static void post(String urlString,RequestParams params,AsyncHttpResponseHandler res)
    {
        client.post(urlString, params, res);
    }
    public static AsyncHttpClient getClient()
    {
        return client;
    }
    public static void patch(String urlString,RequestParams params,AsyncHttpResponseHandler res){
       client.put(urlString,params,res);
    }
    public static void delete(String urlString,AsyncHttpResponseHandler res){
        client.delete(urlString,res);
    }

    public static void cancelRequest(Context context){
        client.cancelRequests(context,true);
    }

}