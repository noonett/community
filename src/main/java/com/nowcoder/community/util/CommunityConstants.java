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

}
