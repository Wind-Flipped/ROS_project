package com.se.hw.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.common.Result;
import com.se.hw.entity.Map;
import com.se.hw.entity.Point;
import com.se.hw.mode.DeliveryMode;
import com.se.hw.mode.WelcomeMode;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

import java.util.HashMap;
import java.util.List;

/** 
* RosController Tester. 
* 
* @author ¡ı‘À‰ø
* @since <pre>5‘¬ 16, 2023</pre> 
* @version 1.0 
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class RosControllerTest {
    @Autowired
    private RosController rosController;
    @Autowired
    private MapController mapController;
    @Autowired
    private PointController pointController;

    private Point point;
    private Point point2;
    private int mapId;
    private RosBridge rosBridge;

@Before
public void before() throws Exception {
    // RosGlobal.init("http://localhost:8080");
    mapController.save("map");
    List<Map> maps = (List<Map>) mapController.findAll().getData();
    mapId = maps.get(0).getId();
    point = new Point(1,"point",1.0,1.0,0,mapId,1.0,1.0,1.0,1.0,1.0);
    point2 = new Point(2,"point2",2.0,2.0,0,mapId,1.0,1.0,1.0,1.0,1.0);
    pointController.save(point);
    rosBridge = RosGlobal.rosBridge;
} 

@After
public void after() throws Exception {
    // End current mode
    pointController.delete(1);
    mapController.delete(mapId);
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
    // Mapping
    Result result1 = rosController.change(1,mapId,point.getId());
    assert result1.getCode() == 200;
    Thread.sleep(1000);
    // Reopen Mapping
    Result result2 = rosController.change(1,mapId,point.getId());
    assert result2.getCode() == 404 && result2.getMsg().equals("Can't open the mode,please check if there's other mode opening");
    Thread.sleep(1000);
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    Thread.sleep(1000);
    // Map not found
    Result result4 = rosController.change(2,0x3f3f3f3f,point.getId());
    assert result4.getCode() == 405 && result4.getMsg().equals("the map doesn't exist");
    Thread.sleep(1000);

    // Welcome
    Result result5 = rosController.change(2,mapId,point.getId());
    assert result5.getCode() == 200;
    Thread.sleep(1000);
    // End welcome
    Result result6 = rosController.end();
    assert result6.getCode() == 200;
    Thread.sleep(1000);

    // Delivery
    Result result7 = rosController.change(3,mapId,point.getId());
    assert result7.getCode() == 200;
    Thread.sleep(1000);
    // End welcome
    Result result8 = rosController.end();
    assert result8.getCode() == 200;
    Thread.sleep(1000);
    // PointEdit
    Result result9 = rosController.change(4,mapId,point.getId());
    assert result9.getCode() == 200;
    Thread.sleep(1000);

    // Point not found
    Result result10 = rosController.change(1,mapId, 0x3f3f3f3f);
    assert result10.getCode() == 606 && result10.getMsg().equals("Can't find the point!");
} 

/** 
* 
* Method: end() 
* 
*/ 
@Test
public void testEnd() throws Exception { 
//TODO: Test goes here...
    final int[] isEnd = {0};
    rosBridge.subscribe(SubscriptionRequestMsg.generate("/disable")
                    .setType(MsgGlobal.msgString)
                    .setThrottleRate(1)
                    .setQueueLength(1),
            (data, stringRep) -> isEnd[0] = 1
    );
    // Begin mapping
    Result result1 = rosController.change(1,mapId, point.getId());
    assert result1.getCode() == 200;
    // End mapping
    Result result3 = rosController.end();
    Thread.sleep(1000);
    assert result3.getCode() == 200 && isEnd[0] == 1;

    // ReEnd mapping
    isEnd[0] = 0;
    Result result2 = rosController.end();
    Thread.sleep(1000);
    assert result2.getCode() == 404 && result2.getMsg().equals("already ending all modes!") && isEnd[0] == 0;
} 

/** 
* 
* Method: confirmEat() 
* 
*/ 
@Test
public void testConfirmEat() throws Exception { 
//TODO: Test goes here...
    final int[] isGuide = {0};
    rosBridge.subscribe(SubscriptionRequestMsg.generate("/guidance")
                    .setType(MsgGlobal.msgFloatArray)
                    .setThrottleRate(1)
                    .setQueueLength(1),
            (data, stringRep) -> {
                JSONObject json = JSONObject.parseObject(data.toString());
                JSONArray array = json.getJSONObject("msg").getJSONArray("data");
                if (point.getXAxis() == array.getDoubleValue(0)
                && point.getYAxis() == array.getDoubleValue(1)
                && point.getZAxis() == array.getDoubleValue(2)
                && point.getOriX() == array.getDoubleValue(3)
                && point.getOriY() == array.getDoubleValue(4)
                && point.getOriZ() == array.getDoubleValue(5)
                && point.getOriW() == array.getDoubleValue(6)) {
                    isGuide[0] = 1;
                }

            }
    );
    // Begin mapping
    Result result1 = rosController.change(1,mapId,point.getId());
    assert result1.getCode() == 200;

    Result result2 = rosController.confirmEat();
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not welcome mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    // Welcome
    Result result4 = rosController.change(2,mapId, point.getId());
    assert result4.getCode() == 200;

    Result result5 = rosController.confirmEat();
    Thread.sleep(1000);
    assert result5.getCode() == 200 && isGuide[0] == 1;

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
    final int[] isGuide = {0};
    rosBridge.subscribe(SubscriptionRequestMsg.generate("/delivery")
                    .setType(MsgGlobal.msgFloatArray)
                    .setThrottleRate(1)
                    .setQueueLength(1),
            (data, stringRep) -> {
                JSONObject json = JSONObject.parseObject(data.toString());
                JSONArray array = json.getJSONObject("msg").getJSONArray("data");
                if (point2.getXAxis() == array.getDoubleValue(0)
                        && point2.getYAxis() == array.getDoubleValue(1)
                        && point2.getZAxis() == array.getDoubleValue(2)
                        && point2.getOriX() == array.getDoubleValue(3)
                        && point2.getOriY() == array.getDoubleValue(4)
                        && point2.getOriZ() == array.getDoubleValue(5)
                        && point2.getOriW() == array.getDoubleValue(6)) {
                    isGuide[0] = 1;
                }

            }
    );
    // Begin mapping
    Result result1 = rosController.change(1,mapId, point.getId());
    assert result1.getCode() == 200;

    Result result2 = rosController.confirmSend(point2);
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not delivery mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    // Delivery
    Result result4 = rosController.change(3,mapId,point.getId());
    assert result4.getCode() == 200;
    Result result5 = rosController.confirmSend(point2);
    Thread.sleep(1000);
    assert result5.getCode() == 200 && isGuide[0] == 1;
    Result result6 = rosController.confirmSend(point2);
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

    final int[] isGuide = {0};
    rosBridge.subscribe(SubscriptionRequestMsg.generate("/delivery_confirm")
                    .setType(MsgGlobal.msgString)
                    .setThrottleRate(1)
                    .setQueueLength(1),
            (data, stringRep) -> isGuide[0] = 1
    );
    // Begin mapping
    Result result1 = rosController.change(1,mapId,point.getId());
    assert result1.getCode() == 200;

    Result result2 = rosController.confirmReceive();
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not delivery mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    // Delivery
    Result result4 = rosController.change(3,mapId,point.getId());
    assert result4.getCode() == 200;
    Result result5 = rosController.confirmReceive();
    Thread.sleep(1000);
    assert result5.getCode() == 200 && isGuide[0] == 1;

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
    Result result = rosController.getException();
    assert result.getCode() == 100;
} 

/** 
* 
* Method: getLocation() 
* 
*/ 
@Test
public void testGetLocation() throws Exception { 
//TODO: Test goes here...
    new Publisher("/get_pos", MsgGlobal.msgFloatArray, rosBridge).publish(new float[] {1.0F,1.0F,1.0F,1.0F,1.0F,1.0F,1.0F,1.0F});
    Result result = rosController.getLocation();
    HashMap<String,Double> axis = (HashMap<String,Double>) result.getData();
    assert result.getCode() == 100 && axis.get("xAxis") == 1.0 && axis.get("yAxis") == 1.0;
} 

/** 
* 
* Method: getGesture() 
* 
*/ 
@Test
public void testGetGesture() throws Exception { 
//TODO: Test goes here...

    new Publisher("/gesture_detect", MsgGlobal.msgFloat, rosBridge).publish(10);
    Result result = rosController.getGesture();
    assert result.getCode() == 100 && (int) result.getData() == 10;
} 

/** 
* 
* Method: getPower() 
* 
*/ 
@Test
public void testGetPower() throws Exception { 
//TODO: Test goes here...
    new Publisher("/power_detect", MsgGlobal.msgFloat, rosBridge).publish(10);
    Result result = rosController.getPower();
    assert result.getCode() == 100 && (int) result.getData() == 10;
} 


} 
