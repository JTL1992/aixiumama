package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.model.CuteRank;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.view.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/1/5.
 */
public class CuteRankAdapter extends BaseAdapter {
    ArrayList<CuteRank> cuteRanks;
    Context context;
    public CuteRankAdapter (Context context,ArrayList<CuteRank> cuteRanks){
        this.context = context;
        this.cuteRanks = cuteRanks;
    }
    @Override
    public int getCount() {
        return cuteRanks.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
     final ViewHolder viewHolder ;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.layout_cute_rank,null);
            viewHolder.age = (TextView) view.findViewById(R.id.age);
            viewHolder.gender = (TextView) view.findViewById(R.id.sex);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.head = (RoundedImageView) view.findViewById(R.id.image_head);
            viewHolder.rank_num = (Button) view.findViewById(R.id.rank_num);
            viewHolder.rank_num2 = (Button) view.findViewById(R.id.rank_num2);
            viewHolder.layout = (RelativeLayout) view.findViewById(R.id.layout);
//            CuteApplication.downloadIamge(API.PIC_HEAD + , viewHolder.head);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.name.setText(cuteRanks.get(i).getName());
        viewHolder.gender.setText(cuteRanks.get(i).getGender());
        viewHolder.age.setText(cuteRanks.get(i).getAge());
        viewHolder.rank_num.setText((i+1)+"");
        if (i == 0){
            viewHolder.rank_num.setBackgroundResource(R.drawable.button_circle_rank1);
        }

        else if (i == 1){
            viewHolder.rank_num.setBackgroundResource(R.drawable.button_circle_rank2);
        }
        else if (i == 2){
            viewHolder.rank_num.setBackgroundResource(R.drawable.button_circle_rank3);
        }
        else{
//            viewHolder.rank_num.setBackgroundResource(R.drawable.button_circle_rank);
            viewHolder.rank_num.setVisibility(View.INVISIBLE);
            viewHolder.rank_num2.setVisibility(View.VISIBLE);
            viewHolder.rank_num2.setText((i+1)+"");
        }
        CuteApplication.downloadIamge(API.PIC_HEAD + cuteRanks.get(i).getIcon(), viewHolder.head);
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra("person_id",cuteRanks.get(i).getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return view;
    }
    private class ViewHolder{

        Button rank_num,rank_num2;
        RoundedImageView head;
        TextView name, age, gender;
        RelativeLayout layout;

    }
}
