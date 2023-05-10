package com.se.hw.mood;

import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

import java.util.HashMap;

/**
 * Abstract class Mood
 * contains rosBridge and publisher nodes with topics.
 * We assume that each publish message node corresponds to a unique topic,
 * which we use a hash map to ensure
 */
public abstract class Mood {
    private final HashMap<String,Publisher> publishers;
    private final RosBridge rosBridge;
    protected static final String STD_MSG = "std_msgs/String";
    protected static final String END_TOPIC = "disable";
    protected static final String FLOAT_MSG = "std_msgs/Float64MultiArray";

    public Mood(RosBridge rosBridge) {
        publishers = new HashMap<>();
        this.rosBridge = rosBridge;
    }

    /**
     * Add a node with given topic and message type
     * @param topic message topic
     * @param msgType message type
     * @return 1 if success, -1 if duplicate
     */
    protected int addPublisher(String topic,String msgType) {
        if (publishers.containsKey(topic)) {
            return -1;
        } else {
            publishers.put(topic,new Publisher(topic,msgType,rosBridge));
            return 1;
        }
    }

    /**
     * Get the corresponding Publisher object according to the given topic
     * @param topic message topic
     * @return the Publisher object if success, null if not found
     */
    protected Publisher getPublisher(String topic) {
        return publishers.getOrDefault(topic,null);
    }

    /**
     * Start the current mood
     * @return status code, return 0 if success, else return -1
     */
    public abstract int start();

    /**
     * End the current mood
     * @return status code, return 0 if success, else return -1
     */
    public abstract int end();
}
