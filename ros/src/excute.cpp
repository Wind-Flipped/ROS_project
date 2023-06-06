#include "../include/excute/excute.h"

pthread_t thread_id;
char spot_str[256];
char map_str[256];

int main(int argc, char** argv) {
    strcpy(map_str, "map");

    ros::init(argc, argv, "excute");
    ros::NodeHandle n;

    ros::Subscriber eg_sub = n.subscribe("enable_gmapping", 1000, enable_gmapping);
    ros::Subscriber epe_sub = n.subscribe("enable_pointsedit", 1000, enable_pointsedit);
    ros::Subscriber sm_sub = n.subscribe("setmap", 1000, setmap);
    ros::Subscriber ew_sub = n.subscribe("enable_welcome", 1000, enable_welcome);
    ros::Subscriber ed_sub = n.subscribe("enable_delivery", 1000, enable_delivery);
    ros::Subscriber de_sub = n.subscribe("disable", 1000, disable);

    ros::spin();

    return 0;
}

//tool function 1: mutiple thread excute entrance
void* excute(void* ins) {
    int res;

    res = system((char*) ins);
} 

//tool function 2: set spot
void set_spot(std::vector<double> array) {
    sprintf(spot_str, "%f %f %f %f %f %f %f", 
        array.at(0), array.at(1), array.at(2), array.at(3),
        array.at(4), array.at(5), array.at(6));
    
    ROS_INFO("The current spot is %s!", spot_str);

    char ins[128];
    strcpy(ins, "rosparam set spot \"");
    strcat(ins, spot_str);
    strcat(ins, "\"");
    ROS_INFO("ins is <%s>", ins);

    excute((void*) ins);
}

//core function 1: disable
void disable(const std_msgs::String::ConstPtr& msg) {
    pthread_cancel(thread_id);
	excute((void*) "rosnode list | grep -v \"/excute\\|/pos_listener\\|/imu_listener\\|/power_detect\\|/testsd\\|/rosbridge_websocket\\|/rosapi\" | tr \"\\n\\r\" \" \" | xargs rosnode kill");
}

//core function 2: enable gmapping
void enable_gmapping(const std_msgs::String::ConstPtr& msg) {
    char ins[256];
    strcpy(ins, "cd ");
    strcat(ins, PATH);
    if (ENABLE_GAZEBO) {
        strcat(ins, " && source ./devel/setup.bash && roslaunch thursday4 gmapping.launch");
    } else {
        strcat(ins, " && source ./devel/setup.bash && roslaunch thursday4 gmapping_wpb.launch");
    }

    strcat(ins, " map_name:=\"");
    strcat(ins, msg->data.c_str());
    strcat(ins, "\"");

    ROS_INFO("ins is <%s>", ins);

    pthread_create(&thread_id, NULL, excute, (void*) ins);
}

//core function 3: ponits_edit
void enable_pointsedit(const std_msgs::String::ConstPtr& msg) {
    char ins[256];
    strcpy(ins, "cd ");
    strcat(ins, PATH);
    if (ENABLE_GAZEBO) {
        strcat(ins, " && source ./devel/setup.bash && roslaunch thursday4 pointsedit.launch");
    } else {
        strcat(ins, " && source ./devel/setup.bash && roslaunch thursday4 pointsedit_wpb.launch");
    }
    strcat(ins, " map_name:=\"");
    strcat(ins, map_str);
    strcat(ins, ".yaml\"");

    ROS_INFO("ins is <%s>", ins);

    pthread_create(&thread_id, NULL, excute, (void*) ins);
}

//core function 4: setmap
void setmap(const std_msgs::String::ConstPtr& msg) {
    ROS_INFO("Start setmap!");
    strcpy(map_str, msg->data.c_str());
    ROS_INFO("The current map is %s!", map_str);
}


//core function 5: enable welcome
void enable_welcome(const std_msgs::Float64MultiArray& array) {
    ROS_INFO("Start ew!");
    set_spot(array.data);

    char ins[256];
    strcpy(ins, "cd ");
    strcat(ins, PATH);
    if (ENABLE_GAZEBO) {
        strcat(ins, " && source ./devel/setup.bash && roslaunch thursday4 welcome.launch");
    } else {
        strcat(ins, " && source ./devel/setup.bash && roslaunch thursday4 welcome_wpb.launch");
    }
    strcat(ins, " map_name:=\"");
    strcat(ins, map_str);
    strcat(ins, ".yaml\"");

    ROS_INFO("ins is <%s>", ins);
    
    pthread_create(&thread_id, NULL, excute, (void*) ins);
}

//core function 6: enable delivery
void enable_delivery(const std_msgs::Float64MultiArray& array) {
    ROS_INFO("Start ed!");
    set_spot(array.data);

    char ins[256];
    strcpy(ins, "cd ");
    strcat(ins, PATH);
    if (ENABLE_GAZEBO) {
        strcat(ins, " && source ./devel/setup.bash && roslaunch thursday4 delivery.launch");
    } else {
        strcat(ins, " && source ./devel/setup.bash && roslaunch thursday4 delivery_wpb.launch");
    }
    strcat(ins, " map_name:=\"");
    strcat(ins, map_str);
    strcat(ins, ".yaml\"");

    ROS_INFO("ins is <%s>", ins);
    
    pthread_create(&thread_id, NULL, excute, (void*) ins);
}
