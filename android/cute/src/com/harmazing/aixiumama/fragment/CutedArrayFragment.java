package com.harmazing.aixiumama.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.harmazing.aixiumama.adapter.CutedListAdapter2;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.model.CutedItem;
import com.harmazing.aixiumama.model.CutedItem2;
import com.harmazing.aixiumama.adapter.CutedItemAdapter2;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2014/12/8.
 */
public class CutedArrayFragment extends android.support.v4.app.Fragment {
    View v = null;
    int mNum;
    long timeSta;
    View addView;
    Boolean flag = true;
    String nextCuteItemPage = null;
    String nextCommentItemPage = null;
    ArrayList<CutedItemAdapter2> arrayLists;
    CutedListAdapter2 listAdapter2,commentAdapter;

    private final long MIN = 60;
    private final long HOUR = 3600;
    private final long DAY = 3600*24;
    private final long TWO_DAY = 2*DAY;
    private final long THREE_DAY = 3*DAY;
    private final long FOUR_DAY = 4*DAY;
    private final long WEEK = 7*DAY;
    private final long TWO_WEEK = 2*WEEK;
    public static CutedArrayFragment newInstance(int num) {
        CutedArrayFragment array= new CutedArrayFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        array.setArguments(args);
        return array;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 0;
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0,10);
        timeSta = Long.decode(timeStamp);

    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        final Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0,10);
//        timeSta = Long.decode(timeStamp);
//        String str = formatter.format(curDate);
//        long timeSta = Long.decode(str);
//        final String year = str.substring(0,4);
//        final String month = str.substring(5,7);
//        final   String day = str.substring(8,10);
//        final  String hour = str.substring(11,13);
//        final String min = str.substring(14,16);
//        Log.v("时间分解","year:"+year+"month:"+month+"day:"+day+"hour:"+hour+"min:"+min);
        Log.v("时间戳",""+timeSta);
        switch (mNum){
            case 0 :
                v = inflater.inflate(R.layout.cuted_listview, container, false);
                final ListView cuteList = (ListView)v.findViewById(R.id.list_cuted);
                RequestParams params = new RequestParams();
                params.put("user",AppSharedPref.newInstance(getActivity()).getUserId());
                params.put("like","cute");
                HttpUtil.addClientHeader(getActivity());
                HttpUtil.get(API.LIKES_CUTE,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.v("LIKES_CUTE",response.toString());
                        try {
                            CutedItem2 cutedItemCach = null;
                            arrayLists = new ArrayList<CutedItemAdapter2>();
                            ArrayList<CutedItem2> cutedItem2s = new ArrayList<CutedItem2>();
                            nextCuteItemPage = response.getString("next");
                            JSONArray result = response.getJSONArray("results");
                            for (int i = 0; i < result.length(); i++){
                                String  name = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("name");
                                String image  = result.getJSONObject(i).getJSONObject("cute_detail").getString("image");
                                String icon  = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("image_small");
                                int publisherId = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getInt("auth_user");
                                String description  = result.getJSONObject(i).getJSONObject("cute_detail").getString("text");
                                String date  = result.getJSONObject(i).getString("create_date");
                                int cuteId = result.getJSONObject(i).getJSONObject("cute_detail").getInt("id");
                                int  userId = result.getJSONObject(i).getJSONObject("cute_detail").getInt("user");
                                String title= getTitle(timeStr2timestamp(date));
                                if (title.equals(""))
                                    title = date.replace("T"," ").substring(0,10);
                                if (i == 0){
                                    CutedItem2 cutedItem2 = new CutedItem2(name,description,image,icon,date,cuteId,publisherId,title);
                                    cutedItem2s.add(cutedItem2);
                                    cutedItemCach = cutedItem2;
                                }
                                else{
                                    CutedItem2 cutedItem2 = new CutedItem2(name,description,image,icon,date,cuteId,publisherId,title);
                                    if (cutedItem2.getTitle().equals(cutedItemCach.getTitle())){
                                        cutedItem2s.add(cutedItem2);
                                        cutedItemCach = cutedItem2;
                                    }
                                    else{
                                        CutedItemAdapter2 cutedItemAdapter2 = new CutedItemAdapter2(getActivity(),cutedItem2s);
                                        arrayLists.add(cutedItemAdapter2);
                                        cutedItem2s = new ArrayList<CutedItem2>();
                                        cutedItem2s.add(cutedItem2);
                                        cutedItemCach = cutedItem2;
                                    }
                                  if (i == result.length()-1){
                                        CutedItemAdapter2 cutedItemAdapter2 = new CutedItemAdapter2(getActivity(),cutedItem2s);
                                        arrayLists.add(cutedItemAdapter2);

                                    }

                                }
                            }
                            listAdapter2 = new CutedListAdapter2(getActivity(),arrayLists);
                            cuteList.setAdapter(listAdapter2);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        cuteList.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(final AbsListView absListView, int i) {
                                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                                    if(absListView.getLastVisiblePosition() == absListView.getCount() - 1){
                                        Log.v("nextCutePage",nextCuteItemPage);
                                        if (nextCuteItemPage.equals("null")){
                                            Log.v("viewNum",""+cuteList.getFooterViewsCount());
//                                            addView = inflater.inflate(R.layout.no_more, absListView, false);

                                            addView = inflater.inflate(R.layout.no_more, absListView, false);
                                            if (cuteList.getFooterViewsCount() == 0)
                                                cuteList.addFooterView(addView);
                                            Log.v("nextCutePage","&&&&&&&");
                                            Log.v("viewNum",""+cuteList.getFooterViewsCount());
                                        }
                                        if (!nextCuteItemPage.equals("null")){
                                            addView = inflater.inflate(R.layout.loading, absListView, false);
                                            HttpUtil.get(nextCuteItemPage,new JsonHttpResponseHandler(){
                                                @Override
                                                public void onStart() {
                                                    Log.v("请求开始","");
                                               if (cuteList.getFooterViewsCount() == 0)
//                                                   cuteList.addFooterView(addView);
                                                    super.onStart();
                                                }

                                                @Override
                                                public void onFinish() {
                                                    super.onFinish();
                                                    Log.v("请求结束","");
//                                                    cuteList.removeFooterView(addView);
                                                }
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                    super.onSuccess(statusCode, headers, response);
                                                    try {
                                                        CutedItem2 cutedItemCach = null;
                                                        arrayLists = new ArrayList<CutedItemAdapter2>();
                                                        ArrayList<CutedItem2> cutedItem2s = new ArrayList<CutedItem2>();
                                                        nextCuteItemPage = response.getString("next");
//                                                        if (nextCuteItemPage.equals("null")){
////                                                            addView = inflater.inflate(R.layout.no_more, absListView, false);
////                                                            cuteList.removeFooterView(addView);
//                                                            addView = inflater.inflate(R.layout.no_more, absListView, false);
//                                                            cuteList.addFooterView(addView);
//                                                        }
                                                        JSONArray result = response.getJSONArray("results");
                                                        for (int i = 0; i < result.length(); i++){
                                                            String  name = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("name");
                                                            String image  = result.getJSONObject(i).getJSONObject("cute_detail").getString("image");
                                                            String icon  = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("image_small");
                                                            int publisherId = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getInt("auth_user");
                                                            String description  = result.getJSONObject(i).getJSONObject("cute_detail").getString("text");
                                                            String date  = result.getJSONObject(i).getString("create_date");
                                                            int cuteId = result.getJSONObject(i).getJSONObject("cute_detail").getInt("id");
                                                            int  userId = result.getJSONObject(i).getJSONObject("cute_detail").getInt("user");
                                                            String title= getTitle(timeStr2timestamp(date));
                                                            if (title.equals(""))
                                                                title = date.replace("T"," ").substring(0,10);
                                                            if (i == 0){
                                                                CutedItem2 cutedItem2 = new CutedItem2(name,description,image,icon,date,cuteId,publisherId,title);
                                                                cutedItem2s.add(cutedItem2);
                                                                cutedItemCach = cutedItem2;
                                                            }
                                                            else{
                                                                CutedItem2 cutedItem2 = new CutedItem2(name,description,image,icon,date,cuteId,publisherId,title);
                                                                if (cutedItem2.getTitle().equals(cutedItemCach.getTitle())){
                                                                    cutedItem2s.add(cutedItem2);
                                                                    cutedItemCach = cutedItem2;
                                                                }
                                                                else{
                                                                    CutedItemAdapter2 cutedItemAdapter2 = new CutedItemAdapter2(getActivity(),cutedItem2s);
                                                                    arrayLists.add(cutedItemAdapter2);
                                                                    cutedItem2s = new ArrayList<CutedItem2>();
                                                                    cutedItem2s.add(cutedItem2);
                                                                    cutedItemCach = cutedItem2;
                                                                }
                                                                if (i == result.length()-1){
                                                                    CutedItemAdapter2 cutedItemAdapter2 = new CutedItemAdapter2(getActivity(),cutedItem2s);
                                                                    arrayLists.add(cutedItemAdapter2);
                                                                }
                                                            }
                                                        }
//                                                        if (nextCuteItemPage.equals("null")){
////                                                            addView = inflater.inflate(R.layout.no_more, absListView, false);
////                                                            cuteList.removeFooterView(addView);
//                                                            addView = inflater.inflate(R.layout.no_more, absListView, false);
//                                                                cuteList.addFooterView(addView);
//                                                        }
                                                        listAdapter2.addCuteItemList(arrayLists);
                                                        listAdapter2.notifyDataSetChanged();

                                                    }catch (Exception e){
                                                        Log.v("解析失败","");
                                                        addView = inflater.inflate(R.layout.no_more, absListView, false);
                                                        if (cuteList.getFooterViewsCount()<1)
                                                        cuteList.addFooterView(addView);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                            }
                        });
                    }
                });break;
            case 1:
                v = inflater.inflate(R.layout.layout_msg, container, false);
                final ListView commentList = (ListView)v.findViewById(R.id.list_comment);
                RequestParams param = new RequestParams();
                param.put("user",AppSharedPref.newInstance(getActivity()).getUserId());
                param.put("type","cute");
                HttpUtil.addClientHeader(getActivity());
                HttpUtil.get(API.COMMENTS_CUTE_ID+"?",param, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        ArrayList<CutedItem> cutedItems = new ArrayList<CutedItem>();

                        try {
//                            JSONArray result = response.getJSONArray("results");
//                            for (int i = 0; i < result.length(); i++){
//                                name [i%2] = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("name");
//                                image [i%2] = result.getJSONObject(i).getJSONObject("cute_detail").getString("image");
//                                icon [i%2] = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("image_small");
//                                description [i%2] = result.getJSONObject(i).getJSONObject("cute_detail").getString("text");
//                                date [i%2] = result.getJSONObject(i).getString("create_date");
//                                cuteId[i%2] = result.getJSONObject(i).getJSONObject("cute_detail").getInt("id");
//                                userId[i%2] = result.getJSONObject(i).getJSONObject("cute_detail").getInt("user");
//                                title[i%2] = getTitle(timeStr2timestamp(date[i%2]));
////                                Log.v("name[i%2]+description [i%2]",name[i%2]+description [i%2]);
//                                if (i%2 == 1){
//                                    if (title[0].equals(title[1])){
//                                        cutedItems.add(new CutedItem(name, description, image, icon, date, cuteId, userId, title));
//                                    }
//                                    else{
//                                        String [] name1 = new String[2];String [] name2 = new String[2];
//                                        String [] description1 = new String[2];String [] description2 = new String[2];
//                                        String [] image1 = new String[2];String [] image2 = new String[2];
//                                        String [] icon1 = new String[2]; String [] icon2 = new String[2];
//                                        String [] date1 = new String[2]; String [] date2 = new String[2];
//                                        int [] cuteId1 = new int [2]; int [] cuteId2 = new int [2];
//                                        int [] userId1 = new int [2]; int [] userId2 = new int [2];
//                                        String [] title1 = new String[2]; String [] title2 = new String[2];
//                                        name1[0] = name[0]; description1[0] = description[0]; image1[0] = image[0];
//                                        icon1[0] = icon[0]; date1[0] = date[0]; cuteId1[0] = cuteId[0];userId1[0] = userId[0];
//                                        title1[0] = title[0];
//                                        name2[0] = name[1]; description2[0] = description[1]; image2[0] = image[1];
//                                        icon2[0] = icon[1]; date2[0] = date[1]; cuteId2[0] = cuteId[1];userId2[0] = userId[1];
//                                        title2[0] = title[1];
//                                        cutedItems.add(new CutedItem(name1, description1, image1, icon1, date1, cuteId1, userId1, title1));
//                                        cutedItems.add(new CutedItem(name2, description2, image2, icon2, date2, cuteId2, userId2, title2));
//                                    }
//                                    name = new String[2];
//                                    image = new String[2];
//                                    icon = new String[2];
//                                    description = new String[2];
//                                    date = new String[2];
//                                    cuteId = new int[2];
//                                    userId = new int[2];
//                                    title  = new String[2];
//                                }
//                                if (i == result.length()-1 && i%2 == 0)
//                                    cutedItems.add(new CutedItem(name, description, image, icon, date, cuteId, userId, title));
//                            }
//                            CommentedItemAdapter commentedItemAdapter = new CommentedItemAdapter(getActivity(),cutedItems);
//                            commentList.setAdapter(commentedItemAdapter);
                            CutedItem2 cutedItemCach = null;
                            arrayLists = new ArrayList<CutedItemAdapter2>();
                            ArrayList<CutedItem2> cutedItem2s = new ArrayList<CutedItem2>();
                            JSONArray result = response.getJSONArray("results");
                            nextCommentItemPage = response.getString("next");
                            for (int i = 0; i < result.length(); i++){
                                String  name = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("name");
                                String image  = result.getJSONObject(i).getJSONObject("cute_detail").getString("image");
                                String icon  = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("image_small");
                                String description  = result.getJSONObject(i).getJSONObject("cute_detail").getString("text");
                                String date  = result.getJSONObject(i).getString("create_date");
                                int cuteId = result.getJSONObject(i).getJSONObject("cute_detail").getInt("id");
                                int  userId = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getInt("auth_user");
                                String title= getTitle(timeStr2timestamp(date));
                                if (title.equals(""))
                                    title = date.replace("T"," ").substring(0,10);
                                if (i == 0){
                                    CutedItem2 cutedItem2 = new CutedItem2(name,description,image,icon,date,cuteId,userId,title);
                                    cutedItem2s.add(cutedItem2);
                                    cutedItemCach = cutedItem2;
                                }
                                else{
                                    CutedItem2 cutedItem2 = new CutedItem2(name,description,image,icon,date,cuteId,userId,title);
                                    if (cutedItem2.getTitle().equals(cutedItemCach.getTitle())){
                                        cutedItem2s.add(cutedItem2);
                                        cutedItemCach = cutedItem2;
                                    }
                                    else{
                                        CutedItemAdapter2 cutedItemAdapter2 = new CutedItemAdapter2(getActivity(),cutedItem2s);
                                        arrayLists.add(cutedItemAdapter2);
                                        cutedItem2s = new ArrayList<CutedItem2>();
                                        cutedItem2s.add(cutedItem2);
                                        cutedItemCach = cutedItem2;
                                    }
                                }
                                if (i == result.length()-1){
                                    CutedItemAdapter2 cutedItemAdapter2 = new CutedItemAdapter2(getActivity(),cutedItem2s);
                                    arrayLists.add(cutedItemAdapter2);
                                }
                            }
                            commentAdapter = new CutedListAdapter2(getActivity(),arrayLists);
                            commentList.setAdapter(commentAdapter);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                       commentList.setOnScrollListener(new AbsListView.OnScrollListener() {
                           @Override
                           public void onScrollStateChanged(final AbsListView absListView, int i) {
                               if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                                   if(absListView.getLastVisiblePosition() == absListView.getCount() - 1){
                                       Log.v("nextCommentItemPage",nextCommentItemPage);
                                        if (nextCommentItemPage.equals("null")){
                                            Log.v("viewNum",""+commentList.getFooterViewsCount());
//                                            addView = inflater.inflate(R.layout.no_more, absListView, false);
//                                            if (commentList.getFooterViewsCount() == 1){
//                                                commentList.removeFooterView(addView);
//                                            }
//                                            Log.v("viewNum",""+commentList.getFooterViewsCount());
                                            addView = inflater.inflate(R.layout.no_more, absListView, false);
                                            if (commentList.getFooterViewsCount() == 0)
                                                commentList.addFooterView(addView);
                                            Log.v("nextCutePage","&&&&&&&");
                                            Log.v("viewNum",""+commentList.getFooterViewsCount());
                                        }
                                       if (!nextCommentItemPage.equals("null")){
                                           addView = inflater.inflate(R.layout.loading, absListView, false);
                                           HttpUtil.get(nextCommentItemPage,new JsonHttpResponseHandler(){
                                               @Override
                                               public void onStart() {
                                                   Log.v("请求开始","");
//                                                   if (commentList.getFooterViewsCount() == 0)
//                                                       commentList.addFooterView(addView);
                                                   super.onStart();
                                               }

                                               @Override
                                               public void onFinish() {
                                                   super.onFinish();
                                                   Log.v("请求结束","");
//                                                   commentList.removeFooterView(addView);
                                               }
                                               @Override
                                               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                   super.onSuccess(statusCode, headers, response);
                                                   try {
                                                       CutedItem2 cutedItemCach = null;
                                                       arrayLists = new ArrayList<CutedItemAdapter2>();
                                                       ArrayList<CutedItem2> cutedItem2s = new ArrayList<CutedItem2>();
                                                       nextCommentItemPage = response.getString("next");

                                                       JSONArray result = response.getJSONArray("results");
                                                       for (int i = 0; i < result.length(); i++){
                                                           String  name = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("name");
                                                           String image  = result.getJSONObject(i).getJSONObject("cute_detail").getString("image");
                                                           String icon  = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getString("image_small");
                                                           String description  = result.getJSONObject(i).getJSONObject("cute_detail").getString("text");
                                                           String date  = result.getJSONObject(i).getString("create_date");
                                                           int cuteId = result.getJSONObject(i).getJSONObject("cute_detail").getInt("id");
                                                           int  userId = result.getJSONObject(i).getJSONObject("cute_detail").getJSONObject("publish_user").getInt("auth_user");
                                                           String title= getTitle(timeStr2timestamp(date));
                                                           if (title.equals(""))
                                                               title = date.replace("T"," ").substring(0,10);
                                                           if (i == 0){
                                                               CutedItem2 cutedItem2 = new CutedItem2(name,description,image,icon,date,cuteId,userId,title);
                                                               cutedItem2s.add(cutedItem2);
                                                               cutedItemCach = cutedItem2;
                                                           }
                                                           else{
                                                               CutedItem2 cutedItem2 = new CutedItem2(name,description,image,icon,date,cuteId,userId,title);
                                                               if (cutedItem2.getTitle().equals(cutedItemCach.getTitle())){
                                                                   cutedItem2s.add(cutedItem2);
                                                                   cutedItemCach = cutedItem2;
                                                               }
                                                               else{
                                                                   CutedItemAdapter2 cutedItemAdapter2 = new CutedItemAdapter2(getActivity(),cutedItem2s);
                                                                   arrayLists.add(cutedItemAdapter2);
                                                                   cutedItem2s = new ArrayList<CutedItem2>();
                                                                   cutedItem2s.add(cutedItem2);
                                                                   cutedItemCach = cutedItem2;
                                                               }
                                                           }
                                                           if (i == result.length()-1){
                                                               CutedItemAdapter2 cutedItemAdapter2 = new CutedItemAdapter2(getActivity(),cutedItem2s);
                                                               arrayLists.add(cutedItemAdapter2);
                                                           }
                                                       }
                                                       commentAdapter.addCuteItemList(arrayLists);
                                                       commentAdapter.notifyDataSetChanged();
                                                   }catch (Exception e){
                                                       Log.v("解析失败","");
                                                       addView = inflater.inflate(R.layout.no_more, absListView, false);
                                                       if (commentList.getFooterViewsCount()<1)
                                                       commentList.addFooterView(addView);
                                                   }
                                               }
                                           });
                                       }
                                   }
                               }
                           }

                           @Override
                           public void onScroll(AbsListView absListView, int i, int i2, int i3) {

                           }
                       });
                    }
                });
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
    public void compareDay(String date1 ,String date2){
        String y1 = date1.substring(0,4);
        String y2 = date2.substring(0,4);
        String m1 = date1.substring(5,7);
        String m2 = date2.substring(5,7);
        String d1 = date1.substring(8,10);
        String d2 = date2.substring(8,10);
//        if (y1.equals(y2) && )
    }
    public  long timeStr2timestamp(String timeStr) {
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
    public String getTitle(long timeStamp){
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

}