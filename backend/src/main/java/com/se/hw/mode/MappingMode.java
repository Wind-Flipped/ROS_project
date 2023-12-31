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
        // begin mapping mood
        if (RosGlobal.nowMode != null) {
            return -1;
        }
        System.out.println(mapName);
        for (int i = 0; i < 5; i++) {
            getPublisher(START_MAPPING_TOPIC).publish(new PrimitiveMsg<String>(mapName));
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (RosGlobal.launch_success) {
                RosGlobal.nowMode = this;
                return 1;
            }
        }
        return -2;
    }

}
