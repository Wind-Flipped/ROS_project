#include "../include/test/test.h"

ros::Publisher ts_pub;

ros::Publisher tew_pub;
ros::Publisher tg_pub;


ros::Publisher ted_pub;
ros::Publisher td_pub;
ros::Publisher tdc_pub;

ros::Publisher tdis_pub;

ros::Publisher tmap_pub;
ros::Publisher tpedi_pub;

int main(int argc, char** argv) {
    ros::init(argc, argv, "testsd");
    ros::NodeHandle n;

    ts_pub = n.advertise<std_msgs::String>("setmap", 1000);

    tew_pub = n.advertise<std_msgs::Float64MultiArray>("enable_welcome", 1000);
    tg_pub = n.advertise<std_msgs::Float64MultiArray>("guidance", 1000);

    ted_pub = n.advertise<std_msgs::Float64MultiArray>("enable_delivery", 1000);
    td_pub = n.advertise<std_msgs::Float64MultiArray>("delivery", 1000);
    tdc_pub = n.advertise<std_msgs::String>("delivery_confirm", 1000);

    tdis_pub = n.advertise<std_msgs::String>("disable", 1000);
    tmap_pub = n.advertise<std_msgs::String>("enable_gmapping", 1000);
    tpedi_pub = n.advertise<std_msgs::String>("enable_pointsedit", 1000);

    client();
}

//client
void client() {
    int ins = 0;
    do {
        printf("********************\nPlease input the unit to test:\n");
        printf("[1] setmap\n");
        printf("[2] welcome\n");
        printf("[3] delivery\n");
        printf("[4] disable\n");
        printf("[5] gmapping\n");
        printf("[6] pointsedit\n");
        printf("[0] end test\n********************\n");

        scanf("%d", &ins);

        if (ins == 1) {
            test_setmap();
        } else if (ins == 2) {
            test_welcome();
        } else if (ins == 3) {
            test_delivery();
        } else if (ins == 4) {
            test_disable();
        } else if (ins == 5) {
            test_gmapping();
        } else if (ins == 6) {
            test_pointsedit();
        }
    } while (ins != 0);
}

//core test 1: setmap
void test_setmap() {
    std_msgs::String msg;
    char mapname[50];
    printf("please input mapname\n");
    scanf("%s",mapname);
    std::stringstream ss;
    ss << mapname;
    msg.data = ss.str();
    ts_pub.publish(msg);
}

//core test 2: welcome
void test_welcome() {
    std_msgs::Float64MultiArray msg;
    printf("Please input the spot:\n");
    for (int i = 0; i < 7; i++) {
        double spot_entry;
        scanf("%lf", &spot_entry);
        msg.data.push_back(spot_entry);
    }
    tew_pub.publish(msg);
    
    int ef;
    printf("Input 1 to input des.\n");
    scanf("%d", &ef);
    while(ef == 1) {
        std_msgs::Float64MultiArray des_msg;
        printf("Please input the des:\n");
        for (int i = 0; i < 7; i++) {
            double des_entry;
            scanf("%lf", &des_entry);
            des_msg.data.push_back(des_entry);
        }
        tg_pub.publish(des_msg);

        printf("Input 1 to input des.\n");
        scanf("%d", &ef);
    }
}

//core test 3: delivery
void test_delivery() {
    std_msgs::Float64MultiArray msg;
    printf("Please input the spot:\n");
    for (int i = 0; i < 7; i++) {
        double spot_entry;
        scanf("%lf", &spot_entry);
        msg.data.push_back(spot_entry);
    }
    ted_pub.publish(msg);
    
    int ef;
    printf("Input 1 to input des.\n");
    scanf("%d", &ef);
    while(ef == 1) {
        std_msgs::Float64MultiArray des_msg;
        printf("Please input the des:\n");
        for (int i = 0; i < 7; i++) {
            double des_entry;
            scanf("%lf", &des_entry);
            des_msg.data.push_back(des_entry);
        }
        td_pub.publish(des_msg);

        printf("Input 1 to confirm.\n");
        scanf("%d", &ef);

        std_msgs::String tdc_msg;
        std::stringstream ss;
        ss << "1";
        tdc_msg.data = ss.str();
        tdc_pub.publish(tdc_msg);

        printf("Input 1 to input des.\n");
        scanf("%d", &ef);
    }
}

//core test 4: disable
void test_disable() {
    std_msgs::String msg;
    std::stringstream ss;
    ss << "1";
    msg.data = ss.str();
    tdis_pub.publish(msg);
}

//core test 5: gmapping
void test_gmapping() {
    std_msgs::String msg;
    char mapname[50];
    printf("please input mapname\n");
    scanf("%s",mapname);
    std::stringstream ss;
    ss << mapname;
    msg.data = ss.str();
    tmap_pub.publish(msg);
}

//core test 6: ponitsedit
void test_pointsedit() {
    std_msgs::String msg;
    std::stringstream ss;
    ss << "1";
    msg.data = ss.str();
    tpedi_pub.publish(msg);
}
