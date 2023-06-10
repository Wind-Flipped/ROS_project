package com.se.hw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.common.Result;

import com.se.hw.common.TestUtil;
import com.se.hw.entity.Map;
import com.se.hw.entity.Point;
import com.se.hw.exception.ExceptionRecv;
import com.se.hw.mode.DeliveryMode;
import com.se.hw.mode.MappingMode;
import com.se.hw.mode.Mode;
import com.se.hw.mode.WelcomeMode;
import com.se.hw.service.IMapService;
import com.se.hw.service.IPointService;
import javafx.util.Pair;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.velocity.runtime.parser.node.ASTIntegerRange;
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

    public static Integer nowMapId;
    public static Integer nowPointId;

    @GetMapping("/getMode")
    public Result getMode() {
        //System.out.println(RosGlobal.nowMode);
        HashMap<String, Integer> h = new HashMap<>();
        h.put("mapId", nowMapId);
        if (RosGlobal.nowMode == null) {
            h.put("status", 0);
            h.remove("mapId");
            return Result.success(100, h);
        } else if (RosGlobal.nowMode instanceof MappingMode) {
            h.put("status", 1);
            return Result.success(100, h);
        } else if (RosGlobal.nowMode instanceof WelcomeMode) {
            h.put("status", 2);
            return Result.success(100, h);
        } else if (RosGlobal.nowMode instanceof DeliveryMode) {
            h.put("status", 3);
            return Result.success(100, h);
        }
        return Result.success(100, 4);
    }


    @GetMapping("/isConnect")
    public Result isConnect() {
        if (RosGlobal.rosBridge == null || !RosGlobal.rosBridge.hasConnected()) {
            return Result.error(404, "not connecting!!");
        }
        return Result.success(100, "connecting!!!");
    }

    @GetMapping("/connect")
    public Result connect(@RequestParam String rosIp) {
        if (!RosGlobal.init("ws://" + rosIp + ":9090")) {
            return Result.error(505, "Connection refused");
        }
        return Result.success(100);
    }

    /**
     * 切换模式，在切换模式前一定要注意先关闭当前模式，否则后端返回错误状态码，ROS端不执行模式切换
     *
     * @param type  模式类型，由前端确保值在 1--4 之间, 从1到4分别为建图，迎宾，送餐，航点编辑模式
     * @param mapId 已有的地图 id ，前端需先新建场景后再开启建图模式
     * @return code=200，
     * 则启动成功；
     * code=405，
     * 则找不到所给的地图；
     * code=404，
     * 则说明未退出当前模式，无法切换其他模式；
     * code=500，
     * 则说明与 ROS 连接出现问题
     * code=606，
     * 说明找不到迎宾点
     * code=707，
     * 说明找不到取餐点
     */
    @GetMapping("/changeMode")
    public Result change(@RequestParam Integer type, @RequestParam Integer mapId) {
        Map map = mapService.getById(mapId);
        nowMapId = mapId;
        RosGlobal.nowMapName = map.getName();
        if (map == null) {
            return Result.error(405, "the map doesn't exist");
        }
        Mode givenMode = RosGlobal.modes.get(type);
        //RosGlobal.nowMode = givenMode;
        givenMode.setMapName(map.getRosname());
        QueryWrapper<Point> queryWrapper = new QueryWrapper<>();
        List<Point> points;
        TestUtil.log("change: " + type + " " + mapId);
        switch (type) {
            case 1:// mapping
                break;
            case 2://welcome
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("map_id", mapId);
                queryWrapper.eq("status", 0);
                points = pointService.list(queryWrapper);
                if (points.size() == 0) {
                    return Result.error(606, "can't find the welcome");
                }
                givenMode.setPoint(points.get(0));
                break;
            case 3://Delivery
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("map_id", mapId);
                queryWrapper.eq("status", 1);
                points = pointService.list(queryWrapper);
                if (points.size() == 0) {
                    return Result.error(707, "can't find the kitchen");
                }
                givenMode.setPoint(points.get(0));
                break;
            case 4:// Point Edit
                break;
        }
        int status = givenMode.start();
        if (status == 1) {
            return Result.success(200);
        } else if (status == -1) {
            return Result.error(404, "Can't open the mode,please check if there's other mode opening");
        } else { // status == -2
            return Result.error(500, "Connection to ROS has failed");
        }
    }

    @PostMapping("/endMode")
    public Result end() {
        if (RosGlobal.nowMode instanceof MappingMode) {
            Map map = mapService.getById(nowMapId);
            map.setUrl(map.getName() + ".png");
            mapService.updateById(map);
        }
        if (RosGlobal.nowMode == null) {
            return Result.error(404, "already ending all modes!");
        }
        RosGlobal.nowMode.end();
        return Result.success(200);
    }

    @PostMapping("/confirmEat")
    public Result confirmEat() {
        if (!(RosGlobal.nowMode instanceof WelcomeMode) || !RosGlobal.launch_success) {
            return Result.error(404, "now is not welcome mode!");
        }
        if (!RosGlobal.arrive_welcome) {
            return Result.error(505, "robots is guiding!");
        }
        QueryWrapper<Point> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("map_id", nowMapId);
        List<Point> points = pointService.list(queryWrapper);
        for (Point point : points) {
            if (point.getStatus() == 2) {
                WelcomeMode mode = (WelcomeMode) RosGlobal.nowMode;
                point.setStatus(3);
                RosGlobal.arrive_welcome = false;
                mode.startGuide(point);
                RosGlobal.startClock();
                return Result.success(200);
            }
        }
        return Result.error(606, "full!");
    }

    @GetMapping("/confirmSend")
    public Result confirmSend(@RequestParam Integer pointId) {
        Point point = new Point();
        if ((point = pointService.getById(pointId)) == null) {
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
        RosGlobal.arrive_kitchen = false;
        deliveryMode.startReceive();
        RosGlobal.startClock();
        return Result.success(200);
    }

    @GetMapping("/savePoint")
    public Result savePoint(@RequestParam Integer mapId, @RequestParam String pointName, @RequestParam Integer type) {
        Point point = RosGlobal.point;
        point.setName(pointName);
        point.setMapId(mapId);
        point.setStatus(type);
        QueryWrapper<Point> queryWrapper;
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("map_id", point.getMapId());
        queryWrapper.eq("name", point.getName());
        List<Point> points = pointService.list(queryWrapper);
        if (points.size() != 0) {
            return Result.error(400, "name repetition");
        }
        if (point.getStatus() <= 1) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("map_id", point.getMapId());
            queryWrapper.eq("status", point.getStatus());
            List<Point> points1 = pointService.list(queryWrapper);
            if (points1.size() != 0 && point.getStatus() == 0) {
                return Result.error(606, "the welcome exists");
            } else if (points1.size() != 0 && point.getStatus() == 1) {
                return Result.error(505, "the kitchen exists");
            }
        }
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
                return Result.success(500, status);
            }
        }
        return Result.success(100);
    }

    @GetMapping("/getLocation")
    public Result getLocation() {
        HashMap<String, Float> axis = new HashMap<>();
        axis.put("xAxis", RosGlobal.point.getXAxis());
        axis.put("yAxis", RosGlobal.point.getYAxis());
        return Result.success(100, axis);
    }

    @GetMapping("/getGesture")
    public Result getGesture() {
        return Result.success(100, ExceptionRecv.getGesture());
    }

    @GetMapping("/getPower")
    public Result getPower() {
        return Result.success(100, ExceptionRecv.getPower());
    }

    @GetMapping("/getTime")
    public Result getTime() {
        if (ExceptionRecv.isTimeoutN()) {
            return Result.success(100, "no problem");
        }
        return Result.error(200, "time out!!!");
    }

    @GetMapping("/getPicture")
    public Result getPicture() {
        return Result.success(100, RosGlobal.nowMapName);
    }

}
