package com.example.controller;

import com.example.common.Result;
import com.example.entity.Answer;
import com.example.service.AnswerService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 回答信息前端操作接口
 **/
@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Resource
    private AnswerService answerService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Answer answer) {
        answerService.add(answer);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        answerService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        answerService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Answer answer) {
        answerService.updateById(answer);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Answer answer = answerService.selectById(id);
        return Result.success(answer);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Answer answer) {
        List<Answer> list = answerService.selectAll(answer);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Answer answer,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Answer> page = answerService.selectPage(answer, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/selectFrontPage")
    public Result selectFrontPage(Answer answer,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Answer> page = answerService.selectFrontPage(answer, pageNum, pageSize);
        return Result.success(page);
    }

}