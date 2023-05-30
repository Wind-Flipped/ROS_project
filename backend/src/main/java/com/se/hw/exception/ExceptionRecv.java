package com.se.hw.exception;

import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.fasterxml.jackson.databind.JsonNode;
import com.se.hw.Ros.MsgGlobal;
import com.se.hw.common.TestUtil;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;

import java.util.HashMap;

/**
 * 异常处理类，接收异常消息并处理，调用类中方法可获取当前是否存在异常
 * rad(表示与z轴倾角)
 * percent(表示电量百分比)
 * startTime(表示开始出发的时间，单位为毫秒)
 * 安全偏离角度 SAFE_RAD = π / 8 ~~ 22.5°
 * 安全电量 SAFE_POWER = 15.0 ~~ 15%
 * 最长出发时间 LONGEST_TIME = 30 * 1000 ~~ 30 秒
 */
public class ExceptionRecv {
    private static final HashMap<String, Publisher> publishers = new HashMap<>();
    private static final String GET_GESTURE = "/gesture_detect";
    private static final String GET_POWER = "/power_detect";
    private static RosBridge rosBridge = null;
    private static final double SAFE_RAD = 3.14 / 8;
    private static final Integer SAFE_POWER = 15;
    private static final long LONGEST_TIME = 30 * 1000;
    private static double rad = 0.0;
    private static int power = 100;
    private static long startTime = 0L;

    public static int run(RosBridge rosBridge2) {
        rosBridge = rosBridge2;
        // 当ROS端异常出现时，下面的TODO则表明需要向ROS端发送的对应异常处理消息，默认ROS端无反应，建议可以增加一个语音播报异常的话题。
        runGesture();
        runPower();
        return 0;
    }

    private static void runGesture() {
        rosBridge.subscribe(SubscriptionRequestMsg.generate(GET_GESTURE)
                        .setType(MsgGlobal.msgFloat)
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        MessageUnpacker<PrimitiveMsg<Float>> unpacker = new MessageUnpacker<PrimitiveMsg<Float>>(PrimitiveMsg.class);
                        PrimitiveMsg<Float> msg = unpacker.unpackRosMessage(data);
                        rad = msg.data;
                        TestUtil.log("getsture: " + rad);
                        if (!getGestureState()) {
                            // TODO
                        }
                    }
                }
        );
    }

    private static void runPower() {
        rosBridge.subscribe(SubscriptionRequestMsg.generate(GET_POWER)
                        .setType(MsgGlobal.msgInteger)
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        MessageUnpacker<PrimitiveMsg<Integer>> unpacker = new MessageUnpacker<PrimitiveMsg<Integer>>(PrimitiveMsg.class);
                        PrimitiveMsg<Integer> msg = unpacker.unpackRosMessage(data);
                        power = msg.data;
                        //TestUtil.log("power: " + power);
                        if (!getPowerState()) {
                            // TODO
                        }
                    }
                }
        );
    }

    /**
     * Get the gesture state
     *
     * @return true if gesture is safe
     */
    public static boolean getGestureState() {
        return rad < SAFE_RAD;
    }


    public static Integer getPower() {
        return power;
    }

    public static Double getGesture() {
        return rad;
    }

    /**
     * Get the power state
     *
     * @return true if power is safe
     */
    public static boolean getPowerState() {
        return power >= SAFE_POWER;
    }

    /**
     * 到达指定位置等候指令时，不计时，重置 startTime 为 0
     */
    public static void resetStartTime() {
        startTime = 0L;
    }

    /**
     * 开始导航时，计时，存储开始导航时间
     */
    public static void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    /**
     * 获取机器人当前导航是否已超时，startTime 为 0 时，不计为超时。
     *
     * @return false if is timeout
     */
    public static boolean isTimeoutN() {
        if (startTime == 0) {
            return true;
        } else {
            return (System.currentTimeMillis() - startTime) < LONGEST_TIME;
        }
    }
}
