package com.harmazing.aixiumama.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ListView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.UserCenterAdapter;
import com.harmazing.aixiumama.base.BaseFragment;
import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.model.LeftUser;
import com.harmazing.aixiumama.parser.LeftUserParser;
import com.harmazing.aixiumama.utils.ListViewUtility;
import com.harmazing.aixiumama.utils.RequestVo;
import com.harmazing.aixiumama.R;

/**
 * Created by gyw on 2014/12/19.
 */
@SuppressLint("ValidFragment")
public class UserCenterFragment2 extends BaseFragment {

    private String userID;
    private RequestVo userLeftRequestVo;
    private DataCallback userLeftCallback;
    private ListView user_center_listview;
    private UserCenterAdapter userCenterAdapter;

    UserCenterFragment2(String userID) {
        this.userID = userID;
    }

    protected int setMyContentView() {
        return R.layout.attention2;
    }

    @Override
    protected void initRequestVo() {
        String requestUrl = API.GET_CUTES + "?user=" + userID;
        BaseParser leftUserParser = new LeftUserParser();
        userLeftRequestVo = new RequestVo(requestUrl, context, null, leftUserParser);
    }


    @Override
    protected void initCallBall() {
        userLeftCallback = new DataCallback<LeftUser>(){
            @Override
            public void processData(LeftUser data) {
                if (data != null) {
                    if(userCenterAdapter == null) {
                        userCenterAdapter = new UserCenterAdapter(context, data.results);
                        user_center_listview.setAdapter(userCenterAdapter);
                    } else {
                        userCenterAdapter.notifyDataSetChanged();
                    }
                    ListViewUtility.setListViewHeightBasedOnChildren(user_center_listview);
                }
                closeProgressDialog();
            }
        };

    }

    @Override
    protected void initView() {
        user_center_listview = (ListView) view.findViewById(R.id.user_center_listview);
    }

    @Override
    protected void initData() {
        getDataFromServer(userLeftRequestVo, userLeftCallback);
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void onClick(View view) {

    }
}
