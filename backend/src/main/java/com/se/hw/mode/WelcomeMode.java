package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.entity.Point;
import ros.Publisher;
import ros.RosBridge;
import ros.msgs.geometry_msgs.Vector3;
import ros.msgs.std_msgs.PrimitiveMsg;

public class WelcomeMode extends Mode {
    private static final String START_WELCOME_TOPIC = "/enable_welcome";
    private static final String START_GUIDE = "/guidance";


    public WelcomeMode(RosBridge rosBridge) {
        super(rosBridge);
        addPublisher(START_WELCOME_TOPIC, MsgGlobal.msgString);
        addPublisher(START_GUIDE, MsgGlobal.msgFloatArray);
    }

    @Override
    public int start() {
        if (RosGlobal.nowMode != null) {
            return -1;
        }
        for (int i = 0; i < 10; i++) {
            point2arr(point);
            getPublisher(START_WELCOME_TOPIC).publish(new PrimitiveMsg<Float[]>(point2arr(point)));
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (RosGlobal.launch_success) {
                RosGlobal.nowMode = this;
                RosGlobal.arrive_welcome = true;
                return 1;
            }
        }
        return -2;
    }

    public void startGuide(Point point) {
        getPublisher(START_GUIDE).publish(point2arr(point));
    }
}
