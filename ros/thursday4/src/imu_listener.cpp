#include "ros/ros.h"
#include "nav_msgs/Odometry.h"
#include "tf/transform_datatypes.h"
#include <iostream>
using namespace std;

void chatterCallback(const nav_msgs::Odometry::ConstPtr& msg)
{
    double x = msg->pose.pose.orientation.x;
    double y = msg->pose.pose.orientation.y;
    double z = msg->pose.pose.orientation.z;
    double w = msg->pose.pose.orientation.w;
    ROS_INFO("Imu Seq: [%d]", msg->header.seq);
    ROS_INFO("Imu Orientation x: [%f], y: [%f], z: [%f], w: [%f]",x,y,z,w);
    tf::Quaternion q(x,y,z,w);
    tf::Matrix3x3 m(q);
    double roll,pitch,yaw;
    m.getRPY(roll,pitch,yaw);
    double alpha = acos(cos(yaw) * cos(pitch));
    double beta = acos(cos(roll) * cos(yaw));
    double gamma = acos(cos(roll) * cos(pitch));
    cout << "angle with x axis:" << alpha << endl;
    cout << "angle with y axis:" << beta << endl;
    cout << "angle with z axis:" << gamma << endl;
}

int main(int argc, char **argv)
{
    ros::init(argc, argv, "imu_listener");
    ros::NodeHandle n;

    ros::Subscriber sub = n.subscribe("/odom", 1000, chatterCallback);
    ros::spin();

    return 0;
}
