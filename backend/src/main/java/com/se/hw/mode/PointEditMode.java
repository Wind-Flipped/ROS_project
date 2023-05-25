package com.se.hw.mode;

import com.se.hw.Ros.MsgGlobal;
import com.se.hw.Ros.RosGlobal;
import com.se.hw.entity.Point;
import ros.RosBridge;

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
        RosGlobal.nowMode = this;
        int i;
        for (i = 0; i < 10; i++) {
            getPublisher(START_POINTEDIT_TOPIC).publish("launch the point_edit!");
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

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
