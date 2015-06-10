package com.harmazing.aixiumama.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.CuteMostActivity;
import com.harmazing.aixiumama.activity.HotStickersActivity;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.activity.SearchResultsActivity2;
import com.harmazing.aixiumama.activity.StickersActivity;
import com.harmazing.aixiumama.adapter.HotLabelListAdapter;
import com.harmazing.aixiumama.adapter.RecommendUserAdapter;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.service.CuteService;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.HorizontalListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pullrefreshview.PullToRefreshBase;
import com.pullrefreshview.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 11/2/14.
 */
public class DiscoveryFragment extends Fragment {
    View addView;
    SearchView mSearchView;
    ListView hotLabelListView;
    ImageView bigiv, rightIV1, rightIV2,rightIV3, rightIV4;
    ImageView sticker1, sticker2, sticker3, sticker4, stickerBanner;
    HotLabelListAdapter hotLabelListAdapter;
    JSONArray nextArray;
    RelativeLayout searchTopLayout,cuteMostLayout;

    private PullToRefreshListView hot_label_pulltoreflushlistview = null;

    int[] WH;
    HorizontalListView hListView;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        WH = CuteApplication.getScreenHW(getActivity());
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_discovery, container, false);

        hot_label_pulltoreflushlistview = (PullToRefreshListView) v.findViewById(R.id.hot_label_pulltoreflushlistview);
        hotLabelListView = hot_label_pulltoreflushlistview.getRefreshableView();

        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        hot_label_pulltoreflushlistview.setLastUpdatedLabel(label);
        /**
         * 屏蔽下拉加载操作,
         */
        hot_label_pulltoreflushlistview.setPullLoadEnabled(false);
        hot_label_pulltoreflushlistview.setPullRefreshEnabled(true);

        hot_label_pulltoreflushlistview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
               public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                   new GetDataTask().execute();
               }

               @Override
               public void onPullUpToRefresh (PullToRefreshBase < ListView > refreshView) {
               }
           }
        );

        View headView = inflater.inflate(R.layout.discovery_list_headerview, null);
        cuteMostLayout = (RelativeLayout)headView.findViewById(R.id.cute_most_layout);
        searchTopLayout = (RelativeLayout) v.findViewById(R.id.search_top);

        hotLabelListView.addHeaderView(headView);

        int searchPlateId = getActivity().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = v.findViewById(searchPlateId);
        searchPlate.setBackgroundColor(Color.WHITE);
        //  SearchView 里的 TextView
        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchEditText = (TextView) searchPlate.findViewById(searchSrcTextId);
        searchEditText.setTextSize(13);
        searchEditText.setGravity(Gravity.BOTTOM|Gravity.LEFT);

        hListView = (HorizontalListView) headView.findViewById(R.id.horizon_listview);
        hListView.getParent().requestDisallowInterceptTouchEvent(false);


        bigiv = (ImageView) headView.findViewById(R.id.big_imageView);
        rightIV1 = (ImageView) headView.findViewById(R.id.right_imageview1);
        rightIV2 = (ImageView) headView.findViewById(R.id.right_imageview2);
        rightIV3 = (ImageView) headView.findViewById(R.id.right_imageview3);
        rightIV4 = (ImageView) headView.findViewById(R.id.right_imageview4);

        stickerBanner = (ImageView) headView.findViewById(R.id.sticker_banner);
        sticker1 = (ImageView) headView.findViewById(R.id.sticker1);
        sticker2 = (ImageView) headView.findViewById(R.id.sticker2);
        sticker3 = (ImageView) headView.findViewById(R.id.sticker3);
        sticker4 = (ImageView) headView.findViewById(R.id.sticker4);

        mSearchView = (SearchView) v.findViewById(R.id.search);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.onActionViewExpanded();
        mSearchView.setFocusable(false);
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(getActivity(), SearchResultsActivity2.class);
                intent.putExtra("searchContent", mSearchView.getQuery().toString());
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        v.findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchContent = mSearchView.getQuery().toString();
                if(TextUtils.isEmpty(searchContent)) {
                    ToastUtil.show(getActivity(), "请输入搜索内容");
                    return;
                }
                Intent intent = new Intent(getActivity(), SearchResultsActivity2.class);
                intent.putExtra("searchContent", searchContent);
                startActivity(intent);
            }
        });


        /**
         *      共同话题的宝马
         */
        HttpUtil.get(API.GET_USER + "?key=recommend", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                try {
                    if(response.length() > 0)
                        hListView.setAdapter(new RecommendUserAdapter(response,getActivity()));
                    else
                        hListView.setLayoutParams(new LinearLayout.LayoutParams(0,0));

                } catch (Exception e) {

                }
                super.onSuccess(statusCode, headers, response);
            }
        });


        /**
         *      获得cute最多
         */
        HttpUtil.get(API.GET_CUTES + "?key=recommend_hot", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray jsonArray = new JSONArray(response.getString("results"));
                    JSONObject obj;
                    for (int i = 0; i < 5; i++) {
                        switch (i) {
                            case 0:
                                obj = (JSONObject) jsonArray.get(i);
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), bigiv);
                                final int id0 = obj.getInt("id");
                                bigiv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                                        intent.putExtra("id", id0);
                                        intent.putExtra("showAllComments", true);
                                        getActivity().startActivity(intent);
                                    }
                                });

//                                TextView bigText = (TextView) v.findViewById(R.id.big_text);
//                                bigText.setText(obj.getString("text"));
                                //  计算这个imageview 的宽高：宽慰屏幕的1/2 高为200dp（父容器高度）
//                                bigiv.setLayoutParams(new RelativeLayout.LayoutParams(WH[0] * (211/321),RelativeLayout.LayoutParams.MATCH_PARENT));
                                break;
                            case 1:
                                obj = (JSONObject) jsonArray.get(i);
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), rightIV1);
                                final int id1 = obj.getInt("id");
                                rightIV1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                                        intent.putExtra("id", id1);
                                        intent.putExtra("showAllComments", true);
                                        getActivity().startActivity(intent);
                                    }
                                });
//                                TextView text1 = (TextView) v.findViewById(R.id.right_text1);
//                                text1.setText(obj.getString("text"));
//                                rightIV1.setLayoutParams(new RelativeLayout.LayoutParams(new ViewGroup.LayoutParams(WH[0] * (110/321), BitmapUtil.dip2px(getActivity(),100))));
                                break;
                            case 2:
                                obj = (JSONObject) jsonArray.get(i);
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), rightIV2);
                                final int id2 = obj.getInt("id");
                                rightIV2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                                        intent.putExtra("id", id2);
                                        intent.putExtra("showAllComments", true);
                                        getActivity().startActivity(intent);
                                    }
                                });
//                                TextView text2 = (TextView) v.findViewById(R.id.right_text2);
//                                text2.setText(obj.getString("text"));
//                                rightIV2.setLayoutParams(new RelativeLayout.LayoutParams(new ViewGroup.LayoutParams(WH[0] * (110/321), BitmapUtil.dip2px(getActivity(),100))));
                                break;
                            case 3:
                                obj = (JSONObject) jsonArray.get(i);
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), rightIV3);
                                final int id3 = obj.getInt("id");
                                rightIV3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                                        intent.putExtra("id", id3);
                                        intent.putExtra("showAllComments", true);
                                        getActivity().startActivity(intent);
                                    }
                                });
                                break;
                            case 4:
                                obj = (JSONObject) jsonArray.get(i);
                                CuteApplication.downloadIamge(API.STICKERS + obj.getString("image_small"), rightIV4);
                                final int id4 = obj.getInt("id");
                                rightIV4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                                        intent.putExtra("id", id4);
                                        intent.putExtra("showAllComments", true);
                                        getActivity().startActivity(intent);
                                    }
                                });
                                break;

                            default:
                                break;
                        }
                    }
                    cuteMostLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CuteMostActivity.class);
                            startActivity(intent);

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        /**
         *      热门贴纸
         */
        headView.findViewById(R.id.sticker_hot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HotStickersActivity.class);
                startActivity(intent);
            }
        });

        try {
            CuteApplication.downloadIamge(API.STICKERS + CuteService.hotStickersList.get(0).getBanner(), stickerBanner);

            stickerBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), StickersActivity.class);
                    intent.putExtra("stickerID", CuteService.hotStickersList.get(0).getId());
                    startActivity(intent);
                }
            });

            CuteApplication.downloadIamge(API.STICKERS + CuteService.hotStickersList.get(1).getIcon(), sticker1);
            sticker1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), StickersActivity.class);
                    intent.putExtra("stickerID", CuteService.hotStickersList.get(1).getId());
                    startActivity(intent);
                }
            });

            CuteApplication.downloadIamge(API.STICKERS + CuteService.hotStickersList.get(2).getIcon(), sticker2);
            sticker2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), StickersActivity.class);
                    intent.putExtra("stickerID", CuteService.hotStickersList.get(2).getId());
                    startActivity(intent);
                }
            });

            CuteApplication.downloadIamge(API.STICKERS + CuteService.hotStickersList.get(3).getIcon(), sticker3);
            sticker3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), StickersActivity.class);
                    intent.putExtra("stickerID", CuteService.hotStickersList.get(3).getId());
                    startActivity(intent);
                }
            });

            CuteApplication.downloadIamge(API.STICKERS + CuteService.hotStickersList.get(4).getIcon(), sticker4);
            sticker4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), StickersActivity.class);
                    intent.putExtra("stickerID", CuteService.hotStickersList.get(4).getId());
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.show(getActivity(),"网络不给力");
        }
        /**
         *  热门标签ListView
         */
        HttpUtil.get(API.GET_LABELS + "?key=hot&certificate=true&format=json", new JsonHttpResponseHandler() {
            ProgressDialog dialog = new ProgressDialog(getActivity());


            @Override
            public void onStart() {
                dialog.setMessage("努力加载中...");
                dialog.show();
                super.onStart();
            }

            @Override
            public void onFinish() {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("results"));
                    hotLabelListAdapter = new HotLabelListAdapter(jsonArray, getActivity());
                    hotLabelListView.setAdapter(hotLabelListAdapter);
                    CuteApplication.hotLabelListNextPageUrl = response.getString("next");
                    /**
                     *  hotLabelListView 的滑动到底加载更多
                     */
                    hotLabelListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(final AbsListView view, int scrollState) {
                            // 当不滚动时
                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                                // 判断是否滚动到底部
                                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                                    //加载更多功能的代码
                                    addView = inflater.inflate(R.layout.loading, view, false);
                                    LogUtil.v("CuteApplication.hotLabelListNextPageUrl", CuteApplication.hotLabelListNextPageUrl.toString());
                                    if (CuteApplication.hotLabelListNextPageUrl.length() > 0) {
                                        HttpUtil.addClientHeader(getActivity());
                                        HttpUtil.get(CuteApplication.hotLabelListNextPageUrl, new JsonHttpResponseHandler() {

                                            @Override
                                            public void onStart() {
                                                if (hotLabelListView.getFooterViewsCount() < 1)
                                                    hotLabelListView.addFooterView(addView);
                                                super.onStart();
                                            }

                                            @Override
                                            public void onFinish() {
                                                hotLabelListView.removeFooterView(addView);
                                                super.onFinish();
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                try {
                                                    nextArray = new JSONArray(response.getString("results"));
                                                    hotLabelListAdapter.addKingdArray(nextArray);
                                                    hotLabelListAdapter.notifyDataSetChanged();

                                                    LogUtil.v("next", response.getString("next"));
                                                    if (response.getString("next").length() > 10) {
                                                        CuteApplication.hotLabelListNextPageUrl = response.getString("next");
                                                    } else {
                                                        LogUtil.v("next", "没有更多数据");
                                                        CuteApplication.hotLabelListNextPageUrl = "";
                                                        addView = inflater.inflate(R.layout.no_more, view, false);
                                                        hotLabelListView.addFooterView(addView);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                super.onSuccess(statusCode, headers, response);
                                            }
                                        });
                                    }


                                }
                                //  判断滑动到顶部
                                if(hotLabelListView.getFirstVisiblePosition() == 0){

//                                    searchTopLayout.startAnimation(set);
                                }
                            }
                        }

                        @Override
                        public void onScroll(AbsListView absListView, int i, int i2, int i3) {


                    }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });

        return v;
    }


    class RecommendedMumOnClick implements View.OnClickListener {
        int ID;

        public RecommendedMumOnClick(int id) {
            ID = id;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra("person_id", ID);
            startActivity(intent);
        }
    }


    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            hotLabelListAdapter.notifyDataSetChanged();
            hot_label_pulltoreflushlistview.onPullDownRefreshComplete();

            super.onPostExecute(result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().startService(new Intent(CuteService.ACTION));
    }
}