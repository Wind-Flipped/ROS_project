#include "ros/ros.h"
#include <opencv2/opencv.hpp>
#include <string>

int main(int argc, char **argv) {
    ros::init(argc, argv, "pgm2png");
    std::string pgmFile = "/home/dyt/catkin_ws/src/thursday4/maps/map.pgm";  // 输入的PGM文件名
    std::string pngFile = "/home/dyt/catkin_ws/src/thursday4/maps/map.png";  // 输出的PNG文件名

    cv::Mat image = cv::imread(pgmFile, cv::IMREAD_UNCHANGED);

    if (image.empty()) {
        std::cout << "无法打开输入文件: " << pgmFile << std::endl;
        return -1;
    }

    cv::imwrite(pngFile, image);

    std::cout << "转换完成！" << std::endl;

    return 0;
}