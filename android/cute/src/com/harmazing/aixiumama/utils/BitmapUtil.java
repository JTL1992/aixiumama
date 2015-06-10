package com.harmazing.aixiumama.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lyn on 2014/11/13.
 */
public class BitmapUtil {

    /***
     * 保存图片 成File
     *
     */
    public static File saveBitmap2File(Context context,Bitmap bm,String name) {

        File file = new File( context.getFilesDir().getAbsolutePath().toString() +"/"+ name);
        FileOutputStream fos = null;

        try {
            file.createNewFile();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100,os);
            byte[] bytes = os.toByteArray();
            fos = new FileOutputStream(file);

            fos.write(bytes);

            os.flush();
            os.close();
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    public static File saveBitmap2File(Context context,Bitmap bm) {

        File file = new File( context.getFilesDir().getPath().toString() + "/cute.JPG");
        FileOutputStream fos = null;

        try {
            file.createNewFile();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100,os);
            byte[] bytes = os.toByteArray();
            fos = new FileOutputStream(file);

            fos.write(bytes);

            os.flush();
            os.close();
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    return file;
    }

    /***
     * 获取SD卡图片
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmap(String url ) {
        InputStream inputStream = null;
        String filename = "";
        Bitmap map = null;
        URL url_Image = null;
        String LOCALURL = "";
        if (url == null)
            return null;
        try {
            filename = url;
        } catch (Exception err) {
        }


            try {
                url_Image = new URL("file://"+url);
                inputStream = url_Image.openStream();
                map = BitmapFactory.decodeStream(inputStream);
                // url = URLEncoder.encode(url, "UTF-8");

                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        return map;
    }

    /***
     * 判断图片是存在
     *
     * @param url
     * @return
     */
    public static boolean isExist(String url) {
        File file = new File(url);
        return file.exists();
    }

    /**
     * 以当前时间命名的名字
     * @return
     */
    public static String getDatePhotoName(){
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".JPG";
    }



    /**
     * 把uri 转换为 bitmap          注意要关闭硬件加速！！否则会因为图片超大而不显示
     * @param mContext
     * @param uri
     * @param size
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Bitmap getImageFromUri(Context mContext,Uri uri,int size) throws FileNotFoundException, IOException{
        InputStream input = mContext.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.RGB_565;//optional
        input = mContext.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);// 根据Uri从数据库中找
        if (cursor != null) {
            String filePath = "";
            cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
            LogUtil.v("cursor.getColumnIndex", cursor.getColumnIndex("_data"));
//            if (cursor.getColumnIndex("_data") < 0) {
//                filePath = cursor.getString(1);
//                LogUtil.v("filePath",filePath);
//            }
            if (cursor.getColumnIndex("_data") > 0) {
                filePath = cursor.getString((cursor.getColumnIndex("_data")));// 获取图片路
                int degree = readPictureDegree(filePath);
                LogUtil.v("图片角度", degree);
                if (degree == 90) {
                    Matrix m = new Matrix();
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    m.setRotate(90); // 旋转angle度
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                            m, true);
                }
            }
            cursor.close();
        }
              if(bitmap == null)
            LogUtil.v("bitmap uri", "bitmap 为空");
        return bitmap;
    }

    /**
     * 把uri 转换为 bitmap
     * @param mContext
     * @param uri
     * @return
     */
    public static Bitmap getImageFromUri2(Context mContext,Uri uri) throws IOException{
        return MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    public static enum ScalingLogic {
        CROP, FIT
    }

    /**
     * 读取之后裁剪图片
     * @param pathName
     * @param dstWidth
     * @param dstHeight
     * @param scalingLogic
     * @return
     */
    public static Bitmap decodeFile(String pathName, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);
        Bitmap unscaledBitmap = BitmapFactory.decodeFile(pathName, options);
        return unscaledBitmap;
    }

    /**
     * 不裁减图片读取
     * @param pathName
     * @return
     */
    public static Bitmap decodeFile(String pathName) {

        Bitmap unscaledBitmap = BitmapFactory.decodeFile(pathName);
        return unscaledBitmap;
    }


    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;
            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;
            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }

    /**
     * 压缩图片节省内存，单位PX
     * @param image
     * @param height    px
     * @param width     px
     * @return
     */
    public static Bitmap compress(Bitmap image,float height,float width) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > width) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / width);
        } else if (w < h && h > height) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / height);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }


    /**
     * 获取SDCard的目录路径功能
     * @return
     */
    public static String getSDCardPath(){
        File sdcardDir = null;

        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(sdcardExist){
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }


    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    /**
     * 节省内存读本地图片
     * @param context
     * @param resId
     * @return
     */
    public static Drawable readBitMapToDrawable(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return new BitmapDrawable(BitmapFactory.decodeStream(is,null,opt));
    }

    /*
        // 将Bitmap转换成InputStream
 */
    public static InputStream bitmapToInputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }


    /**
     * 将Bitmap转换成字节数组
     */
    public static byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     *  获取图片大小
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    public static String saveBitmap2sd(Bitmap bitmap,String imageName ) throws IOException
    {
        if(bitmap == null)
        {
            LogUtil.v("saveBitmap2sd图片为null","");
            return null;
        }
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if  (sdCardExist)
        {

            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            String path=sdDir.getPath()+"/Pictures/cute";
            File path1 = new File(path);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();}
            File file = new File(sdDir,"/Pictures/cute/"+ imageName+".png");
            FileOutputStream out;
            try{
                out = new FileOutputStream(file);
                if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
                {
                    out.flush();
                    out.close();
                }
                LogUtil.v("saveBitmap2sd存入内存卡", file.getAbsolutePath());
//                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), imageName+".jpg", "description");
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                return file.getAbsolutePath();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else{
//            Toast.makeText(getApplicationContext(), "当前无内存卡，分享图片等功能无效！", Toast.LENGTH_LONG).show();
            return null;
        }

    }
}
