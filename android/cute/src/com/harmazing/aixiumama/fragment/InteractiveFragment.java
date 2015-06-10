package com.harmazing.aixiumama.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.adapter.CutedTabPageAdapter;

/**
 * Created by Administrator on 2014/12/8.
 */
public class InteractiveFragment extends Fragment {
    ViewPager mViewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_interactive, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ImageView btnBack = (ImageView)getView().findViewById(R.id.left_back);
        btnBack.setOnClickListener(onBackListener);
        mViewPager = (ViewPager) getView().findViewById(R.id.mViewPager);
        mViewPager.setAdapter(new CutedTabPageAdapter(this.getChildFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
              switch (i){
                  case 0 :
                       getView().findViewById(R.id.pink_line1).setVisibility(View.VISIBLE);
                       getView().findViewById(R.id.pink_line2).setVisibility(View.INVISIBLE);
                      break;
                  case 1 :
                      getView().findViewById(R.id.pink_line1).setVisibility(View.INVISIBLE);
                      getView().findViewById(R.id.pink_line2).setVisibility(View.VISIBLE);
                      break;
              }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

//                   getView().findViewById(R.id.text_cuted).setOnClickListener(new View.OnClickListener() {
//                       @Override
//                       public void onClick(View view) {
//                           mViewPager.setCurrentItem(0);
//                       }
//                   });
//                getView().findViewById(R.id.text_commented).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mViewPager.setCurrentItem(1);
//                    }
//                });

            }
        });
        getView().findViewById(R.id.text_cuted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });
        getView().findViewById(R.id.text_commented).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
            }
        });

        super.onActivityCreated(savedInstanceState);
    }
    View.OnClickListener onBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           getActivity().finish();
        }
    };

}