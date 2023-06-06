#include "ros/ros.h"
#include "nav_msgs/Odometry.h"
#include  "sensor_msgs/Imu.h"
#include "tf/transform_datatypes.h"
#include "std_msgs/Float64.h"
#include <iostream>
using namespace std;

ros::Publisher pub;

void chatterCallback(const sensor_msgs::Imu::ConstPtr& msg)
{
    double x = msg->orientation.x;
    double y = msg->orientation.y;
    double z = msg->orientation.z;
    double w = msg->orientation.w;
    tf::Quaternion q(x,y,z,w);
    tf::Matrix3x3 m(q);
    double roll,pitch,yaw;
    m.getRPY(roll,pitch,yaw);
    double alpha = acos(cos(yaw) * cos(pitch));
    double beta = acos(cos(roll) * cos(yaw));
    double gamma = acos(cos(roll) * cos(pitch));
    // cout << "angle with x axis:" << alpha << endl;
    // cout << "angle with y axis:" << beta << endl;
    // cout << "angle with z axis:" << gamma << endl;
    std_msgs::Float64 pubmsg;
    pubmsg.data = gamma;
    pub.publish(pubmsg);
    ros::spinOnce();
}

int main(int argc, char **argv)
{
    ros::init(argc, argv, "imu_listener");
    ros::NodeHandle n;

    pub = n.advertise<std_msgs::Float64>("/gesture_detect",1000);
    ros::Subscriber sub = n.subscribe("/imu/data", 1000, chatterCallback);
    ros::spin();

    return 0;
}
