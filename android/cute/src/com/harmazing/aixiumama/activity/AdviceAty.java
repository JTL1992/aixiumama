package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by JTL on 2014/11/26.
 * 建议
 */
public class AdviceAty extends Activity {
    EditText advice;
    ImageView imageAdvice;
    Bitmap photo;
    Boolean isEdited;
    JsonHttpResponseHandler jsonHttpResponseHandler;
    private static final int RESULT_LOAD_IMAGE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_advice);
        isEdited = false;
        ImageView btnBack = (ImageView) findViewById(R.id.title_advice).findViewById(R.id.left_view);
        TextView commit = (TextView) findViewById(R.id.title_advice).findViewById(R.id.right_view);
        advice = (EditText) findViewById(R.id.edit_advice);
        imageAdvice = (ImageView) findViewById(R.id.image_advice);
        btnBack.setOnClickListener(onBackListener);
        commit.setOnClickListener(onCommitListner);
        advice.addTextChangedListener(onEditedListener);
        imageAdvice.setOnClickListener(onImageAdviceListener);
        jsonHttpResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("yijian", response.toString());
                Toast.makeText(getApplicationContext(),"意见提交成功",Toast.LENGTH_LONG).show();
                finish();
            }
        };
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
            if (!editable.toString().equals(""))
                isEdited = true;
        }
    };
    View.OnClickListener onBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isEdited){
         final Dialog mDialog =  new AlertDialog.Builder(AdviceAty.this).setMessage("您是否放弃提交意见？").setNegativeButton("放弃",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setPositiveButton("提交",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isEdited = false;
                            RequestParams params = new RequestParams();
                            params.put("text",advice.getText().toString());
                            if (photo != null)
                                try{
                                    params.put("images", BitmapUtil.saveBitmap2File(getApplicationContext(), photo));
                                }catch (Exception e ){
                                    LogUtil.v("images参数错误","@@@@@@@");
                                }
                            HttpUtil.addClientHeader(getApplicationContext());
                            HttpUtil.post(API.SEND_PERSON_ADVICE, params,jsonHttpResponseHandler );
                    }
                }).create();
                mDialog.show();
            }
          else
            finish();
        }
    };
    View.OnClickListener onCommitListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isEdited = false;
            if (advice.getText().toString().equals(""))
                Toast.makeText(getApplicationContext(),"请您填写您的建议，再提交！",Toast.LENGTH_LONG).show();
            else{
                RequestParams params = new RequestParams();
                params.put("text",advice.getText().toString());
                if (photo != null)
                    try{
                        params.put("images", BitmapUtil.saveBitmap2File(getApplicationContext(), photo));
                    }catch (Exception e ){
                        LogUtil.v("images参数错误","@@@@@@@");
                    }
                HttpUtil.addClientHeader(getApplicationContext());
                HttpUtil.post(API.SEND_PERSON_ADVICE, params,jsonHttpResponseHandler );
            }

        }
    };
    View.OnClickListener onImageAdviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    };
    public void startPhotoZoom(Uri uri) {
        if(uri==null){
            LogUtil.v("tag", "The uri is not exist.");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);

    }
    private void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
         photo = extras.getParcelable("data");
         Drawable drawable = new BitmapDrawable(photo);
         imageAdvice.setImageDrawable(drawable);

        }
    }
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
           String picturePath = cursor.getString(columnIndex);
            cursor.close();
            LogUtil.v("picturePath",picturePath);
//            file = new File(picturePath,"20141124_131859.jpg");
//            file.mkdir();
//            imageHead.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            // String picturePath contains the path of selected Image
        }
        if (requestCode == RESULT_REQUEST_CODE && null != data) {
            setImageToView(data);
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