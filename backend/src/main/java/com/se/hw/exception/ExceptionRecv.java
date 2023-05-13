package com.se.hw.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.se.hw.Ros.MsgGlobal;
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
 * 安全偏离角度 SAFE_RAD = π / 8 ~ 22.5°
 * 安全电量 SAFE_POWER = 15.0 ~ 15%
 */
public class ExceptionRecv {
    private static final HashMap<String,Publisher> publishers = new HashMap<>();
    private static final String GET_GESTURE = "/gesture_detect";
    private static final String GET_POWER = "/power_detect";
    private static RosBridge rosBridge = null;
    private static final double SAFE_RAD = 3.14 / 8;
    private static final double SAFE_POWER = 15.0;
    private static double rad = 0.0;
    private static double power = 100.0;

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
                        MessageUnpacker<PrimitiveMsg<Double>> unpacker = new MessageUnpacker<PrimitiveMsg<Double>>(PrimitiveMsg.class);
                        PrimitiveMsg<Double> msg = unpacker.unpackRosMessage(data);
                        rad = msg.data;
                        if (!getGestureState()) {
                            // TODO
                        }
                    }
                }
        );
    }

    private static void runPower() {
        rosBridge.subscribe(SubscriptionRequestMsg.generate(GET_POWER)
                        .setType(MsgGlobal.msgFloat)
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {
                    @Override
                    public void receive(JsonNode data, String stringRep) {
                        MessageUnpacker<PrimitiveMsg<Double>> unpacker = new MessageUnpacker<PrimitiveMsg<Double>>(PrimitiveMsg.class);
                        PrimitiveMsg<Double> msg = unpacker.unpackRosMessage(data);
                        power = msg.data;
                        if (!getPowerState()) {
                            // TODO
                        }
                    }
                }
        );
    }

    /**
     * Get the gesture state
     * @return true if gesture is safe
     */
    public static boolean getGestureState() {
        return rad < SAFE_RAD;
    }

    /**
     * Get the power state
     * @return true if power is safe
     */
    public static boolean getPowerState() {
        return power >= SAFE_POWER;
    }
}
