package com.se.hw.Ros;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.se.hw.common.TestUtil;
import com.se.hw.common.UploadImgUtil;
import com.se.hw.entity.Point;
import com.se.hw.exception.ExceptionRecv;
import com.se.hw.mode.*;
import javafx.scene.chart.ScatterChart;
import org.junit.Test;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class RosGlobal {

    public static RosBridge rosBridge;

    /**
     * MappingMood, WelcomeMood, DeliveryMood, PointEditMood
     */
    public static HashMap<Integer, Mode> modes;

    public static Mode nowMode;

    public static Point point;
    public static boolean launch_success = false;

    public static boolean arrive_welcome = false;
    public static boolean arrive_kitchen = false;

    public static String mapUrl = null;

    public static String nowMapName = "map_default";

    public static Boolean init(String url) {
        nowMode = null;
        rosBridge = new RosBridge();
        point = new Point();

        connect(url);
        if (!rosBridge.hasConnected()) {
            return false;
        }

        modes = new HashMap<>();
        modes.put(1, new MappingMode(rosBridge));
        modes.put(2, new WelcomeMode(rosBridge));
        modes.put(3, new DeliveryMode(rosBridge));
        modes.put(4, new PointEditMode(rosBridge));


        ExceptionRecv.run(rosBridge);
        /*
          init subscriber
         */
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/pos_pub")
                        .setType("std_msgs/Float64MultiArray")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        JSONObject json = JSONObject.parseObject(data.toString());
                        JSONArray array = json.getJSONObject("msg").getJSONArray("data");
                        Float[] floats = new Float[7];
                        for (int i = 0; i < 7; i++) {
                            floats[i] = array.getFloat(i);
                        }
                        //System.out.println("from ros:" + floats[0] + " " + floats[1]);
                        point = ros2front(floats);
                        // TestUtil.log("robot location: " + point);
                    }
                }
        );
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/enable_confirm")
                        .setType("std_msgs/String")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        launch_success = true;
                        MessageUnpacker<PrimitiveMsg<String>> unpacker = new MessageUnpacker<PrimitiveMsg<String>>(PrimitiveMsg.class);
                        PrimitiveMsg<String> msg = unpacker.unpackRosMessage(data);
                        TestUtil.log(msg.data);
                        endClock();
                    }
                }
        );
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/delivery_completed")
                        .setType("std_msgs/String")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        arrive_kitchen = true;
                        MessageUnpacker<PrimitiveMsg<String>> unpacker = new MessageUnpacker<PrimitiveMsg<String>>(PrimitiveMsg.class);
                        PrimitiveMsg<String> msg = unpacker.unpackRosMessage(data);
                        TestUtil.log(msg.data);
                        endClock();
                    }
                }
        );
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/guidance_completed")
                        .setType("std_msgs/String")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        arrive_welcome = true;
                        MessageUnpacker<PrimitiveMsg<String>> unpacker = new MessageUnpacker<PrimitiveMsg<String>>(PrimitiveMsg.class);
                        PrimitiveMsg<String> msg = unpacker.unpackRosMessage(data);
                        TestUtil.log("arrive welcome " + msg.data);
                        TestUtil.log(point.toString());
                        endClock();
                    }
                }
        );
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/base64_pub")
                        .setType("std_msgs/String")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        MessageUnpacker<PrimitiveMsg<String>> unpacker = new MessageUnpacker<PrimitiveMsg<String>>(PrimitiveMsg.class);
                        PrimitiveMsg<String> msg = unpacker.unpackRosMessage(data);
                        UploadImgUtil.save(msg.data, nowMapName);
                        mapUrl = nowMapName + ".png";
                        //TestUtil.log("地图已更新 " + mapUrl);
                    }
                }
        );
        return true;
    }


    /**
     * @param url 对ros端连接的方法进行封装，
     *            如果3秒钟连接不上则直接退出方法
     */
    public static void connect(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Void> task = () -> {
            rosBridge.connect(url, true);
            return null;
        };

        Future<Void> future = executor.submit(task);
        try {
            // 如果在3秒内没有返回值，则取消任务
            future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            // 如果超时或者出现异常，则取消任务
            future.cancel(true);
        }
        executor.shutdown();
    }

    /**
     * 前端在init之后不停调用该接口，以获取当前是否存在异常，若无任何异常，则所有布尔值都为 TRUE ；反之，相对应布尔值为 FALSE
     *
     * @return An array of length 3, represents exception status;
     * state[0] represents gesture, state[1] represents power, state[2] represents timeout
     */
    public static boolean[] getException() {
        boolean[] state = new boolean[3];
        state[0] = ExceptionRecv.getGestureState();
        state[1] = ExceptionRecv.getPowerState();
        state[2] = ExceptionRecv.isTimeoutN();
        // TODO insert other exception status
        return state;
    }

    /**
     * 私有方法，只能 ROS 端发出的消息重置计时器
     */
    private static void endClock() {
        ExceptionRecv.resetStartTime();
    }

    /**
     * 公有方法，前端发出需要导航的指令时，需要开启计时器
     */
    public static void startClock() {
        ExceptionRecv.setStartTime();
    }

    /**
     * 公有方法，前端和ros端坐标转换，地图大小默认400像素x400像素，对应实际大小20mx20m
     * 前端以左上角为坐标原点，单位是像素，ros端以图片中心为坐标原点，单位是m
     * e.g.
     * 前端(0,0) --- ros(-10,10) 左上角
     * 前端(200,200) --- ros(0,0) 中心
     * 前端(0,200) --- ros(-10,-10) 左下角
     */
    public static Float[] front2ros(Point point) {
        Float[] floats = new Float[7];
        Float x, y;
        x = point.getXAxis() / 20 - 10;
        y = -point.getYAxis() / 20 + 10;
        floats[0] = x;
        floats[1] = y;
        floats[2] = point.getZAxis();
        floats[3] = point.getOriX();
        floats[4] = point.getOriY();
        floats[5] = point.getOriZ();
        floats[6] = point.getOriW();
        return floats;
    }

    /**
     * 公有方法，上面方法的逆运算
     */
    public static Point ros2front(Float[] floats) {
        Float x = floats[0], y = floats[1];
        Point point = new Point();
        point.setName("CURRENT_POSITION");
        point.setXAxis(20 * (x + 10));
        point.setYAxis(-20 * (y - 10));
        point.setZAxis(floats[2]);
        point.setOriX(floats[3]);
        point.setOriY(floats[4]);
        point.setOriZ(floats[5]);
        point.setOriW(floats[6]);
        return point;
    }
}
