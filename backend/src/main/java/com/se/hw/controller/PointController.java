package com.se.hw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.se.hw.common.Result;
import com.se.hw.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/createPoint")
    public Result save(@RequestParam Float x, @RequestParam Float y, @RequestParam String name, @RequestParam Integer mapId) {
        Point point = new Point();
       // System.out.println(x+" "+y);
        point.setXAxis(x);
        point.setYAxis(y);
        point.setName(name);
        point.setMapId(mapId);
        QueryWrapper<Point> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", point.getName());
        List<Point> points = pointService.list(queryWrapper);
        if (points != null && !points.isEmpty()) {
            return Result.error(400, "naming repetition!");
        }
        try {
            pointService.save(point);
            return Result.success(200);
        } catch (Exception e) {
            return Result.error(500, "other error!");
        }

    }

    @PostMapping("/updatePoint")
    public Result update(@RequestBody Map<String, Object> req) {
        Point point = req2point(req);
        if (point == null) {
            return Result.error(404, "can't find the point!");
        }
        try {
            pointService.updateById(point);
            return Result.success(100);
        } catch (Exception e) {
            return Result.error(500, "other error");
        }
    }

    @GetMapping("/deletePoint")
    public Result delete(@RequestParam Integer pointId) {
        Point point = pointService.getById(pointId);
        if (point == null) {
            return Result.error(404, "can't find the point!");
        }
        pointService.removeById(pointId);
        return Result.success(100);
        // return Result.error(500, "other error!");
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

    @GetMapping("/mapToPoints")
    public Result mapToPoints(@RequestParam Integer mapId) {
        QueryWrapper<Point> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("map_id", mapId);
        List<Point> points = pointService.list(queryWrapper);
        return Result.success(100, points);
    }


    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(100, pointService.page(new Page<>(pageNum, pageSize)));
    }

    private Point req2point(Map<String, Object> req) {
        Map<String, Object> request = (Map<String, Object>) req.get("point");
        int id = (int) request.get("id");
        Point point = pointService.getById(id);
        if (point == null) {
            return null;
        }
        return new Point(id, (String) request.get("name"), (Float) request.get("xAxis"), (Float) request.get("yAxis"), (Integer) request.get("status"), (Integer) request.get("mapId"), (Float) request.get("zAxis"),
                (Float) request.get("oriX"), (Float) request.get("oriY"), (Float) request.get("oriZ"), (Float) request.get("oriW"));
    }

    public static java.util.Map<String, Object> point2req(Point point) {
        java.util.Map<String, Object> request = new HashMap<>();
        request.put("id",point.getId());
        request.put("name",point.getName());
        request.put("mapId",point.getMapId());
        request.put("status",point.getStatus());
        request.put("xAxis",point.getXAxis());
        request.put("yAxis",point.getYAxis());
        request.put("zAxis",point.getZAxis());
        request.put("oriX",point.getOriX());
        request.put("oriY",point.getOriY());
        request.put("oriZ",point.getOriZ());
        request.put("oriW",point.getOriW());
        java.util.Map<String, Object> req = new HashMap<>();
        req.put("point",request);
        return req;
    }
}

