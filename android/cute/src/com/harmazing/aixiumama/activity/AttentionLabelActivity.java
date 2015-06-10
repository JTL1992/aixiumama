package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.adapter.LabelsListImageAdapter;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  改用户关注的所有标签（已经认证的商标）
 *  参数  userID
 */
public class AttentionLabelActivity extends Activity {

    int userID;
    GridView gridView;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_label);
        gridView = (GridView)findViewById(R.id.gridview);

        progress = new ProgressDialog(AttentionLabelActivity.this);
        progress.setMessage("努力加载中...");
        progress.show();

        userID = getIntent().getIntExtra("userID", 0);

        HttpUtil.get(API.FOLLOW_LABELS + "?user=" + userID + "&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray array = null;

                try {
                    array = new JSONArray(response.getString("results"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(array.length() > 0){
                    gridView.setAdapter(new LabelsListImageAdapter(getApplication(),array));
                }
                if(progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }

        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                finish();
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
