package com.harmazing.aixiumama.API;

/**
 * Created by Lyn on 2014/11/4.
 */
public class API {
    public static String POST_QQ = HttpHead.head + "api-token-auth/qq";

    public static String POST_WEIBO = HttpHead.head +"api-token-auth/weibo";

    public static String POST_WECHAT = HttpHead.head+ "api-token-auth/wechat";

    public static String GET_USER = HttpHead.head +"cute/api/v1.0/users/"; //{id}/?format=json

    public static String POST_BABY = HttpHead.head +"cute/api/v1.0/babies/"; //  POST   1.gender：性别, 0, 1, 2    2.birthday：生日， 示例2014-10-10     3.parent:当前用户id

    public static String GET_CUTES =  HttpHead.head +"cute/api/v1.0/cutes/";

    public static String ALL_STICKERS =  HttpHead.head +"cute/api/v1.0/stickers/?format=json";

    public static String STICKERS = HttpHead.image ;

    public static String COMMENTS_CUTE_ID = HttpHead.head +"cute/api/v1.0/comments/";     //&format=json";

    public static String LIKE = HttpHead.head +"cute/api/v1.0/likes/";

    public static String ADD_FRIENDS =  HttpHead.head +"cute/api/v1.0/follow_users/";

    public static String CUTE_LABELS = HttpHead.head +"cute/api/v1.0/cute_labels/";

    public static String FOLLOW_LABELS = HttpHead.head +"cute/api/v1.0/follow_labels/";

    public static String GET_LABELS = HttpHead.head +"cute/api/v1.0/labels/";

    public static String BLOCK = HttpHead.head +"cute/api/v1.0/block_users/";

    public static String REPORT = HttpHead.head +"cute/api/v1.0/report_users/";

    public static String SINGLE_STICKER = HttpHead.head +"cute/api/v1.0/stickers/";

    public static String NOTIFICATION = HttpHead.head +"cute/api/v1.0/notifications/";

    public static String FAVORITE = HttpHead.head +"cute/api/v1.0/favorite_cutes/?format=json";

    public static String BIND_WEIBO = HttpHead.head + "api-token-auth/bind-weibo";

    public static String BIND_QQ = HttpHead.head + "api-token-auth/bind-qq";

    public static String BIND_WECHAT = HttpHead.head +"api-token-auth/bind-wechat";

    public static String INVITE_WEIBO_FRIENDS ="https://api.weibo.com/2/friendships/friends.json?";

    public static String SEND_MESSAGE_TO_WEIBO_FRIENDS = "https://upload.api.weibo.com/2/statuses/upload.json?";

    public static String CURSOR_CITY = HttpHead.head +"cute/api/v1.0/provinces/";

    public static String SEND_PERSON_DATA = HttpHead.head +"cute/api/v1.0/users/";

    public static String SEND_PERSON_ADVICE = HttpHead.head +"cute/api/v1.0/feedbacks/";

    public static String SHARE_TO_FRIENDS = HttpHead.head +"cute/users/";

    public static String SHARE_TO_FRIENDS_IMAGE = HttpHead.head +"cute/cutes/";

    public static String UPDATA_WEIBO = "https://upload.api.weibo.com/2/statuses/upload.json?";

    public static String SHARE_PIC_TO = HttpHead.head +"cute/cutes/";

    public static String FAVOURITE_CUTES = HttpHead.head +"cute/api/v1.0/favorite_cutes/";

    public static String ACCESS_TOKEN_IS_VALID = "https://api.weibo.com/oauth2/get_token_info?";

    public static String PIC_HEAD = HttpHead.image ;

    public static String HISTORY = HttpHead.head +"cute/api/v1.0/visit_user_histories/";
    public static String LIKES_CUTE = HttpHead.head + "cute/api/v1.0/likes/?";


    public static String BAIDU_PUSH = HttpHead.head+"cute/api/v1.0/devices/?";

    public static String VERSION_CHECK = HttpHead.head+"cute/api/v1.0/android/last/";

    public static String RANK_CUTE = HttpHead.head+"cute/api/v1.0/follow_users/?";

    public static String WEI_XIN_REFRESH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?";

    public static String WEI_XIN_GET_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
}
