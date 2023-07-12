#include "ros/ros.h"
#include "tf/transform_datatypes.h"
#include "std_msgs/Float64.h"
#include <iostream>
#include <sound_play/SoundRequest.h>
#include "std_msgs/String.h"
using namespace std;

ros::Publisher pub;
sound_play::SoundRequest sp;

void chatterCallback(const std_msgs::String::ConstPtr& msg)
{
    pub.publish(sp);
    ros::spinOnce();
}

int main(int argc, char **argv)
{
    ros::init(argc, argv, "alarm");
    ros::NodeHandle n;

    ros::Rate r(0.2);
    sp.sound = sound_play::SoundRequest::SAY;
    sp.command = sound_play::SoundRequest::PLAY_ONCE;
    sp.volume = 10;
    sp.arg = "can not move to the point";

    pub = n.advertise<sound_play::SoundRequest>("/robotsound", 20);
    ros::Subscriber sub = n.subscribe("/unreach", 1000, chatterCallback);

    ros::spin();

    return 0;
}
