package com.example.mapper;

import com.example.entity.Likes;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface LikesMapper {

    @Insert("insert into likes (fid, user_id, module) values  (#{fid}, #{userId}, #{module})")
    void insert(Likes likes);

    @Delete("delete from likes where id = #{id}")
    void deleteById(Integer id);

    @Select("select * from likes where user_id = #{userId} and fid = #{fid}")
    Likes selectByUserIdAndFid(@Param("userId") Integer userId, @Param("fid") Integer fid);

    @Select("select count(*) from likes where fid = #{fid}")
    int selectCountByFid(Integer fid);
}
