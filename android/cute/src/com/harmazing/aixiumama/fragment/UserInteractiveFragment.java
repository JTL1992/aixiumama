package com.harmazing.aixiumama.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2014/11/26.
 */
public class UserInteractiveFragment extends Fragment {
    View v = null;
    int mNum;
    int userID;
    String labelID1,labelID2,labelID3,labelID4;
    static UserInteractiveFragment newInstance(int num,int id) {
        UserInteractiveFragment array= new UserInteractiveFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putInt("id", id);
        array.setArguments(args);
        return array;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        userID = getArguments() != null ? getArguments().getInt("id") : 1;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (mNum){
            case 0 : break;
            case 1 : break;
        }
        return v;
    }
}