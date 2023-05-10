package com.se.hw.Ros;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.se.hw.mood.MappingMood;
import com.se.hw.mood.Mood;
import com.se.hw.mood.WelcomeMood;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;

import java.util.ArrayList;
import java.util.List;

public class RosGlobal {

    private static RosBridge rosBridge;
    /**
     * MappingMood, WelcomeMood, DeliveryMood, PointEditMood
     */
    private static List<Mood> moods;


    public static void init(String url) {
        rosBridge = new RosBridge();
        rosBridge.connect(url, true);
        moods = new ArrayList<>(4);
        moods.add(new MappingMood(rosBridge));
        moods.add(new WelcomeMood(rosBridge));
        // TODO add other moods
    }

    public static int startMapping(String mapName) {
        Mood mappingMood = moods.get(0);
        ((MappingMood) mappingMood).setMapName(mapName);
        return mappingMood.start();
    }

    public static int endMapping() {
        Mood mappingMood = moods.get(0);
        return mappingMood.end();
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
