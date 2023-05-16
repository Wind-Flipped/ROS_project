#include "ros/ros.h"
#include "nav_msgs/Odometry.h"
#include "tf/transform_datatypes.h"
#include "std_msgs/String.h"
#include "std_msgs/Float64MultiArray.h"
#include "std_msgs/MultiArrayDimension.h"
#include <iostream>
using namespace std;

ros::Publisher pub;
void chatterCallback(const nav_msgs::Odometry::ConstPtr& msg)
{
    double x = msg->pose.pose.position.x;
    double y = msg->pose.pose.position.y;
    double z = msg->pose.pose.position.z;
    ROS_INFO("Imu Seq: [%d]", msg->header.seq);
    ROS_INFO("Imu Position x: [%f], y: [%f], z: [%f]",x,y,z);
    std_msgs::Float64MultiArray msg2;
    msg2.layout.dim.push_back(std_msgs::MultiArrayDimension());
    msg2.layout.dim[0].size = 3;
    msg2.layout.dim[0].stride = 1;
    msg2.layout.dim[0].label = "x";
    msg2.data.resize(3);
    msg2.data[0] = x, msg2.data[1] = y, msg2.data[2] = z;
    pub.publish(msg2);
}

int main(int argc, char **argv)
{
    ros::init(argc, argv, "pos_listener");
    ros::NodeHandle n;

    ros::Subscriber sub = n.subscribe("/odom", 1000, chatterCallback);
    pub = n.advertise<std_msgs::Float64MultiArray>("/pos_pub",1000);
    ros::spin();

    return 0;
}
