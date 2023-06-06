#ifndef WELCOME
#define WELCOME

#include <ros/ros.h>
#include <move_base_msgs/MoveBaseAction.h>
#include <actionlib/client/simple_action_client.h>
#include <std_msgs/String.h>
#include <std_msgs/Float64MultiArray.h>
#include <tf/transform_datatypes.h>
#include <tf/transform_listener.h>
#include <sound_play/SoundRequest.h>
#include <visualization_msgs/Marker.h>
#include <geometry_msgs/Twist.h>

#include <iostream>
#include <vector>
#include <string>
#include <cstring>
#include <cstdio>

typedef actionlib::SimpleActionClient<move_base_msgs::MoveBaseAction> MoveBaseActionClient;

void move(std::vector<double> array);
void get_spot();
void check();

void face_detecting();
void face_detected(const std_msgs::String::ConstPtr& msg);
void guidance(const std_msgs::Float64MultiArray& array);
void guidance_completed();

#endif
