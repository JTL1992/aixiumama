package com.harmazing.aixiumama.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.UserLeftAdapter;
import com.harmazing.aixiumama.base.BaseGActivity;
import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.model.LeftUser;
import com.harmazing.aixiumama.parser.LeftUserParser;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.fragment.UserFragment2;
import com.harmazing.aixiumama.utils.RequestVo;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015/2/1.
 */
public class MyCutePhotoActivity extends BaseGActivity {
    ImageView backBtn;
    private String userID;
    GridView mGridView;
    private RequestVo userLeftRequestVo;
    private BaseGActivity.DataCallback userLeftCallback;
    private UserLeftAdapter userLeftAdapter;
    BaseGActivity.DataCallback<LeftUser> userLeftCallback2 =null;
    @Override
    protected void initContentView() {
        setContentView(R.layout.my_cute_photo_layout);
        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("user_id");
    }

    @Override
    protected void initView() {
        backBtn = (ImageView) findViewById(R.id.back);
        mGridView = (GridView) findViewById(R.id.gridView);
    }



    @Override
    protected void initRequestVo() {
        String requestUrl = API.GET_CUTES + "?user=" + userID+"&page_size=12";
        BaseParser leftUserParser = new LeftUserParser();
        userLeftRequestVo = new RequestVo(requestUrl, context, null, leftUserParser);
    }
    @Override
    protected void initCallBack() {
        userLeftCallback = new BaseGActivity.DataCallback<LeftUser>() {
            @Override
            public void processData(final LeftUser data) {
                if (data != null) {
                    if (UserFragment2.photosNumTV != null) {
                        UserFragment2.photosNumTV.setText(data.count + "");
                    }
                    if (PersonActivity.photosNumTV != null) {
                        PersonActivity.photosNumTV.setText(data.count + "");
                    }
                    if (userLeftAdapter == null) {
                        userLeftAdapter = new UserLeftAdapter(context, data.results);
                        mGridView.setAdapter(userLeftAdapter);
//                        bottomBtn.setVisibility(View.VISIBLE);
                    }
//                     else {
//                        userLeftAdapter.notifyDataSetChanged();
//                    }


                    if (data.next != null) {
                        RequestVo userLeftRequestVo2 = new RequestVo(data.next, context, null, new LeftUserParser());
                        userLeftRequestVo2.setDialogShowed(false);
                        final LeftUser lu = new LeftUser();
                         userLeftCallback2 = new DataCallback<LeftUser>() {
                            @Override
                            public void processData(LeftUser data2) {
                                  lu.addResultsList(data2.results);
//                                userLeftAdapter = new UserLeftAdapter(context, data.results);
                                if (data2.next != null){
                                    RequestVo userLeftRequestVo2 = new RequestVo(data2.next, context, null, new LeftUserParser());
                                    userLeftRequestVo2.setDialogShowed(false);
                                    getDataFromServer(userLeftRequestVo2, userLeftCallback2);
//                                    userLeftAdapter.addDataList(lu.results);
//                                    userLeftAdapter.notifyDataSetChanged();
//                                    closeProgressDialog();
                                }
                                else{
                                 userLeftAdapter.addDataList(lu.results);
                                 userLeftAdapter.notifyDataSetChanged();
                                }
                            }

                        };
                        getDataFromServer(userLeftRequestVo2, userLeftCallback2);
                        closeProgressDialog();
                    }
                }
                closeProgressDialog();
            }
        };
    }
    @Override
    protected void initData() {
        getDataFromServer(userLeftRequestVo, userLeftCallback);
        closeProgressDialog();
    }

    @Override
    protected void setListeners() {
    backBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });
    }

    @Override
    public void onClick(View view) {

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
