#ifndef TEST
#define TEST

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
#include <cstdio>

void client();

void test_setmap();

void test_welcome();
void test_guidance_completed(const std_msgs::String::ConstPtr& msg);

void test_delivery();
void test_delivery_completed(const std_msgs::String::ConstPtr& msg);

void test_disable();

void test_enable_confirm(const std_msgs::String::ConstPtr& msg);

void test_gmapping();
void test_base64_pub(const std_msgs::String::ConstPtr& msg);

void test_pointsedit();
void test_pos_pub(const std_msgs::Float64MultiArray::ConstPtr& msg);

#endif