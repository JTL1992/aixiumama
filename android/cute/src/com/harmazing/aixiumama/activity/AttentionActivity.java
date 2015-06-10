package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.FollowUserListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 个人中心跳转到的 用户关注页
 * 参数： userID userName
 */
public class AttentionActivity extends Activity {

    ImageView attIV1,attIV2,attIV3,attIV4;
    int userID;
    String userName;
    TextView topName;
    ListView followUserList;
    String labelID1,labelID2,labelID3;
    public static int USER = 5;

    View addView;
    JSONArray nextArray;
    FollowUserListAdapter followUserListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        final LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(R.layout.attention_headview,null);

        attIV1 = (ImageView)headerView.findViewById(R.id.att_label1);
        attIV2 = (ImageView)headerView.findViewById(R.id.att_label2);
        attIV3 = (ImageView)headerView.findViewById(R.id.att_label3);
        attIV4 = (ImageView)headerView.findViewById(R.id.att_label4);
        topName = (TextView)findViewById(R.id.label_name);
        followUserList = (ListView)findViewById(R.id.follow_users);


        followUserList.addHeaderView(headerView);

        userID = getIntent().getIntExtra("userID",0);
        userName = getIntent().getStringExtra("userName");
        topName.setText(userName + "的关注");

        /**
         *  关注的其他用户
         */
        HttpUtil.get(API.ADD_FRIENDS + "?user=" + userID, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray array = null;
                try {
                    array = new JSONArray(response.getString("results"));
                    if(array.length() > 0) {
                        followUserListAdapter =  new FollowUserListAdapter(array,AttentionActivity.this,USER);
                        followUserList.setAdapter(followUserListAdapter);
                        CuteApplication.nextPageUrl = response.getString("next");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });

        followUserList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        addView = inflater.inflate(R.layout.loading, view, false);
                        LogUtil.v("CuteApplication.nextPageUrl", CuteApplication.nextPageUrl);
                        if (CuteApplication.nextPageUrl.length() > 4) {
                            HttpUtil.get(CuteApplication.nextPageUrl, new JsonHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    if (followUserList.getFooterViewsCount() < 1)
                                        followUserList.addFooterView(addView);
                                    super.onStart();
                                }


                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    try {
                                        ToastUtil.show(AttentionActivity.this, "没有更多了");
                                        followUserList.removeFooterView(addView);
                                        followUserList.requestFocus();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        LogUtil.e("cute推荐", "三星note3");
                                    }
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                }



                                @Override
                                public void onFinish() {
                                    try {
                                        followUserList.removeFooterView(addView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        LogUtil.e("cute推荐", "三星note3");
                                    }
                                    super.onFinish();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try { followUserList.removeFooterView(addView);
                                        nextArray = new JSONArray(response.getString("results"));

                                        followUserListAdapter.addKingdArray(nextArray);
                                        followUserListAdapter.notifyDataSetChanged();

                                        LogUtil.v("next", response.getString("next"));
                                        if (response.getString("next").length() > 10) {
                                            CuteApplication.nextPageUrl = response.getString("next");
                                            followUserList.removeFooterView(addView);
                                        } else {
//                                            ToastUtil.show(AttentionActivity.this,"没有更多了");
                                            LogUtil.v("next", "没有更多数据");
                                            CuteApplication.nextPageUrl = "";
                                            followUserList.removeFooterView(addView);
                                            addView = inflater.inflate(R.layout.no_more, view, false);
                                            if(followUserList.getFooterViewsCount() == 0)
                                            followUserList.addFooterView(addView);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    super.onSuccess(statusCode, headers, response);
                                }
                            });
                        }
                        else{
                            if(followUserList.getFooterViewsCount() == 0){
                                addView = inflater.inflate(R.layout.no_more, view, false);
                                followUserList.addFooterView(addView);
                            }

                        }
                    }
                }

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }
        });
        
        
        /**
         *  点击计入关注标签详情
         */
        headerView.findViewById(R.id.att_label_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttentionActivity.this,AttentionLabelActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });

        /**
         * 关注的标签（已经认证的商标）
         */
        HttpUtil.get(API.FOLLOW_LABELS + "?user=" + userID + "&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    JSONObject obj;
                    for (int i = 0; i < array.length(); i++) {
                        if (i == 0) {
                            obj = (JSONObject) array.get(i);
                            obj = new JSONObject(obj.getString("follow_detail"));
                             labelID1 = obj.getString("id");
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), attIV1);
                            attIV1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(AttentionActivity.this, LabelActivity.class);
                                    intent.putExtra("labelID",labelID1);
                                    startActivity(intent);
                                }
                            });
                        } else if (i == 1) {
                            obj = (JSONObject) array.get(i);
                            obj = new JSONObject(obj.getString("follow_detail"));
                           labelID2 = obj.getString("id");
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), attIV2);
                            attIV2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(AttentionActivity.this, LabelActivity.class);
                                    intent.putExtra("labelID",labelID2);
                                    startActivity(intent);
                                }
                            });
                        } else if (i == 2) {
                            obj = (JSONObject) array.get(i);
                            obj = new JSONObject(obj.getString("follow_detail"));
                            labelID3 = obj.getString("id");
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), attIV3);
                            attIV3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(AttentionActivity.this, LabelActivity.class);
                                    intent.putExtra("labelID",labelID3);
                                    startActivity(intent);
                                }
                            });
                        } else if (i == 4) {
                            obj = (JSONObject) array.get(i);
                            obj = new JSONObject(obj.getString("follow_detail"));
                            final String labelID = obj.getString("id");
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), attIV4);
                            attIV4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(AttentionActivity.this, LabelActivity.class);
                                    intent.putExtra("labelID",labelID);
                                    startActivity(intent);
                                }
                            });
                        } else
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                super.onSuccess(statusCode, headers, response);
            }
        });



        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        HttpUtil.get(API.ADD_FRIENDS + "?user=" + userID, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray array = null;
                try {
                    array = new JSONArray(response.getString("results"));
                    if(array.length() > 0) {
                        followUserListAdapter =  new FollowUserListAdapter(array,AttentionActivity.this,USER);
                        followUserList.setAdapter(followUserListAdapter);
                        CuteApplication.nextPageUrl = response.getString("next");
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
        getMenuInflater().inflate(R.menu.attention, menu);
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
