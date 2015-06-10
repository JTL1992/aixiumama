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
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.activity.TabHostActivity;
import com.harmazing.aixiumama.application.CuteApplication;

import com.harmazing.aixiumama.fragment.MessageFragment;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.view.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Lyn on 2014/11/6.
 */
public class MsgListViewAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;
    private String imageUrl;
    private int newCount;

    public MsgListViewAdapter(JSONArray kindArray
            ,Context ctx)
    {
        this.kindArray = kindArray;
        this.ctx = ctx;
        newCount = MessageFragment.newMessageNum;
    }
    public Boolean isRefresh(String date){
        try {
            Log.v("isrefresh",date);
           return (kindArray.getJSONObject(0).getString("create_date").equals(date));
        }catch (Exception e){
            Log.v("isrefresh","falure");
            return false;
        }

    }
    public void refresh(JSONArray jsonArray){
        this.kindArray = jsonArray;
    }
    public void addKingdArray(JSONArray array){
        int len = kindArray.length();
        for(int i=0;i<array.length();i++)
            try {
                kindArray.put( len + i,array.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
    @Override
    public int getCount()
    {

        return kindArray.length();
    }

    @Override
    public Object getItem(int position)
    {

        return kindArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position)
    {
        return -1;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent)
    {
        final ViewHolder holder ;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater flater = LayoutInflater.from(ctx);
            convertView = flater.inflate(R.layout.msg_listview_item, parent,false);
            holder.receiverIcon = (RoundedImageView)convertView.findViewById(R.id.receiver_icon);
            holder.dateBefore = (TextView)convertView.findViewById(R.id.date_before);
            holder.mumName = (TextView)convertView.findViewById(R.id.mum_name);
            holder.messageTxt = (TextView)convertView.findViewById(R.id.message);
            holder.cuteImage = (ImageView)convertView.findViewById(R.id.cute_image);
            holder.typeTxt = (TextView)convertView.findViewById(R.id.typeTxt);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
            holder.redPoint = (ImageView) convertView.findViewById(R.id.redpoint);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject obj= null;
        JSONObject user_detailObj = null;
        JSONObject replyToUser = null;
        JSONObject cuteJsonObject = null;
        try {
            if (!((JSONObject)getItem(position)).getString("type").equals("Like")) {
//                if (newCount > 0){
//                    newCount--;
//                    holder.redPoint.setVisibility(View.VISIBLE);
//                }
                // 计算时间
                Log.v("position", "" + position);
                holder.time = ((JSONObject) getItem(position)).getString("create_date");
                if (!CuteApplication.getTitle(CuteApplication.timeStr2timestamp(holder.time)).equals(""))
                holder.dateBefore.setText(CuteApplication.getTitle(CuteApplication.timeStr2timestamp(holder.time)));
                else
                holder.dateBefore.setText(holder.time);
                //  获得 type
                holder.type = ((JSONObject) getItem(position)).getString("type");
                if (holder.type.equals("Comment")) {
                    redpointShow(holder);
                    holder.typeTxt.setText("评论");
                    obj = new JSONObject(((JSONObject) getItem(position)).getString("content"));
                    //  解析content
                    final int replyUserId = obj.getInt("user");
                    user_detailObj = new JSONObject(obj.getString("user_detail"));
                    replyToUser = obj.getJSONObject("reply_to_user_detail");
                    cuteJsonObject = obj.getJSONObject("cute");
                    final String replyToUserName = user_detailObj.getString("name");
                    holder.mumName.setText(user_detailObj.getString("name"));
                    holder.messageTxt.setText(obj.getString("text"));
                    Log.v("urlpic", API.STICKERS + user_detailObj.getString("image"));
                    CuteApplication.downloadIamge(API.STICKERS + user_detailObj.getString("image"), holder.receiverIcon);
                    final int userId = obj.getInt("user");
                    final int cuteId = cuteJsonObject.getInt("id");
                    holder.receiverIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in = new Intent(ctx, PersonActivity.class);
                            in.putExtra("person_id", userId);
                            ctx.startActivity(in);
                        }
                    });
                    CuteApplication.downloadIamge(API.STICKERS + cuteJsonObject.getString("image_small"), holder.cuteImage);
                    holder.cuteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                            intent.putExtra("id", cuteId);
                            intent.putExtra("is_from_home",true);
                            intent.putExtra("commnet_person",replyToUserName);
                            intent.putExtra("commnet_person_id",String.valueOf(replyUserId));
                            ctx.startActivity(intent);
                        }
                    });
                    holder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                            intent.putExtra("id", cuteId);
                            intent.putExtra("is_from_home",true);
                            intent.putExtra("commnet_person",replyToUserName);
                            intent.putExtra("commnet_person_id",String.valueOf(replyUserId));
                            holder.redPoint.setVisibility(View.INVISIBLE);
                            MessageFragment.newMessageNum -= 1;
                            TabHostActivity.messageHaveRead++;
                            Intent broadcastIntent = new Intent("com.cute.broadcast");
                            broadcastIntent.putExtra("action",5);//messagehaveread
                            ctx.sendBroadcast(broadcastIntent);
                            ctx.startActivity(intent);
                        }
                    });
                }
                else if (holder.type.equals("System")) {
                    redpointShow(holder);
                    holder.typeTxt.setText("系统通知");
                    holder.mumName.setText("爱秀妈妈");
                    holder.receiverIcon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_launcher));
                    holder.messageTxt.setText(((JSONObject) getItem(position)).getString("content"));
                    holder.cuteImage.setVisibility(View.GONE);

                }
                else if (holder.type.equals("Reply")) {
                    redpointShow(holder);
                    holder.typeTxt.setText("回复");
                    obj = new JSONObject(((JSONObject) getItem(position)).getString("content"));
                    //  解析content
                    final int replyUserId = obj.getInt("user");
                    user_detailObj = new JSONObject(obj.getString("user_detail"));
                    cuteJsonObject = obj.getJSONObject("cute");
                    replyToUser = obj.getJSONObject("reply_to_user_detail");
                    final String replyToUserName = user_detailObj.getString("name");
                    holder.mumName.setText(user_detailObj.getString("name"));
                    holder.messageTxt.setText(obj.getString("text"));
                    Log.v("urlpic", API.STICKERS + user_detailObj.getString("image"));
                    CuteApplication.downloadIamge(API.STICKERS + user_detailObj.getString("image"), holder.receiverIcon);
                    final int userId = obj.getInt("user");
                    final int cuteId = cuteJsonObject.getInt("id");
                    holder.receiverIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in = new Intent(ctx, PersonActivity.class);
                            in.putExtra("person_id", userId);
                            ctx.startActivity(in);
                        }
                    });

                    CuteApplication.downloadIamge(API.STICKERS + cuteJsonObject.getString("image_small"), holder.cuteImage);
                    holder.cuteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                            intent.putExtra("id", cuteId);
                            intent.putExtra("is_from_home",true);
                            intent.putExtra("commnet_person",replyToUserName);
                            intent.putExtra("commnet_person_id",String.valueOf(replyUserId));
                            ctx.startActivity(intent);
                        }
                    });
                    holder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                            intent.putExtra("id", cuteId);
                            intent.putExtra("is_from_home",true);
                            intent.putExtra("commnet_person",replyToUserName);
                            intent.putExtra("commnet_person_id",String.valueOf(replyUserId));
                            ctx.startActivity(intent);
                        }
                    });
                }
//            else if(holder.type.equals("Like")){
//                holder.typeTxt.setText("cute了");
//                obj = new JSONObject(((JSONObject) getItem(position)).getString("content"));
//                //  解析content
//                user_detailObj = new JSONObject(obj.getString("user_detail"));
//
//                holder.mumName.setText(user_detailObj.getString("name"));
////                holder.messageTxt.setText("cute了");
//                CuteApplication.downloadIamge(API.STICKERS + user_detailObj.getString("image"),holder.receiverIcon);
//                final int userId = obj.getInt("user");
//                holder.receiverIcon.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent in  = new Intent(ctx, PersonActivity.class);
//                        in.putExtra("person_id",userId);
//                        ctx.startActivity(in);
//                    }
//                });
//                /**
//                 *  加载cute图片，获得id
//                 */
//                final String cuteID = obj.getString("cute");
//                user_detailObj = new JSONObject(obj.getString("cute_detail"));
//                CuteApplication.downloadIamge(API.STICKERS + user_detailObj.getString("image"),holder.cuteImage);
//                holder.layout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(ctx, PhotoDetailActivity.class);
//                        intent.putExtra("id",Integer.parseInt(cuteID));
//                        ctx.startActivity(intent);
//                    }
//                });
//            }
                if (holder.type.equals("Follow")) {
                    redpointShow(holder);
                    holder.typeTxt.setText("加你为粉丝");
                    obj = new JSONObject(((JSONObject) getItem(position)).getString("content"));
                    //  解析content
                    holder.messageTxt.setText("");
                    user_detailObj = new JSONObject(obj.getString("user_detail"));
                    holder.mumName.setText(user_detailObj.getString("name"));
                    Log.v("urlpic", API.STICKERS + user_detailObj.getString("image"));
                    CuteApplication.downloadIamge(API.STICKERS + user_detailObj.getString("image"), holder.receiverIcon);
                    final int userId = obj.getInt("user");
                    holder.receiverIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in = new Intent(ctx, PersonActivity.class);
                            in.putExtra("person_id", userId);
                            ctx.startActivity(in);
                        }
                    });
                    CuteApplication.downloadIamge(API.STICKERS + AppSharedPref.newInstance(ctx).getPicDir(), holder.cuteImage);
                    holder.cuteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ctx, PersonActivity.class);
                            intent.putExtra("person_id", AppSharedPref.newInstance(ctx).getUserId());
                            ctx.startActivity(intent);
                        }
                    });

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




        return convertView;
    }

    private class ViewHolder {
        ImageView cuteImage,redPoint;
        String type,time;
        TextView dateBefore,mumName,messageTxt,typeTxt;
        RelativeLayout layout;
        RoundedImageView receiverIcon;
    }
    private void redpointShow(ViewHolder viewHolder){
        if (newCount > 0){
            newCount--;
            viewHolder.redPoint.setVisibility(View.VISIBLE);
        }
    }

}
