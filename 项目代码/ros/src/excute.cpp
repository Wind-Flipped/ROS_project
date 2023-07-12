#include "../include/excute/excute.h"

pthread_t thread_id;
char spot_str[256];
char map_str[256];

MoveBaseActionClient *acp;

int flag = 0;
/*state table*/
//0 is not working
//1 is welcome or delivery
//-1 is not welcome and not delivery

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
    
    if (flag == 1) {
    	return_origin();
	}

    excute((void*) "rosnode list | grep -v \"/excute\\|/pos_listener\\|/imu_listener\\|/power_detect\\|/testsd\\|/rosbridge_websocket\\|/rosapi\" | tr \"\\n\\r\" \" \" | xargs rosnode kill");

	flag = 0;
}

void return_origin() {
    acp = new MoveBaseActionClient("move_base", true);
    while (!acp->waitForServer(ros::Duration(5.0))) {
        ROS_INFO("[#]Waiting for the move_base action server to come up!");
    }

    move_base_msgs::MoveBaseGoal goal;
    goal.target_pose.header.frame_id = "map";
    goal.target_pose.header.stamp = ros::Time::now();

    goal.target_pose.pose.position.x = 0.0;
    goal.target_pose.pose.position.y = 0.0;
    goal.target_pose.pose.position.z = 0.0;
    goal.target_pose.pose.orientation.x = 0.0;
    goal.target_pose.pose.orientation.y = 0.0;
    goal.target_pose.pose.orientation.z = 0.0;
    goal.target_pose.pose.orientation.w = 1.0;

    ROS_INFO("Start return origin!");
    acp->sendGoal(goal);

    acp->waitForResult();

    if (acp->getState() == actionlib::SimpleClientGoalState::SUCCEEDED) {
        ROS_INFO("Arrived!");
    } else {
        ROS_INFO("Error!");
    }
}

//core function 2: enable gmapping
void enable_gmapping(const std_msgs::String::ConstPtr& msg) {
	flag = -1;
	
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
	flag = -1;
	
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
	flag = 1;
	
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
	flag = 1;
	
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
