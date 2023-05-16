package com.se.hw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.se.hw.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.se.hw.service.IPointService;
import com.se.hw.entity.Point;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author SE2304
 * @since 2023-04-27
 */
@RestController
@RequestMapping("/point")
public class PointController {

    @Resource
    private IPointService pointService;

    @PostMapping("/createPoint")
    public Result save(@RequestBody Point point) {
        QueryWrapper<Point> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", point.getName());
        List<Point> points = pointService.list(queryWrapper);
        if (points != null && !points.isEmpty()) {
            return Result.error(400, "naming repetition!");
        }
        if (pointService.save(point)) {
            return Result.success(200);
        }
        return Result.error(500, "other error!");
    }

    @PostMapping("/updatePoint")
    public Result update(@RequestBody Point point) {
        if (pointService.getById(point.getId()) == null) {
            return Result.error(404, "can't find the point!");
        }
        if (pointService.updateById(point)) {
            return Result.success(100);
        }
        return Result.error(500, "other error");
    }

    @DeleteMapping("/deletePoint")
    public Result delete(@RequestParam Integer pointId) {
        Point point = pointService.getById(pointId);
        if (point == null) {
            return Result.error(404, "can't find the point!");
        }
        if (pointService.removeById(pointId)) {
            return Result.success(100);
        }
        return Result.error(500, "other error!");
    }

    @GetMapping("/getAllPoints")
    public Result findAll() {
        return Result.success(100, pointService.list());
    }

    @GetMapping("/getPoint")
    public Result findOne(@RequestParam Integer pointId) {
        Point point = pointService.getById(pointId);
        if (point == null) {
            return Result.error(404, "can't find the point!");
        }
        return Result.success(100, point);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(100,pointService.page(new Page<>(pageNum, pageSize)));
    }

}

