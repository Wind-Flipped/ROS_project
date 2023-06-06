#ifndef DELIVERY
#define DELIVERY

#include <ros/ros.h>
#include <move_base_msgs/MoveBaseAction.h>
#include <actionlib/client/simple_action_client.h>
#include <std_msgs/String.h>
#include <std_msgs/Float64MultiArray.h>
#include <tf/transform_datatypes.h>
#include <sound_play/SoundRequest.h>
#include <geometry_msgs/Twist.h>

#include <iostream>
#include <vector>
#include <string>
#include <cstring>
#include <cstdio>
#include <cstdlib>

typedef actionlib::SimpleActionClient<move_base_msgs::MoveBaseAction> MoveBaseActionClient;

void move(std::vector<double> array);
void get_spot();
void check();

void delivery (const std_msgs::Float64MultiArray& array);
void delivery_confirm(const std_msgs::String::ConstPtr& msg);
void delivery_completed();

#endif