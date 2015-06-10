package com.harmazing.aixiumama.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.fragment.SearchBrandFragment;
import com.harmazing.aixiumama.fragment.SearchLabelFragment;
import com.harmazing.aixiumama.fragment.SearchUserFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * guoyongwei
 * 显示搜索的结果, 仿照in的界面
 */
public class SearchResultsActivity2 extends FragmentActivity {


    private String searchContent;

    private SearchView search_view;
    private Button btn_back;

    private RadioGroup mRadioGroup;

    private RadioButton rb_search_user;
    private RadioButton rb_search_brand;
    private RadioButton rb_search_label;

    private FrameLayout fl_container_search_result;

    private FragmentManager fragmentManager;

    private Fragment searchUserFragment;
    private Fragment searchBrandFragment;
    private Fragment searchLabelFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results2);

        initView();
        initData();

        //默认选中第一个
        //setSearchSelection(0);
    }



    private void initView() {
        search_view = (SearchView)findViewById(R.id.search_view);
        btn_back = (Button) findViewById(R.id.btn_back);

        mRadioGroup = (RadioGroup) findViewById(R.id.rg_search);

        rb_search_user = (RadioButton) findViewById(R.id.rb_search_user);
        rb_search_brand = (RadioButton) findViewById(R.id.rb_search_brand);
        rb_search_label = (RadioButton) findViewById(R.id.rb_search_label);

        fl_container_search_result = (FrameLayout) findViewById(R.id.fl_container_search_result);

        fragmentManager = getSupportFragmentManager();

    }

    private void initData() {
        //从上一个界面传过来的值
        searchContent = getIntent().getStringExtra("searchContent");

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_search_user:   //用户
                        setSearchSelection(0);
                        break;
                    case R.id.rb_search_brand:  //品牌
                        setSearchSelection(1);
                        break;
                    case R.id.rb_search_label:  //标签
                        setSearchSelection(2);
                        break;
                }
            }
        });

        //默认选中第一个
        mRadioGroup.check(R.id.rb_search_user);

        /**
         * 搜索框
         */
        int searchPlateId = getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = findViewById(searchPlateId);
        searchPlate.setBackgroundColor(Color.WHITE);
        //  SearchView 里的 TextView
        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchEditText = (TextView) searchPlate.findViewById(searchSrcTextId);
        searchEditText.setTextSize(13);
        searchEditText.setGravity(Gravity.BOTTOM|Gravity.LEFT);

        search_view.onActionViewExpanded();
        search_view.setFocusable(false);
        search_view.clearFocus();
        search_view.setQuery(searchContent, true);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (search_view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(search_view.getWindowToken(), 0);
                    }
                    search_view.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s != null && s.length() > 0 ) {
                    System.out.println("gywgyw "+s);

                    searchContent = s;

                    int radioButtonId = mRadioGroup.getCheckedRadioButtonId();
                    //System.out.println("radioButtonId  " + radioButtonId + " : " + (R.id.rb_search_user == radioButtonId));
                    switch (radioButtonId) {
                        case R.id.rb_search_user:   //用户
                            setSearchSelection(0);
                            break;
                        case R.id.rb_search_brand:  //品牌
                            setSearchSelection(1);
                            break;
                        case R.id.rb_search_label:  //标签
                            setSearchSelection(2);
                            break;
                    }
                    //显示第一个搜索结果
                    //setSearchSelection(radioButtonId);
                }

                return true;
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //显示的
    private void setSearchSelection(int index) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        hideFragments(ft);
        switch (index) {
            case 0: // 用户
//                if (searchUserFragment == null) {
                    // 如果searchUserFragment为空，则创建一个并添加到界面上
                    searchUserFragment = new SearchUserFragment(searchContent);
                    ft.add(R.id.fl_container_search_result, searchUserFragment);
//                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    ft.show(searchUserFragment);
//                }
                break;
            case 1: //品牌
                    searchBrandFragment = new SearchBrandFragment(searchContent);
                    ft.add(R.id.fl_container_search_result, searchBrandFragment);
                    ft.show(searchBrandFragment);
                break;
            case 2: //标签
                    searchLabelFragment = new SearchLabelFragment(searchContent);
                    ft.add(R.id.fl_container_search_result, searchLabelFragment);
                    ft.show(searchLabelFragment);
                break;
        }

        ft.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     */
    private void hideFragments(FragmentTransaction ft) {
        if (searchUserFragment != null) {
            ft.hide(searchUserFragment);
        }
        if (searchBrandFragment != null) {
            ft.hide(searchBrandFragment);
        }
        if (searchLabelFragment != null) {
            ft.hide(searchLabelFragment);
        }
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


