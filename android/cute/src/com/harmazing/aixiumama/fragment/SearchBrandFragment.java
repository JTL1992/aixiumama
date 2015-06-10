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
import com.harmazing.aixiumama.activity.LabelActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.base.BaseMyAdapter;
import com.harmazing.aixiumama.model.SearchResultBrand;
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
public class SearchBrandFragment extends BaseMyFragment {

    private ListView listview_brand;
    private TextView tv_search_result_none;
    private String searchContent;

    private List<SearchResultBrand.SearchResultBrandResult> brandList = new ArrayList<SearchResultBrand.SearchResultBrandResult>();

    private SearchBrandAdapter searchBrandAdapter;

    public SearchBrandFragment(String searchContent) {
        super();
        this.searchContent = searchContent;
    }

    @Override
    public View initView(LayoutInflater inflater) {

        view = inflater.inflate(R.layout.fragment_brand_search, null);

        listview_brand = (ListView) view.findViewById(R.id.listview_brand);

        tv_search_result_none = (TextView) view.findViewById(R.id.tv_search_result_none);

        return view;
    }

    @Override
    public void initData() {

        String data = SharedPreferencesUtil.getStringData(context,API.GET_LABELS + "?search=" + searchContent, null);
        if(!TextUtils.isEmpty(data)) { // 请求网络之前先判断本地有没有数据
            parseData(data);
        }

        getDataFromServer(searchContent);
    }

    private void getDataFromServer(final String searchContent) {
        if(!TextUtils.isEmpty(searchContent)) {
            getData(HttpRequest.HttpMethod.GET, API.GET_LABELS + "?search=" + searchContent + "&certificate=true", new RequestParams(), new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                    //保存到本地
                    //SharedPreferencesUtil.SaveStringData(context, API.GET_USER+"?search="+searchContent, stringResponseInfo.result);

                    System.out.println("gyw　　" + stringResponseInfo.result);

                    //解析数据
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

        SearchResultBrand searchResultBrand = GsonUtil.json2Bean(result, SearchResultBrand.class);

        if(searchResultBrand.results.size() > 0) {
            brandList.addAll(searchResultBrand.results);
        } else {
            tv_search_result_none.setVisibility(View.VISIBLE);
        }

        //填充布局
        if(searchBrandAdapter == null) {
            searchBrandAdapter = new SearchBrandAdapter(context, brandList);
            listview_brand.setAdapter(searchBrandAdapter);
        } else {
            searchBrandAdapter.notifyDataSetChanged();
        }
    }

    private class SearchBrandAdapter extends BaseMyAdapter<SearchResultBrand.SearchResultBrandResult> {


        public SearchBrandAdapter(Context context, List<SearchResultBrand.SearchResultBrandResult> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if(convertView == null) {
                convertView = View.inflate(context, R.layout.search_brand_item, null);
                holder = new ViewHolder();
                holder.rl_search_result_brand = (RelativeLayout) convertView.findViewById(R.id.rl_search_result_brand);
                holder.iv_brand = (ImageView) convertView.findViewById(R.id.iv_brand);
                holder.tv_brand_name = (TextView) convertView.findViewById(R.id.tv_brand_name);
                holder.tv_brand_desc = (TextView) convertView.findViewById(R.id.tv_brand_desc);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //图像
            CuteApplication.downloadCornImage(API.STICKERS + list.get(position).image_small, holder.iv_brand);
            //名字
            holder.tv_brand_name.setText(list.get(position).name);
            //生日
            holder.tv_brand_desc.setText("已有"+list.get(position).usage_count+"张图片  "+list.get(position).follow_count+"人关注");


            holder.labelID = list.get(position).id;

            //根据id跳转
            holder.rl_search_result_brand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, LabelActivity.class);
                    intent.putExtra("labelID", holder.labelID);
                    context.startActivity(intent);
                }
            });

            return convertView;
        }

    }
    static class ViewHolder {
        public RelativeLayout rl_search_result_brand;
        public ImageView iv_brand;
        public TextView tv_brand_name;
        public TextView tv_brand_desc;
        public String labelID;
    }
}
