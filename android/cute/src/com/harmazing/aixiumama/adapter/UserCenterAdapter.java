package com.harmazing.aixiumama.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.base.BaseMyAdapter;
import com.harmazing.aixiumama.model.Cute;
import com.harmazing.aixiumama.model.Label;
import com.harmazing.aixiumama.utils.CustomTransformUtils;
import com.harmazing.aixiumama.view.CutePersonThumbNail;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.model.LeftUser;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.view.LabelImageView;
import com.lidroid.xutils.BitmapUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by guoyongwei on 2014/12/19.
 *
 * 该类是用于显示个人中心三条杠的中间的adapter的
 */
public class UserCenterAdapter extends BaseMyAdapter<LeftUser.LeftUserResults> {

    private BitmapUtils bitmapUtils;

    public UserCenterAdapter(Context context, List<LeftUser.LeftUserResults> list) {
        super(context, list);
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder ;
        if (view == null ) {
            view = View.inflate(context, R.layout.attention2_home_listview_item,null);
            holder = new ViewHolder();
            //icon
            holder.user_icon = (ImageView) view.findViewById(R.id.user_icon);
            holder.pulisher = (TextView) view.findViewById(R.id.pulisher);
            holder.baby_info = (TextView) view.findViewById(R.id.baby_info);

            //图片描述
            holder.pic_text = (TextView) view.findViewById(R.id.pic_text);

            //labelImageView
            holder.label_image = (LabelImageView) view.findViewById(R.id.label_image);
            holder.cute = new Cute(context);

            //是否被点赞了  和 点赞过的数量
            holder.liked = (TextView) view.findViewById(R.id.liked);
            holder.liked_icon = (ImageView) view.findViewById(R.id.liked_icon);

            //评论是数量
            holder.coments = (TextView) view.findViewById(R.id.coments);

            //点过赞的图片
            holder.cute_head = (CutePersonThumbNail) view.findViewById(R.id.cute_head);

            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        if(list.size() > 0) {
            //publish_user的id
            holder.publishUserID = list.get(i).publish_user.auth_user;
            holder.cuteId = list.get(i).id;
            //icon
            CuteApplication.downloadCornImage(API.STICKERS + list.get(i).publish_user.image, holder.user_icon);
            //名字
            holder.pulisher.setText(list.get(i).publish_user.name);
            holder.baby_info.setText(CustomTransformUtils.data2BabyInfo(list.get(i).publish_user.babies.get(0)));

            //Cute图片
            holder.label_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CuteApplication.getScreenHW(context)[0]));


            /**
             * 为cute对象加标签
             */
            List<LeftUser.Labels> labelsList = list.get(i).labels;

            holder.cute.clear();
            holder.label_image.clear();
            double x, y;
            for (int index = 0; index < labelsList.size(); index++) {
                x = Double.valueOf(labelsList.get(index).x);
                y = Double.valueOf(labelsList.get(index).y);
                LogUtil.v("原始 xy", labelsList.get(index).x + "    " + labelsList.get(index).y);
                if (y > 1)
                    y = 0.95d;
                if (y < 0)
                    y = 0.5d;

                if (x > 1)
                    x = 0.95d;
                if (x < 0)
                    x = 0.5d;

                Label label = new Label(context);
                label.setName(labelsList.get(index).name);
                label.setDirection(Integer.parseInt(labelsList.get(index).direction));
                label.setX(x);
                label.setY(y);

//                LogUtil.v("double xy", x + "     " + y);
//                LogUtil.v("label xy", label.getX() + "    " + label.getY());

                label.setId(Integer.parseInt(labelsList.get(index).id));

                holder.cute.addLabel(label);
            }
            //添加标签
            holder.label_image.setCute(holder.cute);
            //显示图片
            bitmapUtils.display(holder.label_image, API.STICKERS + list.get(i).image);

            holder.label_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**
                     *      所有标签点击闪烁、再点消失
                     */
                    holder.label_image.setLabelVisbility();
//                    if (mSlidingDrawer.isOpened()){
//                        mSlidingDrawer.animateClose();
//                    }
                }
            });

            //图片的描述
            if(!"".equals(list.get(i).text)) {
                holder.pic_text.setText(list.get(i).text);
            } else {
                holder.pic_text.setVisibility(View.GONE);
            }

            //点赞
            if (list.get(i).is_liked) {
                holder.liked_icon.setImageResource(R.drawable.pl_ico1_red);
            } else {
                holder.liked_icon.setImageResource(R.drawable.pl_ico1);
            }
            //点赞的数量
            holder.liked.setText(list.get(i).liked_count);

            //评论的数量
            holder.coments.setText(list.get(i).comments_count);

            //点过赞的图像
            List<LeftUser.LastLikes> lastLikesList = list.get(i).last_likes;
            if (lastLikesList.size() > 0) {
                holder.cute_head.clear();
                LinkedList<String> imageUrllist = new LinkedList<String>();
                LinkedList<Integer> userIDList = new LinkedList<Integer>();
                for (int index = 0; index < lastLikesList.size(); index++) {
                    if (i < 9) {
                        imageUrllist.add(lastLikesList.get(index).user_detail.image_small);
                        userIDList.add(Integer.parseInt(lastLikesList.get(index).user_detail.auth_user));
                    } else
                        break;
                    }
                holder.cute_head.setThumbnail(imageUrllist, lastLikesList.size(), Integer.parseInt(holder.cuteId));
                holder.cute_head.setOnClick(userIDList);
            } else
                holder.cute_head.clear();
            }

        /*holder.user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra("person_id",Integer.parseInt(holder.publishUserID));
                context.startActivity(intent);
            }
        });*/

        return view;
    }


    static class ViewHolder {
        ImageView user_icon;
        TextView pulisher;
        TextView baby_info;
        ImageView follow_state;
        LabelImageView label_image;
        TextView pic_text;
        ImageView liked_icon;
        TextView liked;
        TextView coments;
        Button pl_dd_btn;
        CutePersonThumbNail cute_head;
        ListView comments_listview;
        String publishUserID,cuteId,commentsNum;
        Cute cute;
    }
}

