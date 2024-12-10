package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Feedback;
import com.example.mapper.FeedbackMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 意见反馈业务处理
 **/
@Service
public class FeedbackService {

    @Resource
    private FeedbackMapper feedbackMapper;

    /**
     * 新增
     */
    public void add(Feedback feedback) {
        Account currentUser = TokenUtils.getCurrentUser();
        feedback.setUserId(currentUser.getId());
        feedback.setTime(DateUtil.now());
        feedbackMapper.insert(feedback);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        feedbackMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            feedbackMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Feedback feedback) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.ADMIN.name())) {
            feedback.setReplyTime(DateUtil.now());
        }
        feedbackMapper.updateById(feedback);
    }

    /**
     * 根据ID查询
     */
    public Feedback selectById(Integer id) {
        return feedbackMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Feedback> selectAll(Feedback feedback) {
        return feedbackMapper.selectAll(feedback);
    }

    /**
     * 分页查询
     */
    public PageInfo<Feedback> selectPage(Feedback feedback, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            feedback.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Feedback> list = feedbackMapper.selectAll(feedback);
        return PageInfo.of(list);
    }

}