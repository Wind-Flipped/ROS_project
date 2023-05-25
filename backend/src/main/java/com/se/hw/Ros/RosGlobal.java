package com.se.hw.Ros;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.se.hw.common.UploadImgUtil;
import com.se.hw.entity.Point;
import com.se.hw.exception.ExceptionRecv;
import com.se.hw.mode.*;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RosGlobal {

    public static RosBridge rosBridge;

    /**
     * MappingMood, WelcomeMood, DeliveryMood, PointEditMood
     */
    public static HashMap<Integer, Mode> modes;

    public static Mode nowMode;
    public static Point point;
    public static double rad = 0;
    public static double power = 0;
    public static boolean launch_success = false;

    public static boolean arrive_welcome = false;
    public static boolean arrive_kitchen = false;

    public static String mapUrl = "https://img1.imgtp.com/2023/05/25/7Gu6ool3.jpg";

    public static void init(String url) {
        nowMode = null;
        rosBridge = new RosBridge();
        point = new Point();
        rosBridge.connect(url, true);
        modes = new HashMap<>();
        modes.put(1, new MappingMode(rosBridge));
        modes.put(2, new WelcomeMode(rosBridge));
        modes.put(3, new DeliveryMode(rosBridge));
        modes.put(4, new PointEditMode(rosBridge));

        ExceptionRecv.run(rosBridge);
        /*
          init subscriber
         */
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/get_pos")
                        .setType("std_msgs/Float64MultiArray")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        JSONObject json = JSONObject.parseObject(data.toString());
                        JSONArray array = json.getJSONObject("msg").getJSONArray("data");
                        point.setXAxis(array.getDoubleValue(0));
                        point.setYAxis(array.getDoubleValue(1));
                        point.setZAxis(array.getDoubleValue(2));
                        point.setOriX(array.getDoubleValue(3));
                        point.setOriY(array.getDoubleValue(4));
                        point.setOriZ(array.getDoubleValue(5));
                        point.setOriW(array.getDoubleValue(6));

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
                        mapUrl = UploadImgUtil.post(msg.data);
                    }
                }
        );
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



    /*
    public static void test() {

        Publisher pub1 = new Publisher("/javaChatter", "std_msgs/String", rosBridge);
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/pos_pub")
                        .setType("std_msgs/Float64MultiArray")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        //  System.out.println(data);
                        JSONObject json = JSONObject.parseObject(data.toString());
                        JSONArray axis = json.getJSONObject("msg").getJSONArray("data");
                        double xAxis = axis.getDoubleValue(0);
                        double yAxis = axis.getDoubleValue(1);
                        // System.out.println(data);
                    }
                }
        );

        rosBridge.subscribe(SubscriptionRequestMsg.generate("/pos_pub2")
                        .setType("std_msgs/Float64MultiArray")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        //  System.out.println(data);
                        JSONObject json = JSONObject.parseObject(data.toString());
                        JSONArray axis = json.getJSONObject("msg").getJSONArray("data");
                        double xAxis2 = axis.getDoubleValue(0);
                        double yAxis2 = axis.getDoubleValue(1);
                        // System.out.println(data);
                    }
                }
        );
    }
     */
}
