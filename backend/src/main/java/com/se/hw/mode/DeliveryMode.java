package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.entity.Point;
import ros.RosBridge;
import ros.msgs.std_msgs.PrimitiveMsg;

public class DeliveryMode extends Mode {

    private static final String START_DELIVERY_TOPIC = "/enable_delivery";
    private static final String START_SEND = "/delivery";

    private static final String START_RECEIVE = "/delivery_confirm";


    public DeliveryMode(RosBridge rosBridge) {
        super(rosBridge);
        addPublisher(START_DELIVERY_TOPIC, MsgGlobal.msgFloatArray);
        addPublisher(START_SEND, MsgGlobal.msgFloatArray);
        addPublisher(START_RECEIVE, MsgGlobal.msgString);
    }

    @Override
    public int start() {
        if (RosGlobal.nowMode != null) {
            return -1;
        }
        /*
          这只是一个思路，还需修改完善
         */
        int i;
        for (i = 0; i < 5; i++) {
            getPublisher(START_DELIVERY_TOPIC).publish(new PrimitiveMsg<Float[]>(point2arr(point)));
            getPublisher(START_DELIVERY_TOPIC).publish(new PrimitiveMsg<Float[]>(point2arr(point)));
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (RosGlobal.launch_success) {
                RosGlobal.nowMode = this;
                RosGlobal.arrive_kitchen = true;
                return 1;
            }
        }
        return -2;
    }

    public void startSend(Point point) {
        getPublisher(START_SEND).publish(new PrimitiveMsg<Float[]>(point2arr(point)));
    }

    public void startReceive() {
        getPublisher(START_RECEIVE).publish(new PrimitiveMsg<String>("the dishes is already picked up!"));
    }
}
