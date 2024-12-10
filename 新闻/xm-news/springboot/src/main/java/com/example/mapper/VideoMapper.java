package com.example.mapper;

import com.example.entity.Video;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 操作video相关数据接口
 */
public interface VideoMapper {

    /**
     * 新增
     */
    int insert(Video video);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Video video);

    /**
     * 根据ID查询
     */
    Video selectById(Integer id);

    /**
     * 查询所有
     */
    List<Video> selectAll(Video video);

    @Update("update video set read_count = read_count + 1 where id = #{id}")
    void updateCount(Integer id);

}