package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.CuteRankAdapter;
import com.harmazing.aixiumama.model.CuteRank;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/1/5.
 */
public class CuteRankActivity extends Activity {
    ListView list;
    String userId;
    ImageView back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cute_rank);
        list = (ListView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        userId = AppSharedPref.newInstance(getApplicationContext()).getUserId();
        RequestParams params = new RequestParams();
//        params.put("user",userId);
        params.put("key","rank");
        HttpUtil.addClientHeader(getApplicationContext());
        HttpUtil.get(API.RANK_CUTE,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    ArrayList<CuteRank> cuteRanks = new ArrayList<CuteRank>();
                    CuteRank cuteRank ;
                    JSONArray array = response.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        String name = object.getString("name");
                        String icon = object.getString("image_small");
                        String age = object.getJSONArray("babies").getJSONObject(0).getString("birthday");
                        int gender = object.getJSONArray("babies").getJSONObject(0).getInt("gender");
                        int userId = object.getInt("auth_user");
                        cuteRanks.add(new CuteRank(name, age, gender, icon,userId));
                    }
                    CuteRankAdapter cuteRankAdapter = new CuteRankAdapter(getApplicationContext(),cuteRanks);
                    list.setAdapter(cuteRankAdapter);
                }catch (Exception e){
                    LogUtil.v("解析错", "JSON");
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(),"网络异常，请稍后重试！",Toast.LENGTH_LONG).show();
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