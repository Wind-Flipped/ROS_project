package com.se.hw.Ros;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.se.hw.entity.Point;
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
    public static Publisher pub_mapNameUpdate;

    public static boolean arrive_kitchen = false;
    public static boolean arrive_welcome = false;

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
        // TODO add other moods

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
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/gesture_detect")
                        .setType("std_msgs/Float64")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        MessageUnpacker<PrimitiveMsg<Double>> unpacker = new MessageUnpacker<PrimitiveMsg<Double>>(PrimitiveMsg.class);
                        PrimitiveMsg<Double> msg = unpacker.unpackRosMessage(data);
                        rad = msg.data;
                    }
                }
        );
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/power_detect")
                        .setType("std_msgs/Float64")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        MessageUnpacker<PrimitiveMsg<Double>> unpacker = new MessageUnpacker<PrimitiveMsg<Double>>(PrimitiveMsg.class);
                        PrimitiveMsg<Double> msg = unpacker.unpackRosMessage(data);
                        power = msg.data;
                    }
                }
        );
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/arrive_kitchen")
                        .setType("std_msgs/String")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        arrive_kitchen = true;
                    }
                }
        );
        rosBridge.subscribe(SubscriptionRequestMsg.generate("/arrive_welcome")
                        .setType("std_msgs/String")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        arrive_welcome = true;
                    }
                }
        );
        pub_mapNameUpdate = new Publisher("/rename_map", MsgGlobal.msgString, rosBridge);
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
