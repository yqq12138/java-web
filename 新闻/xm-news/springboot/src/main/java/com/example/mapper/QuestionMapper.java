package com.example.mapper;

import com.example.entity.Question;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 操作question相关数据接口
 */
public interface QuestionMapper {

    /**
     * 新增
     */
    int insert(Question question);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Question question);

    /**
     * 根据ID查询
     */
    Question selectById(Integer id);

    /**
     * 查询所有
     */
    List<Question> selectAll(Question question);

    List<Question> selectFrontPage(Question question);

    List<Question> selectNoAnswer();

    @Update("update question set read_count = read_count + 1 where id = #{id}")
    void updateCount(Integer id);

}