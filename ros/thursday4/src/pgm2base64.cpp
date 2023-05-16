#include <opencv2/opencv.hpp>
#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
#include <string>
#include "ros/ros.h"
#include "std_msgs/String.h"

std::string encodeBase64(const std::vector<uchar>& data) {
    std::string base64;
    const std::string base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    int numCharsToEncode = data.size() / 3 * 3;

    for (int i = 0; i < numCharsToEncode; i += 3) {
        int b1 = data[i];
        int b2 = data[i + 1];
        int b3 = data[i + 2];

        int index1 = b1 >> 2;
        int index2 = ((b1 & 3) << 4) | (b2 >> 4);
        int index3 = ((b2 & 15) << 2) | (b3 >> 6);
        int index4 = b3 & 63;

        base64 += base64Chars[index1];
        base64 += base64Chars[index2];
        base64 += base64Chars[index3];
        base64 += base64Chars[index4];
    }

    int padding = data.size() % 3;
    if (padding == 1) {
        int b1 = data[numCharsToEncode];

        int index1 = b1 >> 2;
        int index2 = (b1 & 3) << 4;

        base64 += base64Chars[index1];
        base64 += base64Chars[index2];
        base64 += "==";
    }
    else if (padding == 2) {
        int b1 = data[numCharsToEncode];
        int b2 = data[numCharsToEncode + 1];

        int index1 = b1 >> 2;
        int index2 = ((b1 & 3) << 4) | (b2 >> 4);
        int index3 = (b2 & 15) << 2;

        base64 += base64Chars[index1];
        base64 += base64Chars[index2];
        base64 += base64Chars[index3];
        base64 += "=";
    }

    return base64;
}

std::string convertPNGtoBase64(const std::string& pngFile) {
    std::ifstream file(pngFile, std::ios::binary);
    if (!file) {
        std::cout << "无法打开文件: " << pngFile << std::endl;
        return "";
    }

    std::vector<uchar> data((std::istreambuf_iterator<char>(file)), std::istreambuf_iterator<char>());
    std::string base64 = encodeBase64(data);

    return base64;
}

int main(int argc, char **argv) {
    ros::init(argc, argv, "pgm2base64");

    std::string pgmName = argv[1]; 
    std::string mapDir = "/home/dyt/catkin_ws/src/thursday4/maps/";
    std::string pgmFile = mapDir + pgmName + ".pgm";  // 输入的PGM文件名
    std::string pngFile = mapDir + pgmName + ".png";  // 输出的PNG文件名

    cv::Mat image = cv::imread(pgmFile, cv::IMREAD_UNCHANGED);

    if (image.empty()) {
        std::cout << "无法打开输入文件: " << pgmFile << std::endl;
        return -1;
    }

    cv::imwrite(pngFile, image);

    std::cout << "转换完成！" << std::endl;

    std::string base64 = convertPNGtoBase64(pngFile);
    //std::cout << base64;

    ros::NodeHandle nh;
    ros::Publisher pub = nh.advertise<std_msgs::String>("/base64_pub",1000);
    ros::Duration(1.0).sleep();

    std_msgs::String msg;
    msg.data = base64;
    pub.publish(msg);
    ros::spinOnce();

    while(ros::ok());
    // std::string txtname = mapDir + pgmName + ".txt";  // 输出的文件名

    // std::ofstream file(txtname);
    // if (!file) {
    //     std::cout << "无法创建文件: " << txtname << std::endl;
    //     return -1;
    // }

    // file << base64;  // 将字符串写入文件

    // file.close();
    //ros::Duration(10.0).sleep();

    return 0;
}