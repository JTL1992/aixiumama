/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harmazing.aixiumama.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harmazing.aixiumama.adapter.FilterViewAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.HSuperImageView;
import com.harmazing.aixiumama.view.NoTouchViewPager;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.umeng.analytics.MobclickAgent;

import jp.co.cyberagent.android.gpuimage.GPUImage;


public class ActivityGallery extends FragmentActivity  {
    private static final String TAG = "ActivityGallery";
    private static final int REQUEST_PICK_IMAGE = 1;
    public static final int SELECT_PIC_BY_TACK_PHOTO = 5;
    private static final int REQUEST_CROP_FINISH = 6;
    public static int stickerID;
    public static ImageView imageView = null;
    public static GPUImage mGPUImage = null;
    public static HSuperImageView sticker = null;
    NoTouchViewPager viewpager = null;
    Button beautifyBtn,paperBtn = null;
    RelativeLayout relativeLayout = null;
    FrameLayout frame = null;
    private Uri photoUri = null;
    public static Bitmap bitmap = null;
    public static int margin;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        CuteApplication.activityList.add(this);

        mGPUImage = new GPUImage(getApplication());
        relativeLayout = (RelativeLayout)findViewById(R.id.top_view);
        frame = (FrameLayout)findViewById(R.id.frame);
        sticker = (HSuperImageView)findViewById(R.id.sticker);
        // 设置图片显示大小
//        sticker.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CuteApplication.getScreenHW(getApplicationContext())[0]));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(sticker.getLayoutParams());
        margin = CuteApplication.getScreenHW(getApplicationContext())[0]/2 - CuteApplication.getWidth(sticker)/2;
        params.setMargins(margin/2 -2,margin/2 -2 ,0,0);
        sticker.setLayoutParams(params);

        sticker.setOnDeleteListener(new HSuperImageView.OnDeleteListener() {
            @Override
            public void onDelete() {
                AlertDialog builder = new AlertDialog.Builder(ActivityGallery.this)
                        .setMessage("您要删除当前贴纸吗？").
                                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sticker.setVisibility(View.GONE);
                                        stickerID = 0;
                                    }
                                }).
                                setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                builder.show();
            }
        });

        viewpager=  (NoTouchViewPager)findViewById(R.id.viewpager);

        viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return true;
            }
        });

        beautifyBtn = (Button)findViewById(R.id.beautify_btn);
        beautifyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(0);
                paperBtn.setTextColor(getResources().getColor(R.color.white));
                beautifyBtn.setTextColor(getResources().getColor(R.color.pink));
            }
        });

        paperBtn = (Button)findViewById(R.id.paper_btn);
        paperBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(1);
                paperBtn.setTextColor(getResources().getColor(R.color.pink));
                beautifyBtn.setTextColor(getResources().getColor(R.color.white));
            }
        });

        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog builder = new AlertDialog.Builder(ActivityGallery.this)
                        .setMessage("确定放弃这张编辑过的图片吗？").
                                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).
                                setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                builder.show();
            }
        });

        /*
            点击
         */
        findViewById(R.id.next).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    final   Intent intent = new Intent(ActivityGallery.this, AddLabelActivity.class);
                    if(sticker != null) {
                        sticker.clearBorder(true);
                    }

                //  设置完滤镜之后保存，mGPUImage.第二次赋值
//                    CuteApplication.setFilterCLear(mGPUImage);
//                    mGPUImage.setImage(GetandSaveCurrentImage());

//                    Bitmap mBitamp = GetandSaveCurrentImage();
//                    byte[] bytes = BitmapUtil.bitmap2ByteArray(mBitamp);

                bitmap = GetandSaveCurrentImage();
                LogUtil.v("activityGallery"+"stickerID",stickerID);
                if (stickerID != 0)
                intent.putExtra("stickerID",stickerID);
//              intent.putExtra("bitmap_byte", bytes);
                startActivity(intent);
                stickerID = 0;
            }
        });

        imageView = (ImageView) findViewById(R.id.image);


   //     Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        if(getIntent().getIntExtra("action",10) == TabHostActivity.PHOTO) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
        }else if(getIntent().getIntExtra("action",10) == TabHostActivity.CAMERA){
            takePhoto();
        }
    }


    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED))
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            CuteApplication.tempUri = photoUri;
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }else{
            Toast.makeText(this,"内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap GetandSaveCurrentImage() {
//构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565 );
//获取屏幕
        View decorview = this.getWindow().getDecorView();
//        decorview.setDrawingCacheEnabled(true);
//        decorview.buildDrawingCache();
//        Bmp = decorview.getDrawingCache();
        Canvas c = new Canvas(Bmp);
        decorview .draw(c);
        // 剪切
        Bmp = Bitmap.createBitmap(Bmp,0,BitmapUtil.dip2px(getApplicationContext(),50)+ getTopViewHeight() ,w,CuteApplication.getScreenHW(getApplicationContext())[0] - BitmapUtil.dip2px(getApplicationContext(),10) );

        decorview.destroyDrawingCache();
        decorview.setDrawingCacheEnabled(false);
        return Bmp;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Intent cropIntent = new Intent(ActivityGallery.this, CropImageActivity.class);
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    CuteApplication.tempUri = data.getData();
                    startActivityForResult(cropIntent, REQUEST_PICK_IMAGE);
                }else if(resultCode == REQUEST_CROP_FINISH){
//                  new SetImageAync().execute();
                    handleImage();
                } else {
                    finish();
                }
                break;
            case SELECT_PIC_BY_TACK_PHOTO:
                LogUtil.v("resultCode", resultCode);
                if(resultCode == 0)
                    finish();
                else
                    startActivityForResult(cropIntent, REQUEST_PICK_IMAGE);

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private FilterViewAdapter filterViewAdapter;

    private void handleImage() {
        imageView.setImageBitmap(bitmap);
        mGPUImage.setImage(bitmap);

        if(filterViewAdapter == null) {
            filterViewAdapter = new FilterViewAdapter(getSupportFragmentManager());
            viewpager.setAdapter(filterViewAdapter);
        } else {
            filterViewAdapter.notifyDataSetChanged();
        }
        viewpager.setCurrentItem(0);
        beautifyBtn.setTextColor(getResources().getColor(R.color.pink));
    }


    public int getTopViewHeight(){
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return  frame.top;
    }

    class SetImageAync extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... strings) {
            //  这一步很耗时呢
            return  mGPUImage.getBitmapWithFilterApplied();
        }

        @Override
        protected void onPostExecute(Bitmap b) {

            imageView.setImageBitmap(b);
            viewpager.setAdapter(new FilterViewAdapter(getSupportFragmentManager()));
            viewpager.setCurrentItem(0);
            beautifyBtn.setTextColor(getResources().getColor(R.color.pink));
            super.onPostExecute(b);
        }
    }

    @Override
    protected void onDestroy() {
        CuteApplication.activityList.remove(this);

        super.onDestroy();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                // 进行资源释放操作


                break;
            case TRIM_MEMORY_RUNNING_MODERATE :
                ToastUtil.showLongTime(ActivityGallery.this, "内存过低...");
                break;
            case TRIM_MEMORY_RUNNING_LOW:
                System.out.println("释放资源11");
                ToastUtil.showLongTime(ActivityGallery.this, "亲,休息一下吧!我有点累了!");
                break;
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
