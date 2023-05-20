package com.se.hw;

import com.se.hw.Ros.RosGlobal;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.se.hw.mapper")
public class HwApplication {

    public static void main(String[] args) {
        // RosGlobal.init("ws://10.193.140.102:9090");
        SpringApplication.run(HwApplication.class, args);
    }

}
