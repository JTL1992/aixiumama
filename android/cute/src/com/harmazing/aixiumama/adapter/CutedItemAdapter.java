package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.model.CutedItem;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/8.
 */
public class CutedItemAdapter extends BaseAdapter {

    Context context;
    ArrayList<CutedItem> cuteds;
    int Num ;


    public CutedItemAdapter(Context context,ArrayList<CutedItem> cutedItem){
        this.context = context;
        this.cuteds = cutedItem;
        for (int i = 0; i < cuteds.size(); i++){
            Log.v("传进的cutes",cuteds.get(i).getUserName()[0]+"#"+cuteds.get(i).getUserName()[1]);
        }
    }

    @Override
    public int getCount() {
        return cuteds.size();
    }

    @Override
    public Object getItem(int i) {
        return cuteds.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
      final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.layout_cut_and_comment, null);
            viewHolder.name1 = (TextView) view.findViewById(R.id.visit_layout);
            viewHolder.name2 = (TextView) view.findViewById(R.id.text_name);
            viewHolder.day = (TextView)view.findViewById(R.id.day);
            viewHolder.description1 = (TextView)view.findViewById(R.id.textContent);
            viewHolder.description2 = (TextView)view.findViewById(R.id.textcontent2);
            viewHolder.image1 = (ImageView)view.findViewById(R.id.image_cute);
            viewHolder.image2 = (ImageView)view.findViewById(R.id.image_cute2);
            viewHolder.icon1 = (ImageView) view.findViewById(R.id.image_person);
            viewHolder.icon2 = (ImageView) view.findViewById(R.id.image_person2);
            viewHolder.layout2 = (RelativeLayout) view.findViewById(R.id.layout).findViewById(R.id.layout2);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        Log.v("cutes",i+"@"+cuteds.get(i).getTitle()[0]+"@"+cuteds.get(i).getTitle()[1]);
        if (i > 0){
           if (cuteds.get(i).getTitle()[0].equals(cuteds.get(i-1).getTitle()[0]))
               viewHolder.day.setText("");
           else
               viewHolder.day.setText(cuteds.get(i).getTitle()[0]);
           }
        else {
            viewHolder.day.setText(cuteds.get(i).getTitle()[0]);
        }
           viewHolder.name1.setText(cuteds.get(i).getUserName()[0]);
           CuteApplication.downloadIamge(API.PIC_HEAD + cuteds.get(i).getImageUrl()[0], viewHolder.image1);
           if (cuteds.get(i).getDescription()[0].length() > 17)
               viewHolder.description1.setText(cuteds.get(i).getDescription()[0].substring(0,17)+"...");
           else
               viewHolder.description1.setText(cuteds.get(i).getDescription()[0]);

           CuteApplication.downloadLittleCornImage(API.PIC_HEAD+cuteds.get(i).getIconUrl()[0],viewHolder.icon1);
//           if (i == getCount()-1 && cuteds.get(i).getUserName()[1] == null)
//               layout2.setVisibility(View.INVISIBLE);
//           else{
        if(cuteds.get(i).getUserName()[1] != null)
           viewHolder.name2.setText(cuteds.get(i).getUserName()[1]);
        else
           viewHolder.name2.setText("");
        if (cuteds.get(i).getDescription()[1] != null && cuteds.get(i).getDescription()[1].length() > 17)
           viewHolder.description2.setText(cuteds.get(i).getDescription()[1].substring(0,17)+"...");
        else
           viewHolder.description2.setText(cuteds.get(i).getDescription()[1]);
        if (cuteds.get(i).getDescription()[1] == null){
            viewHolder.description2.setText("");
        }
        if (cuteds.get(i).getImageUrl()[1] != null){
//             viewHolder.layout2.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
//             viewHolder.image2.setImageResource(context.getResources().getColor(R.color.light_grey));
//             viewHolder.icon2.setImageResource(context.getResources().getColor(R.color.light_grey));
             CuteApplication.downloadIamge(API.PIC_HEAD+cuteds.get(i).getImageUrl()[1],viewHolder.image2);
    }
        else{
              viewHolder.image2.setImageResource(context.getResources().getColor(R.color.white));
//              viewHolder.image2.setBackgroundColor(context.getResources().getColor(R.color.white));
//              viewHolder.layout2.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        if (cuteds.get(i).getIconUrl()[1] != null){
            CuteApplication.downloadLittleCornImage(API.PIC_HEAD+cuteds.get(i).getIconUrl()[1],viewHolder.icon2);
//            viewHolder.layout2.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
        }
        else{
           viewHolder.icon2.setImageResource(context.getResources().getColor(R.color.white));
//           viewHolder.icon2.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
//      if (i == (getCount()-1) ){
//               if (cuteds.get(i).getUserName()[1] == null){
//                 viewHolder.layout2.setVisibility(View.INVISIBLE);
//                  Log.v("消失",i+cuteds.get(i).getUserName()[1]);
//               }
//               else
//                   viewHolder.layout2.setVisibility(View.VISIBLE);
//           }
//        if (i == (getCount()-1) && viewGroup.findViewById(R.id.list_cuted)!=null){
//            if (cuteds.get(i).getUserName()[1] == null){
//                viewHolder.layout2.setVisibility(View.INVISIBLE);
//                Log.v("消失",i+cuteds.get(i).getUserName()[1]);
//            }
//
//        }
//        else
//           viewHolder.layout2.setVisibility(View.VISIBLE);



//            viewHolder.layout2.setVisibility(View.INVISIBLE);
           Log.v("cuteds.get(i).getIconUrl()[1]",API.PIC_HEAD+cuteds.get(i).getIconUrl()[1]);

//           }
          viewHolder.image1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(context, PhotoDetailActivity.class);
                 intent.putExtra("id",cuteds.get(i).getCuteId()[0]);
                 context.startActivity(intent);
             }
         });
          viewHolder.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.name2.getText() != ""){
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("id",cuteds.get(i).getCuteId()[1]);
                context.startActivity(intent);}
            }
        });

        return view;
    }
    class ViewHolder{
        TextView name1,name2,day,description1,description2;
        ImageView image1,image2,icon1,icon2;
        RelativeLayout layout2;
    }


}
