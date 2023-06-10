package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.entity.Point;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;

import java.util.HashMap;
import java.util.PrimitiveIterator;

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

    protected static final String SETMAP_TOPIC = "/setmap";

    protected static final Integer WAIT_TIME = 8000;

    protected String mapName;

    protected Point point;

    public Mode(RosBridge rosBridge) {
        publishers = new HashMap<>();
        this.rosBridge = rosBridge;
        publishers.put(END_TOPIC, new Publisher(END_TOPIC, MsgGlobal.msgString, rosBridge));
        publishers.put(SETMAP_TOPIC, new Publisher(SETMAP_TOPIC, MsgGlobal.msgString, rosBridge));
        mapName = "map";
        point = new Point();
    }

    public void setMapName(String mapName) {
        publishers.get(SETMAP_TOPIC).publish(new PrimitiveMsg<String>(mapName));
        this.mapName = mapName;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    protected Float[] point2arr(Point point) {
        return RosGlobal.front2ros(point);
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
     * Start the current mode
     *
     * @return status code, return 1 if success, return -1 if now is other mode, return -2 if connection error
     */
    public abstract int start();

    /**
     * End the current mode
     *
     * @return status code, return 1 if success, else return -1
     */
    public int end() {
        if (RosGlobal.nowMode == null) {
            return -1;
        }
        getPublisher(END_TOPIC).publish("End the mode!");
        try {
            Thread.sleep(WAIT_TIME / 2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        getPublisher(END_TOPIC).publish(new PrimitiveMsg<String>("End the mode!"));
        RosGlobal.nowMode = null;
        RosGlobal.launch_success = false;
        return 1;
    }
}
