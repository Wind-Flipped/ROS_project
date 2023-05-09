package com.se.hw.controller;

import com.se.hw.Ros.RosGlobal;
import com.se.hw.common.Result;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.util.Pair;
import org.springframework.web.bind.annotation.*;
import ros.RosBridge;
import ros.msgs.std_msgs.PrimitiveMsg;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ros")
public class RosController {

    @PostMapping("/changeMode")
    public Result change(@RequestParam Integer op) {
        if (op == 1) {
            /*
            TODO
            create map
             */
            return Result.success(200);
        } else if (op == 2) {
            /*
            TODO
            greet guests
             */
            return Result.success(200);
        } else if (op == 3) {
            /*
            TODO
            deliver food
             */
            return Result.success(200);
        }
        return Result.error(500, "changeMode error!");
    }

    @GetMapping("/getAxis")
    public Result getLocation() {
        List<Double> list = new ArrayList<>();
        list.add(RosGlobal.xAxis);
        list.add(RosGlobal.yAxis);
        list.add(RosGlobal.xAxis2);
        list.add(RosGlobal.yAxis2);
        return Result.success(100, list);
    }

    @PostMapping("/send")
    public Result send() {
        RosGlobal.pub1.publish(new PrimitiveMsg<String>("java is sending" + Math.random()));
        return Result.success(1);
    }
}
