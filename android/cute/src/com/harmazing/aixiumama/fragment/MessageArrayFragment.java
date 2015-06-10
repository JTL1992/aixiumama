package com.harmazing.aixiumama.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Lyn on 2014/11/6.
 */
public class MessageArrayFragment extends Fragment {
    View v = null;
    int mNum;
    int followCount ;
    public static HashMap<Integer,JSONArray> mouthArray = new HashMap<Integer, JSONArray>();
    public static LinkedList<String> keyOfArray = new LinkedList<String>();
    static MessageArrayFragment newInstance(int num) {
        MessageArrayFragment array= new MessageArrayFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        array.setArguments(args);
        return array;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        switch (mNum){
            case 0:


                break;
            case 1:


                break;
            case 2:

                break;
        }

        return v;
    }

    public static String hashMapToJson(HashMap map) {
        String string = "[{";
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry e = (Map.Entry) it.next();
            string += "'" + e.getKey() + "':";
            string += "'" + e.getValue() + "',";
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}]";
        return string;
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