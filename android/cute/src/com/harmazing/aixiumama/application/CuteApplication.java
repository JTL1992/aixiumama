package com.harmazing.aixiumama.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.baidu.frontia.FrontiaApplication;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.service.CuteService;
import com.harmazing.aixiumama.BuildConfig;
import com.harmazing.aixiumama.GPUImageFilterTools;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.model.sina.LoginButton;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import io.rong.imkit.RongIM;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import pl.droidsonroids.gif.GifDrawable;
//import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by liujinghui on 11/2/14.
 */
public class CuteApplication extends FrontiaApplication {
    //微信APP_ID cute
//    public static final String APP_ID = "wx14cd620827ff9a70";
//    public static final String APP_SECREAT = "01176f3e5880309202cc96d7693fd004";
    //aixiumama
    public static final String APP_ID = "wx166739c24a6b51d5";
    public static final String APP_SECREAT = "c38f2adbe15357534476d06d67a55626";
    public static boolean isDebug = BuildConfig.DEBUG;
    public static int expires_in = 0;
    public static AppSharedPref appSharedPref;
    public static String nextPageUrl;
    public static HashMap<Integer, String> provinces;
    public static HashMap<Integer, HashMap<Integer,Integer>> areaId;
    public static HashMap<Integer, HashMap<Integer, String>> locations;
    public static HashMap<Integer,HashMap<Integer,String>> findCity;
    public static String hotLabelListNextPageUrl;
    public static ImageLoader imageLoader;
    static DisplayImageOptions options;
    String TAG = "CuteApplication";
    //为了listview评论动态添加
    public static String commentToUser;
    public static Uri tempUri;
    public static int []screenWH = new int[2];
    public  LoginButton weibo;
    private static Boolean isSave = true;
    public static Boolean isVoice = true;
    public static Boolean isShake = true;
    public static Boolean isAvoid = false;
    public static List<Activity> activityList = new ArrayList<Activity>();
    //  计算个人中心的三个List高度
    public static int leftHeight = 0,centerHeight = 0,rightHeight = 0;
    //  防止用户直接点击右侧按钮，rightHeight没计算出来的标志位
    public static boolean isRightHeight0 ;
    public static JSONArray userInfoArray;
    static BitmapUtils bitmapUtils;
    public static GifDrawable gifFromResource;
    private static long MIN = 60;
    private static long HOUR = 3600;
    private static long DAY = 3600*24;
    private static long TWO_DAY = 2*DAY;
    private static long THREE_DAY = 3*DAY;
    private static long FOUR_DAY = 4*DAY;
    private static long WEEK = 7*DAY;
    private static long TWO_WEEK = 2*WEEK;
    private static String token = "dibUzW6y+pbl1NPTTQ+K/nEf0t7bkix7wKfO/hUaB+IKbWp58fpxcpUCuRvMClB8ALww1QxvQsmC448mUYpA/w==";
    public static Animation black1_alpha,black1_1,black2_1,black2_alpha,white_anim1,white_anim2,white_anim3;
    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);//初始化荣云
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .memoryCache(new WeakMemoryCache())
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);//初始化 ImageLoader
//        try {
//            RongIM.connect(token, new RongIMClient.ConnectCallback(){
//                @Override
//                public void onSuccess(String s) {
//                    // 此处处理连接成功。
//                    Log.d("此处处理连接成功Connect:", "Login successfully.");
//                }
//
//                @Override
//                public void onError(ErrorCode errorCode) {
//                    // 此处处理连接错误。
//                    Log.d("此处处理连接错误Connect:", "Login failed."+errorCode.toString());
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        black1_alpha =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.black1_alpha);

         black1_1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.black1_1);


        //  black2
         black2_1=
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.black2_1);

         black2_alpha=
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.black2_alpha);
        //  white
         white_anim1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.white_anim1);
         white_anim2 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.white_anim2);

         white_anim3 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.white_anim3);

//        try {
//            RongIM.connect(token, new RongIMClient.ConnectCallback(){
//                @Override
//                public void onSuccess(String s) {
//                    // 此处处理连接成功。
//                    Log.d("此处处理连接成功Connect:", "Login successfully.");
//                }
//
//                @Override
//                public void onError(ErrorCode errorCode) {
//                    // 此处处理连接错误。
//                    Log.d("此处处理连接错误Connect:", "Login failed."+errorCode.toString());
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }


//        startService(new Intent(CuteService.ACTION));
        provinces = new HashMap<Integer, String>();
        areaId = new HashMap<Integer, HashMap<Integer,Integer>>();
        locations = new HashMap<Integer, HashMap<Integer, String>>();
        findCity = new HashMap<Integer, HashMap<Integer, String>>();
        appSharedPref = AppSharedPref.newInstance(getApplicationContext());
        bitmapUtils = new BitmapUtils(getApplicationContext());

//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
//                .threadPoolSize(5)
//                .memoryCache(new WeakMemoryCache())
//                .build();
//
//        imageLoader = ImageLoader.getInstance();
//        imageLoader.init(config);
        //  init
        GPUImageFilterTools.initFilters();
        screenWH = getScreenHW(getApplicationContext());


        LogUtil.v("screen", BitmapUtil.px2dip(getApplicationContext(),(float)screenWH[0]));
        try {
            gifFromResource = new GifDrawable(getResources(), R.drawable.milk );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearCache(){
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();

    }
    public static void setSavePic(Boolean s){
       isSave = s;
    }
    public static Boolean getIsSave(){
        return isSave;
    }

    public static void downloadCornImage(String url,ImageView imageView){
        options = new DisplayImageOptions.Builder()

                .showImageForEmptyUri(R.drawable.blank)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .showImageOnFail(R.drawable.blank)
//                .displayer(new FadeInBitmapDisplayer(1000))
                .displayer(new RoundedBitmapDisplayer(180))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.displayImage(url,imageView,options);
    }
    public static void downloadLittleCornImage(String url,ImageView imageView){
        options = new DisplayImageOptions.Builder()
//                .showImageForEmptyUri(R.drawable.ic_launcher)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .showImageOnFail(R.drawable.ic_launcher)
//                .displayer(new FadeInBitmapDisplayer(1000))
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        imageLoader.displayImage(url,imageView,options);
    }

    public static void downloadIamge(String url,ImageView imageView){
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.blank)
                .showImageForEmptyUri(R.drawable.blank)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .showImageOnFail(R.drawable.blank)
                        //     .displayer(new RoundedBitmapDisplayer(150))
//                .displayer(new FadeInBitmapDisplayer(1000))
                .build();

        imageLoader.displayImage(url,imageView,options);
    }


    public static void downloadIamge(String url,ImageView imageView, ImageLoadingListener imageLoadingListener){
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.blank)
                .showImageForEmptyUri(R.drawable.blank)
                .showImageOnFail(R.drawable.blank)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
//                .displayer(new FadeInBitmapDisplayer(500))
                .build();

        imageLoader.displayImage(url,imageView,options, imageLoadingListener);
    }

//    public static void downloadIamge(String url,ImageView imageView){
//        bitmapUtils.display(imageView, url);
//    }

    /*
        为防止layout文件加载图片而OOM诞生的
     */
//    private void setLayoutBackgroundDrawable(int resId,int drawableId)
//    {
//        int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            findViewById(resId).setBackgroundDrawable(readBitMapToDrawable(getApplicationContext(),drawableId));
//        } else {
//            findViewById(resId).setBackground(readBitMapToDrawable(getApplicationContext(),drawableId));
//        }
//
//    }

    /*
* 获取控件宽
*/
    public static int getWidth(View view)
    {
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }
    /*
    * 获取控件高
    */
    public static int getHeight(View view)
    {
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredHeight());
    }



    /**
     * 为指定 GPUImage  添加滤镜
     * @param gpuImage
     * @param file1
     * @param file2
     */
    public static void setFilter(GPUImage gpuImage,String file1,String file2){
        GPUImageToneCurveFilter gpuImageToneCurveFilter = new GPUImageToneCurveFilter();
        SequenceInputStream sis = null;
        try {
            Vector<InputStream> vector = new Vector<InputStream>();

            vector.addElement(Thread.currentThread().getContextClassLoader().getResourceAsStream(file1));
            vector.addElement(Thread.currentThread().getContextClassLoader().getResourceAsStream(file2));
            Enumeration<InputStream> e = vector.elements();
            sis = new SequenceInputStream(e);

            gpuImageToneCurveFilter.setFromCurveFileInputStream(sis);
            gpuImage.setFilter(gpuImageToneCurveFilter);
     //       gpuImageView.setImage(CuteApplication.tempUri);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (sis != null)
                    sis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 为指定 GPUImageView 添加滤镜
     * @param gpuImageView
     * @param file1
     * @param file2
     */
    public static void setFilter(GPUImageView gpuImageView,String file1,String file2){
        GPUImageToneCurveFilter gpuImageToneCurveFilter = new GPUImageToneCurveFilter();
        SequenceInputStream sis = null;
        try {
            Vector<InputStream> vector = new Vector<InputStream>();

            vector.addElement(Thread.currentThread().getContextClassLoader().getResourceAsStream(file1));
            vector.addElement(Thread.currentThread().getContextClassLoader().getResourceAsStream(file2));
            Enumeration<InputStream> e = vector.elements();
            sis = new SequenceInputStream(e);

            gpuImageToneCurveFilter.setFromCurveFileInputStream(sis);
            gpuImageView.setFilter(gpuImageToneCurveFilter);
            //       gpuImageView.setImage(CuteApplication.tempUri);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (sis != null)
                    sis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setFilterCLear(GPUImage gpuImage) {
        GPUImageToneCurveFilter gpuImageToneCurveFilter = new GPUImageToneCurveFilter();
        gpuImage.setFilter(gpuImageToneCurveFilter);
    }


    public static Bitmap getViewShotBitmap(View v){
        v.clearFocus(); // 清除视图焦点
        v.setPressed(false);// 将视图设为不可点击

        boolean willNotCache = v.willNotCacheDrawing(); // 返回视图是否可以保存他的画图缓存
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation //将视图在此操作时置为透明
        int color = v.getDrawingCacheBackgroundColor(); // 获得绘制缓存位图的背景颜色
        v.setDrawingCacheBackgroundColor(0); // 设置绘图背景颜色
        if (color != 0) { // 如果获得的背景不是黑色的则释放以前的绘图缓存
            v.destroyDrawingCache(); // 释放绘图资源所使用的缓存
        }

        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache(); // 重新创建绘图缓存，此时的背景色是黑色
        Bitmap cacheBitmap = v.getDrawingCache(); // 将绘图缓存得到的,注意这里得到的只是一个图像的引用
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap); // 将位图实例化
        // Restore the view //恢复视图
        v.destroyDrawingCache();// 释放位图内存
        v.setWillNotCacheDrawing(willNotCache);// 返回以前缓存设置
        v.setDrawingCacheBackgroundColor(color);// 返回以前的缓存颜色设置
        return bitmap;
    }

    public static Bitmap convertViewToBitmap(View view){
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    public static int[] getScreenHW(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        return new int[]{width,height};
    }

    public static String gender2String(int gender){
        String str;
        if(gender == 1){
            str = "男";
        }else{
            str = "女";
        }

        return str;
    }

    /**
     * 日期(2014-1-1)转年龄(1岁1个月)
     * @param dateStr
     * @return
     */
    public static String date2Age(String dateStr){
        if(dateStr.equals("null") ||dateStr.equals("") ){
            return "未输入baby出生日期";
        }
        String babyAge ="";
        String a[] = dateStr.split("-");
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        final String year = str.substring(0,4);
        final String month = str.substring(5,7);
        final String day = str.substring(8,10);
        int y = Integer.parseInt(year) - Integer.parseInt(a[0]);
        int m = Integer.parseInt(month) - Integer.parseInt(a[1]);
        int d = Integer.parseInt(day) - Integer.parseInt(a[2]);
        Log.v("this.year"+"@this.month",year+"@"+month);
        Log.v("birthday.year"+"@birthday.month",a[0]+"@"+a[1]);
        if (y == 0)
            if (m>0)
                babyAge = m+"个月";
            else if (m == 0){
                if (d >= 0){
                    babyAge = m+"个月";
                }
                else
                    babyAge = "预产期"+" "+a[1]+"月"+a[2]+"日";
            }
            else
                babyAge = "预产期"+" "+a[1]+"月"+a[2]+"日";
        if (y < 0)
            babyAge = "预产期"+" "+a[1]+"月"+a[2]+"日";
        if (y > 0)
           if (m >= 0)
                if (m == 12)
             babyAge = (y+1)+"岁";
                else
             babyAge = (y)+"岁"+""+m+"个月";
           else
            {
                if (y-1 == 0){
                    babyAge = (m+12)+"个月";
                }
                else
                    babyAge = (y-1)+"岁"+""+(m+12)+"个月";
            }
        return babyAge;
    }


    public static void ExitApplication(){
        for (Activity activity : activityList)
           activity.finish();
        appSharedPref.clear();
        CuteService.deleteStickersCach();

//        System.exit(0);
    }

    /**
     * 计算该日期与当前日期所差的天数
     * @param date 如：(2014-11-26T03:28:10)
     * @return
     */
    /*
    按发布时间由近到远排序，如果是今天发布的，则显示“xxx小时前”；如果是昨天发布的显示“一天前”；两天以前三天以内发布的显示“两天前”；三天之前一周以内显示“三天前”；一周前两周内显示“一周前”；两周之前的直接显示“年月日”
     */
    public static String calculateDays(String date){
        int year = Integer.parseInt(date.substring(0,4));
        int day = Integer.parseInt(date.substring(8,10));
        int mouth = Integer.parseInt(date.substring(5,7));
        int hour = Integer.parseInt(date.substring(11,13));
        String result = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int curDay = calendar.get(Calendar.DAY_OF_MONTH) ;
        int curMouth= calendar.get(Calendar.MONTH) + 1;
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);

        if(day == curDay && (mouth == curMouth)) {
            result = (curHour - hour) + "小时前";
        }else if(((curDay - day) ==1) && (mouth == curMouth)){
            result = "一天前";
        }else if((curDay - day) == 2 && (mouth == curMouth)){
            result = "两天前";
        }else if((curDay - day) > 2 && (curDay - day) <=7 && (mouth == curMouth)){
            result = "三天前";
        }else if((curDay - day) > 7 && (curDay - day) <=14 && (mouth == curMouth)){
            result = "一周前";
        }else if((day - curDay)>=21 && (day - curDay) <= 30 && (mouth+1 == curMouth)){
            result = "一周前";
        }else{
            result = year + "年" + mouth + "月" + day + "日";
        }
        return result;
    }

    /**
     *  返回日期的 年月日  0,1,2
     * @param date 如：(2014-11-26T03:28:10)
     * @return
     */
    public static int[] getYMDdate(String date){
        int day = Integer.parseInt(date.substring(8,10));
        int mouth = Integer.parseInt(date.substring(5,7));
        int year = Integer.parseInt(date.substring(0,4));

        return new int[]{year,mouth,day};
    }


    /*
     {
            "gender": 0,
            "birthday": "2011-10-16",
            "parent": 4234,
            "create_date": "2014-11-18T05:30:05"
        }
     */

    /**
     *  返回格式：  两个月 男
     * @param obj
     * @return
     */
    public static String calculateBabyInfo(JSONObject obj){
        String str = "";
        try {
            if(obj.getInt("gender") == 1){
                str = date2Age(obj.getString("birthday"))  + "  男";
            }else if(obj.getInt("gender") == 2){
                str = date2Age(obj.getString("birthday"))  + "  女";
            }else
                str = date2Age(obj.getString("birthday"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return str;
    }


    @Override
    public void onLowMemory() {

        clearCache();
        super.onLowMemory();
    }
    public static Boolean isWechatTokenValid(Context ctx){
        String token = AppSharedPref.newInstance(ctx).getWeichatToken();

        return  false;
    }
    public static Boolean isBinded(Context ctx){

        return AppSharedPref.newInstance(ctx).getWeichatToken() != null ;
    }
    public static long timeStr2timestamp(String timeStr) {
        timeStr = timeStr.replaceAll("-", " ").replaceAll(":", " ").replaceAll("T", " ");

        String newTimeStr = "";
        String[] timeStrArray = timeStr.split(" ");

        int[] timeArray = new int[6];
        for (int i = 0; i < timeStrArray.length; i++) {
            timeArray[i] = Integer.valueOf(timeStrArray[i]);
            Log.v("timeArray[i]",i+"@"+timeArray[i]);
        }
        newTimeStr = String.format("%s-%s-%s %s:%s:%s", timeArray[0], timeArray[1], timeArray[2], timeArray[3], timeArray[4], timeArray[5]);
//        System.out.println(newTimeStr);
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(newTimeStr);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Long.decode(re_time);
    }
    public static String getTitle(long timeStamp){
        String timeS= String.valueOf(System.currentTimeMillis()).substring(0,10);
        long timeSta = Long.decode(timeS);
        String title = "无法计算";
        long time = timeSta - timeStamp;
        if (time < DAY){
            if (time < HOUR){
                if (time/MIN == 0)
                    title = "刚刚";
                else
                    title = time/MIN+"分钟前";
            }
            else{
                title = time/HOUR+"小时前";
            }
        }
        if (time < TWO_DAY  && time > DAY){
            title = "昨天";
        }
        if (time < THREE_DAY && time > TWO_DAY){
            title = "两天前";}
        if (time < FOUR_DAY && time > THREE_DAY){
            title = "三天前";
        }
        if (time < WEEK && time > FOUR_DAY){
            title = "一周前";
        }
        if (time < TWO_WEEK && time >WEEK ){
            title = "两周前";
        }
        if (time > TWO_WEEK){
            title =  "";
        }
        return title;
    }
    public void saveThumbNail(String url,String name){
        if (CuteApplication.imageLoader != null)
            CuteApplication.imageLoader.loadImage(API.STICKERS + url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    Log.v(TAG,"onLoadingStarted");
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    LogUtil.v(TAG,"头像获取失败");
                }

                @Override
                public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                    Log.v(TAG,"开始保存user头像");
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                BitmapUtil.saveBitmap2sd(bitmap,AppSharedPref.newInstance(getApplicationContext()).getUserId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            super.run();
                        }
                    }.start();
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    LogUtil.v(TAG,"Cancelled");
                }
            });
    }

}



