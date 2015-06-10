package com.harmazing.aixiumama.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by liujinghui on 11/2/14.
 */
public class UserProfileStart extends FragmentActivity implements View.OnClickListener {

    View mCurrentSelectView;
    //1 : man
    //2 : feman
    //0 : other
    int mCurrentSex = 1;
    String date = null;
    Calendar calendar;
    Button choice,button_finish;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        button_finish = (Button)findViewById(R.id.button_finish);
        findViewById(R.id.button_finish).setOnClickListener(this);
        mCurrentSelectView = findViewById(R.id.man);
        mCurrentSelectView.setSelected(true);
        calendar = Calendar.getInstance();
        findViewById(R.id.man).setOnClickListener(this);
        findViewById(R.id.feman).setOnClickListener(this);
        findViewById(R.id.other).setOnClickListener(this);
        choice = (Button)findViewById(R.id.choice);
        findViewById(R.id.choice).setOnClickListener(this);
        findViewById(R.id.titleBar).findViewById(R.id.left_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_finish) {

            if(date == null){
                ToastUtil.show(UserProfileStart.this, "请选择时间");
                return;
            }
            button_finish.setEnabled(false);

            /*
            1.gender：性别, 0, 1, 2
            2.birthday：生日， 示例2014-10-10
            3.parent:当前用户id
             */
            HttpUtil.addClientHeader(getApplicationContext());
            RequestParams params = new RequestParams();
            params.put("gender",mCurrentSex);
            params.put("birthday",date);
            params.put("parent", AppSharedPref.newInstance(getApplicationContext()).getUserId());

            HttpUtil.post(API.POST_BABY,params,new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    AppSharedPref.newInstance(UserProfileStart.this).setBirthday(date);
                    AppSharedPref.newInstance(UserProfileStart.this).setUserSex(mCurrentSex);
                    ToastUtil.show(getApplicationContext(),"提交宝宝信息成功");
                    LogUtil.v("提交宝宝信息成功", response.toString());
                    startActivity(new Intent(UserProfileStart.this, TabHostActivity.class));
                    finish();
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    ToastUtil.show(getApplicationContext(),"提交宝宝信息失败");
                    LogUtil.e("提交宝宝信息失败",responseString);
                    super.onFailure(statusCode, headers, responseString, throwable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    ToastUtil.show(getApplicationContext(),"提交宝宝信息失败");
                    LogUtil.e("提交宝宝信息失败",errorResponse.toString());
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });


        }
        else if (id == R.id.man) {
            mCurrentSelectView.setSelected(false);
            mCurrentSelectView = view;
            mCurrentSelectView.setSelected(true);
            mCurrentSex = 1;
        }
        else if (id == R.id.feman) {
            mCurrentSelectView.setSelected(false);
            mCurrentSelectView = view;
            mCurrentSelectView.setSelected(true);
            mCurrentSex = 2;
        }
        else if (id == R.id.other) {
            mCurrentSelectView.setSelected(false);
            mCurrentSelectView = view;
            mCurrentSelectView.setSelected(true);
            mCurrentSex = 0;
        }
        else if (id == R.id.choice) {
            final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month,
                                      int dayOfMonth) {
                    calendar.set(year,month,dayOfMonth);
                    if(System.currentTimeMillis() < calendar.getTimeInMillis()){
                        findViewById(R.id.other).setVisibility(View.GONE);
                    }else
                        findViewById(R.id.other).setVisibility(View.VISIBLE);
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    date = year + "-" + (month+1) + "-" + dayOfMonth;
                    choice.setText( year + "-" + (month+1) + "-" + dayOfMonth);
                }

            };

            DatePickerDialog dialog =  new DatePickerDialog(UserProfileStart.this,dateListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            if (mCurrentSex == 0){//未知性别限制10个月
                Calendar c =  Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                c = get10PlusDate(c);
                dialog.getDatePicker().setMaxDate(c.getTime().getTime());
            }
            dialog.show();

        }
    }
    public Calendar get10PlusDate(Calendar c){
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (month <= 1){
            month += 10;
        }
        else{
            year += 1;
            month = month-2;
        }
         c.set(year, month, day);
        return c;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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