package com.se.hw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.se.hw.service.IMapService;
import com.se.hw.entity.Map;


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

    @PostMapping("/createMap")
    public Result save(@RequestParam String mapName) {
        Map map = new Map();
        map.setName(mapName);
        map.setWelcome("Welcome to our restaurant!");
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
    public Result update(@RequestBody Map map) {
        if (mapService.getById(map.getId()) == null) {
            return Result.error(404, "map doesn't exist");
        }
        mapService.saveOrUpdate(map);
        return Result.success(200);
    }

    @DeleteMapping("/deleteMap")
    public Result delete(@RequestParam Integer id) {
        if (mapService.getById(id) == null) {
            return Result.error(404, "can't find the map");
        }
        mapService.removeById(id);
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
}
