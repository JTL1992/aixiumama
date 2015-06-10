package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.view.DragImageView;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

public class CropImageActivity extends Activity {

    private static final String TAG = "CropImageActivity";

    public static int REQUEST_CROP_FINISH = 6;
    private DragImageView dragImageView;// 自定义控件
    Bitmap bitmap;
    View line2,bottom_bg;
    ImageButton checkBox;
    boolean flag = true;
    private Matrix matrix  = null;
    FrameLayout.LayoutParams params;

    private int state_height;
    private ViewTreeObserver viewTreeObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        System.gc();
        System.runFinalization();
        checkBox =(ImageButton)findViewById(R.id.scale);
        dragImageView = (DragImageView)findViewById(R.id.touch_view);
        line2  = findViewById(R.id.line2);
        bottom_bg = findViewById(R.id.bottom_bg);
        params = new FrameLayout.LayoutParams(dragImageView.getLayoutParams());

        FrameLayout.LayoutParams params0 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,BitmapUtil.dip2px(getApplicationContext(),2));
        params0.setMargins(0, CuteApplication.getScreenHW(getApplicationContext())[0] + BitmapUtil.dip2px(getApplicationContext(),100),0,0);
        line2.setLayoutParams(params0);

        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(bottom_bg.getLayoutParams());
        params1.setMargins(0,CuteApplication.getScreenHW(getApplicationContext())[0] + BitmapUtil.dip2px(getApplicationContext(),102),0,0);
        params1.height = CuteApplication.getScreenHW(getApplicationContext())[1] - (CuteApplication.getScreenHW(getApplicationContext())[0] + BitmapUtil.dip2px(getApplicationContext(),100 + 50));
        bottom_bg.setLayoutParams(params1);

        //.setImageURI(CuteApplication.tempUri);

        //显示要剪裁的图片
        new GetImageFromCamera().execute();

        viewTreeObserver = dragImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (state_height == 0) {
                    Rect frame = new Rect();
                    getWindow().getDecorView()
                            .getWindowVisibleDisplayFrame(frame);
                    state_height = frame.top;
                    dragImageView.setScreen_H(CuteApplication.getScreenHW(getApplicationContext())[1]-state_height);
                    dragImageView.setScreen_W(CuteApplication.getScreenHW(getApplicationContext())[0]);
                }

            }
        });


        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mGPUImage 第一次赋值
//                ActivityGallery.mGPUImage.setImage(GetandSaveCurrentImage());
                ActivityGallery.bitmap = GetandSaveCurrentImage();
                System.gc();
                System.runFinalization();
                Intent intent = new Intent(CropImageActivity.this, ActivityGallery.class);
                setResult(REQUEST_CROP_FINISH, intent);

                finish();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        findViewById(R.id.rotate).setOnClickListener(new View.OnClickListener() {

//            float startAngle = 0;

            @Override
            public void onClick(View view) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                matrix.setRotate(90); // 旋转angle度
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                        matrix, true);
                dragImageView.setImageBitmap(bitmap);

            }
        });






        //初始化 图片位置
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(dragImageView.getLayoutParams());
        params2.setMargins(0, 0, 0, 0);
        params2.width =  CuteApplication.getScreenHW(getApplicationContext())[0];
        params2.height = CuteApplication.getScreenHW(getApplicationContext())[1];
        dragImageView.setLayoutParams(params2);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag){

                    params.setMargins(0, BitmapUtil.dip2px(getApplicationContext(), 100), 0, 0);
                    params.height = CuteApplication.getScreenHW(getApplicationContext())[0];
                    dragImageView.setLayoutParams(params);
                    flag = false;
                    checkBox.setImageResource(R.drawable.zhoriginal_yes);
                }else {
                    FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(dragImageView.getLayoutParams());
                    params2.setMargins(0, 0, 0, 0);
                    params2.width =  CuteApplication.getScreenHW(getApplicationContext())[0];
                    params2.height = CuteApplication.getScreenHW(getApplicationContext())[1];
                    dragImageView.setLayoutParams(params2);

//                    params.setMargins(0, BitmapUtil.dip2px(getApplicationContext(), 102), 0, 0);
//                    params.height = CuteApplication.getScreenHW(getApplicationContext())[0];
//                    params.width = CuteApplication.getScreenHW(getApplicationContext())[0];
//                    dragImageView.setLayoutParams(params);
                    flag = true;
                    checkBox.setImageResource(R.drawable.zhoriginal_no);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            if(bitmap != null)
                bitmap.recycle();
        }catch (Exception e){e.printStackTrace();}

        dragImageView.setImageBitmap(null);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crop_image, menu);
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

    /**
     * 获取和保存当前屏幕的截图
     */
    private Bitmap GetandSaveCurrentImage() {
        //构建Bitmap
        int w= CuteApplication.getScreenHW(getApplicationContext())[0];
        int h = CuteApplication.getScreenHW(getApplicationContext())[1];
        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565 );
        //获取屏幕
        View decorview = this.getWindow().getDecorView();

        Canvas c = new Canvas(Bmp);
        decorview .draw(c);

        // 剪切   高度要加上 两个分割线的高度:2dp
        Bmp = Bitmap.createBitmap(Bmp,0,BitmapUtil.dip2px(getApplicationContext(),102)+ getTopViewHeight() ,w,CuteApplication.getScreenHW(getApplicationContext())[0] - BitmapUtil.dip2px(getApplicationContext(),2));

        decorview.destroyDrawingCache();
        decorview.setDrawingCacheEnabled(false);
        return Bmp;
    }

    public int getTopViewHeight(){
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return  frame.top;
    }



    private class GetImageFromCamera extends AsyncTask<String, Integer, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                bitmap = BitmapUtil.getImageFromUri(CropImageActivity.this, CuteApplication.tempUri, CuteApplication.getScreenHW(getApplicationContext())[0]);

                if(matrix == null) {
                    matrix = new Matrix();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            System.out.println("高等于宽..." + b.getHeight() + "   ...  " + CuteApplication.getScreenHW(getApplicationContext())[0]);
            if(b.getHeight() <= CuteApplication.getScreenHW(getApplicationContext())[0]) {
                params.setMargins(0, BitmapUtil.dip2px(getApplicationContext(), 100), 0, 0);
                dragImageView.setLayoutParams(params);
            }

            // 设置图片
            dragImageView.setImageBitmap(b);
            dragImageView.setmActivity(CropImageActivity.this);
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
