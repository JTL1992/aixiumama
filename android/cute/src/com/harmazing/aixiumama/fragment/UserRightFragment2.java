package com.harmazing.aixiumama.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.AttentionLabelActivity;
import com.harmazing.aixiumama.adapter.UserRightAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.base.BaseFragment;
import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.model.RightUserFollowLabel;
import com.harmazing.aixiumama.model.RightUserlabels;
import com.harmazing.aixiumama.parser.RightUserFollowLabelParser;
import com.harmazing.aixiumama.parser.RightUserParser;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.adapter.UserRightFollowLabelAdapter;
import com.harmazing.aixiumama.utils.RequestVo;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.ListViewUtility;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * Created by gyw on 2014/12/19.
 */
@SuppressLint("ValidFragment")
public class UserRightFragment2 extends BaseFragment {

    private String userID;
    private RequestVo userRightRequestVo;
    private DataCallback<RightUserlabels> userRightCallBack;

    private RequestVo rightUserFollowLabelRequestVo;
    private DataCallback<RightUserFollowLabel> rightUserFollowLabelCallBack;

    private ListView share_label_list;
    private GridView gv_follow_label;

    private RelativeLayout att_label_layout;

    private UserRightAdapter userRightAdapter;
    private UserRightFollowLabelAdapter userRightFollowLabelAdapter;

    public UserRightFragment2(String userID) {
        this.userID = userID;
    }

    @Override
    protected int setMyContentView() {
        return R.layout.user_right2;
    }


    protected void initRequestVo() {
        String requestUrl = API.CUTE_LABELS + "?user=" + userID + "&format=json";
        BaseParser rightUserParser = new RightUserParser();
        userRightRequestVo = new RequestVo(requestUrl, context, null, rightUserParser);

        /**
         * 关注的标签
         */
        String requestUrl2 = API.FOLLOW_LABELS +"?user="+ userID + "&format=json";
        BaseParser rightUserFollowLabelParser = new RightUserFollowLabelParser();
        rightUserFollowLabelRequestVo = new RequestVo(requestUrl2, context, null, rightUserFollowLabelParser);
    };

    @Override
    protected void initCallBall() {
        userRightCallBack = new DataCallback<RightUserlabels>() {
            @Override
            public void processData(RightUserlabels data) {
                if (data != null) {
                    //System.out.println("gyw1 :　" + data.toString());
                    if(userRightAdapter == null) {
                        userRightAdapter =  new UserRightAdapter(context, data.results);
                        share_label_list.setAdapter(userRightAdapter);
                    } else {
                        share_label_list.deferNotifyDataSetChanged();
                    }

                    ListViewUtility.setListViewHeightBasedOnChildren(share_label_list);
                    //  还是listview 高度计算有偏差，就这样调了
                    int height = BitmapUtil.dip2px(context, 90);
                    CuteApplication.rightHeight = ListViewUtility.getListViewHeightByMySelf(share_label_list, height) + BitmapUtil.dip2px(context, 170);   //因为要加上header 的高度
                    LogUtil.v("CuteApplication.rightHeight1", CuteApplication.rightHeight);
                }
                closeProgressDialog();
            }
        };

        /**
         * 关注的标签
         */
        rightUserFollowLabelCallBack = new DataCallback<RightUserFollowLabel>() {
            public void processData(RightUserFollowLabel data) {
                if(data != null) {
                    //System.out.println("gyw2 :　" + data.toString());
                    if(userRightFollowLabelAdapter == null) {
                        userRightFollowLabelAdapter = new UserRightFollowLabelAdapter(context, data.results);
                        gv_follow_label.setAdapter(userRightFollowLabelAdapter);
                    } else {
                        userRightFollowLabelAdapter.notifyDataSetChanged();
                    }
                }
                closeProgressDialog();
            }
        };
    }

    @Override
    protected void initView() {
        share_label_list = (ListView)view.findViewById(R.id.share_label_list);
        share_label_list .setOnScrollListener(new PauseOnScrollListener(CuteApplication.imageLoader,true,true));

        gv_follow_label = (GridView) view.findViewById(R.id.gv_follow_label);



        //关注标签详情
        att_label_layout = (RelativeLayout)view.findViewById(R.id.att_label_layout);

    }

    @Override
    protected void initData() {
        getDataFromServer(userRightRequestVo, userRightCallBack);
        getDataFromServer(rightUserFollowLabelRequestVo, rightUserFollowLabelCallBack);
    }

    @Override
    protected void setListeners() {
        att_label_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.att_label_layout:     //点击计入关注标签详情
                Intent intent = new Intent(getActivity(),AttentionLabelActivity.class);
                intent.putExtra("userID",Integer.parseInt(userID));
                startActivity(intent);
                break;
        }
    }
}
