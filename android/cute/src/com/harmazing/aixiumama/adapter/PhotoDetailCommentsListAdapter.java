package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.harmazing.aixiumama.activity.PersonActivity;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.TextUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.PhotoDetailActivity;
import com.rockerhieu.emojicon.EmojiconTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lyn on 2014/11/7.
 */
public class PhotoDetailCommentsListAdapter extends BaseAdapter {
    private JSONArray kindArray;
    private Context ctx;
    ListView commentsList;
    public PhotoDetailCommentsListAdapter(JSONArray kindArray
            , Context ctx,ListView lv) {
        this.kindArray = kindArray;
        this.ctx = ctx;
        this.commentsList = lv;
    }
    public void addCommentList(JSONArray jsonArray){
        int len = kindArray.length();
        for(int i=0;i<jsonArray.length();i++)
            try {
                kindArray.put( len + i,jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        Log.v("kindArray.length()",""+kindArray.length());
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
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final ViewHolder holder ;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.comments_list_item, parent,false);
            holder.contentTV = (EmojiconTextView)convertView.findViewById(R.id.content);

            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        try
        {
            holder.holderName = new JSONObject(((JSONObject) getItem(position)).getString("user_detail")).getString("name");
            holder.id  = new JSONObject(((JSONObject) getItem(position)).getString("user_detail")).getString("id");
            holder.publishUserID = ((JSONObject) getItem(position)).getInt("user");
            holder.relyUserID = ((JSONObject) getItem(position)).getInt("reply_to_user");


            int len1 = new JSONObject(((JSONObject) getItem(position)).getString("user_detail")).getString("name").length();
            int len2 = new JSONObject(((JSONObject) getItem(position)).getString("reply_to_user_detail")).getString("name").length();


            String user_detail_name = new JSONObject(((JSONObject) getItem(position)).getString("user_detail")).getString("name");
            String reply_to_user_detail_name = new JSONObject(((JSONObject) getItem(position)).getString("reply_to_user_detail")).getString("name");

            String type = ((JSONObject) getItem(position)).getString("type");

            String commentsHtml = null;

            if(type.equals("1")) {
                commentsHtml = user_detail_name +": " +((JSONObject) getItem(position)).getString("text");
            } else if(type.equals("2")) {
                commentsHtml =  user_detail_name +" 回复 " + reply_to_user_detail_name+ ": " +
                        ((JSONObject) getItem(position)).getString("text");
            }

            final  String temp = holder.holderName;
            CuteApplication.commentToUser = temp;
            final String id = holder.id;


            holder.style = new SpannableStringBuilder(TextUtil.ToDBC(commentsHtml));

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
                        PhotoDetailActivity.rely.setHint("回复 " + temp);
                        PhotoDetailActivity.relyToPerson = id;
                        InputMethodManager imm = (InputMethodManager)PhotoDetailActivity.rely.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(PhotoDetailActivity.rely, InputMethodManager.SHOW_FORCED);
                    }
                },  len1, commentsHtml.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else if(type.equals("2")){
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

                //  回复内容
                holder.style.setSpan(new ClickableSpan() {
                    @Override
                    public void updateDrawState(TextPaint ds) {

                    }

                    @Override
                    public void onClick(View view) {
                        PhotoDetailActivity.rely.setHint("回复 " + temp);
                        PhotoDetailActivity.relyToPerson = id;
                        InputMethodManager imm = (InputMethodManager)PhotoDetailActivity.rely.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(PhotoDetailActivity.rely, InputMethodManager.SHOW_FORCED);
                    }
                },  len1 + 4 + len2,commentsHtml.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            holder.contentTV.setText(holder.style);
            holder.contentTV.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return convertView;
    }

    private class ViewHolder {

        int publishUserID,relyUserID;
        EmojiconTextView contentTV;
        String holderName,id;
        Spannable style;
    }
}