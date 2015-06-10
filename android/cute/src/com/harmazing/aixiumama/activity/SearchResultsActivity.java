package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.adapter.SearchResultsBrandImageAdapter;
import com.harmazing.aixiumama.adapter.SearchResultsImageAdapter2;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.adapter.SearchResultsListAdapter;
import com.harmazing.aixiumama.base.BaseMyGridView;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.ListViewUtility;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 显示 搜索结果
 * 参数 searchContent(String)
 */
public class SearchResultsActivity extends Activity {
    public static int USER = 1,BRAND = 2;
    ListView labelListview;
    BaseMyGridView userGridView,brandGridView;
    TextView userTV,breanTV,labelTV;
    SearchView mSearchView;
    TextView userNoMore,brandNoMore,labelNoMore;
    boolean isFirst = true;        //是否是第一次进来, 默认是
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
//        searchContent = getIntent().getStringExtra("searchContent");

        userTV = (TextView)findViewById(R.id.relative_user);
        labelListview = (ListView)findViewById(R.id.realtive_label_list);
        labelTV = (TextView) findViewById(R.id.relative_label);
        breanTV  = (TextView)  findViewById(R.id.relative_brand);
        userGridView = (BaseMyGridView)  findViewById(R.id.gridview_user);
        brandGridView = (BaseMyGridView)  findViewById(R.id.gridview_brand);
//        final View headerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_result_label_list_header, null);
//        labelListview.addHeaderView(headerView);

        /*
            搜索框
         */
        int searchPlateId = getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = findViewById(searchPlateId);
        searchPlate.setBackgroundColor(Color.WHITE);
        //  SearchView 里的 TextView
        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchEditText = (TextView) searchPlate.findViewById(searchSrcTextId);
        searchEditText.setTextSize(13);
        searchEditText.setGravity(Gravity.BOTTOM|Gravity.LEFT);

        mSearchView = (SearchView) findViewById(R.id.search);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.onActionViewExpanded();
        mSearchView.setFocusable(false);
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                doSearch(mSearchView.getQuery().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        userNoMore  = (TextView)  findViewById(R.id.user_no_more);
        brandNoMore = (TextView) findViewById(R.id.brand_no_more);
        labelNoMore = (TextView)findViewById(R.id.label_no_more);

        String content = getIntent().getStringExtra("searchContent");
        if(content != null && isFirst == true) {
            //跳转过来的第一次搜索
            doSearch(content);
            isFirst = false;
        }
    }
    private void doSearch(final String searchContent){
        /**
         * 初始化 no_noew 控件显示
         */
        userNoMore.setVisibility(View.GONE);
        brandNoMore.setVisibility(View.GONE);
        labelNoMore.setVisibility(View.GONE);
        /**
         *  adapter 适配器初始化
         */
        userGridView.setAdapter(null);
        brandGridView.setAdapter(null);
        labelListview.setAdapter(null);

        userTV.setText(searchContent + "  相关用户");
        breanTV.setText(searchContent + "  相关商标");
        labelTV.setText(searchContent + "  相关标签");


        LogUtil.i("SeachResultsActivity : ", API.GET_USER + "?search=" + searchContent);
        /**
         *  搜索用户
         */
        HttpUtil.get(API.GET_USER + "?search=" + searchContent,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    if(array.length() > 0){
                        userGridView.setAdapter(new SearchResultsImageAdapter2(SearchResultsActivity.this,array));
//                        GridViewUtility.setGridViewHeightOnChildren(userGridView);
                    }else{
//                        userGridView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                        userNoMore.setVisibility(View.VISIBLE);
                        userNoMore.setText("没有找到 "+searchContent +" 相关用户");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });

        LogUtil.i("SeachResultsActivity : " , API.GET_LABELS + "?search=" + searchContent + "&certificate=true");
        /**
         * 搜索商标
         */
        HttpUtil.get(API.GET_LABELS + "?search=" + searchContent + "&certificate=true",new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    if(array.length() > 0){
                        brandGridView.setAdapter(new SearchResultsBrandImageAdapter(SearchResultsActivity.this,array));
//                        GridViewUtility.setGridViewHeightOnChildren(brandGridView);
                    }else{
                        brandNoMore.setText("没有找到 "+searchContent +" 相关品牌");
                        brandNoMore.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });

        /**
         *  搜索普通标签
         */
        HttpUtil.get(API.GET_LABELS + "?search=" + searchContent,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = new JSONArray(response.getString("results"));
                    if(array.length() > 0){
                        labelListview.setAdapter(new SearchResultsListAdapter(array,SearchResultsActivity.this));
                        ListViewUtility.setListViewHeightBasedOnChildren(labelListview);
                    }else{
                        labelListview.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                        labelNoMore.setText("没有找到 "+searchContent +" 相关标签");
                        labelNoMore.setVisibility(View.VISIBLE);
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
        getMenuInflater().inflate(R.menu.search_results, menu);
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
}
