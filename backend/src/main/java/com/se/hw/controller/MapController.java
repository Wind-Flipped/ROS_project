package com.se.hw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.common.Result;
import com.se.hw.common.TestUtil;
import com.se.hw.entity.Map;
import com.se.hw.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

import com.se.hw.service.IMapService;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author SE2304
 * @since 2023-05-24
 */
@RestController
@RequestMapping("/map")
public class MapController {

    @Resource
    private IMapService mapService;

    @GetMapping("/createMap")
    public Result save(@RequestParam String mapName, @RequestParam String bg) {
        Map map = new Map();
        map.setName(mapName);
        map.setBg(bg);
        map.setRosname(mapName);
        QueryWrapper<Map> mapQueryWrapper = new QueryWrapper<>();
        mapQueryWrapper.eq("name", mapName);
        List<Map> maps = mapService.list(mapQueryWrapper);
        if (maps != null && maps.size() != 0) {
            return Result.error(400, "naming repetition!");
        }
        mapService.save(map);
        return Result.success(200);
    }

    @PostMapping("/updateMap")
    public Result update(@RequestBody java.util.Map<String, Object> req) {
        Map map = req2map(req);
        if (map == null) {
            return Result.error(404, "map doesn't exist");
        }
        mapService.saveOrUpdate(map);
        return Result.success(200);
    }

    @GetMapping("/deleteMap")
    public Result delete(@RequestParam Integer mapId) {
        if (mapService.getById(mapId) == null) {
            return Result.error(404, "can't find the map");
        }
        mapService.removeById(mapId);
        return Result.success(200);
    }

    @GetMapping("/getAllMaps")
    public Result findAll() {
        return Result.success(200, mapService.list());
    }

    @GetMapping("/getMapInfo")
    public Result findOne(@RequestParam Integer mapId) {
        if (mapService.getById(mapId) == null) {
            return Result.error(404, "can't find the map");
        }
        return Result.success(200, mapService.getById(mapId));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(200, mapService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/getPicture")
    public Result getPicture() {
        return Result.success(200, RosGlobal.mapUrl);
    }

    private Map req2map(java.util.Map<String, Object> req) {
        java.util.Map<String, Object> request = (java.util.Map<String, Object>) req.get("map");
        int id = (int) request.get("id");
        Map map = mapService.getById(id);
        if (mapService.getById(map.getId()) == null) {
            return null;
        }
        return new Map(id, (String) request.get("name"), (String) request.get("url"), (String) request.get("welcome"),
                (String) request.get("rosname"), (String) request.get("bg"));
    }

    public static java.util.Map<String, Object> map2req(Map map) {
        java.util.Map<String, Object> request = new HashMap<>();
        request.put("id",map.getId());
        request.put("name",map.getName());
        request.put("url",map.getUrl());
        request.put("welcome",map.getWelcome());
        request.put("rosname",map.getRosname());
        request.put("bg",map.getBg());
        java.util.Map<String, Object> req = new HashMap<>();
        req.put("map",request);
        return req;
    }
}
