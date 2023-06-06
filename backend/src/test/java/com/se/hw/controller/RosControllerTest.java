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
import org.junit.experimental.theories.Theories;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * RosController Tester.
 *
 * @author 刘运淇
 * @version 1.0
 * @since <pre>5月 16, 2023</pre>
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

    private static int mapId;
    private static int point1Id;
    private static int point2Id;
    private static int point3Id;
    private static RosBridge rosBridge;
    private static final int waitMiliSecond = 10000;

    /*
    @BeforeAll
    public static void init() throws InterruptedException {
        mapController.save("map","#FFFFFF");
        List<Map> maps = (List<Map>) mapController.findAll().getData();
        mapId = maps.get(0).getId();

        List<Point> points = (List<Point>) pointController.findAll().getData();
        point1Id = points.get(0).getId();
        point2Id = points.get(1).getId();
        point3Id = points.get(2).getId();
        rosBridge = RosGlobal.rosBridge;
        RosGlobal.init("ws://10.193.151.216:9090");
        // Mapping
        Result result1 = rosController.change(1,mapId,point1Id);
        assert result1.getCode() == 200;
        // 30 s
        Thread.sleep(waitMiliSecond * 3);
        Result result2 = rosController.end();
        assert result2.getCode() == 200;
    }

    @AfterAll
    public static void cleanUp() {
        mapController.delete(mapId);
        pointController.delete(point1Id);
        pointController.delete(point2Id);
        pointController.delete(point3Id);
    }

     */

    @Before
    public void before() throws Exception {
        // RosGlobal.init("http://localhost:8080");
        // point = new Point(1,"point",1.0f,1.0f,0,mapId,1.0f,1.0f,1.0f,1.0f,1.0f);
        // point2 = new Point(2,"point2",2.0f,2.0f,0,mapId,1.0f,1.0f,1.0f,1.0f,1.0f);
//        mapController.save("map", "#FFFFFF");
//        List<Map> maps = (List<Map>) mapController.findAll().getData();
//        mapId = maps.get(0).getId();
//
//        List<Point> points = (List<Point>) pointController.findAll().getData();
//        point1Id = points.get(0).getId();
//        point2Id = points.get(1).getId();
//        point3Id = points.get(2).getId();

        RosGlobal.init("ws://10.193.151.216:9090");
        rosBridge = RosGlobal.rosBridge;
        RosController.nowMapId = 142;
        mapId = 142;
        point1Id = 13;
        /**
         * ying bin dian
         */
        point2Id = 18;
        /**
         * table
         */
        point3Id = 19;
    }

    @After
    public void after() throws Exception {
        // End current mode
        //mapController.delete(mapId);
        //pointController.delete(point1Id);
        //pointController.delete(point2Id);
        //pointController.delete(point3Id);
        rosController.end();
        Thread.sleep(waitMiliSecond);
    }

    @Test
    public void testMapping() throws Exception {
        // Mapping
        Result result1 = rosController.change(1, mapId);
        assert result1.getCode() == 200;
        // 60 s
        Thread.sleep(waitMiliSecond * 5);
        Result result12 = rosController.end();
        assert result12.getCode() == 200;
        //Thread.sleep(waitMiliSecond * 3);
        return;
    }

    /**
     * Method: change(@RequestParam Integer type, @RequestParam(value = "") String mapName)
     */
    @Test
    public void testChange() throws Exception {
//TODO: Test goes here...
        // Map not found 测试地图是否存在（后端）
        Result result4 = rosController.change(2, 0x3f3f3f3f);
        assert result4.getCode() == 405 && result4.getMsg().equals("the map doesn't exist");
        Thread.sleep(waitMiliSecond);

        // Welcome 开启迎宾模式（ROS）
        Result result5 = rosController.change(2, mapId);
        assert result5.getCode() == 200;
        Thread.sleep(waitMiliSecond);
        // Reopen Mapping 再次开启迎宾模式，后端应不予相应（后端）
        Result result2 = rosController.change(1, mapId);
        assert result2.getCode() == 404 && result2.getMsg().equals("Can't open the mode,please check if there's other mode opening");
        Thread.sleep(waitMiliSecond);
        // End welcome 关闭迎宾模式（ROS）
        Result result6 = rosController.end();
        assert result6.getCode() == 200;
        Thread.sleep(waitMiliSecond);

        // Delivery 开启送餐模式（ROS）
        Result result7 = rosController.change(3, mapId);
        assert result7.getCode() == 200;
        Thread.sleep(waitMiliSecond);
        // End delivery 关闭送餐模式（ROS）
        Result result8 = rosController.end();
        assert result8.getCode() == 200;
        Thread.sleep(waitMiliSecond);
        // PointEdit 开启航点编辑模式（ROS）
        Result result9 = rosController.change(4, mapId);
        assert result9.getCode() == 200;
        Thread.sleep(waitMiliSecond);

        // Point not found 测试航点无法找到（后端）
        Result result10 = rosController.change(1, mapId);
        assert result10.getCode() == 606 && result10.getMsg().equals("Can't find the point!");
    }

    /**
     * Method: end()
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

        // Begin welcome 启动迎宾模式（ROS）
        Result result1 = rosController.change(2, 142);
        assert result1.getCode() == 200;
        Thread.sleep(waitMiliSecond);
        // End welcome 关闭迎宾模式（ROS）
        Result result3 = rosController.end();
        Thread.sleep(waitMiliSecond);
        assert result3.getCode() == 200 && isEnd[0] == 1;
        Thread.sleep(waitMiliSecond);
        // ReEnd welcome 重新关闭迎宾模式（后端）
        isEnd[0] = 0;
        Result result2 = rosController.end();
        Thread.sleep(waitMiliSecond);
        assert result2.getCode() == 404 && result2.getMsg().equals("already ending all modes!") && isEnd[0] == 0;
    }

    /**
     * Method: confirmEat()
     */
    @Test
    public void testConfirmEat() throws Exception {
//TODO: Test goes here...
        final int[] isGuide = {0};
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/guidance_completed")
                        .setType(MsgGlobal.msgFloatArray)
                        .setThrottleRate(1)
                        .setQueueLength(1),
                (data, stringRep) -> isGuide[0] = 1
        );
    /*
    // Begin mapping
    Result result1 = rosController.change(1,mapId,point1Id);
    assert result1.getCode() == 200;
    Thread.sleep(waitMiliSecond);

    Result result2 = rosController.confirmEat();
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not welcome mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    Thread.sleep(waitMiliSecond);

     */
        // Welcome 开启迎宾模式（ROS）
        Result result4 = rosController.change(2, mapId);
        Thread.sleep(waitMiliSecond * 3);
        assert result4.getCode() == 200 && isGuide[0] == 1;

        System.out.println("here!");
        // 客人就餐（ROS）
        Result result5 = rosController.confirmEat();
        Thread.sleep(waitMiliSecond * 3);
        assert result5.getCode() == 200;
        //assert result5.getCode() == 200 && isGuide[0] == 1;
        //客人在 5 s内再次就餐，模拟前者带位未完成情况，后端不予处理（后端）
        // Result result7 = rosController.confirmEat();
        //assert result7.getCode() == 505 && result7.getMsg().equals("robots is guiding!");

        RosGlobal.arrive_welcome = true;

//    Result result6 = rosController.confirmEat();
//    assert result6.getCode() == 606 && result6.getMsg().equals("full!");

    }

    /**
     * Method: confirmSend(Point point)
     */
    @Test
    public void testConfirmSend() throws Exception {
//TODO: Test goes here...
        final int[] isGuide = {0, 0};
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/delivery")
                        .setType(MsgGlobal.msgFloatArray)
                        .setThrottleRate(1)
                        .setQueueLength(1),
                (data, stringRep) -> isGuide[0] = 1
        );
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/delivery_completed")
                        .setType(MsgGlobal.msgString)
                        .setThrottleRate(1)
                        .setQueueLength(1),
                (data, stringRep) -> isGuide[1] = 1
        );
    /*
    // Begin mapping
    Result result1 = rosController.change(1,mapId, point2Id);
    assert result1.getCode() == 200;
    Thread.sleep(waitMiliSecond);

    Result result2 = rosController.confirmSend(point2Id);
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not delivery mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    Thread.sleep(waitMiliSecond);

     */
        // Delivery 开启送餐模式（ROS）
        Result result4 = rosController.change(3, mapId);
        assert result4.getCode() == 200;
        Thread.sleep(waitMiliSecond * 2);
        // 命令机器人送往指定位置（ROS）
        Result result5 = rosController.confirmSend(point3Id);
        Thread.sleep(waitMiliSecond / 2);
        assert result5.getCode() == 200 && isGuide[0] == 1;
        // 再次命令机器人送往指定位置，此时机器人还未送完完整的餐，后端不予处理（后端）
        Result result6 = rosController.confirmSend(point3Id);
        assert result6.getCode() == 505 && result6.getMsg().equals("robots is sending!");
        Thread.sleep(waitMiliSecond * 2);
        RosGlobal.arrive_kitchen = true;
        // 顾客取完餐，机器人返程（ROS）
        Result result7 = rosController.confirmReceive();
        Thread.sleep(waitMiliSecond * 2);
        assert result7.getCode() == 200 && isGuide[1] == 1;
    /*
    // 航点的信息有误（后端）
    Result result7 = rosController.confirmSend(0x3f3f);
    assert result7.getCode() ==  400 && result7.getMsg().equals("can't find the point");
    */
    }

    /**
     * Method: confirmReceive()
     */
    @Test
    public void testConfirmReceive() throws Exception {
//TODO: Test goes here...

        final int[] isGuide = {0};
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/delivery_completed")
                        .setType(MsgGlobal.msgString)
                        .setThrottleRate(1)
                        .setQueueLength(1),
                (data, stringRep) -> isGuide[0] = 1
        );
    /*
    // Begin mapping
    Result result1 = rosController.change(1,mapId,point1Id);
    assert result1.getCode() == 200;
    Thread.sleep(waitMiliSecond);
    Result result2 = rosController.confirmReceive();
    assert result2.getCode() == 404 && result2.getMsg().equals("now is not delivery mode!");
    // End mapping
    Result result3 = rosController.end();
    assert result3.getCode() == 200;
    Thread.sleep(waitMiliSecond);

     */
        // Delivery 开启送餐模式
    /*
    Result result4 = rosController.change(3,mapId,point1Id);
    assert result4.getCode() == 200;
    Thread.sleep(waitMiliSecond);
    Result result5 = rosController.confirmReceive();
    Thread.sleep(waitMiliSecond);
    assert result5.getCode() == 200 && isGuide[0] == 1;

     */

    }

    /**
     * Method: savePoint(Integer mapId, String pointName)
     */
    @Test
    public void testSavePoint() throws Exception {
//TODO: Test goes here...
        // 开启航点编辑模式（ROS）
        rosController.change(4, mapId);
        System.out.println("save welcome！！！！！");
        Thread.sleep(waitMiliSecond * 3);
        // 建立正常点（ROS端）
        Result result = rosController.savePoint(mapId, "welcome1", 0);
       // rosController.savePoint(mapId, "kitchen", 1);
        assert result.getCode() == 200;
//

        System.out.println("save table！！！！！");
        Thread.sleep(waitMiliSecond * 3);
        //Thread.sleep(waitMiliSecond * 2);
        // 建立正常点（ROS端）
        Result result1 = rosController.savePoint(mapId, "table_test1", 2);
        assert result1.getCode() == 200;
        rosController.end();
        System.out.println("end the save_point!");
        // 建立异常点（后端）
//        Result result2 = rosController.savePoint(1000, "new_pointpoint");
//        assert result2.getCode() == 404 && result2.getMsg().equals("map doesn't exist");
        // TODO 405 error not test
    }

    /**
     * Method: getException()
     */
    @Test
    public void testGetException() throws Exception {
//TODO: Test goes here...
        Result result = rosController.getException();
        assert result.getCode() == 100;
        // 开启迎宾模式（ROS）
        Result result4 = rosController.change(2, mapId);
        assert result4.getCode() == 200;
        Thread.sleep(waitMiliSecond * 4);
        // 到迎宾点 超时
        Result result5 = rosController.getException();
        System.out.println(result5.getCode());
        assert result5.getCode() == 100;
        // 模拟超时点
        Result result1 = rosController.confirmEat();
        assert result1.getCode() == 200;
        Thread.sleep(waitMiliSecond * 4);
        Result result2 = rosController.getException();
        assert result2.getCode() == 500;
    }

    /**
     * Method: getLocation()
     */
    @Test
    public void testGetLocation() throws Exception {
//TODO: Test goes here...
        new Publisher("/get_pos", MsgGlobal.msgFloatArray, rosBridge).publish(new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F});
        Result result = rosController.getLocation();
        HashMap<String, Double> axis = (HashMap<String, Double>) result.getData();
        assert result.getCode() == 100 && axis.get("xAxis") == 1.0 && axis.get("yAxis") == 1.0;
    }

    /**
     * Method: getGesture()
     */
    @Test
    public void testGetGesture() throws Exception {
        //TODO: Test goes here...
        new Publisher("/gesture_detect", MsgGlobal.msgFloat, rosBridge).publish(new PrimitiveMsg<Double>(10.0));
        Result result = rosController.getGesture();
        assert result.getCode() == 100 && (Double) result.getData() == 10.0;
    }

    /**
     * Method: getPower()
     */
    @Test
    public void testGetPower() throws Exception {
//TODO: Test goes here...
        new Publisher("/power_detect", MsgGlobal.msgFloat, rosBridge).publish(new PrimitiveMsg<Integer>(10));
        Result result = rosController.getPower();
        assert result.getCode() == 100 && (int) result.getData() == 10;
    }

} 
