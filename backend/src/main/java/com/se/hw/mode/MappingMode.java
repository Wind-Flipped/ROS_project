package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;

/**
 * Mapping mode,
 * needs mapName to start.
 */
public class MappingMode extends Mode {

    private static final String START_MAPPING_TOPIC = "/enable_gmapping";

    public MappingMode(RosBridge rosBridge) {
        super(rosBridge);
        addPublisher(START_MAPPING_TOPIC, MsgGlobal.msgString);
    }

    @Override
    public int start() {
        Publisher startMapping = getPublisher(START_MAPPING_TOPIC);
        // begin mapping mood
        if (RosGlobal.nowMode != null) {
            return -1;
        }
        int i;
        for (i = 0; i < 5; i++) {
            getPublisher(START_MAPPING_TOPIC).publish(mapName);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (RosGlobal.launch_success) {
                return 1;
            }
        }
        return -1;
    }

}
