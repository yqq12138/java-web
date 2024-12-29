package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Answer;
import com.example.entity.Likes;
import com.example.mapper.AnswerMapper;
import com.example.mapper.LikesMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 回答信息业务处理
 **/
@Service
public class AnswerService {

    @Resource
    private AnswerMapper answerMapper;

    @Resource
    LikesMapper likesMapper;

    /**
     * 新增
     */
    public void add(Answer answer) {
        answer.setStatus("待审核");
        Account currentUser = TokenUtils.getCurrentUser();
        answer.setUserId(currentUser.getId());
        answer.setTime(DateUtil.now());
        answerMapper.insert(answer);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        answerMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            answerMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Answer answer) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            answer.setStatus("待审核");
        }
        answerMapper.updateById(answer);
    }

    /**
     * 根据ID查询
     */
    public Answer selectById(Integer id) {
        return answerMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Answer> selectAll(Answer answer) {
        return answerMapper.selectAll(answer);
    }

    /**
     * 分页查询
     */
    public PageInfo<Answer> selectPage(Answer answer, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            answer.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Answer> list = answerMapper.selectAll(answer);
        return PageInfo.of(list);
    }

    public PageInfo<Answer> selectFrontPage(Answer answer, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        PageHelper.startPage(pageNum, pageSize);
        List<Answer> list = answerMapper.selectFrontPage(answer);
        for (Answer a : list) {
            Likes likes = likesMapper.selectByUserIdAndFid(currentUser.getId(), a.getId());
            a.setLikes(likes != null);
            int likesCount = likesMapper.selectCountByFid(a.getId());
            a.setLikesCount(likesCount);
        }
        return PageInfo.of(list);
    }

}