#include "../include/welcome/welcome.h"

ros::Publisher pub;
ros::Publisher cm_pub;
ros::Publisher sr_pub;
ros::Publisher gc_pub;

sound_play::SoundRequest sp;
MoveBaseActionClient *acp;
move_base_msgs::MoveBaseGoal goal;

std::vector<double> spot_array;

int main(int argc, char** argv) {
    ros::init(argc, argv, "welcome");
    ros::NodeHandle n;
    
    ros::Subscriber sub = n.subscribe("face_detect", 1000, face_detected);
    cm_pub = n.advertise<geometry_msgs::Twist>("/cmd_vel", 10);
    pub = n.advertise<std_msgs::String>("face_detect", 1000);
    sr_pub = n.advertise<sound_play::SoundRequest>("/robotsound", 20);
    ros::Subscriber g_sub = n.subscribe("guidance", 1000, guidance);
    gc_pub = n.advertise<std_msgs::String>("guidance_completed", 1000);

    acp = new MoveBaseActionClient("move_base", true);
    while (!acp->waitForServer(ros::Duration(5.0))) {
        ROS_INFO("Waiting for the move_base action server to come up!");
    }

    //get param from launch
    goal.target_pose.header.frame_id = "map";
    goal.target_pose.header.stamp = ros::Time::now();

    ros::Rate r(0.2);
    sp.sound = sound_play::SoundRequest::SAY;
    sp.command = sound_play::SoundRequest::PLAY_ONCE;
    sp.volume = 10;

    check();

    get_spot();

    move(spot_array);

    face_detecting();
    ros::spin();

    return 0;
}

//tool function 1: move
void move(std::vector<double> array) {
    goal.target_pose.pose.position.x = array.at(0);
    goal.target_pose.pose.position.y = array.at(1);
    goal.target_pose.pose.position.z = array.at(2);
    goal.target_pose.pose.orientation.x = array.at(3);
    goal.target_pose.pose.orientation.y = array.at(4);
    goal.target_pose.pose.orientation.z = array.at(5);
    goal.target_pose.pose.orientation.w = array.at(6);

    ROS_INFO("Start navigation!");
    acp->sendGoal(goal);

    acp->waitForResult();

    if (acp->getState() == actionlib::SimpleClientGoalState::SUCCEEDED) {
        ROS_INFO("Arrived!");
        guidance_completed();
    } else {
        ROS_INFO("Error!");
    }
}

//tool function 2: get spot
void get_spot() {
    std::string spot_str;
    ros::param::get("spot", spot_str);
    
    char* spot_str_entry = strtok((char*)spot_str.data(), " ");
    do {
        spot_array.push_back(strtod(spot_str_entry, NULL));
        spot_str_entry = strtok(NULL, " ");
    } while (spot_str_entry != NULL);
}

//tool function 3: check
void check() {
    geometry_msgs::Twist cm;
    cm.linear.x = 0.2;
    cm.linear.y = 0;
    cm.angular.z = 0;
    cm_pub.publish(cm);
    ros::Duration(2.5).sleep();

    cm.linear.x = -0.2;
    cm_pub.publish(cm);
    ros::Duration(2.5).sleep();

    cm.linear.x = 0;
    cm.linear.y = 0.2;
    cm_pub.publish(cm);
    ros::Duration(2.5).sleep();

    cm.linear.y = -0.2;
    cm_pub.publish(cm);
    ros::Duration(2.5).sleep();
}

//core function 1: face detect
void face_detecting() {
    std_msgs::String msg;
    std::stringstream ss;
    ss << "2";
    msg.data = ss.str();
    pub.publish(msg);
    ROS_INFO("Sended the signal to face_detect!");
}

void face_detected(const std_msgs::String::ConstPtr& msg) {
    if ((msg->data.c_str())[0] == '1') {
        ROS_INFO("Received the signal from face_detect!");

        sp.arg = "Hello, do you want to have a meal?";
        sr_pub.publish(sp);

        ros::Duration(3.0).sleep();

        face_detecting();
    }

    //return to spin()
}

//core function 2
void guidance(const std_msgs::Float64MultiArray& array) {
    ROS_INFO("Start a new guidance!");

    sp.arg = "I will lead you to the destination table.";
    sr_pub.publish(sp);

    move(array.data);

    sp.arg = "This is your table.";
    sr_pub.publish(sp);

    move(spot_array);

    //return to spin()
}

void guidance_completed() {
    ROS_INFO("Complete a guidance!");
    std_msgs::String msg;
    std::stringstream ss;
    ss << "1";
    msg.data = ss.str();
    gc_pub.publish(msg);
}