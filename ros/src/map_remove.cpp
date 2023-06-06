#include <cstdio>
#include "ros/ros.h"

int main(int argc,char **argv)
{
    ros::init(argc, argv, "map_remove");

    std::string mapName = argv[1];
    std::string mapsPath = "/home/robot/catkin_ws/src/thursday4/maps/";
    std::string pgmPath = mapsPath + mapName + ".pgm";
    std::string yamlPath = mapsPath + mapName + ".yaml";
    
    std::remove(pgmPath.c_str());
    std::remove(yamlPath.c_str());

    return 0;
}