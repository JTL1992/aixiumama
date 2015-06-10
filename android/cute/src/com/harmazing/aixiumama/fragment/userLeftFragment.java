package com.harmazing.aixiumama.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.adapter.ImageAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.GridViewUtility;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
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
public class userLeftFragment extends Fragment {
    String userID;

    public userLeftFragment(String id){
        userID = id;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.gridview_left, container, false);
        final GridView gridView = (GridView)v.findViewById(R.id.gridview);
        gridView.setOnScrollListener(new PauseOnScrollListener(CuteApplication.imageLoader,true,true));
        HttpUtil.get(API.GET_CUTES + "?user=" + userID, new JsonHttpResponseHandler() {

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
                        CuteApplication.userInfoArray = array;
                        gridView.setAdapter(new ImageAdapter(getActivity(), array));
                        GridViewUtility.setGridViewHeightByMySelf(gridView, BitmapUtil.dip2px(getActivity(), 80));
                        CuteApplication.leftHeight = GridViewUtility.getGridViewHeight(gridView, BitmapUtil.dip2px(getActivity(), 80));
                        LogUtil.v("CuteApplication.centerHeight22", CuteApplication.centerHeight);

                        if (userID.equals(AppSharedPref.newInstance(getActivity()).getUserId()))
                            AppSharedPref.newInstance(getActivity()).setDataInt("UserPhotoNum", array.length());
                        else
                            PersonActivity.photosNumTV.setText(array.length() + "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                super.onSuccess(statusCode, headers, response);
            }
        });
        return v;
    }
}
