#include "../include/welcome/face_detect.h"

static CascadeClassifier face_cascade;

static Mat frame_gray;
static std::vector<Rect> faces;
static std::vector<cv::Rect>::const_iterator face_iter;
int cansend = 0;

void chatterCallback(const std_msgs::String::ConstPtr& msg)
{
    ROS_INFO("Receive the signal from welcome!");
    if ((msg->data.c_str())[0] == '2') {
        cansend = 1;
    }
}

ros::Publisher fd_pub;
ros::Subscriber fd_sub;

void callbackRGB(const sensor_msgs::Image msg)
{
    cv_bridge::CvImagePtr cv_ptr;
    try
    {
        cv_ptr = cv_bridge::toCvCopy(msg, sensor_msgs::image_encodings::BGR8);
    }
    catch (cv_bridge::Exception& e)
    {
        ROS_ERROR("cv_bridge exception: %s", e.what());
        return;
    }

    Mat imgOriginal = cv_ptr->image;

    // change contrast: 0.5 = half  ; 2.0 = double
    imgOriginal.convertTo(frame_gray, -1, 1.5, 0);

    // create B&W image
    cvtColor( frame_gray, frame_gray, CV_BGR2GRAY );
	equalizeHist( frame_gray, frame_gray );

    //-- Detect faces
	face_cascade.detectMultiScale( frame_gray, faces, 1.1, 9, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );

    if(faces.size() > 0)
    {
        if (cansend == 1) {
            ROS_INFO("Observe a person!");
            std_msgs::String msg;
            std::stringstream ss;
            ss << "1";
            msg.data = ss.str();
            fd_pub.publish(msg);
            ROS_INFO("Sended the signal to face_detect!");
            cansend = 0;
        }
        std::vector<cv::Rect>::const_iterator i;
        for (face_iter = faces.begin(); face_iter != faces.end(); ++face_iter) 
        {
            cv::rectangle(
                imgOriginal,
                cv::Point(face_iter->x , face_iter->y),
                cv::Point(face_iter->x + face_iter->width, face_iter->y + face_iter->height),
                CV_RGB(255, 0 , 255),
                2);
        }
    }
    imshow("faces", imgOriginal);
    waitKey(1);
}

int main(int argc, char **argv)
{
    cv::namedWindow("faces");
    ros::init(argc, argv, "face_detect");

    ROS_INFO("face_detect");

    std::string strLoadFile;
    char const* home = getenv("HOME");
    strLoadFile = home;
    strLoadFile += WS_NAME;    //工作空间目录
    strLoadFile += "/src/thursday4/config/haarcascade_frontalface_alt.xml";

    bool res = face_cascade.load(strLoadFile);
	if (res == false)
	{
		ROS_ERROR("fail to load haarcascade_frontalface_alt.xml");
        return 0;
	}
    ros::NodeHandle nh;
    fd_pub = nh.advertise<std_msgs::String>("face_detect",1000);
    fd_sub = nh.subscribe("face_detect",1000, chatterCallback);
    ros::Subscriber rgb_sub = nh.subscribe("/kinect2/qhd/image_color_rect", 1 , callbackRGB);

    ros::spin();
    return 0;
}