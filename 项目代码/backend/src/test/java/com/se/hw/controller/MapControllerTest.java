package com.se.hw.controller;

import com.se.hw.common.Result;
import com.se.hw.entity.Map;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

/** 
* MapController Tester. 
* 
* @author ¡ı‘À‰ø
* @since <pre>5‘¬ 15, 2023</pre> 
* @version 1.0 
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class MapControllerTest {

    @Autowired
    private MapController mapController;

    private Map map1;
    private Map map4;
    private int mapId;


@Before
public void before() throws Exception {
    // mapController = new MapController(mapService);
    map1 = new Map();
    map4 = new Map();

    mapController.save("map1","#FFFFFF");
    mapController.save("map2","#FFFFFF");
    mapController.save("map3","#FFFFFF");
    List<Map> maps = (List<Map>) mapController.findAll().getData();
    mapId = maps.get(0).getId();
    map1.setWelcome("welcome!!!");
    map1.setRosname("maps/map1lalala");
    map1.setName("map1");
    map1.setId(mapId);
    map4.setWelcome("welcome");
    map4.setRosname("maps/map4");
    map4.setName("map4");
    map4.setId(10000);
    map4.setBg("#DDDDDD");
    map4.setUrl("http");
}

@After
public void after() throws Exception {
    List<Map> maps = (List<Map>) mapController.findAll().getData();
    int size = maps.size();

    for (int i = 0; i < size; i++) {
        mapController.delete(maps.get(i).getId());
    }
} 

/** 
* 
* Method: save(@RequestParam String mapName) 
* 
*/ 
@Test
public void testSave() throws Exception { 
//TODO: Test goes here...
    Result result = mapController.save("new_map","#FFFFFF");
    assert result.getCode() == 200;
    Result result1 = mapController.save("new_map","#FFFFFF");
    assert result1.getCode() == 400 && result1.getMsg().equals("naming repetition!");
} 

/** 
* 
* Method: update(@RequestBody Map map) 
* 
*/ 
@Test
public void testUpdate() throws Exception { 
//TODO: Test goes here...
    Result result = mapController.update(MapController.map2req(map1));
    assert result.getCode() == 200;
    Result result2 = mapController.findOne(map1.getId());
    assert result2.getCode() == 200;
    assert ((Map) result2.getData()).getName().equals("map1") && ((Map) result2.getData()).getRosname().equals("maps/map1lalala")
            && ((Map) result2.getData()).getWelcome().equals("welcome!!!");
    Result result1 = mapController.update(MapController.map2req(map4));
    assert result1.getCode() == 404 && result1.getMsg().equals("map doesn't exist");
} 

/** 
* 
* Method: delete(@RequestParam Integer id) 
* 
*/ 
@Test
public void testDelete() throws Exception { 
//TODO: Test goes here...
    Result result = mapController.delete(mapId);
    assert result.getCode() == 200;
    Result result1 = mapController.delete(10000);
    assert result1.getCode() == 404 && result1.getMsg().equals("can't find the map");
} 

/** 
* 
* Method: findAll() 
* 
*/ 
@Test
public void testFindAll() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: findOne(@RequestParam Integer mapId) 
* 
*/ 
@Test
public void testFindOne() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) 
* 
*/ 
@Test
public void testFindPage() throws Exception { 
//TODO: Test goes here... 
} 


} 
