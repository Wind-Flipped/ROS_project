#include <opencv2/opencv.hpp>
#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
#include <string>
#include "ros/ros.h"

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
    ros::init(argc, argv, "png2base64");
    std::string pngFile = "/home/dyt/catkin_ws/src/thursday4/maps/map.png";  // 输入的PNG文件名

    std::string base64 = convertPNGtoBase64(pngFile);

    std::string filename = "/home/dyt/catkin_ws/src/thursday4/maps/map.txt";  // 输出的文件名

    std::ofstream file(filename);
    if (!file) {
        std::cout << "无法创建文件: " << filename << std::endl;
        return -1;
    }

    file << base64;  // 将字符串写入文件

    file.close();

    return 0;
}