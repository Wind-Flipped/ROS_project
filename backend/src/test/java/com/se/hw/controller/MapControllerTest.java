package com.se.hw.controller;

import com.se.hw.common.Result;
import com.se.hw.entity.Map;
import com.se.hw.entity.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

    private MapController mapController;

    private User user;
    private Map map1;
    private Map map4;

@Before
public void before() throws Exception {
    mapController = new MapController();
    map1.setWelcome("welcome!!!");
    map1.setPath("maps/map1lalala");
    map1.setName("map1");
    map1.setId(1);
    map4.setWelcome("welcome");
    map4.setPath("maps/map4");
    map4.setName("map4");
    mapController.save("map1");
    mapController.save("map2");
    mapController.save("map3");
}

@After
public void after() throws Exception {
    for (int i = 1; i <= 5; i++) {
        mapController.delete(i);
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
    Result result = mapController.save("new_map");
    assert result.getCode() == 200;
    Result result1 = mapController.save("new_map");
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
    Result result = mapController.update(map1);
    assert result.getCode() == 200;
    Result result2 = mapController.findOne(map1.getId());
    assert result2.getCode() == 200;
    assert ((Map) result2.getData()).getName().equals("map1") && ((Map) result2.getData()).getPath().equals("maps/map1lalala")
            && ((Map) result2.getData()).getWelcome().equals("welcome!!!");
    Result result1 = mapController.update(map4);
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
    Result result = mapController.delete(1);
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
