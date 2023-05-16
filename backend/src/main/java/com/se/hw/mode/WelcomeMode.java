package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.entity.Point;
import ros.Publisher;
import ros.RosBridge;
import ros.msgs.geometry_msgs.Vector3;
import ros.msgs.std_msgs.PrimitiveMsg;

public class WelcomeMode extends Mode {

    private Point welcome_point;
    private static final String START_WELCOME_TOPIC = "/enable_welcome";
    private static final String START_GUIDE = "/guidance";


    public WelcomeMode(RosBridge rosBridge) {
        super(rosBridge);
        // default value
        welcome_point = new Point();
        addPublisher(START_WELCOME_TOPIC, MsgGlobal.msgString);
        addPublisher(START_GUIDE, MsgGlobal.msgFloatArray);
    }

    @Override
    public int start() {
        if (RosGlobal.nowMode != null) {
            return -1;
        }
        RosGlobal.nowMode = this;
        getPublisher(START_WELCOME_TOPIC).publish(welcome_point);
        return 1;
    }

    public Point getPoint() {
        return welcome_point;
    }

    public void setPoint(Point point) {
        this.welcome_point = point;
    }

    public void startGuide(Point point) {
        Float[] floats = new Float[10];
        floats[0] = point.getXAxis().floatValue();
        floats[1] = point.getYAxis().floatValue();
        floats[2] = point.getZAxis().floatValue();
        floats[3] = point.getOriX().floatValue();
        floats[4] = point.getOriY().floatValue();
        floats[5] = point.getOriZ().floatValue();
        floats[6] = point.getOriW().floatValue();
        getPublisher(START_GUIDE).publish(floats);
    }
}
