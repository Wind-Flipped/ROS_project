#include "../include/test/test.h"

ros::Subscriber tgc_sub;
ros::Subscriber tdc_sub;
ros::Subscriber tec_sub;
ros::Subscriber tbp_sub;
ros::Subscriber tpp_sub;

int main(int argc, char** argv) {
    ros::init(argc, argv, "testrcv");
    ros::NodeHandle n;
    
    tgc_sub = n.subscribe("guidance_completed", 1000, test_guidance_completed);
    tdc_sub = n.subscribe("delivery_completed", 1000, test_delivery_completed);
    tec_sub = n.subscribe("enable_confirm", 1000, test_enable_confirm);
    tbp_sub = n.subscribe("base64_pub", 1000, test_base64_pub);
    tpp_sub = n.subscribe("pos_pub", 1000, test_pos_pub);

    ros::spin();
}

//core test 1: guidance completed
void test_guidance_completed(const std_msgs::String::ConstPtr& msg) {
    printf("Backend receive /guidance_completed!\n");
}

//core test 2: delivery completed
void test_delivery_completed(const std_msgs::String::ConstPtr& msg) {
    printf("Backend receive /delivery_completed!\n");
}

//core test 3: enable confirm
void test_enable_confirm(const std_msgs::String::ConstPtr& msg) {
    printf("Backend receive /enable_confirm!\n");
}

//core test 4: base64 pub
void test_base64_pub(const std_msgs::String::ConstPtr& msg) {
    printf("Backend receive /base64_pub!\n");
}

//core test 5: pos pub
void test_pos_pub(const std_msgs::Float64MultiArray::ConstPtr& msg) {
    printf("Backend receive /pos_pub!\n");
}