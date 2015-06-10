package com.harmazing.aixiumama.model;


import com.harmazing.aixiumama.utils.LogUtil;

import java.util.List;

public class LeftUser {
    public String count;
    public String next;
    public List<LeftUserResults> results = null;
    public void addResultsList(List<LeftUserResults> result){
        int len = 0;
        if (results != null)
            len = results.size();
        if (results != null)
           for (int i = 0;i < result.size(); i++){
            results.add(len+i,result.get(i));
        }
        else
          try{
            results = result;
        }catch (Exception e){
            LogUtil.v("clone", "");
        }
    }
    public class LeftUserResults {
        public String age;
        public String comments_count;
        public String create_date;
        public String id;
        public String image;
        public String image_small;
        public int count;
        public boolean is_liked;
        public boolean is_private;

        public List<Labels> labels;
        public List<LastComments> last_comments;
        public List<LastLikes> last_likes;

        public String liked_count;
        public PublishUser publish_user;

        public boolean recommend;
        public String text;
        public String user;
        @Override
        public String toString() {
            return "Result [age=" + age + ", comments_count=" + comments_count + ", create_date=" + create_date + ", id=" + id + ", image=" + image + ", image_small=" + image_small + ", is_liked=" + is_liked + ", is_private=" + is_private + ", labels=" + labels + ", last_comments=" + last_comments + ", last_likes=" + last_likes + ", liked_count=" + liked_count + ", publish_user=" + publish_user + ", recommend=" + recommend + ", text=" + text + ", user=" + user + "]";
        }




    }

    public class Labels {
        public String direction;
        public String id;
        public String name;
        public double x;
        public double y;


        @Override
        public String toString() {
            return "Labels [direction=" + direction + ", id=" + id + ", name=" + name + ", x=" + x + ", y=" + y + "]";
        }

    }

    public class LastComments {
        public String create_date;
        public String cute;
        public String reply_to_user;
        public ReplyToUserDetail Reply_to_user_detail;

        public String text;
        public String type;
        public String user;
        public UserDetail user_detail;
        @Override
        public String toString() {
            return "LastComments [create_date=" + create_date + ", cute=" + cute + ", reply_to_user=" + reply_to_user + ", Reply_to_user_detail=" + Reply_to_user_detail + ", text=" + text + ", type=" + type + ", user=" + user + ", user_detail=" + user_detail + "]";
        }
    }

    public class LastLikes {
        public String create_date;
        public String cute;
        public String user;
        public UserDetail user_detail;
        @Override
        public String toString() {
            return "LastLikes [create_date=" + create_date + ", cute=" + cute + ", user=" + user + ", user_detail=" + user_detail + "]";
        }

    }

    public class ReplyToUserDetail {
        public String id;
        public String image;
        public String name;
        @Override
        public String toString() {
            return "ReplyToUserDetail [id=" + id + ", image=" + image + ", name=" + name + "]";
        }



    }

    public class UserDetail {

        public String auth_user;

        public List<Babies> babies;

        public String id;
        public String image;
        public String name;

        public String image_small;

        @Override
        public String toString() {
            return "UserDetail [auth_user=" + auth_user + ", babies=" + babies + ", id=" + id + ", image=" + image + ", name=" + name + ", image_small=" + image_small + "]";
        }

    }
    public class PublishUser {
        public String auth_user;
        public List<Babies> babies;
        public String image;
        public String image_small;
        public String name;


        @Override
        public String toString() {
            return "PublishUser [auth_user=" + auth_user + ", babies=" + babies + ", image=" + image + ", image_small=" + image_small + ", name=" + name + "]";
        }
    }
    public class Babies {
        public String birthday;
        public String gender;


        @Override
        public String toString() {
            return "Babies [birthday=" + birthday + ", gender=" + gender + "]";
        }
    }

    @Override
    public String toString() {
        return "LeftUser [count=" + count + ", next=" + next + ", results=" + results + "]";
    }

}







