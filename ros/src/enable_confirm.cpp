#include <iostream>
#include <fstream>
#include "ros/ros.h"
#include "std_msgs/String.h"

ros::Publisher ec_pub;

int main(int argc, char** argv) {
    ros::init(argc, argv, "enable_confrim");
    ros::NodeHandle n;

    ec_pub = n.advertise<std_msgs::String>("enable_confirm", 1000);

    ros::Duration(1).sleep();

    std_msgs::String msg;
    std::stringstream ss;
    ss << "1";
    msg.data = ss.str();
    ec_pub.publish(msg);
    
    ROS_INFO("Sended an enable_confirm!");

    while(ros::ok());
}