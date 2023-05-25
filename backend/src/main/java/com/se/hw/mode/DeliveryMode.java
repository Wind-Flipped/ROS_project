package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.entity.Point;
import ros.RosBridge;

public class DeliveryMode extends Mode {

    private static final String START_DELIVERY_TOPIC = "/enable_delivery";
    private static final String START_SEND = "/delivery";

    private static final String START_RECEIVE = "/delivery_confirm";

    private Point kitchen_point;

    public DeliveryMode(RosBridge rosBridge) {
        super(rosBridge);
        kitchen_point = new Point();
        addPublisher(START_DELIVERY_TOPIC, MsgGlobal.msgFloatArray);
        addPublisher(START_SEND, MsgGlobal.msgFloatArray);
        addPublisher(START_RECEIVE, MsgGlobal.msgString);
    }

    public void setPoint(Point point) {
        this.kitchen_point = point;
    }

    @Override
    public int start() {
        if (RosGlobal.nowMode != null) {
            return -1;
        }
        RosGlobal.nowMode = this;
        /*
          这只是一个思路，还需修改完善
         */
        int i;
        for (i = 0; i < 5; i++) {
            getPublisher(START_DELIVERY_TOPIC).publish("launch the delivery!");
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

    public void startSend(Point point) {
        Float[] floats = new Float[10];
        floats[0] = point.getXAxis().floatValue();
        floats[1] = point.getYAxis().floatValue();
        floats[2] = point.getZAxis().floatValue();
        floats[3] = point.getOriX().floatValue();
        floats[4] = point.getOriY().floatValue();
        floats[5] = point.getOriZ().floatValue();
        floats[6] = point.getOriW().floatValue();
        getPublisher(START_SEND).publish(point);
    }

    public void startReceive() {
        getPublisher(START_RECEIVE).publish("the dishes is already picked up!");
    }
}
