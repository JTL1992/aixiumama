package com.harmazing.aixiumama.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harmazing.aixiumama.R;

/**
 * Created by liujinghui on 11/2/14.
 */
public class CameraFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  View.inflate(getActivity(), R.layout.fragment_camera, null);
    }

}