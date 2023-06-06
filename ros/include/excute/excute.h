#ifndef EXCUTE
#define EXCUTE

#include <ros/ros.h>
#include <move_base_msgs/MoveBaseAction.h>
#include <actionlib/client/simple_action_client.h>
#include <std_msgs/String.h>
#include <std_msgs/Float64MultiArray.h>
#include <tf/transform_datatypes.h>
#include <tf/transform_listener.h>
#include <sound_play/SoundRequest.h>
#include <visualization_msgs/Marker.h>

#include <iostream>
#include <cstring>
#include <vector>

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#define ENABLE_GAZEBO 0
#if ENABLE_GAZEBO==0
#define PATH "/home/robot/catkin_ws/"
#else
#define PATH "/home/miomo323/tsk-com/"
#endif

void* excute(void* ins);
void set_spot(std::vector<double> array);

void disable(const std_msgs::String::ConstPtr& msg);
void enable_gmapping(const std_msgs::String::ConstPtr& msg);
void enable_pointsedit(const std_msgs::String::ConstPtr& msg);
void setmap(const std_msgs::String::ConstPtr& msg);
void enable_welcome(const std_msgs::Float64MultiArray& array);
void enable_delivery(const std_msgs::Float64MultiArray& array);

#endif
