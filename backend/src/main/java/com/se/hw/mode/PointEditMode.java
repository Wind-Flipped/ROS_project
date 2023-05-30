package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.entity.Point;
import ros.RosBridge;
import ros.msgs.std_msgs.PrimitiveMsg;

public class PointEditMode extends Mode {

    private Point point;

    private static final String START_POINTEDIT_TOPIC = "/enable_pointsedit";


    public PointEditMode(RosBridge rosBridge) {
        super(rosBridge);
        point = new Point();
        addPublisher(START_POINTEDIT_TOPIC, MsgGlobal.msgString);
    }

    @Override
    public int start() {
        if (RosGlobal.nowMode != null) {
            return -1;
        }
        for (int i = 0; i < 5; i++) {
            getPublisher(START_POINTEDIT_TOPIC).publish(new PrimitiveMsg<String>("launch the point_edit!"));
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

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
