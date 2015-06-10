package com.harmazing.aixiumama.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.util.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageUtils2 {
	private static final int HARD_CACHE_CAPACITY = 30;
	/**
	 * 常用数据池 WeakReference与SoftReference都属于软引用,只不过WeakReference是弱引用,而SoftReference是强引用
	 */
	public final static HashMap<String, SoftReference<Bitmap>> mHardBitmapCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		public static final long serialVersionUID = -6716765964916804088L;
		@Override
		protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// 当map的size大于30时，把最近不常用的key放到mSoftBitmapCache中，从而保证mHardBitmapCache的效率
				SoftReference<Bitmap> value = eldest.getValue();
				if (value.get() != null) {
					mSoftBitmapCache.put(eldest.getKey(),new SoftReference<Bitmap>(value.get()));
				}
				return true;
			} else
				return false;
		}
	};
	/**
	 * TODO 第二级内存缓存,当mHardBitmapCache的key大于30的时候，会根据LRU算法把最近没有被使用的key放入到这个缓存中。
	 * Bitmap使用了SoftReference，当内存空间不足时，此cache中的bitmap会被垃圾回收掉
	 * ConcurrentHashMap是支持 高并发、高吞吐量的线程安全HashMap实现.
	 */
	public static Map<String, SoftReference<Bitmap>> mSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);
	/**
	 * TODO 从缓存中获取图片
	 */
	public static Bitmap getBitmapFromCache(String imagePath) {
		// 先从mHardBitmapCache缓存中获取
		synchronized (mHardBitmapCache) {
			if (mHardBitmapCache.containsKey(imagePath)) {
				SoftReference<Bitmap> softbitmap = mHardBitmapCache.get(imagePath);
				if (softbitmap != null) {
					final Bitmap bitmap = softbitmap.get();
					if (bitmap != null) {
						// 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
						mHardBitmapCache.remove(imagePath);
						mHardBitmapCache.put(imagePath,new SoftReference<Bitmap>(bitmap));
						return bitmap;
					}
				}
			}
		}
		// 如果mHardBitmapCache中找不到，到mSoftBitmapCache中找,但要先判断一下这里是否包含key,如果不包含肯定没有
		if (mSoftBitmapCache.containsKey(imagePath)) {
			SoftReference<Bitmap> bitmapReference = mSoftBitmapCache.get(imagePath);
			if (bitmapReference != null) {
				final Bitmap bitmap = bitmapReference.get();
				if (bitmap != null) {
					return bitmap;
				} else {
					// 如果key存在,但Soft里的Bitmap为空,那么清除这一组对象
					mSoftBitmapCache.remove(imagePath);
				}
			}
		}
		return null;
	}


	public static Bitmap loadImageDefault(Context context,File dir, String imgUrl,
			ImageCallback callback,boolean isCompress) {
		try {
			if (dir != null && imgUrl != null) {
				if (imgUrl.startsWith("http://")) {
					String imagePath = new File(dir, URLEncoder.encode(imgUrl,"utf-8")).getAbsolutePath();
					return loadImage(context, imagePath, imgUrl, callback,isCompress);
				} else {
					return loadImage(context, imgUrl, null, callback,isCompress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public interface ImageCallback {
        public void loadImage(Bitmap bitmap, String imagePath);
    }

	/**
	 * 方法描述：新:从本地或者服务端加载图片
    */
	private static Bitmap loadImage(final Context context,final String imagePath,final String imgUrl, final ImageCallback callback,final boolean isCompress) {
		Bitmap bitmap = null;
		if (imagePath != null)
			bitmap = getBitmapFromCache(imagePath);
		if (bitmap != null) {
			return bitmap;
		} else {// 从网上加载
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.obj != null) {
						Bitmap bitmap = (Bitmap) msg.obj;
						if (callback != null) {
							callback.loadImage(bitmap, imagePath);
						}
					}
				}
			};
			Runnable runnable = new Runnable() {
			
				@Override
				public void run() {
					try {
						Message msg = handler.obtainMessage();
						//检查本地缓存是否存在
						File imageFile = new File(imagePath);
						if (imageFile.exists() || imgUrl == null) {
							if (imageFile.exists()) {
								LogUtils.d("图片本地缓存加载url=:"+imagePath);
								putImageToMap(imagePath,isCompress);
								msg.obj = getBitmapFromCache(imagePath);
								handler.sendMessage(msg);
							}
						} else {//不存在则从网络获取
							Bitmap bitmap = null;
							SharedPreferences sp= context.getSharedPreferences("config",Context.MODE_PRIVATE);
							// 仅在wifi下加载
							if (sp.getBoolean("isWifiLoad", false)) {
								if (!NetUtil.isWifi(context)) {
									return;
								}
							}
							URL url = new URL(imgUrl);//URLEncoder.encode(string, "utf-8");
							LogUtils.d("图片从服务器加载url=:" + imgUrl);
							HttpURLConnection conn = null;
							conn = (HttpURLConnection) url.openConnection();
							conn.setConnectTimeout(5000);
							conn.setReadTimeout(10000);
							conn.connect();

                            if(conn.getResponseCode() == 200) {
                                InputStream is = conn.getInputStream();
                                LogUtils.d("is  =:" + is);
                                BufferedInputStream bis = new BufferedInputStream(is, 8192);
                                if (bis != null) {
                                    bitmap = BitmapFactory.decodeStream(bis);
                                }
                                try {
                                    // 保存文件到sd卡
                                    saveToSD(imagePath, bitmap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(ImageUtils2.class.getName(), "保存图片至SD卡出错！");
                                }
                                msg.what = 0;
                                putBitmapToMap(imagePath,bitmap);
                                msg.obj = getBitmapFromCache(imagePath);
                                handler.sendMessage(msg);

                            } else {
                                Log.e(ImageUtils2.class.getName(), "链接服务器失败!");
                            }
						}
					} catch (IOException e) {
						Log.e(ImageUtils2.class.getName(), "网络请求图片出错！");
						e.printStackTrace();
					}
				}
			};
			ThreadPoolManager.getInstance().addTask(runnable);
		}
		return null;
	}

	/**
	 * TODO 保存图片至sd卡
	 * @param fileName 文件名
	 * @param bitmap 
	 */
	public static synchronized void saveToSD(final String fileName,
			final Bitmap bitmap) {
		if (bitmap == null || fileName == null) {
			return;
		}
		Runnable runnable = new Runnable() {
			
			public void run() {
				FileOutputStream b = null;
				try {
					b = new FileOutputStream(fileName);

					bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						b.flush();
						b.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		};
		ThreadPoolManager.getInstance().addTask(runnable);

	}

	
	
	/**
	 * 添加本地缓存进map集合,并与路径对应
	 * @param imagePath 图片路径
	 * @param isCompress 是否要压缩
	 */
	public static void putImageToMap(String imagePath,boolean isCompress) {
		Bitmap bitmap=null;
		if(isCompress){
			bitmap= getZoomBitmap(imagePath, 150f);
		}else{
			bitmap = getZoomBitmap(imagePath, 0f);
		}
		if (bitmap != null) {
			mHardBitmapCache.put(imagePath, new SoftReference<Bitmap>(bitmap));
		}
	}
	/**
	 * 保存网络图片到map中
	 * @param imagePath 图片路径
	 * @param bitmap
	 */
	public static void putBitmapToMap(String imagePath,Bitmap bitmap) {
			if (bitmap != null) {
				mHardBitmapCache.put(imagePath, new SoftReference<Bitmap>(bitmap));
			}
	}
	/**
	 * TODO 从本地加载图片,并缩放
	 * 
	 * @param imagePath
	 * @param scale 压缩比率 可以为空,那么是默认是200,这个值越大则 压缩比越小  如果压缩率是0,则表示不压缩
	 * @return 一个缩放好的bitmap
	 */
	public static Bitmap getZoomBitmap(String imagePath, Float scale) {
		if (!new File(imagePath).exists())
			return null;
		if (scale == null){
			scale = 200f;
		}
		Bitmap bm=null;
		if(scale>0){
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bm = BitmapFactory.decodeFile(imagePath, options);
			int be = (int) (options.outHeight / (float) scale);
			if (be <= 1) {
				be = 1;
			}
			options.inSampleSize = be;// be=2.表示压缩为原来的1/2,以此类推
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(imagePath, options);
		}else{
			bm = BitmapFactory.decodeFile(imagePath);
		}
		return bm;
	}
}
