package com.se.hw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.common.Result;
import com.se.hw.entity.Map;
import com.se.hw.entity.Point;
import com.se.hw.mode.DeliveryMode;
import com.se.hw.mode.WelcomeMode;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/** 
* RosController Tester. 
* 
* @author ¡ı‘À‰ø
* @since <pre>5‘¬ 16, 2023</pre> 
* @version 1.0 
*/ 
public class RosControllerTest {
    @Autowired
    private RosController rosController;
    @Autowired
    private MapController mapController;
    @Autowired
    private PointController pointController;

    private Point point;

@Before
public void before() throws Exception {
//    rosController = new RosController();
//    mapController = new MapController();
//    pointController = new PointController();

    point = new Point(1,"point",1.0,1.0,0,1,1.0,1.0,1.0,1.0,1.0);

    mapController.save("map");
    pointController.save(point);

} 

@After
public void after() throws Exception {
    // End current mode
    rosController.end();
} 

/** 
* 
* Method: change(@RequestParam Integer type, @RequestParam(value = "") String mapName) 
* 
*/ 
@Test
public void testChange() throws Exception { 
//TODO: Test goes here...
    // Type error
    Result resultType = rosController.change(100,"map");
    assert resultType.getCode() == 400 && resultType.getMsg().equals("type error!");

    // Mapping
    Result result = rosController.change(1,"map");
    assert result.getCode() == 406 && result.getMsg().equals("the map already exists");
    Result result1 = rosController.change(1,"new_map");
    assert result1.getCode() == 200;
    // Reopen Mapping
    Result result2 = rosController.change(1,"new_map");
    assert result2.getCode() == 404 && result2.getMsg().equals("Can't open the mode,please check if there's other mode opening");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    // Map not found
    Result result4 = rosController.change(2,"new_mapmap");
    assert result4.getCode() == 405 && result4.getMsg().equals("the map doesn't exist");

    // Welcome
    Result result5 = rosController.change(2,"map");
    assert result5.getCode() == 200;
    // End welcome
    Result result6 = rosController.end();
    assert result6.getCode() == 200;

    // Delivery
    Result result7 = rosController.change(3,"map");
    assert result7.getCode() == 200;
    // End welcome
    Result result8 = rosController.end();
    assert result8.getCode() == 200;

    // PointEdit
    Result result9 = rosController.change(4,"map");
    assert result9.getCode() == 200;
} 

/** 
* 
* Method: end() 
* 
*/ 
@Test
public void testEnd() throws Exception { 
//TODO: Test goes here...
    // Begin mapping
    Result result1 = rosController.change(1,"new_map");
    assert result1.getCode() == 200;
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    // ReEnd mapping
    Result result2 = rosController.end();
    assert result2.getCode() == 404 && result2.getMsg().equals("already ending all modes!");
} 

/** 
* 
* Method: confirmEat() 
* 
*/ 
@Test
public void testConfirmEat() throws Exception { 
//TODO: Test goes here...
    // Begin mapping
    Result result1 = rosController.change(1,"new_map");
    assert result1.getCode() == 200;

    Result result2 = rosController.confirmEat();
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not welcome mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    // Welcome
    Result result4 = rosController.change(2,"map");
    assert result4.getCode() == 200;

    Result result5 = rosController.confirmEat();
    assert result5.getCode() == 200;

    Result result7 = rosController.confirmEat();
    assert result7.getCode() == 505 && result7.getMsg().equals("robots is guiding!");

    RosGlobal.arrive_welcome = true;

    Result result6 = rosController.confirmEat();
    assert result6.getCode() == 606 && result6.getMsg().equals("full!");

} 

/** 
* 
* Method: confirmSend(Point point) 
* 
*/ 
@Test
public void testConfirmSend() throws Exception { 
//TODO: Test goes here...
    // Begin mapping
    Result result1 = rosController.change(1,"new_map");
    assert result1.getCode() == 200;

    Result result2 = rosController.confirmSend(point);
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not delivery mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    // Delivery
    Result result4 = rosController.change(3,"map");
    assert result4.getCode() == 200;
    Result result5 = rosController.confirmSend(point);
    assert result5.getCode() == 200;
    Result result6 = rosController.confirmSend(point);
    assert result6.getCode() == 505 && result6.getMsg().equals("robots is sending!");

    RosGlobal.arrive_kitchen = true;
    Result result7 = rosController.confirmSend(new Point(1000,"point",1.0,1.0,0,1,1.0,1.0,1.0,1.0,1.0));
    assert result7.getCode() ==  400 && result7.getMsg().equals("can't find the point");

} 

/** 
* 
* Method: confirmReceive() 
* 
*/ 
@Test
public void testConfirmReceive() throws Exception { 
//TODO: Test goes here...
    // Begin mapping
    Result result1 = rosController.change(1,"new_map");
    assert result1.getCode() == 200;

    Result result2 = rosController.confirmReceive();
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not delivery mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    // Delivery
    Result result4 = rosController.change(3,"map");
    assert result4.getCode() == 200;
    Result result5 = rosController.confirmReceive();
    assert result5.getCode() == 200;

} 

/** 
* 
* Method: savePoint(Integer mapId, String pointName) 
* 
*/ 
@Test
public void testSavePoint() throws Exception { 
//TODO: Test goes here...

    Result result = rosController.savePoint(1,"new_point");
    assert result.getCode() == 200;

    Result result2 = rosController.savePoint(1000,"new_pointpoint");
    assert result2.getCode() == 404 && result2.getMsg().equals("map doesn't exist");

    // TODO 405 error not test
} 

/** 
* 
* Method: getException() 
* 
*/ 
@Test
public void testGetException() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getLocation() 
* 
*/ 
@Test
public void testGetLocation() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getGesture() 
* 
*/ 
@Test
public void testGetGesture() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getPower() 
* 
*/ 
@Test
public void testGetPower() throws Exception { 
//TODO: Test goes here... 
} 


} 
