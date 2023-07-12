# SE2304-智能送餐机器人项目介绍

此文档为北航计算机学院2023《软件工程》-嵌入式方向 **周四第4组** 课程项目介绍

## 团队成员及分工

- 徐柯轩--软件测试计划实施、ROS端开发（迎宾和送餐）、搭建ROS自动化测试、文档撰写与审核	
- 戴羽涛--软件需求分析、ROS端开发（建图和站点编辑）、软件测试、文档撰写与审核
- 刘运淇--软件系统结构设计、数据库设计、后端开发、搭建后端自动化测试、文档撰写与审核
- 郑嘉文--软件测试方案制定、前端开发（包含网页与互动设计）、软件测试、文档撰写与审核	
- 霍达--软件项目管理与运作、数据库设计、后端开发、软件测试、文档撰写与审核

## 功能介绍

该项目为一个智能送餐机器人，主要功能包括**建图、站点标注、人脸识别、语音播报、导航**等；前端用来模拟机器人管理员、餐厅工作人员和顾客与机器人进行连接交互，整体主要分为四大核心功能和三大异常处理功能：

- 建图模式
- 站点编辑模式
- 迎宾模式
- 送餐模式
- 自动避障+导航超时异常
- 电量异常
- 姿态异常

## 项目整体及使用说明

**成果演示材料**文件夹中包含课堂展示PPT及测试视频部分。**项目代码**文件夹中包含前端、后端、ROS端项目源代码。**项目文件**文件夹中包含软件工程过程管理中的文档。

前端采用微信小程序开发框架，后端采用Springboot开发框架与MyBatis Plus数据库访问框架，机器人端采用Ubuntu和ROS框架。前端与后端通过**HTTP协议**发送请求相互通信，后端与ROS端通过**WebSocket协议**订阅发布话题相互通信。

后端需要加载**pom.xml**文件中的maven依赖，并自行建立数据库，且名字为`ros`，将**application.yml**文件中的数据库用户名与密码更改为自己数据库的用户名与密码。

使用时，需保证手机、后端程序、控制机器人的机载电脑处于**同一个局域网**内。在机载电脑中通过`ifconfig`命令查看电脑当前ip地址，用户只需在手机上手动输入或输入该ip产生的二维码即可向后端发送信息，连接机器人。

#### ROS端使用说明

进入项目文件夹，使用`source devel/setup.bash`命令设置环境，然后使用`roslaunch thursdays4 excute.launch`启动ROS Master、ROS Bridge和ROS中控节点，等待后端消息以运行对应功能。

#### 后端使用说明

直接在`HwApplication`类里启动即可，后端默认端口9090

```java
@SpringBootApplication
@MapperScan("com.se.hw.mapper")
public class HwApplication {
    public static void main(String[] args) {
        SpringApplication.run(HwApplication.class, args);
    }
}
```

#### 前端使用说明

将`/utils/config.js` 中，host套接字改为自己服务器（后端）域名/ip及对应端口号即可。

#### 小程序和机器人的连接

前端后端和ros端按照上述正确启动后，小程序界面有“设备连接”字样，直接输入ROS端的ip即可进行连接并获取是否连接成功的信息，连接成功后，即可进行使用。

## ROS端说明

#### 开发环境

- Ubuntu 16.04
- ROS Melodic
- C++
- 启智机器人官方包wpb_home

#### 主要文件说明

- **launch文件夹：**

  - gmapping.launch	仿真环境下开启建图模式的脚本文件
  - gmapping_wpb.launch	实机环境下开启建图模式的脚本文件
  - pointsedit.launch	仿真环境下开启站点编辑模式的脚本文件
  - pointsedit_wpb.launch	实机环境下开启站点编辑模式的脚本文件
  - welcome.launch 仿真环境下开启迎宾模式的脚本文件
  - welcome_wpb.launch 实机环境下开启迎宾模式的脚本文件
  - delivery.launch 仿真环境下开启送餐模式的脚本文件
  - delivery_wpb.launch 实机环境下开启送餐模式的脚本文件
  - excute.launch 实机&仿真环境下开启ROS中控脚本文件

- **maps文件夹：**

  - 存放建图生成的pgm、png、yaml文件

- **config文件夹：**

  - 保存launch文件和cpp文件的配置文件

- **include文件夹：**

  - 保存cpp程序的头文件

- **src文件夹：**

  - alarm.cpp	送餐超时时机器人报警
  - base64_listener.cpp 	用于监听打印地图图片的base64字符串，测试时使用
  - face_detect.cpp	进行人脸检测，向迎宾节点发送客人抵达信息
  - imu_listener.cpp	用于监听imu/data_raw话题，将监听到的信息转换成机器人倾角发布
  - map_remove.cpp	用于删除地图
  - pgm2base64.cpp	用于将地图pgm文件转换成base64字符串发布
  - pos_listener.cpp	用于监听acml_pose话题，发布机器人实时位置
  - power_detect.cpp 	用于获取机载电脑电量并发布
  - delivery.cpp 机器人送餐模式功能节点
  - enable_confirm.cpp 用于机器人启动模式后通知后端的节点
  - excute.cpp ROS中控节点，用于接收后端消息并启动对应模式涉及的节点
  - face_detect.cpp 机器人迎宾模式中人脸识别功能节点
  - testrcv.cpp 用于ROS端单元测试监听消息的节点
  - testsd.cpp ROS端单元测试节点
  - welcome.cpp 机器人迎宾模式核心功能节点

## 后端说明

#### 开发环境

* 语言：Java8
* 数据库：MySql 8.0.33
* 开发框架：SpringBoot
* 数据库访问框架：MyBatis Plus

#### 主要文件说明

- **test文件夹**

  后端单元测试+后端/ROS端集成测试的**自动化测试**代码

- **common文件夹**

  包含http请求、微信登录请求、存储文件、返回结果封装等工具类

- **config文件夹**

  包含跨域请求等基本配置类

- **controller文件夹**

  接受前端传递的参数，实现前后端相关接口

- **exception文件夹**

  存储相关异常信息，实时监听异常并告知ROS端和前端

- **mode文件夹**

  采用继承关系，根据功能设置了四大模式（建图、站点编辑、迎宾、导航），封装了与ROS端通信的方法

- **Ros文件夹**

  和ROS端进行对接以及消息通信，主要负责连接、消息初始化、实时接收ROS端的消息并存储

#### 数据库介绍

- **地图Map**：包含主键、名字、图片url、欢迎语、背景颜色等信息
- **站点Point**：包含主键、名字、类型、地图主键（外键属性）、坐标以及机器人四元数等信息
- **用户User**：包含openId（根据微信号生成）、sessionKey、用户创建时间、上次登陆时间、头像、性别等。

## 前端说明

#### 开发环境

- 开发框架：微信小程序原生框架

#### 额外说明

- 单元测试采用微信miniTest框架

#### 主要文件说明

- **components文件夹**

  包含自定义组件

- **custom-tab-bar文件夹**

  包含自定义tab-bar导航栏组件

- **pages文件夹**

  各页面组成文件

- **static文件夹**

  小程序静态资源

- **utils文件夹**

  一些工具函数及配置集

- **app.js**

  全局逻辑控制

- **app.json**

  全局配置文件

- **app.wxss**

  全局样式文件
