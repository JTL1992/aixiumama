package com.harmazing.aixiumama.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.StickersImageAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.GridViewUtility;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;

import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.view.BorderScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  贴纸详情
 *  参数 stickerID (int)
 */
public class StickersActivity extends FragmentActivity {

    int stickerID;
    Button mostBtn,newBtn;
    BorderScrollView borderScrollView;
    ImageView labelIcon;
    TextView stickerName,stickerTxt;
    GridView stickerGridView;
    StickersImageAdapter stickersImageAdapter;
    JSONArray nextArray;
    boolean isExce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers);
        stickerGridView = (GridView)findViewById(R.id.gv_sticker_container);
        mostBtn = (Button)findViewById(R.id.the_most);
        newBtn = (Button)findViewById(R.id.the_new);
        stickerName = (TextView)findViewById(R.id.sticker_name);
        stickerTxt = (TextView)findViewById(R.id.sticker_text);
        labelIcon = (ImageView)findViewById(R.id.sticker_icon);
        borderScrollView = (BorderScrollView)findViewById(R.id.borderScrollView);
        stickerID = getIntent().getIntExtra("stickerID",0);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getLastSticker();
        newBtn.setTextColor(getResources().getColor(R.color.pink));
        LogUtil.v("sticker url", API.SINGLE_STICKER + stickerID);
        HttpUtil.get(API.SINGLE_STICKER + stickerID + "/?format=json",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    stickerName.setText(response.getString("name"));
                    stickerTxt.setText(response.getString("description"));
                    CuteApplication.downloadIamge(API.STICKERS + response.getString("banner_image"), labelIcon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                super.onSuccess(statusCode, headers, response);
            }
        });

        mostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostBtn.setTextColor(getResources().getColor(R.color.pink));
                newBtn.setTextColor(getResources().getColor(R.color.black));
                getHotSticker();
            }
        });

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostBtn.setTextColor(getResources().getColor(R.color.black));
                newBtn.setTextColor(getResources().getColor(R.color.pink));
                getLastSticker();
            }
        });

        borderScrollView.setOnBorderListener(new BorderScrollView.OnBorderListener() {
            @Override
            public void onBottom() {

                if(!isExce){
                    isExce = true;
                LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                if (CuteApplication.nextPageUrl.length() > 4) {
                    HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            findViewById(R.id.loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.no_more).setVisibility(View.GONE);
                            super.onStart();
                        }

                        @Override
                        public void onFinish() {
                            findViewById(R.id.loading).setVisibility(View.GONE);
                            super.onFinish();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                isExce = false;
                                nextArray = new JSONArray(response.getString("results"));
                                stickersImageAdapter.addKingdArray(nextArray);
                                GridViewUtility.setGridViewHeightByMySelf(stickerGridView, BitmapUtil.dip2px(getApplicationContext(), 80));
                                stickersImageAdapter.notifyDataSetChanged();

                                LogUtil.v("next", response.getString("next"));
                                if (response.getString("next").length() > 10) {
                                    CuteApplication.nextPageUrl = response.getString("next");
                                } else {
                                    LogUtil.v("next", "没有更多数据");
                                    CuteApplication.nextPageUrl = "";
                                    findViewById(R.id.no_more).setVisibility(View.VISIBLE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            super.onSuccess(statusCode, headers, response);
                        }
                    });
                } else {
                    LogUtil.v("next", "没有更多数据");
                    findViewById(R.id.no_more).setVisibility(View.VISIBLE);
                }

            }
            }

            @Override
            public void onTop() {
            }
        });
    }


    public void getHotSticker(){
        LogUtil.v("url@@@@@",API.GET_CUTES + "?sticker=" + stickerID + "&key=hot");
        HttpUtil.get(API.GET_CUTES + "?sticker=" + stickerID + "&key=hot", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    if (array.length() > 0) {
                        CuteApplication.nextPageUrl = response.getString("next");
                        stickersImageAdapter = new StickersImageAdapter(StickersActivity.this , array);
                        stickerGridView.setAdapter(stickersImageAdapter);
                        GridViewUtility.setGridViewHeightByMySelf(stickerGridView, BitmapUtil.dip2px(getApplicationContext(), 80));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    public void getLastSticker(){
        HttpUtil.get(API.GET_CUTES + "?sticker=" + stickerID + "&key=last", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    if (array.length() > 0) {
                        CuteApplication.nextPageUrl = response.getString("next");
                        stickersImageAdapter = new StickersImageAdapter(StickersActivity.this , array);
                        stickerGridView.setAdapter(stickersImageAdapter);
                        GridViewUtility.setGridViewHeightByMySelf(stickerGridView, BitmapUtil.dip2px(getApplicationContext(), 80));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stickers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
