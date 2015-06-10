package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/22.
 */
public class LabelsListImageAdapter extends BaseAdapter {

    private Context mContext;
    JSONArray kindArray;

    public LabelsListImageAdapter(Context c,JSONArray array) {
        mContext = c;
        kindArray  = array;
    }

    @Override
    public int getCount() {
        return kindArray.length();
    }

    @Override
    public Object getItem(int position) {
        return  kindArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        try {
            return ((JSONObject) getItem(position)).getInt("user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GirdTemp temp;

        if (convertView == null) {
            convertView = View.inflate(mContext,R.layout.gridview_follow_item, null);
            temp = new GirdTemp();
            temp.iv_gridview = (ImageView) convertView.findViewById(R.id.iv_gridview);
            temp.tv_text =(TextView)convertView.findViewById(R.id.tv_text);

            try {
                temp.labelID = ((JSONObject) getItem(position)).getInt("follow");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            convertView.setTag(temp);
        } else {
            temp = (GirdTemp) convertView.getTag();
        }

        try {
            final JSONObject obj = new JSONObject(((JSONObject) getItem(position)).getString("follow_detail"));


            CuteApplication.downloadIamge(API.STICKERS + obj.getString("image"), temp.iv_gridview);
            temp.tv_text.setText("已关注");
            temp.tv_text.setBackgroundColor(mContext.getResources().getColor(R.color.light_grey));

            /**
             *  判断当前用户是否关注了这些标签
             */
            HttpUtil.get(API.FOLLOW_LABELS + "?user=" + AppSharedPref.newInstance(mContext).getUserId() + "&follow=" + temp.labelID,new JsonHttpResponseHandler(){
                boolean likedState = false;
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if(response.getInt("count") == 1){

                            temp.tv_text.setText("已关注");
                            temp.tv_text.setBackgroundColor(mContext.getResources().getColor(R.color.light_grey));
                            likedState = true;

                        }else{
                            temp.tv_text.setText("关注");
                            temp.tv_text.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
                            likedState = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    temp.tv_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(! likedState){
                                ToastUtil.show(mContext, "关注成功！");
                                temp.tv_text.setText("已关注");
                                temp.tv_text.setBackgroundColor(mContext.getResources().getColor(R.color.light_grey));
                                likedState = true;
                                likedState =true;
                            }else{
                                temp.tv_text.setText("关注");
                                temp.tv_text.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
                                likedState = false;
                                ToastUtil.show(mContext, "取消关注");
                                likedState =false;
                            }

                            RequestParams params = new RequestParams();
                            params.put("user", Integer.parseInt(AppSharedPref.newInstance(mContext).getUserId()));
                            params.put("follow",temp.labelID);
                            HttpUtil.post(API.FOLLOW_LABELS, params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    if (statusCode == 201) {
                                        LogUtil.v("关注成功", "");

                                    } else if (statusCode == 204) {
                                        LogUtil.v("取消关注", "");
                                    }

                                    super.onSuccess(statusCode, headers, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    ToastUtil.show(mContext, "网络异常");
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                }
                            });
                        }
                    });



                    super.onSuccess(statusCode, headers, response);
                }
            });




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }


    private class GirdTemp {
        ImageView iv_gridview;
        TextView tv_text;
        int labelID;
    }

}
