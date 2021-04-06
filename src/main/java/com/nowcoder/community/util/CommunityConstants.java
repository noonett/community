package com.nowcoder.community.util;

public interface CommunityConstants {
    /**
     * 激活码状态
     */
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 1;

    /**
     * 默认状态的登陆凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态下的登陆凭证的超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 事件主题
     */
    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";
    String TOPIC_PUBLISH = "publish";
    String TOPIC_DELETE = "delete";
    String TOPIC_SHARE = "share";
    String TOPIC_BROADCAST = "broadcast";
    /**
     * 系统用户Id
     */
    int SYSTEM_USERID = 1;

    /**
     * 用户权限：普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 版主
     */
    String AUTHORITY_MODERATOR = "moderator";

    /**
     * 帖子类型：置顶
     */
    int DISCUSSPOST_TYPE_TOP = 1;
}
