package com.se.hw.mood;

import ros.Publisher;
import ros.RosBridge;
import ros.msgs.geometry_msgs.Vector3;
import ros.msgs.std_msgs.PrimitiveMsg;

public class WelcomeMood extends Mood{
    // 三维坐标
    private double pos_x,pos_y,pos_z;
    private Vector3 pos;
    // 四元组表示朝向
    private double ori_x,ori_y,ori_z,ori_w;
    private static final String START_WELCOME_TOPIC = "enable_welcome";


    public WelcomeMood(RosBridge rosBridge) {
        super(rosBridge);
        // default value
        pos_x = pos_y = pos_z = 0.0;
        pos = new Vector3();
        ori_w = ori_x = ori_y = ori_z = 0.0;
        addPublisher(START_WELCOME_TOPIC,STD_MSG);
        addPublisher(END_TOPIC,STD_MSG);
    }

    @Override
    public int start() {
        Publisher startMapping = getPublisher(START_WELCOME_TOPIC);
        // begin welcome mood
        if (startMapping != null) {
            startMapping.publish(new PrimitiveMsg<Vector3>(pos));
        } else {
            return -1;
        }

        return 0;
    }

    @Override
    public int end() {
        Publisher startMapping = getPublisher(END_TOPIC);
        // end welcome mood
        if (startMapping != null) {
            startMapping.publish(new PrimitiveMsg<Vector3>(pos));
        } else {
            return -1;
        }
        return 0;
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(double pos_x, double pos_y, double pos_z) {
        pos = new Vector3(pos_x,pos_y,pos_z);
    }

    public double getPos_x() {
        return pos_x;
    }

    public void setPos_x(double pos_x) {
        this.pos_x = pos_x;
    }

    public double getPos_y() {
        return pos_y;
    }

    public void setPos_y(double pos_y) {
        this.pos_y = pos_y;
    }

    public double getPos_z() {
        return pos_z;
    }

    public void setPos_z(double pos_z) {
        this.pos_z = pos_z;
    }

    public double getOri_x() {
        return ori_x;
    }

    public void setOri_x(double ori_x) {
        this.ori_x = ori_x;
    }

    public double getOri_y() {
        return ori_y;
    }

    public void setOri_y(double ori_y) {
        this.ori_y = ori_y;
    }

    public double getOri_z() {
        return ori_z;
    }

    public void setOri_z(double ori_z) {
        this.ori_z = ori_z;
    }

    public double getOri_w() {
        return ori_w;
    }

    public void setOri_w(double ori_w) {
        this.ori_w = ori_w;
    }
}
