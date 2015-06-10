package com.harmazing.aixiumama.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.model.Group;
import com.harmazing.aixiumama.R;

import java.util.List;

/**
 * Created by jtl on 2014/11/27.
 * 下拉listview界面
 */
public class FriendViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Group> mGroups;
    private Activity activity;
    private boolean isShowGroupItem = false;

    public FriendViewAdapter(Context mContext,List<Group> mGroups, Activity activity)
    {
        this.mContext=mContext;
        this.mGroups=mGroups;
        this.activity = activity;
    }

    @Override
    public int getCount()
    {
        return mGroups.size();
    }

    @Override
    public Object getItem(int Index)
    {
        return mGroups.get(Index);
    }

    @Override
    public long getItemId(int Index)
    {
        return Index;
    }

    @Override
    public View getView(final int Index, View mView, ViewGroup mParent)
    {
      if (mView == null)
      mView= LayoutInflater.from(mContext).inflate(R.layout.layout_group, null);
        //设置分组的名称
        ((TextView)mView.findViewById(R.id.group_name)).setText(mGroups.get(Index).getGroupName());
        //设置分组容量
//        String mItemsCount=String.valueOf(mGroups.get(Index).getGroupItems().size());
//        ((TextView)mView.findViewById(R.id.Group_ItemCount)).setText(mItemsCount);
        //设置分组下的列表
        final ListView ItemsList = (ListView) mView.findViewById(R.id.list);

        GroupItemAdapter mAdapter = new GroupItemAdapter(mContext, mGroups.get(Index).getGroupItems(),mGroups.get(Index).getType(),activity);
        ItemsList.setAdapter(mAdapter);
//        ItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(mContext, "Item " + i + " Click！", Toast.LENGTH_LONG).show();
//            }
//        });
        //设置分组小的列表高度
        setGroupHeight(ItemsList);
        //给分组添加Click事件
        final RelativeLayout GroupLayout=(RelativeLayout)mView.findViewById(R.id.group_line);
//        GroupLayout.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if(!isShowGroupItem)
//                {
//                    ItemsList.setVisibility(View.VISIBLE);
//                    GroupLayout.findViewById(R.id.image_jiantou).setBackgroundResource(R.drawable.icon_up);
//                    isShowGroupItem=true;
//                }
//                else
//                {
//                    ItemsList.setVisibility(View.GONE);
//                    GroupLayout.findViewById(R.id.image_jiantou).setBackgroundResource(R.drawable.icon_down);
//                    isShowGroupItem=false;
//                }
//            }
//        });
        return mView;
    }
    private void setGroupHeight(ListView mListView)	{
        int mTotalHeight=0;
        ListAdapter mAdapter = mListView.getAdapter();
      if (!mAdapter.isEmpty())
        for(int i=0;i<mAdapter.getCount();i++){
            View ItemView = mAdapter.getView(i, null, mListView);
            ItemView.measure(0, 0);
            mTotalHeight += ItemView.getMeasuredHeight();

        }
        ViewGroup.LayoutParams mParams = mListView.getLayoutParams();
        mParams.height=mTotalHeight;
        mListView.setMinimumHeight(mTotalHeight);
    }
}
