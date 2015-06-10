package com.harmazing.aixiumama.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.harmazing.aixiumama.R;

/**
 * Created by Lyn on 2014/11/25.
 */
public class StickersActivityArrayFragment extends Fragment {
    View v = null;
    int mNum;
    String stickerID;

    public static StickersActivityArrayFragment newInstance(int num, String id) {
        StickersActivityArrayFragment array= new StickersActivityArrayFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putString("stickerID", id);
        array.setArguments(args);

        return array;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        stickerID = getArguments() != null ? getArguments().getString("stickerID") : "";
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.gridview_left, container, false);
        switch (mNum){
            case 0:
                final GridView gridViewNew = (GridView)v.findViewById(R.id.gridview);


                break;
            case 1:
                final GridView gridViewHot = (GridView)v.findViewById(R.id.gridview);



                break;


        }

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}