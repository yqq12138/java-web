package com.example.service;

import com.example.entity.Account;
import com.example.entity.Likes;
import com.example.mapper.LikesMapper;
import com.example.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikesService {

    @Resource
    LikesMapper likesMapper;

    public void add(Likes likes) {
        Account currentUser = TokenUtils.getCurrentUser();
        Integer userId = currentUser.getId();
        Integer fid = likes.getFid();
        Likes dbLikes = likesMapper.selectByUserIdAndFid(userId, fid);
        // 如果当前的登录用户给当前的这个数据点过赞了，那么就取消点赞
        if (dbLikes != null) {
            likesMapper.deleteById(dbLikes.getId());
        } else {
            // 如果当前的登录用户没有点赞，那就插入新的点赞的数据
            likes.setUserId(userId);
            likesMapper.insert(likes);
        }
    }

}
