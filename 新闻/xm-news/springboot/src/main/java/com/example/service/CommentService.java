package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 评论表业务处理
 **/
@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;

    /**
     * 新增
     */
    public void add(Comment comment) {
        comment.setTime(DateUtil.now());
        commentMapper.insert(comment);

        Integer id = comment.getId();
        // 更新root_id
        if (comment.getPid() == null) {
            comment.setRootId(id);
        } else {
            Comment parentComment = commentMapper.selectById(comment.getPid());
            comment.setRootId(parentComment.getRootId());
        }
        this.updateById(comment);
    }

    public void deleteById(Integer id) {
        this.deepDelete(id);
    }

    /**
     * 递归删除
     */
    public void deleteDeep(Integer id) {
        this.deepDelete(id);
    }

    private void deepDelete(Integer pid) {
        List<Comment> children = commentMapper.selectByPid(pid);
        commentMapper.deleteById(pid);
        for (Comment child : children) {
            this.deepDelete(child.getId());
        }
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        return PageInfo.of(list);
    }

    public PageInfo<Comment> selectTree(Integer fid, String module, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> rootList = commentMapper.selectRoot(fid, module);
        PageInfo<Comment> pageInfo = PageInfo.of(rootList);
        for (Comment root : rootList) {
            Integer rootId = root.getRootId();
            List<Comment> children = commentMapper.selectByRootId(rootId);
            root.setChildren(children);
        }
        return pageInfo;
    }

    public Integer selectCount(Integer fid, String module) {
        return commentMapper.selectCount(fid, module);
    }
}