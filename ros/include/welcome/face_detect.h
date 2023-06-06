#ifndef FACE_DETECT
#define FACE_DETECT

#include <ros/ros.h>
#include <image_transport/image_transport.h>
#include <cv_bridge/cv_bridge.h>
#include <sensor_msgs/image_encodings.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include "opencv2/objdetect/objdetect.hpp"
#include <sstream>
#include <std_msgs/String.h>

#define WS_NAME "/tsk-com"

using namespace cv;
using namespace std;

#endif