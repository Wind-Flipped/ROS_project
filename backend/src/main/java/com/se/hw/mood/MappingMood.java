package com.se.hw.mood;

import com.sun.istack.internal.NotNull;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;

/**
 * Mapping mood,
 * needs mapName to start.
 */
public class MappingMood extends Mood {
    @NotNull
    private String mapName;
    private static final String START_MAPPING_TOPIC = "enable_gmapping";

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public MappingMood(RosBridge rosBridge) {
        super(rosBridge);
        // default value
        mapName = "map";
        addPublisher(START_MAPPING_TOPIC,FLOAT_MSG);
        addPublisher(END_TOPIC,FLOAT_MSG);
    }

    @Override
    public int start() {
        Publisher startMapping = getPublisher(START_MAPPING_TOPIC);
        // begin mapping mood
        if (startMapping != null) {
            startMapping.publish(new PrimitiveMsg<String>(mapName));
        } else {
            return -1;
        }

        return 0;
    }

    @Override
    public int end() {
        Publisher startMapping = getPublisher(END_TOPIC);
        // end mapping mood
        if (startMapping != null) {
            startMapping.publish(new PrimitiveMsg<String>());
        } else {
            return -1;
        }
        return 0;
    }
}
