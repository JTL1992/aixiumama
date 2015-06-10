package com.harmazing.aixiumama.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.harmazing.aixiumama.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class BannerView extends LinearLayout {
	private ViewPager adViewPager;
	private ViewGroup group;
	private List<ImageView> bannerViewList = new ArrayList<ImageView>();
	private RelativeLayout pagerLayout;
	private int pageCount;
	private Timer bannerTimer;
	private Handler handler;
	private static BannerTimerTask bannerTimerTask;
	int[] images={R.drawable.banner1,R.drawable.banner2, R.drawable.banner3};
	public BannerView(Context context) {
		super(context);

	}

	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.banner_view, this);
		initView(context);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				adViewPager.setCurrentItem(msg.what);
				super.handleMessage(msg);

			}
		};
		bannerTimer = new Timer();
	}

	private void initView(final Context context) {
		RelativeLayout layout = (RelativeLayout) this
				.findViewById(R.id.view_pager_content);
		adViewPager = (ViewPager) this.findViewById(R.id.viewPager1);
		group = (ViewGroup) findViewById(R.id.iv_image);
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		LayoutParams params = (LayoutParams) layout
				.getLayoutParams();
		//根据图片高和宽的比例计算高度
		//测试图宽为694 高为323
	//	params.height = (int) (((double) dm.widthPixels / (double) 694 * (double) 323));
        LayoutParams MM = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(MM);

		ImageView imageView;


		//初始化数据
		for (int i = 0; i < 3; i++) {
			imageView = new ImageView(context);
			imageView.setImageResource(images[i]);
		//	imageView.scaleType
			bannerViewList.add(imageView);
		}

		pageCount = bannerViewList.size();//对应小点个数
		final ImageView[] imageViews = new ImageView[pageCount];
		for (int i = 0; i < pageCount; i++) {
			LayoutParams margin = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			// 设置每个小圆点距离左边的间距
			margin.setMargins(10, 0, 0, 0);
			imageView = new ImageView(context);
			// 设置每个小圆点的宽高
			imageView.setLayoutParams(new LayoutParams(15, 15));
			imageViews[i] = imageView;
			if (i == 0) {
				// 默认选中第一张图片
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				// 其他图片都设置未选中状态
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
			group.addView(imageViews[i], margin);
		}
		BannerViewAdapter adapter = new BannerViewAdapter(context,
				bannerViewList);
		adViewPager.setAdapter(adapter);
		adViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			 	bannerStartPlay();

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

				 
			}

			@Override
			public void onPageSelected(int arg0) {
				//当viewpager换页时 改掉下面对应的小点
				 

				for (int i = 0; i < imageViews.length; i++) {
					//设置当前的对应的小点为选中状态
					imageViews[arg0]
							.setBackgroundResource(R.drawable.page_indicator_focused);
					if (arg0 != i) {
						//设置为非选中状态
						imageViews[i]
								.setBackgroundResource(R.drawable.page_indicator_unfocused);
					}
				}
			}

		});
	}


	//启动banner自动轮播
	public void bannerStartPlay(){
		if (bannerTimer != null) {
			if (bannerTimerTask != null)
				bannerTimerTask.cancel();
			bannerTimerTask = new BannerTimerTask();
			bannerTimer.schedule(bannerTimerTask, 5000, 5000);//5秒后执行，每隔5秒执行一次
		}
	}
	//暂停banner自动轮播
	public static void bannerStopPlay(){
		if (bannerTimerTask != null)
			bannerTimerTask.cancel();
	}
	class BannerTimerTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("banner playing");
			Message msg = new Message();
			if (bannerViewList.size() <= 1)
				return;
			int currentIndex = adViewPager.getCurrentItem();
			if (currentIndex == bannerViewList.size() - 1)
				msg.what = 0;
			else
				msg.what = currentIndex + 1;

			handler.sendMessage(msg);
		}

	}
}
