package com.harmazing.aixiumama.model;

import java.util.List;

public class User {
    //int
    public String auth_user;
    public Detail auth_user_detail;

    public class Detail {
        public String date_joined;
        public String email;
        public String first_name;
        //int
        public String id;
        public String last_login;
        public String last_name;
        public String username;
    }
	
	
	public List<Babies> babies;
	
	public class Babies {
		public String birthday;
        //
		public String create_date;
        //
		public String gender;
		public String parent;
	}
	
	
	public boolean block;
    //
	public String city;
	public String description;
    //
	public String fans_count;
    //
	public String favorite_cute_count;
    //
	public String follows_count;
    //
	public String gender;
	public String image;
	public String image_small;
    //
	public String liked_count;
	public String location;
	public String name;
	public String qq_openid;
	public String qq_openkey;
    //int
	public String rank;
	public String register_date;
    //int
	public String visited_count;
	public String wechat_id;
	public String wechat_token;
	public String weibo;
	public String weibo_token;

    @Override
    public String toString() {
        return "User >>>>  [auth_user=" + auth_user + ", auth_user_detail=" + auth_user_detail + ", babies=" + babies + ", block=" + block + ", city=" + city + ", description=" + description + ", fans_count=" + fans_count + ", favorite_cute_count=" + favorite_cute_count + ", follows_count=" + follows_count + ", gender=" + gender + ", image=" + image + ", image_small=" + image_small + ", liked_count=" + liked_count + ", location=" + location + ", name=" + name + ", qq_openid=" + qq_openid + ", qq_openkey=" + qq_openkey + ", rank=" + rank + ", register_date=" + register_date + ", visited_count=" + visited_count + ", wechat_id=" + wechat_id + ", wechat_token=" + wechat_token + ", weibo=" + weibo + ", weibo_token=" + weibo_token + "]";
    }

}
