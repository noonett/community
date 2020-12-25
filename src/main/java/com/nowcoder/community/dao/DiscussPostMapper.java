package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode); //每一页的offset行号，limit一页多少行

    List<DiscussPost> selectDiscussPostsByUserId(int userId, int offset, int limit);

    //@Param（）给参数取别名
    // 并且在<if>里使用（动态的方法）且参数只有一个，一定要取别名。
    int selectDiscussPostRows(@Param("userId") int userId);

    DiscussPost selectDiscussPostbyId(int Id);

    int insertDiscussPost(DiscussPost discussPost);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}

