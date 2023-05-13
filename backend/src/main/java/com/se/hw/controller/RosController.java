package com.se.hw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.common.Result;

import com.se.hw.entity.Map;
import com.se.hw.entity.Point;
import com.se.hw.mode.DeliveryMode;
import com.se.hw.mode.WelcomeMode;
import com.se.hw.service.IMapService;
import com.se.hw.service.IPointService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/ros")
public class RosController {

    @Resource
    private IMapService mapService;
    @Resource
    private IPointService pointService;

    @PostMapping("/changeMode")
    public Result change(@RequestParam Integer type, @RequestParam(value = "") String mapName) {
        QueryWrapper<Map> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", mapName);
        if (type != 1 && (mapService.list(queryWrapper).isEmpty())) {
            return Result.error(405, "the map doesn't exist");
        } else if (type == 1 && mapService.list(queryWrapper).size() != 0) {
            return Result.error(406, "the map already exists");
        } else if (type == 1) {
            Map map = new Map();
            map.setName(mapName);
            map.setPath(mapName);
            map.setWelcome("Welcome to our restaurant!");
            mapService.save(map);
        }
        int mapId = mapService.list(queryWrapper).get(0).getId();
        RosGlobal.modes.get(type).setMapName(mapName);
        if (type == 1) { // mapping
            if (RosGlobal.modes.get(type).start() > 0) {
                return Result.success(200);
            } else {
                return Result.error(404, "Can't open the mode,please check if there's other mode opening");
            }
        } else if (type == 2) { //welcome
            WelcomeMode welcomeMode = (WelcomeMode) RosGlobal.modes.get(type);
            List<Point> points = pointService.list();
            for (Point point : points) {
                if (point.getName().contains("welcome") && point.getMapId() == mapId) {
                    welcomeMode.setPoint(point);
                    if (welcomeMode.start() > 0) {
                        RosGlobal.startClock();
                        return Result.success(200);
                    } else {
                        return Result.error(404, "Can't open the mode,please check if there's other mode opening");
                    }
                }
            }
            return Result.error(606, "welcome point doesn't exist");
        } else if (type == 3) { //Delivery
            DeliveryMode deliveryMode = (DeliveryMode) RosGlobal.modes.get(type);
            List<Point> points = pointService.list();
            for (Point point : points) {
                if (point.getName().contains("kitchen") && point.getMapId() == mapId) {
                    deliveryMode.setPoint(point);
                    if (deliveryMode.start() > 0) {
                        RosGlobal.startClock();
                        return Result.success(200);
                    } else {
                        return Result.error(404, "Can't open the mode,please check if there's other mode opening");
                    }
                }
            }
            return Result.error(707, "kitchen point doesn/t exist");
        } else if (type == 4) {
            if (RosGlobal.modes.get(type).start() > 0) {
                return Result.success(200);
            } else {
                return Result.error(404, "Can't open the mode,please check if there's other mode opening");
            }
        }
        return Result.error(400, "type error!");
    }

    @PostMapping("/endMode")
    public Result end() {
        if (RosGlobal.nowMode.end() < 0) {
            return Result.error(404, "already ending all modes!");
        }
        return Result.success(200, "success!");
    }

    @PostMapping("/confirmEat")
    public Result confirmEat() {
        if (!(RosGlobal.nowMode instanceof WelcomeMode)) {
            return Result.error(404, "now is not welcome mode!");
        }
        if (!RosGlobal.arrive_welcome) {
            return Result.error(505, "robots is guiding!");
        }
        List<Point> points = pointService.list();
        for (Point point : points) {
            if (point.getName().contains("table") && point.getStatus() == 0) {
                WelcomeMode mode = (WelcomeMode) RosGlobal.nowMode;
                point.setStatus(1);
                RosGlobal.arrive_welcome = false;
                mode.startGuide(point);
                RosGlobal.startClock();
                return Result.success(200);
            }
        }
        return Result.error(606, "full!");
    }

    @PostMapping("/confirmSend")
    public Result confirmSend(Point point) {
        if (pointService.getById(point.getId()) == null) {
            return Result.error(400, "can't find the point");
        }
        if (!(RosGlobal.nowMode instanceof DeliveryMode)) {
            return Result.error(404, "now is not delivery mode!");
        }
        if (!RosGlobal.arrive_kitchen) {
            return Result.error(505, "robots is sending!");
        }
        RosGlobal.arrive_kitchen = false;
        DeliveryMode deliveryMode = (DeliveryMode) RosGlobal.nowMode;
        deliveryMode.startSend(point);
        RosGlobal.startClock();
        return Result.success(200);
    }

    @PostMapping("/confirmReceive")
    public Result confirmReceive() {
        if (!(RosGlobal.nowMode instanceof DeliveryMode)) {
            return Result.error(404, "now is not delivery mode!");
        }
        DeliveryMode deliveryMode = (DeliveryMode) RosGlobal.nowMode;
        deliveryMode.startReceive();
        RosGlobal.startClock();
        return Result.success(200);
    }

    @PostMapping("/savePoint")
    public Result savePoint(Integer mapId, String pointName) {
        Point point = RosGlobal.point;
        point.setName(pointName);
        point.setMapId(mapId);
        if (mapService.getById(mapId) == null) {
            return Result.error(404, "map doesn't exist");
        }
        if (pointService.save(point)) {
            return Result.success(200);
        }
        return Result.error(405, "save error!");
    }

    @GetMapping("/getException")
    public Result getException() {
        boolean[] status = RosGlobal.getException();
        for (boolean state : status) {
            if (!state) {
                // 有异常返回500，并得到相应异常类型，也可能存在多个异常。
                return Result.success(500,status);
            }
        }
        return Result.success(100);
    }

    @GetMapping("/getLocation")
    public Result getLocation() {
        HashMap<String, Double> axis = new HashMap<>();
        axis.put("xAxis", RosGlobal.point.getXAxis());
        axis.put("yAxis", RosGlobal.point.getXAxis());
        return Result.success(100, axis);
    }

    @GetMapping("/getGesture")
    public Result getGesture() {
        return Result.success(100, RosGlobal.rad);
    }

    @GetMapping("/getPower")
    public Result getPower() {
        return Result.success(100, RosGlobal.power);
    }

}
