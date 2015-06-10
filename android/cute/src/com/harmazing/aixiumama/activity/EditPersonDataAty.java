package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.UserInfoUtils;
import com.harmazing.aixiumama.view.RoundedImageView;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.widget.WheelView;
import com.widget.adapters.ArrayWheelAdapter;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jtl on 2014/11/26.
 * 编辑个人信息
 */
public class EditPersonDataAty extends Activity {
    static int PROVINCE = 1,CITY = 2,AREA = 3;
    String userName;
    String userImage;
    String userBirthday;
    RoundedImageView imageHead;
    RelativeLayout dir;
    View layout;
    File file = new File("");
    NumberPicker city,area;
    Boolean isScroll = true;
    String picturePath;
    Bitmap photo = null;
    HashMap<Integer,String> provinces;
    private boolean scrolling = true;
    EditText editName,editDescribe;
    TextView editDir,editSex,userSex,editBirthday;
    int idCity,idArea;
    AlertDialog sexPicker;
    String date = null;
    Button choice,button_finish;
    HashMap<Integer,HashMap<Integer,String>> locations;
    HashMap<Integer,HashMap<Integer,Integer>> areaId;
    String [] sex = {"男","女"};
    Boolean isSettingBabySex,isCommit;
    Calendar calendar;
    Boolean isEdit;
    String babyS = null;
    String userS = null;
    String errorString;
    private static final int RESULT_LOAD_IMAGE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_persondata);
        isEdit = false;
        isCommit = false;
//        CuteApplication.provinces = new HashMap<Integer, String>();
//        CuteApplication.areaId = new HashMap<Integer, HashMap<Integer,Integer>>();
//        CuteApplication.locations = new HashMap<Integer, HashMap<Integer, String>>();
//        Bundle bundle = getIntent().getExtras();
//        userName = bundle.getString("name");
//        userImage = bundle.getString("image");
        calendar = Calendar.getInstance();
        TextView btnCommit = (TextView) findViewById(R.id.title_editData).findViewById(R.id.right_view);
        ImageView btnBack = (ImageView) findViewById(R.id.title_editData).findViewById(R.id.left_view);
        ImageView btnHead = (ImageView) findViewById(R.id.image_head);
        editBirthday = (TextView) findViewById(R.id.edit_yuchan);
        editName = (EditText) findViewById(R.id.edit_name);
        editDescribe  = (EditText) findViewById(R.id.edit_abstract);
        userSex  = (TextView) findViewById(R.id.edit_sex);
        editSex = (TextView) findViewById(R.id.edit_baby_sex);
        editDir = (TextView) findViewById(R.id.edit_dir);
        btnBack.setOnClickListener(onBackListener);
        btnCommit.setOnClickListener(onCommitListener);
        userSex.setOnClickListener(onUserSexListener);
        btnHead.setOnClickListener(onChangeHeadListener);
        editBirthday.setOnClickListener(onBirthdayDataListener);

        TextView changeHead = (TextView) findViewById(R.id.text_change_head);
        imageHead = (RoundedImageView) findViewById(R.id.image_head);
        dir = (RelativeLayout) findViewById(R.id.dir);
        changeHead.setOnClickListener(onChangeHeadListener);
        editSex.setOnClickListener(onBabySexListener);
//        editName.setText(userName);
        dir.setOnClickListener(onDirWheelListner);
//        if (!AppSharedPref.newInstance(getApplicationContext()).getPicDir().equals("")){
//         imageHead.setImageBitmap(BitmapFactory.decodeFile(AppSharedPref.newInstance(getApplicationContext()).getPicDir()));
//        LogUtil.v("获取头像的地址",AppSharedPref.newInstance(getApplicationContext()).getPicDir());
//        }
//        else
//        CuteApplication.downloadIamge(API.STICKERS + userImage, imageHead);
//        editBirthday.setText(AppSharedPref.newInstance(this).getBirthday());
//        if (AppSharedPref.newInstance(this).getBabtSex() == 1){
//        editSex.setText("男");
//        babyS = "男";}
//        else if (AppSharedPref.newInstance(this).getBabtSex() == 2){
//        editSex.setText("女");
//        babyS = "女";}
//        else
//        editSex.setText("");
//        if (AppSharedPref.newInstance(this).getUserSex() == 1){
//            userSex.setText("男");
//             userS = "男";}
//        else if (AppSharedPref.newInstance(this).getUserSex() == 2){
//            userSex.setText("女");
//             userS = "女";}
//        else
//            userSex.setText("");
//        editDir.setText(User.getDir(getApplicationContext()));
//        editDescribe.setText(User.getDescription(getApplicationContext()));
        sexPicker = new AlertDialog.Builder(this).setItems(sex,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isSettingBabySex){
                editSex.setText(sex[i]);
                    AppSharedPref.newInstance(getApplicationContext()).setSexBaby(1+i);}
//                AppSharedPref.newInstance(getApplicationContext()).setSexBaby(i+1);}
                else{
                    userSex.setText(sex[i]);
                    AppSharedPref.newInstance(getApplicationContext()).setUserSex(1+i); }
            }
        }).create();
        editName.addTextChangedListener(onEditedListener);
        editBirthday.addTextChangedListener(onEditedListener);
        editSex.addTextChangedListener(onEditedListener);
        editDir.addTextChangedListener(onEditedListener);
        editDescribe.addTextChangedListener(onEditedListener);
        userSex.addTextChangedListener(onEditedListener);
        LogUtil.v("privinces", CuteApplication.provinces.isEmpty() + "");
//        LogUtil.v("privinces",CuteApplication.provinces.isEmpty()+"");
//        if (CuteApplication.provinces.isEmpty())
//         HttpUtil.get(API.CURSOR_CITY, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                super.onSuccess(statusCode, headers, response);
//                try {
//
//                    for (int i = 0; i < response.length(); i++) {
//                        HashMap<Integer, String> proName = new HashMap<Integer, String>();
//                        HashMap<Integer, Integer> areaName = new HashMap<Integer, Integer>();
//                        HashMap<Integer, String> idName = new HashMap<Integer, String>();
//                        LogUtil.v(" response.length()", "" + response.length());
//                        String name = response.getJSONObject(i).getString("name");
//                        int id = response.getJSONObject(i).getInt("id");
//                        LogUtil.v("response.getJSONObject(i).getInt(\"id\"):", "" + response.getJSONObject(i).getInt("id"));
//                        CuteApplication.provinces.put(id, name);
//
//                        JSONArray array = response.getJSONObject(i).getJSONArray("cities");
//                        LogUtil.v("array", array.toString());
//                        for (int j = 0; j < array.length(); j++) {
//                            String city = array.getJSONObject(j).getString("name");
//                            int cityId = array.getJSONObject(j).getInt("id");
//                            proName.put(j, city);
//                            areaName.put(j, cityId);
//                            idName.put(cityId,city);
//                        }
////                        sort(proName);
//                        CuteApplication.locations.put(id, proName);
//                        CuteApplication.areaId.put(id,areaName);
//                        CuteApplication.findCity.put(id,idName);
//                        LogUtil.v("location", CuteApplication.locations.get(id).toString());
//                        LogUtil.v("CuteApplication.areaId",CuteApplication.areaId.get(id).toString());
////                 proName.clear();
//                    }
////                sort(provinces);
////                    CuteApplication.provinces = (HashMap<Integer,String>) provinces.clone();
////                    CuteApplication.areaId = (HashMap<Integer,Integer>)areaId.clone();
////                    CuteApplication.locations = (HashMap<Integer,HashMap<Integer,String>>) locations.clone();
//                } catch (Exception e) {
//                    LogUtil.v("JSON", "解析出错");
//                }
//
//            }
//        });
        initPersonData();

    }
    View.OnClickListener onBirthdayDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerDialog dialog =  new DatePickerDialog(EditPersonDataAty.this,dateListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        }
    };
    View.OnClickListener onUserSexListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sexPicker.show();
            isSettingBabySex = false;
        }
    };
    View.OnClickListener onBabySexListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sexPicker.show();
            isSettingBabySex = true;
        }
    };
    final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month,
                              int dayOfMonth) {
            date = year + "-" + (month+1) + "-" + dayOfMonth;
            editBirthday.setText( year + "-" + (month+1) + "-" + dayOfMonth);
            UserInfoUtils.setBirthday(getApplicationContext(), date);
        }

    };
    View.OnClickListener onCommitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             commitEdit();
        }
    };
    View.OnClickListener onChangeHeadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(
            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    };
    View.OnClickListener onBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            if (!isCommit)
            if (isEdit){
                new AlertDialog.Builder(EditPersonDataAty.this).setMessage("您是否保存当前修改？").setNegativeButton("放弃",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                          finish();
                    }
                }).setPositiveButton("保存",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        commitEdit();
                        finish();
                    }
                }).create().show();
            }
            else
               finish();
        }
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE  && null != data) {
            Uri selectedImage = data.getData();
            startPhotoZoom(selectedImage);
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            LogUtil.v("picturePath",picturePath);
//            LogUtil.v("picturePath",picturePath);
//            file = new File(picturePath,"20141124_131859.jpg");
//            file.mkdir();
//            imageHead.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            // String picturePath contains the path of selected Image
        }
        if (requestCode == RESULT_REQUEST_CODE && null != data) {
            setImageToView(data);
//            Uri selectedImage = data.getData();
//            File file1 = new File("/data",".jpg");
//            LogUtil.v("file路径","@@@"+file1.getAbsolutePath());

        }
    }
    public void startPhotoZoom(Uri uri) {
        if(uri == null){
            LogUtil.v("tag", "The uri is not exist.");
//            LogUtil.v("tag", "The uri is not exist.");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", BitmapUtil.dip2px(getApplicationContext(), 80));
        intent.putExtra("outputY", BitmapUtil.dip2px(getApplicationContext(),80));
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);

    }
    private void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            imageHead.setImageDrawable(drawable);

        }
    }
    View.OnClickListener onDirWheelListner = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if ( CuteApplication.provinces.isEmpty()){
                Toast.makeText(getApplicationContext(),"当前网络不稳定，稍后再试！",Toast.LENGTH_LONG).show();
            }
            else{
            idCity = 0;
            idArea = 0;
            LayoutInflater inflater = LayoutInflater.from(EditPersonDataAty.this);
            layout = inflater.inflate(R.layout.wheel_picker, null);
            String[] cities = new String[ CuteApplication.provinces.size()];
            for (int i = 0; i <  CuteApplication.provinces.size(); i++) {
                cities[i] =  CuteApplication.provinces.get(i+1);
                LogUtil.v("@2@",cities[i]);
            }
            String[] areas = new String[CuteApplication.locations.get(1).keySet().size()];
            for (int j = 0 ;j < CuteApplication.locations.get(1).keySet().size(); j++ ){
                areas[j] = CuteApplication.locations.get(1).get(j);
                LogUtil.v("areas",areas[j]+"");
            }
            city = (NumberPicker) layout.findViewById(R.id.city);
            city.setFocusable(false);
            city.setDisplayedValues(cities);
            city.setMinValue(0);
            city.setMaxValue(cities.length - 1);
            area = (NumberPicker) layout.findViewById(R.id.area);
            area.setFocusable(false);
            area.setDisplayedValues(null);
            area.setMinValue(0);
            area.setMaxValue(areas.length - 1);
            area.setDisplayedValues(areas);
            area.getChildAt(area.getValue()).clearFocus();
            city.getChildAt(city.getValue()).clearFocus();
            city.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                    if (!isScroll) {
                        String[] areas = new String[CuteApplication.locations.get(i2 + 1).keySet().size()];
                        LogUtil.v("i++++++i2", i + "+++++++" + i2);
                        idCity = i2 + 1;
                        for (int j = 0; j < CuteApplication.locations.get(i2 + 1).keySet().size(); j++) {
                            areas[j] = CuteApplication.locations.get(i2 + 1).get(j);
                            LogUtil.v("areas", areas[j] + "");
                        }
//                        area.setMinValue(0);
//                        area.setMaxValue(areas.length - 1);
                        if (areas.length != 0) {
//                           NumberPicker area = (NumberPicker)layout.findViewById(R.id.area);
//                           area.setDisplayedValues(areas);
                            area.setDisplayedValues(null);
                            area.setMinValue(0);
                            area.setMaxValue(areas.length - 1);
                            area.setDisplayedValues(areas);
//                           area.setMinValue(0);
//                           area.setMaxValue(areas.length-1);
                        }
//                    area.setMinValue(0);
//                    area.setMaxValue(areas.length-1);
                    }
                }

            });
            city.setOnScrollListener(new NumberPicker.OnScrollListener() {
                @Override
                public void onScrollStateChange(NumberPicker numberPicker, int i) {
                    if (i == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                        isScroll = false;
                    }
                    if (i == NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                        isScroll = false;
                    }

                }
            });

            area.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                    idArea = i2;
                }
            });
//            for (int i = 0; i < provinces.size(); i++) {
//                cities[i] = provinces.get(i+1);
//                LogUtil.v("@2@",cities[i]);
//            }

//
//                try {
//                    city.setViewAdapter(new ArrayWheelAdapter<String>(EditPersonDataAty.this, cities));
//                } catch (Exception e) {
//                    LogUtil.v("@@@@2@@@@@@@", "");
//                }
//                city.addChangingListener(new OnWheelChangedListener() {
//                    @Override
//                    public void onChanged(WheelView wheel, int oldValue, int newValue) {
//                        if (!scrolling) {
//                            ArrayList<String> list = new ArrayList<String>();
//                            for (int i : CuteApplication.locations.get(newValue - 1).keySet()) {
//                                list.add(CuteApplication.locations.get(newValue - 1).get(i));
//                            }
//                            updateCities(area, list);
//                        }
//                    }
//                });
//                city.addScrollingListener(new OnWheelScrollListener() {
//                    @Override
//                    public void onScrollingStarted(WheelView wheel) {
//                        scrolling = true;
//                    }
//
//                    @Override
//                    public void onScrollingFinished(WheelView wheel) {
//                        scrolling = false;
//                    }
//                });
//                city.setCurrentItem(3);
            AlertDialog.Builder builder = new AlertDialog.Builder(EditPersonDataAty.this);
            builder.setView(layout);
            builder.setCancelable(true);
            builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    try {
                        if (idCity == 0)
                            idCity = 1;
                        editDir.setText( CuteApplication.provinces.get(idCity)+"  "+CuteApplication.locations.get(idCity).get(idArea));
                        LogUtil.v(" CuteApplication.provinces.get(idCity)+CuteApplication.locations.get(idCity).get(idArea)", CuteApplication.provinces.get(idCity)+"!!!"+CuteApplication.locations.get(idCity).get(idArea));
//                        tv_address.setText(provincesName.get(country.getCurrentItem()).toString() + " "+
//                                cityName.get(city.getCurrentItem()).toString() + " " +
//                                areaName.get(area.getCurrentItem()).toString());
                    } catch (Exception e) {
//                        tv_address.setText(provincesName.get(country.getCurrentItem()).toString());
                    }
                }
            });
            builder.create().show();
            }
        }
 };



        private void updateCities(WheelView city, ArrayList<String> items) {

            ArrayWheelAdapter<String> adapter =
                    new ArrayWheelAdapter<String>(EditPersonDataAty.this, items.toArray(new String[items.size()]));
            //    LogUtil.v("当前wheel的index", String.valueOf(index));
            adapter.setTextSize(15);
            city.setViewAdapter(adapter);
            city.setCurrentItem(items.size() / 2);
        }

        public void sort(List list) {
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String s, String s2) {
                    return 0;
                }
            });
        }
    public static Bitmap getBitmapByPath(String path) {
        Bitmap temp;
            try {
                temp = BitmapFactory.decodeFile(path);
                return temp;
            } catch (Exception e) {
                return null;
            }
    }
    TextWatcher onEditedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
               if (!editable.toString().equals(userName)||!editable.toString().equals(userS)
                       ||!editable.toString().equals(babyS)&&!editable.toString().equals(AppSharedPref.newInstance(getApplicationContext())
               .getBirthday())||!editable.toString().equals(AppSharedPref.newInstance(getApplicationContext()).getDir())
                       ||!editable.toString().equals(AppSharedPref.newInstance(getApplicationContext())
               .getDescreption()))
                   isEdit = true;
        }
    };
    private void commitEdit(){
        isEdit = false;
        int gender,locationId = 0;
        JSONObject jsonObject;
        RequestParams requestParams = new RequestParams();
        Map<String,String> map = new HashMap<String, String>();
        if (userSex.getText().toString().equals("男"))
            gender = 1;
        else if (userSex.getText().toString().equals("女"))
            gender = 2;
        else
            gender = 0;
//        User.setUserSex(getApplicationContext(), gender);
        if (idCity == 0)
            locationId = 0;
        else{
            for (int i : CuteApplication.areaId.get(idCity).keySet())
                if (idArea == i){
                    locationId = CuteApplication.areaId.get(idCity).get(i);
                    LogUtil.v("%%%%%%%",""+CuteApplication.areaId.get(idCity).get(i)+"@@@"+locationId);
                }

        }
//        User.setDir(getApplicationContext(),editDir.getText().toString());
//        User.setDescription(getApplicationContext(),editDescribe.getText().toString());
        map.put("gender",AppSharedPref.newInstance(getApplicationContext()).getBabtSex()+"");
        map.put("birthday",editBirthday.getText().toString());
        //map.put("birthday","2014-12-25");
        jsonObject = new JSONObject(map);


        try{
            JSONArray jsonArray = new JSONArray().put(jsonObject);
            requestParams.put("name",editName.getText().toString());
            requestParams.put("description",editDescribe.getText().toString());
            if (photo != null)
                requestParams.put("image", BitmapUtil.saveBitmap2File(getApplicationContext(),photo));
//                else
//                requestParams.put("image","");
            if (locationId != 0)
            requestParams.put("city",locationId);
            requestParams.put("gender",gender);
            requestParams.put("babies",jsonArray);
            LogUtil.v("params",editName.getText().toString()+""+editSex.getText().toString()+"@"+editDescribe.getText().toString()+"@"+locationId+"@"+gender+"@"+jsonArray);
        }

        catch (Exception e){
            LogUtil.v("参数错误","@@@@@@@");
        }
        String userId = AppSharedPref.newInstance(getApplicationContext()).getUserId();
        LogUtil.v("userId",userId);
//        HttpUtil.addPatchClientHeader(getApplication());
        HttpUtil.getClient().addHeader("X-HTTP-Method-Override","PATCH");
        HttpUtil.addClientHeader(this);
        HttpUtil.post(API.SEND_PERSON_DATA + userId + "/", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("#####", response.toString());
                Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_LONG).show();
                sendDataBack();
                resetData();
                HttpUtil.removePatchHeader();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("失败", responseString);
                errorString = responseString;
                Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_LONG).show();
                HttpUtil.removePatchHeader();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                HttpUtil.removePatchHeader();
                LogUtil.v("","$$$$$$$$$$$");
            }
        });
    }
    private void sendDataBack(){
        Intent intent = new Intent();
          intent.putExtra("name",editName.getText().toString());
        if (photo != null){
            intent.putExtra("image",photo);
        }
         setResult(0,intent);

    }
   private void resetData(){
       UserInfoUtils.setBirthday(getApplicationContext(), editBirthday.getText().toString());
       AppSharedPref.newInstance(getApplicationContext()).setBirthday(editBirthday.getText().toString());
       AppSharedPref.newInstance(getApplicationContext()).setDescription(editDescribe.getText().toString());
       UserInfoUtils.setDescription(getApplicationContext(), editDescribe.getText().toString());
       AppSharedPref.newInstance(getApplicationContext()).setUserName(editName.getText().toString());
       if (editSex.getText().toString().equals("男"))
       AppSharedPref.newInstance(getApplicationContext()).setSexBaby(1);
       else if(editSex.getText().toString().equals("女"))
           AppSharedPref.newInstance(getApplicationContext()).setSexBaby(2);
       else
           AppSharedPref.newInstance(getApplicationContext()).setSexBaby(0);
       UserInfoUtils.setDir(getApplicationContext(), editDir.getText().toString());
       if (userSex.getText().toString().equals("男"))
           UserInfoUtils.setUserSex(getApplicationContext(), 1);
      else if (userSex.getText().toString().equals("女"))
           UserInfoUtils.setUserSex(getApplicationContext(), 2);
       else
           UserInfoUtils.setUserSex(getApplicationContext(), 0);
       if (photo != null){
           AppSharedPref.newInstance(getApplicationContext()).setPicDir(BitmapUtil.saveBitmap2File(getApplicationContext(),photo,"head.jpg").getAbsolutePath());
           LogUtil.v("头像绝对地址",BitmapUtil.saveBitmap2File(getApplicationContext(),photo,"head.jpg").getAbsolutePath());
       }
//       AppSharedPref.newInstance(getApplicationContext()).setPicDir(picturePath);
   }
    private void initPersonData(){
        Bundle bundle = getIntent().getExtras();
//        userBirthday = bundle.getString("birthday");
        userName = bundle.getString("name");
        userImage = bundle.getString("image");
        editName.setText(userName);
//        if (!AppSharedPref.newInstance(getApplicationContext()).getBirthday().equals(""))
//        editBirthday.setText(AppSharedPref.newInstance(getApplicationContext()).getBirthday());
//        else
//        editBirthday.setText(userBirthday);

//        if (!AppSharedPref.newInstance(getApplicationContext()).getPicDir().equals("")){
//            imageHead.setImageBitmap(BitmapFactory.decodeFile(AppSharedPref.newInstance(getApplicationContext()).getPicDir()));
//            LogUtil.v("获取头像的地址",AppSharedPref.newInstance(getApplicationContext()).getPicDir());
//        }
//        else
            CuteApplication.downloadIamge(API.STICKERS + userImage, imageHead);

        RequestParams params = new RequestParams();
        params.put("userId",AppSharedPref.newInstance(getApplicationContext()).getUserId());
        HttpUtil.addClientHeader(getApplicationContext());
        HttpUtil.get(API.SEND_PERSON_DATA+AppSharedPref.newInstance(getApplicationContext()).getUserId()+"/",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("person_data",response.toString());
                try {
                    AppSharedPref.newInstance(getApplicationContext()).setDescription(response.getString("description"));
                    AppSharedPref.newInstance(getApplicationContext()).setUserSex(response.getInt("gender"));
                    AppSharedPref.newInstance(getApplicationContext()).setSexBaby(response.getJSONArray("babies").getJSONObject(0).getInt("gender"));
                    AppSharedPref.newInstance(getApplicationContext()).setBirthday(response.getJSONArray("babies").getJSONObject(0).getString("birthday"));
                    if (AppSharedPref.newInstance(getApplicationContext()).getBabtSex() == 1){
                        editSex.setText("男");
                        babyS = "男";}
                    else if (AppSharedPref.newInstance(getApplicationContext()).getBabtSex() == 2){
                        editSex.setText("女");
                        babyS = "女";}
                    else
                        editSex.setText("");
                    if (AppSharedPref.newInstance(getApplicationContext()).getUserSex() == 1){
                        userSex.setText("男");
                        userS = "男";}
                    else if (AppSharedPref.newInstance(getApplicationContext()).getUserSex() == 2){
                        userSex.setText("女");
                        userS = "女";}
                    else
                        userSex.setText("");
                    editDir.setText(AppSharedPref.newInstance(getApplicationContext()).getDir());
                    editDescribe.setText(AppSharedPref.newInstance(getApplicationContext()).getDescreption());
                    editBirthday.setText(AppSharedPref.newInstance(getApplicationContext()).getBirthday());
                    if (!CuteApplication.findCity.isEmpty()){
                        for(int i = 1; i <= CuteApplication.findCity.size(); i++){
                            LogUtil.v("provinceID",i+"##"+CuteApplication.findCity.size());
                            for (int id : CuteApplication.findCity.get(i).keySet()){
                                LogUtil.v("遍历",""+id+" @"+response.getInt("city"));
                                if ( id == response.getInt("city")){
                                    AppSharedPref.newInstance(getApplicationContext()).setDir(CuteApplication.provinces.get(i)+" "+CuteApplication.findCity.get(i).get(id));
                                    LogUtil.v("city",CuteApplication.provinces.get(i)+" "+CuteApplication.findCity.get(i).get(id));
                                    editDir.setText(CuteApplication.provinces.get(i)+" "+CuteApplication.findCity.get(i).get(id));
                                    return;
                                }
                                LogUtil.v("Description",response.getString("description"));
                                LogUtil.v("UserSex",""+response.getInt("gender"));

                            }
                        }
                    }
                    LogUtil.v("Description",response.getString("description"));
                    LogUtil.v("UserSex",""+response.getInt("gender"));

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        //HttpUtil.removePatchHeader();
        //editBirthday.setText(AppSharedPref.newInstance(this).getBirthday());
//        if (AppSharedPref.newInstance(this).getBabtSex() == 1){
//            editSex.setText("男");
//            babyS = "男";}
//        else if (AppSharedPref.newInstance(this).getBabtSex() == 2){
//            editSex.setText("女");
//            babyS = "女";}
//        else
//            editSex.setText("");
//        if (AppSharedPref.newInstance(this).getUserSex() == 1){
//            userSex.setText("男");
//            userS = "男";}
//        else if (AppSharedPref.newInstance(this).getUserSex() == 2){
//            userSex.setText("女");
//            userS = "女";}
//        else
//            userSex.setText("");
//        editDir.setText(AppSharedPref.newInstance(getApplicationContext()).getDir());
//        editDescribe.setText(AppSharedPref.newInstance(getApplicationContext()).getDescreption());
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




