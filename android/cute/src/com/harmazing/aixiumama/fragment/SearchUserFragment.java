package com.harmazing.aixiumama.fragment;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.base.BaseMyAdapter;
import com.harmazing.aixiumama.model.SearchResultUser;
import com.harmazing.aixiumama.utils.GsonUtil;
import com.harmazing.aixiumama.utils.SharedPreferencesUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoyongwei on 2015/2/14.
 */
public class SearchUserFragment extends BaseMyFragment {

    private ListView listview_user;
    private TextView tv_search_result_none;
    private String searchContent;

    private List<SearchResultUser.SearchResultUserResult> userList = new ArrayList<SearchResultUser.SearchResultUserResult>();

    private SearchUserAdapter searchUserAdapter;

    public SearchUserFragment(String searchContent) {
        super();
        this.searchContent = searchContent;
    }

    @Override
    public View initView(LayoutInflater inflater) {

        view = inflater.inflate(R.layout.fragment_user_search, null);

        listview_user = (ListView) view.findViewById(R.id.listview_user);

        tv_search_result_none = (TextView) view.findViewById(R.id.tv_search_result_none);

        return view;
    }

    @Override
    public void initData() {

        /*listview_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/

        String data = SharedPreferencesUtil.getStringData(context, API.GET_USER+"?search="+searchContent, null);
        if(!TextUtils.isEmpty(data)) { // 请求网络之前先判断本地有没有数据
            parseData(data);
        }

        getDataFromServer(searchContent);
    }

    private void getDataFromServer(final String searchContent) {
        if(!TextUtils.isEmpty(searchContent)) {
            getData(HttpRequest.HttpMethod.GET, API.GET_USER + "?search=" + searchContent, new RequestParams(), new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                    //保存到本地
                    //SharedPreferencesUtil.SaveStringData(context, API.GET_USER+"?search="+searchContent, stringResponseInfo.result);
                    //解析数据
//                    System.out.println("gyw123" + stringResponseInfo.result);
                    parseData(stringResponseInfo.result);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    e.printStackTrace();
                }
            });
        } else {
            //没有数据了
            ToastUtil.show(context, "没有更多数据了!");
        }
    }

    //解析数据
    private void parseData(String result) {

        SearchResultUser searchResultUser = GsonUtil.json2Bean(result, SearchResultUser.class);

        if(searchResultUser.results.size() > 0) {
            userList.addAll(searchResultUser.results);
        } else {
            tv_search_result_none.setVisibility(View.VISIBLE);
        }

        //填充布局
        if(searchUserAdapter == null) {
            searchUserAdapter = new SearchUserAdapter(context, userList);
            listview_user.setAdapter(searchUserAdapter);
        } else {
            searchUserAdapter.notifyDataSetChanged();
        }

    }

    private class SearchUserAdapter extends BaseMyAdapter<SearchResultUser.SearchResultUserResult> {

        public SearchUserAdapter(Context context, List<SearchResultUser.SearchResultUserResult> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if(convertView == null) {
                convertView = View.inflate(context, R.layout.search_user_item, null);
                holder = new ViewHolder();
                holder.rl_search_result_user = (RelativeLayout) convertView.findViewById(R.id.rl_search_result_user);
                holder.iv_user = (ImageView) convertView.findViewById(R.id.iv_user);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_age = (TextView) convertView.findViewById(R.id.tv_age);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //图像
            CuteApplication.downloadCornImage(API.STICKERS + list.get(position).image_small, holder.iv_user);
            //名字
            holder.tv_name.setText(list.get(position).name);
            //生日
            if(list.get(position).babies.size() > 0) {
                holder.tv_age.setText(CuteApplication.date2Age(list.get(position).babies.get(0).birthday));
            } else {
                holder.tv_age.setText("未知");
            }

            //每个人的id
            holder.person_id = list.get(position).auth_user;

            //根据id跳转
            holder.rl_search_result_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PersonActivity.class);
                    intent.putExtra("person_id", holder.person_id);
                    context.startActivity(intent);
                }
            });


            return convertView;
        }

    }
    static class ViewHolder {
        public RelativeLayout rl_search_result_user;
        public ImageView iv_user;
        public TextView tv_name;
        public TextView tv_age;
        public int person_id;
    }
}
