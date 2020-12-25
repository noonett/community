package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_USER_POST = "user:post";
    private static final String PREFIX_USER_COMMENT = "user:comment";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLike(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // like:user:userId
    public static String getUserLike(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 登陆验证码
    // kaptcha
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 登陆凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    // 用户的回复
    public static String getUserCommentKey(int userId, int commentId) {
        return PREFIX_USER_POST + SPLIT + userId + SPLIT + commentId;
    }

    // 用户的帖子
    // user:post:userId:postId
    // user:comment:userId:commentId
    public static String getUserPostKey(int userId, int postId) {
        return PREFIX_USER_POST + SPLIT + userId + SPLIT + postId;
    }

    // 单日UV 单日独立访客，到达本网站的IP数量包括匿名用户，hyperloglog用于去重，空间小
    public static String getUVkey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    // 区间UV
    public static String getUVkey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    // 单日DAU 单日活跃用户ID，bitmap也是去重，精确
    public static String getDAUkey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    // 区间DAU
    public static String getDAUkey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    // 计算帖子分数的key
    public static String getPostScoreKey(){
        return PREFIX_POST+ SPLIT + "score";
    }
}
