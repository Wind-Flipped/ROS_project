#include "../include/delivery/delivery.h"

ros::Publisher pub;
ros::Publisher cm_pub;
ros::Publisher dc_pub;

sound_play::SoundRequest sp;
MoveBaseActionClient *acp;
move_base_msgs::MoveBaseGoal goal;

int flag = 0;
/*state table*/
//0 befroe move
//1 arrived
//2 arrived and ready to return spot
//-1 error

std::vector<double> spot_array;
std::vector<double> origin_array;

int main(int argc, char** argv) {
    ros::init(argc, argv, "delivery");
    ros::NodeHandle n;

    ros::Subscriber sub = n.subscribe("delivery", 1000, delivery);
    cm_pub = n.advertise<geometry_msgs::Twist>("/cmd_vel", 10);
    pub = n.advertise<sound_play::SoundRequest>("/robotsound", 20);
    ros::Subscriber dc_sub = n.subscribe("delivery_confirm", 1000, delivery_confirm);
    dc_pub = n.advertise<std_msgs::String>("delivery_completed", 1000);

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
        delivery_completed();
        if (flag == 0) {
            flag = 1;
        }
    } else {
        ROS_INFO("Error!");
        if (flag == 0) {
            flag = -1;
        }
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
    
    for (int i = 0; i < 6; i++) {
    	origin_array.push_back(0.0);
	}
	origin_array.push_back(1.0);
}

//tool function 3: check
void check() {
    geometry_msgs::Twist cm;
    cm.linear.x = 0.2;
    cm.linear.y = 0;
    cm.angular.z = 0;
    cm_pub.publish(cm);
    ros::Duration(3.0).sleep();

    cm.linear.x = -0.2;
    cm_pub.publish(cm);
    ros::Duration(6.0).sleep();

    cm.linear.x = 0.2;
    cm_pub.publish(cm);
    ros::Duration(3.0).sleep();

    cm.linear.x = 0;
    cm_pub.publish(cm);
}

//core function 1: delivery
void delivery (const std_msgs::Float64MultiArray& array) {
	flag = 0;
	
    move(array.data);

    if (flag == 1) {
        sp.arg = "Hello, this is your meal!";
        pub.publish(sp);
        flag = 2;
    }

    //return to spin()
}

void delivery_confirm(const std_msgs::String::ConstPtr& msg) {
	if (flag == 2) {
		sp.arg = "Wish you have a nice meal!";
    	pub.publish(sp);
    	
    	//make sense ok
    	move(spot_array);
	}
	
	flag = 0;

    //return to spin()
}

void delivery_completed() {
    std_msgs::String msg;
    std::stringstream ss;
    ss << "1";
    msg.data = ss.str();
    dc_pub.publish(msg);
}
