package com.harmazing.aixiumama.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.AttentionLabelActivity;
import com.harmazing.aixiumama.activity.LabelActivity;
import com.harmazing.aixiumama.adapter.ShareLabelListAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.ListViewUtility;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/12/18.
 */
@SuppressLint("ValidFragment")
public class userRightFragment extends Fragment {

    String userID;
    String labelID1,labelID2,labelID3,labelID4;

    public userRightFragment(String id){
        userID = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.user_right, container, false);
        final ListView shareLabelList = (ListView)v.findViewById(R.id.share_label_list);
        shareLabelList .setOnScrollListener(new PauseOnScrollListener(CuteApplication.imageLoader,true,true));
        final ImageView attIV1,attIV2,attIV3,attIV4;
        attIV1 = (ImageView)v.findViewById(R.id.att_label1);
        attIV2 = (ImageView)v.findViewById(R.id.att_label2);
        attIV3 = (ImageView)v.findViewById(R.id.att_label3);
        attIV4 = (ImageView)v.findViewById(R.id.att_label4);
        RelativeLayout att_label_layout = (RelativeLayout)v.findViewById(R.id.att_label_layout);

        /**
         * 分享的标签
         */
        HttpUtil.get(API.CUTE_LABELS + "?user=" + userID + "&format=json", new JsonHttpResponseHandler() {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());

            @Override
            public void onStart() {
                progressDialog.setMessage("正在加载");
                progressDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    if (array.length() > 0) {
                        shareLabelList.setAdapter(new ShareLabelListAdapter(array, getActivity()));
                        ListViewUtility.setListViewHeightBasedOnChildren(shareLabelList);
                        //  还是listview 高度计算有偏差，就这样调了
                        int height = BitmapUtil.dip2px(getActivity(), 90);
                        CuteApplication.rightHeight = ListViewUtility.getListViewHeightByMySelf(shareLabelList, height) + BitmapUtil.dip2px(getActivity(), 170);   //因为要加上header 的高度
                        LogUtil.v("CuteApplication.rightHeight1", CuteApplication.rightHeight);
                        if (CuteApplication.rightHeight < CuteApplication.getScreenHW(getActivity())[1] / 2) {
                            CuteApplication.rightHeight = new Double(CuteApplication.getScreenHW(getActivity())[1] / 1.9).intValue();
                        }
//                                LogUtil.v("aaaaa",CuteApplication.getScreenHW(getActivity())[1] / 2);
//                                LogUtil.v("screenH",CuteApplication.getScreenHW(getActivity())[1]);
//                                LogUtil.v("CuteApplication.rightHeight2",CuteApplication.rightHeight);
                        if (CuteApplication.isRightHeight0) {
                            UserFragment.mViewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CuteApplication.rightHeight + 100));
                            CuteApplication.isRightHeight0 = false;
                        }
                        v.findViewById(R.id.att_layout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ToastUtil.show(getActivity(), "标签页");
                            }
                        });
                    } else {
                        CuteApplication.rightHeight = new Double(CuteApplication.getScreenHW(getActivity())[1] / 6.4).intValue();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                super.onSuccess(statusCode, headers, response);
            }
        });

        /**
         *  点击计入关注标签详情
         */
        att_label_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AttentionLabelActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });

        /**
         * 关注的标签
         */
        HttpUtil.get(API.FOLLOW_LABELS +"?user="+ userID + "&format=json",new JsonHttpResponseHandler(){

            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            @Override
            public void onStart() {
                progressDialog.setMessage("正在加载");
                progressDialog.show();
                super.onStart();
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    JSONObject obj;
                    for(int i=0;i<array.length();i++){
                        if(i == 0){
                            obj = (JSONObject)array.get(i);
                            obj = new JSONObject(obj.getString("follow_detail"));
                            labelID1 = obj.getString("id");
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"),attIV1);
                            attIV1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), LabelActivity.class);
                                    intent.putExtra("labelID",labelID1);
                                    startActivity(intent);
                                }
                            });


                        }else if(i == 1){
                            obj = (JSONObject)array.get(i);
                            obj = new JSONObject(obj.getString("follow_detail"));
                            labelID2 = obj.getString("id");
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"),attIV2);
                            attIV2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), LabelActivity.class);
                                    intent.putExtra("labelID",labelID2);
                                    startActivity(intent);
                                }
                            });

                        }else if(i == 2){
                            obj = (JSONObject)array.get(i);
                            obj = new JSONObject(obj.getString("follow_detail"));
                            labelID3 = obj.getString("id");
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"),attIV3);

                            attIV3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), LabelActivity.class);
                                    intent.putExtra("labelID",labelID3);
                                    startActivity(intent);
                                }
                            });
                        }else if(i == 4){
                            obj = (JSONObject)array.get(i);
                            obj = new JSONObject(obj.getString("follow_detail"));
                            labelID4 = obj.getString("id");
                            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"),attIV4);
                            attIV4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), LabelActivity.class);
                                    intent.putExtra("labelID",labelID4);
                                    startActivity(intent);
                                }
                            });
                        }else
                            break;


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });


        return v;
    }
}
