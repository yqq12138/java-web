package com.example.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础前端接口
 */
@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    UserService userService;
    @Resource
    QuestionService questionService;
    @Resource
    FeedbackService feedbackService;
    @Resource
    NewsService newsService;
    @Resource
    AnswerService answerService;

    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        if (ObjectUtil.isEmpty(account.getUsername()) || ObjectUtil.isEmpty(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            account = adminService.login(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            account = userService.login(account);
        }
        return Result.success(account);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        } else {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR);
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

    @GetMapping("/count")
    public Result selectCount() {
        List<News> news = newsService.selectAll(null);
        List<Question> questions = questionService.selectAll(null);
        List<Feedback> feedbacks = feedbackService.selectAll(null);
        List<User> users = userService.selectAll(null);
        Dict dict = Dict.create()
                .set("newsCount", news.size())
                .set("questionCount", questions.size())
                .set("feedbackCount", feedbacks.size())
                .set("userCount", users.size());
        return Result.success(dict);
    }

    @GetMapping("/selectLine")
    public Result selectLine() {
        List<Answer> answerList = answerService.selectAll(null);
        Date date = new Date();
        DateTime start = DateUtil.offsetDay(date, -8);
        DateTime end = DateUtil.offsetDay(date, -1);
        List<String> dateList = DateUtil.rangeToList(start, end, DateField.DAY_OF_YEAR).stream()
                .map(DateUtil::formatDate).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        List<Dict> list = new ArrayList<>();
        for (String dateStr : dateList) {
            long count = answerList.stream().filter(answer -> answer.getTime().contains(dateStr)).count();
            Dict dict = Dict.create().set("name", dateStr).set("value", count);
            list.add(dict);
        }
        return Result.success(list);
    }

    @GetMapping("/selectPie")
    public Result selectPie() {
        List<News> newsList = newsService.selectAll(null);
        newsList = newsList.stream().filter(news -> news.getType().equals("common")).collect(Collectors.toList());
        Set<String> set = newsList.stream().map(News::getCategory).collect(Collectors.toSet());
        List<Dict> list = new ArrayList<>();
        for (String category : set) {
            long count = newsList.stream().filter(news -> news.getCategory().equals(category)).count();
            Dict dict = Dict.create().set("name", category).set("value", count);
            list.add(dict);
        }
        return Result.success(list);
    }

}
