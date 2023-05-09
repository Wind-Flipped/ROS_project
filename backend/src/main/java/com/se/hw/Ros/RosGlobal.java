package com.se.hw.Ros;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;

public class RosGlobal {

    public static RosBridge bridge;

    public static double xAxis;
    public static double yAxis;


    public static double xAxis2;
    public static double yAxis2;

    public static Publisher pub1;

    public static void init(String url) {
        bridge = new RosBridge();
        bridge.connect(url, true);

        pub1 = new Publisher("/javaChatter", "std_msgs/String", bridge);
        bridge.subscribe(SubscriptionRequestMsg.generate("/pos_pub")
                        .setType("std_msgs/Float64MultiArray")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                      //  System.out.println(data);
                        JSONObject json = JSONObject.parseObject(data.toString());
                        JSONArray axis = json.getJSONObject("msg").getJSONArray("data");
                        xAxis = axis.getDoubleValue(0);
                        yAxis = axis.getDoubleValue(1);
                       // System.out.println(data);
                    }
                }
        );

        bridge.subscribe(SubscriptionRequestMsg.generate("/pos_pub2")
                        .setType("std_msgs/Float64MultiArray")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        //  System.out.println(data);
                        JSONObject json = JSONObject.parseObject(data.toString());
                        JSONArray axis = json.getJSONObject("msg").getJSONArray("data");
                        xAxis2 = axis.getDoubleValue(0);
                        yAxis2 = axis.getDoubleValue(1);
                        // System.out.println(data);
                    }
                }
        );

    }
}
