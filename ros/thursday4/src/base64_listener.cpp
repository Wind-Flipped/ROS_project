#include "ros/ros.h"
#include "nav_msgs/Odometry.h"
#include "tf/transform_datatypes.h"
#include "std_msgs/String.h"
#include "std_msgs/Float64MultiArray.h"
#include "std_msgs/MultiArrayDimension.h"
#include <iostream>
using namespace std;

void chatterCallback(const std_msgs::String::ConstPtr& msg)
{
    ROS_INFO("base64string:%s",msg->data.c_str());
}

int main(int argc, char **argv)
{
    ros::init(argc, argv, "base64_listener");
    ros::NodeHandle n;

    ros::Subscriber sub = n.subscribe("/base64_pub", 1000, chatterCallback);
    ros::spin();

    return 0;
}
