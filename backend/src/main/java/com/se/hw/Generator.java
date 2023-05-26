package com.se.hw;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;
import java.util.Scanner;

public class Generator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("are you sure to generate new code? [Y/N]");
        String input = scanner.nextLine();
        if (input.contains("Y")||input.contains("y")) {
            generate();
        }
    }

    private static void generate() {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/ros?serverTimezone=GMT%2b8", "root", "200294hd")
                .globalConfig(builder -> {
                    builder.author("SE2304") // 设置作者
//                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:/A_SE/team04-project/backend/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.se.hw") // 设置父包名
                            .moduleName("") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D:/A_SE/team04-project/backend/src/main/resources/mapper/")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
                    builder.mapperBuilder().enableMapperAnnotation().build();
                    builder.controllerBuilder().enableHyphenStyle()  // 开启驼峰转连字符
                            .enableRestStyle();  // 开启生成@RestController 控制器
//                    builder.addInclude("map") // 设置需要生成的表名
//                            .addInclude("point")
//                            .addInclude("user")
//                            .addInclude("wxlogin")
//                            .addTablePrefix("t_"); // 设置过滤表前缀
                    builder.addInclude("point").addTablePrefix("t_");
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }

}
