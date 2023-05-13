package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

import java.util.HashMap;

/**
 * Abstract class Mode
 * contains rosBridge and publisher nodes with topics.
 * We assume that each publish message node corresponds to a unique topic,
 * which we use a hash map to ensure
 */
public abstract class Mode {
    private final HashMap<String, Publisher> publishers;
    private final RosBridge rosBridge;
    protected static final String END_TOPIC = "/disable";

    protected String mapName;

    public Mode(RosBridge rosBridge) {
        publishers = new HashMap<>();
        this.rosBridge = rosBridge;
        publishers.put(END_TOPIC, new Publisher(END_TOPIC, MsgGlobal.msgString, rosBridge));
        mapName = "map";
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return mapName;
    }

    /**
     * Add a node with given topic and message type
     *
     * @param topic   message topic
     * @param msgType message type
     * @return 1 if success, -1 if duplicate
     */
    protected int addPublisher(String topic, String msgType) {
        if (publishers.containsKey(topic)) {
            return -1;
        } else {
            publishers.put(topic, new Publisher(topic, msgType, rosBridge));
            return 1;
        }
    }

    /**
     * Get the corresponding Publisher object according to the given topic
     *
     * @param topic message topic
     * @return the Publisher object if success, null if not found
     */
    protected Publisher getPublisher(String topic) {
        return publishers.getOrDefault(topic, null);
    }

    /**
     * Start the current mood
     *
     * @return status code, return 0 if success, else return -1
     */
    public abstract int start();

    /**
     * End the current mood
     *
     * @return status code, return 0 if success, else return -1
     */
    public int end() {
        if (RosGlobal.nowMode == null) {
            return -1;
        }
        getPublisher(END_TOPIC).publish("End the mode!");
        RosGlobal.nowMode = null;
        return 1;
    }
}
