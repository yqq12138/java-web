package com.example.mapper;

import com.example.entity.Answer;
import java.util.List;

/**
 * 操作answer相关数据接口
 */
public interface AnswerMapper {

    /**
     * 新增
     */
    int insert(Answer answer);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Answer answer);

    /**
     * 根据ID查询
     */
    Answer selectById(Integer id);

    /**
     * 查询所有
     */
    List<Answer> selectAll(Answer answer);

    List<Answer> selectFrontPage(Answer answer);

}