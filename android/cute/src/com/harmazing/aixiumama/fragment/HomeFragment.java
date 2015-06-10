package com.harmazing.aixiumama.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabWidget;

import com.harmazing.aixiumama.API.HttpHead;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.ActivityActivity;
import com.harmazing.aixiumama.activity.PresentActivity;
import com.harmazing.aixiumama.adapter.TabPageAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.view.HMViewPager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.rong.imkit.RongIM;


public class HomeFragment extends Fragment {

    public static HMViewPager mViewPager;
    View view;
    View bottomLine;
    ImageButton btnCostumServer;
    int screenWidth,screenHeight;
    private FragmentTabHost mTabHost;
    private TabWidget mTabWidget;
    public static LinearLayout topView,recommend_attentionLayout;
    LinearLayout.LayoutParams params;
    LinkedList<Fragment> fragmentList;
    private ViewPager home_fagment_viewpager;   //首页活动轮播
    private LinearLayout dots_ll;   //放点的集合
    private List<ImageView> imageList = new ArrayList<ImageView>(); //图片集合
    private List<View> dotList = new ArrayList<View>(); //点的集合
    private int[] imageArr = {R.drawable.lunbo1, R.drawable.lunbo2, R.drawable.lunbo3};
    private String[] imageStrings = {"","",""};
    private HomeFragmentViewPageAdapter homeFragmentViewPageAdapter;
    private int currentPosition;
    private final String customerServiceId = "KEFU1427365229308";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();

        super.onCreate(savedInstanceState);
    }


    public static void setCurItem(int i){
        mViewPager.setCurrentItem(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        fragmentList = new LinkedList<Fragment>();
        fragmentList.add(new RecommendFramgent());
        fragmentList.add(new AttentionFragment());
//        if(mViewPager == null) {
            mViewPager = (HMViewPager) view.findViewById(R.id.viewpager);
//        }
//        if(mViewPager.getAdapter() == null) {
            mViewPager.setAdapter(new TabPageAdapter(this.getChildFragmentManager(),fragmentList));
//        }

        //首页活动的轮播
        home_fagment_viewpager = (ViewPager) view.findViewById(R.id.home_fragment_viewpager);
        //点的线性布局
        dots_ll = (LinearLayout) view.findViewById(R.id.dots_ll);

        btnCostumServer = (ImageButton) view.findViewById(R.id.btn_server);
        //客服按钮
//        btnCostumServer.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
        mTabWidget = mTabHost.getTabWidget();
        mTabHost.getChildAt(0).findViewById(R.id.red_point).setVisibility(View.INVISIBLE);
        topView = (LinearLayout)getView().findViewById(R.id.index_top_view);
        recommend_attentionLayout = (LinearLayout)getView().findViewById(R.id.recommend_attention_layout);
        bottomLine = (View)getView().findViewById(R.id.bottom_line);
        params = new LinearLayout.LayoutParams(bottomLine.getLayoutParams());
        params.setMargins(CuteApplication.getScreenHW(getActivity())[0]/4 - BitmapUtil.dip2px(getActivity(),20),0,0,0);
        bottomLine.setLayoutParams(params);
//        mViewPager = (ViewPager) getView().findViewById(R.id.viewpager);
//        mViewPager.setAdapter(new TabPageAdapter(this.getChildFragmentManager(),fragmentList));
        btnCostumServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Handler mHandler;
                 mHandler = new Handler();
                 mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        RongIM.getInstance().startCustomerServiceChat(getActivity(), customerServiceId, "在线客服");
                    }
                });
            }
        });
        mViewPager.setOnPageChangeListener(new HMViewPager.OnPageChangeListener() {
            int offset = screenWidth/2;


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
//                        startAnimaLeft(bottomLine);
                        params.setMargins(CuteApplication.getScreenHW(getActivity())[0]/4 - BitmapUtil.dip2px(getActivity(), 20),0,0,0);
                        bottomLine.setLayoutParams(params);
                        break;
                    case 1:
//                        startAnimaRight(bottomLine);
                        params.setMargins(CuteApplication.getScreenHW(getActivity())[0] * 3/4 - BitmapUtil.dip2px(getActivity(), 20),0,0,0);
                        bottomLine.setLayoutParams(params);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initImageString();
        //初始化数据
        initData();
        //初始化点
        initDot();



        homeFragmentViewPageAdapter = new HomeFragmentViewPageAdapter();
        home_fagment_viewpager.setAdapter(homeFragmentViewPageAdapter);

        currentPosition = Integer.MAX_VALUE / 2;

        home_fagment_viewpager.setCurrentItem(currentPosition);

        //开始轮播
        startScroll();

        home_fagment_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageSelected(int position) {

                //显示点
                for(int i = 0; i < dotList.size(); i++) {
                    if(i == position % imageList.size()) {
                        dotList.get(position % imageList.size()).setBackgroundResource(R.drawable.dot_focus);
                    } else {
                        dotList.get(i).setBackgroundResource(R.drawable.dot_normal);
                    }
                }

                System.out.println("当前是第 " + position % imageList.size() + " 页");
            }

            @Override
            public void onPageScrolled(int i, float v, int i2) {}
            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        getView().findViewById(R.id.attention).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
//                startAnimaRight(params);
                params.setMargins(CuteApplication.getScreenHW(getActivity())[0] * 3/4 - BitmapUtil.dip2px(getActivity(), 20),0,0,0);
                bottomLine.setLayoutParams(params);

            }
        });

        getView().findViewById(R.id.recommend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
//                startAnimaLeft(bottomLine);
                params.setMargins(CuteApplication.getScreenHW(getActivity())[0]/4 - BitmapUtil.dip2px(getActivity(), 20),0,0,0);
                bottomLine.setLayoutParams(params);

            }
        });
    }




    class HomeFragmentViewPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
//            return imageList.size();
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            ImageView iv = imageList.get(position % imageList.size());
            try{
                ((ViewPager)container).addView(iv);
            } catch (Exception e) {

            }


            //给viewpager的每个照片添加点击事件,
            iv.setOnTouchListener(new View.OnTouchListener() {
                private long downTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //当按下图片时, 图片停止轮播,
                            handler.removeCallbacksAndMessages(null);
                            downTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (System.currentTimeMillis() - downTime < 500) {
                                //当抬起和按下小于500毫秒时,认为是点击了相对于的图片,
//                                ToastUtil.show(getActivity(), "点击了 position = " + position % imageList.size());
                                if (position % imageList.size() == 1){
                                    Intent intent = new Intent(getActivity(), PresentActivity.class);
                                    startActivity(intent);
                                }
                                if (position % imageList.size() == 0){
                                    Intent intent = new Intent(getActivity(), ActivityActivity.class);
                                    intent.putExtra("ACTIVITY",1);//轮播1
                                    startActivity(intent);
                                }
                                if (position % imageList.size() == 2){
                                    Intent intent = new Intent(getActivity(),ActivityActivity.class);
                                    intent.putExtra("ACTIVITY",3);//轮播3
                                    startActivity(intent);
                                }
                            }
                            startScroll();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            //抬起时,再次轮播
                            startScroll();
                            break;
                    }
                    return true;
                }
            });

            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            ((ViewPager)container).removeView((ImageView)object);
        }
    }

    /*
     * 初始化首页轮播的图片
     */
    private void initData() {
        imageList.clear();  //先清空集合
        ImageView iv;
        for(int i = 0; i < imageStrings.length; i++) {
            iv = new ImageView(getActivity());
            CuteApplication.imageLoader.displayImage(HttpHead.image+imageStrings[i],iv);
            imageList.add(iv);
        }
    }
//    private void initData() {
//        imageList.clear();  //先清空集合
//        ImageView iv;
//        for(int i = 0; i < imageArr.length; i++) {
//            iv = new ImageView(getActivity());
//            iv.setBackgroundResource(imageArr[i]);
////            CuteApplication.imageLoader.displayImage(HttpHead.image+imageStrings[i],iv);
//            imageList.add(iv);
//        }
//    }

    /**
     * 初始化点,默认选中第一个是红色的,其他的是白色的
     */
    private void initDot() {
        //先移除其中所有的view对象,方便下次复用
        dots_ll.removeAllViews();
        dotList.clear();

        View view;
        for(int i = 0; i < imageList.size(); i++) {
            view = new View(getActivity());
            if(i == 0) {    //默认第一次显示第一张
                view.setBackgroundResource(R.drawable.dot_focus);
            } else {
                view.setBackgroundResource(R.drawable.dot_normal);
            }
            //设置点在屏幕上显示的宽高
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    BitmapUtil.dip2px(getActivity(), 6),
                    BitmapUtil.dip2px(getActivity(), 6));
            view.setLayoutParams(params);
            params.setMargins(8, 0, 8, 0);

            /**
             * 将点添加到布局中,并且添加到集合中
             */
            dots_ll.addView(view);
            dotList.add(view);
        }
    }


    //处理当前的位置
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            System.out.println("currentPosition : " + currentPosition);
            home_fagment_viewpager.setCurrentItem(currentPosition);
            startScroll();
        };
    };

    //没过3秒钟轮播一次
    private void startScroll() {
        handler.postDelayed(new RunnableTask(), 3000);
    }

    //设置当前的位置,发送消息给handler
    private class RunnableTask implements Runnable {
        @Override
        public void run() {
            //获取当前的位置加1
            currentPosition = home_fagment_viewpager.getCurrentItem()+1;
            handler.obtainMessage().sendToTarget();
        }
    }

//    public void startAnimaLeft(final View view){
//
//        Animation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.25f,
//                Animation.RELATIVE_TO_PARENT, 0.75f,
//                Animation.RELATIVE_TO_PARENT,0.0f,
//                Animation.RELATIVE_TO_PARENT,0.0f );
//
//        translateAnimation.setDuration(500);
//        translateAnimation.setFillAfter(true);
//        view.startAnimation(translateAnimation);
//
//    }


//    public void startAnimaRight(final View view){
//
//        Animation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.75f,
//                Animation.RELATIVE_TO_PARENT, BitmapUtil.dip2px(getActivity(),0.25f),
//                Animation.RELATIVE_TO_PARENT,0.0f,
//                Animation.RELATIVE_TO_PARENT,0.0f );
//
//        translateAnimation.setDuration(500);
//        translateAnimation.setFillAfter(true);
//        view.startAnimation(translateAnimation);
//
//    }
     private void initImageString(){
         imageStrings[0] = AppSharedPref.newInstance(getActivity()).get1WheelImage();
         imageStrings[1] = AppSharedPref.newInstance(getActivity()).get2WheelImage();
         imageStrings[2] = AppSharedPref.newInstance(getActivity()).get3WheelImage();
     }

    @Override
    public void onStop() {
        handler.removeCallbacksAndMessages(null);
        super.onStop();
    }
}