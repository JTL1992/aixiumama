package com.harmazing.aixiumama.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.adapter.UserLeftAdapter;
import com.harmazing.aixiumama.base.BaseFragment;
import com.harmazing.aixiumama.base.BaseParser;
import com.harmazing.aixiumama.model.LeftUser;
import com.harmazing.aixiumama.parser.LeftUserParser;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.MyCutePhotoActivity;
import com.harmazing.aixiumama.base.BaseMyGridView;
import com.harmazing.aixiumama.utils.RequestVo;

/**
 * Created by dell on 2014/12/19.
 */
@SuppressLint("ValidFragment")
public class UserLeftFragment2 extends BaseFragment {
    Button bottomBtn;
    private RequestVo userLeftRequestVo;
    private DataCallback userLeftCallback;
    private BaseMyGridView gridView;
    private String userID;
    DataCallback<LeftUser>  userLeftCallback2 =null;
    private UserLeftAdapter userLeftAdapter;

    public UserLeftFragment2(){}

    public UserLeftFragment2(String userID) {
        this.userID = userID;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bottomBtn = (Button) getActivity().findViewById(R.id.btn);
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyCutePhotoActivity.class);
                intent.putExtra("user_id",userID);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    protected int setMyContentView() {
        return R.layout.gridview_left2;
    }

    @Override
    protected void initRequestVo() {
        String requestUrl = API.GET_CUTES + "?user=" + userID+"&page_size=12";
        BaseParser leftUserParser = new LeftUserParser();
        userLeftRequestVo = new RequestVo(requestUrl, context, null, leftUserParser);
    }
    @Override
    protected void initCallBall() {
        userLeftCallback = new DataCallback<LeftUser>() {
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
                        gridView.setAdapter(userLeftAdapter);
                        if(data.results.size() >= 12) {
                            bottomBtn.setVisibility(View.VISIBLE);
                        }
                    }
//                     else {
//                        userLeftAdapter.notifyDataSetChanged();
//                    }


                    if (data.next != null) {
                        RequestVo userLeftRequestVo2 = new RequestVo(data.next, context, null, new LeftUserParser());
                        userLeftRequestVo2.setDialogShowed(false);
                       final LeftUser lu = new LeftUser();
//                         userLeftCallback2 = new DataCallback<LeftUser>() {
//                            @Override
//                            public void processData(LeftUser data2) {
//                                  lu.addResultsList(data2.results);
////                                userLeftAdapter = new UserLeftAdapter(context, data.results);
//                                if (data2.next != null){
//                                    RequestVo userLeftRequestVo2 = new RequestVo(data2.next, context, null, new LeftUserParser());
//                                    userLeftRequestVo2.setDialogShowed(false);
//                                    getDataFromServer(userLeftRequestVo2, userLeftCallback2);
////                                    closeProgressDialog();
//                                }
//                                else{
//                                 userLeftAdapter.addDataList(lu.results);
//                                 userLeftAdapter.notifyDataSetChanged();
//                                }
//                            }
//
//                        };
//                        getDataFromServer(userLeftRequestVo2, userLeftCallback2);
//                        closeProgressDialog();
                    }
                }
                closeProgressDialog();
            }
        };
    }

    @Override
    protected void initView() {
        gridView = (BaseMyGridView)view.findViewById(R.id.gridview);
    }

    @Override
    protected void initData() {
        getDataFromServer(userLeftRequestVo, userLeftCallback);
        closeProgressDialog();
    }

    @Override
    protected void setListeners() {

    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn){
            Intent intent = new Intent(getActivity(), MyCutePhotoActivity.class);
            intent.putExtra("user_id",userID);
            getActivity().startActivity(intent);
        }
    }
}
