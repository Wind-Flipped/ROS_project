#include <iostream>
#include <fstream>
#include "ros/ros.h"
#include "std_msgs/Int32.h"

int main(int argc, char **argv) {
    ros::init(argc, argv, "power_detect");
    ros::NodeHandle nh;
    ros::Publisher pub = nh.advertise<std_msgs::Int32>("/power_detect",1000);
    ros::Rate loop_rate(10);
    while (ros::ok())
    {
        std::ifstream batteryFile("/sys/class/power_supply/BAT0/capacity");
        int batteryPercentage;
        if (batteryFile) {
            batteryFile >> batteryPercentage;
            std::cout << "电池电量：" << batteryPercentage << "%" << std::endl;
            batteryFile.close();
        } else {
            std::cout << "无法获取电池电量信息" << std::endl;
        }

        std_msgs::Int32 msg;
        msg.data = batteryPercentage;
        pub.publish(msg);
        ros::spinOnce();
        loop_rate.sleep();
    }
    return 0;
}
