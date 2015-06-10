package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.harmazing.aixiumama.utils.TextUtil;
import com.rockerhieu.emojicon.EmojiconTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/7.
 */
public class CommentsListAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;
    int screenWidth;
    int cuteID;
    String pulisherName;

    public CommentsListAdapter(JSONArray kindArray, Context ctx,int id, String pulisherName ) {
        this.kindArray = kindArray;
        this.ctx = ctx;
        this.cuteID = id;
        this.pulisherName = pulisherName;
        // 屏幕宽度
        screenWidth = CuteApplication.getScreenHW(ctx)[0];
    }

    @Override
    public int getCount() {

        return kindArray.length();
    }

    @Override
    public Object getItem(int position) {

        return kindArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {

        return -1;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ctx).inflate(R.layout.comments_list_item, parent, false);
//                holder.comentsConTV = (TextView) convertView.findViewById(R.id.coments_content);
//                holder.publishUserBtn = (TextView) convertView.findViewById(R.id.publish_user);
//                holder.replyToUserBtn = (TextView) convertView.findViewById(R.id.reply_to_user);
                holder.content = (EmojiconTextView)convertView.findViewById(R.id.content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try {
                holder.publishUserID = ((JSONObject) getItem(position)).getInt("user");
                holder.relyUserID = ((JSONObject) getItem(position)).getInt("reply_to_user");

                final String commnet_person_id = new JSONObject(((JSONObject) getItem(position)).getString("user_detail")).getString("id");

                int len1 = new JSONObject(((JSONObject) getItem(position)).getString("user_detail")).getString("name").length();
                int len2 = new JSONObject(((JSONObject) getItem(position)).getString("reply_to_user_detail")).getString("name").length();

                final String user_detail_name = new JSONObject(((JSONObject) getItem(position)).getString("user_detail")).getString("name");
                String reply_to_user_detail_name = new JSONObject(((JSONObject) getItem(position)).getString("reply_to_user_detail")).getString("name");

                String type = ((JSONObject) getItem(position)).getString("type");

                String commentsHtml = null;


                if(type.equals("1")) {
                    commentsHtml=  user_detail_name +": " +((JSONObject) getItem(position)).getString("text");
                } else if(type.equals("2")) {
                    commentsHtml =  user_detail_name +" 回复 " + reply_to_user_detail_name+ ": " +
                            ((JSONObject) getItem(position)).getString("text");
                }

                holder.style = new SpannableStringBuilder(TextUtil.ToDBC(commentsHtml));

                try {

                    //  发布用户
                    holder.style .setSpan(new ClickableSpan() {
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(ctx.getResources().getColor(R.color.pink));
                            ds.setUnderlineText(false);
                        }

                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ctx, PersonActivity.class);
                            intent.putExtra("person_id", holder.publishUserID);
                            ctx.startActivity(intent);
                        }
                    }, 0, len1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if(type.equals("1")) {
                        //  回复内容
                        holder.style.setSpan(new ClickableSpan() {
                            @Override
                            public void updateDrawState(TextPaint ds) {

                            }

                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                                intent.putExtra("id",cuteID);
                                intent.putExtra("is_from_home", true);
                                intent.putExtra("commnet_person", user_detail_name);
                                intent.putExtra("commnet_person_id", commnet_person_id);
                                ctx.startActivity(intent);
                            }
                        },  len1, commentsHtml.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    } else if(type.equals("2")) {
                        //  被回复的人
                        holder.style.setSpan(new ClickableSpan() {
                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(ctx.getResources().getColor(R.color.pink));
                                ds.setUnderlineText(false);
                            }

                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ctx, PersonActivity.class);
                                intent.putExtra("person_id", holder.relyUserID);
                                ctx.startActivity(intent);
                            }
                        }, len1 + 4, len1 + 4 + len2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        // 回复 这两个字
                        holder.style.setSpan(new ClickableSpan() {
                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(ctx.getResources().getColor(R.color.font_grey));
                                ds.setUnderlineText(false);
                            }

                            @Override
                            public void onClick(View view) {

                            }
                        }, len1, len1 + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //  回复内容
                        holder.style.setSpan(new ClickableSpan() {
                            @Override
                            public void updateDrawState(TextPaint ds) {

                            }

                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ctx, PhotoDetailActivity.class);
                                intent.putExtra("id",cuteID);
                                intent.putExtra("is_from_home", true);
                                intent.putExtra("commnet_person", user_detail_name);
                                intent.putExtra("commnet_person_id", commnet_person_id);
                                ctx.startActivity(intent);
                            }
                        },  len1 + 4 + len2,commentsHtml.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    holder.content.setText(holder.style );

                    holder.content.setMovementMethod(LinkMovementMethod.getInstance());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        return convertView;
    }

    private class ViewHolder {
        EmojiconTextView content;
        Spannable  style;
        int publishUserID,relyUserID;
    }
}