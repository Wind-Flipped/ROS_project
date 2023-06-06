package com.se.hw.common;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @ClassName 配置码云图床信息
 * @Author SE2304
 * @Date 2021/7/30 21:38
 * @Description 上传Gitee图床工具类
 */
public class UploadImgUtil {

    /**
      当前static文件绝对路径
     */
    public static final String STATIC_PATH = "D:\\A_SE\\team04-project\\backend\\src\\main\\resources\\static\\";

    /**
     * 码云私人令牌
     */
    public static final String ACCESS_TOKEN = "c1c2299f761925f948cd0fcc3e379d9c";

    /**
     * 码云个人空间名
     */
    public static final String OWNER = "hd20373463";

    /**
     * 上传指定仓库
     */
    public static final String REPO = "pictures";


    /**
     * 上传时指定存放图片路径
     */
    public static final String PATH = "/ros/";


    /**
     * 用于提交描述
     */
    public static final String ADD_MESSAGE = "add img";
    public static final String DEL_MESSAGE = "DEL img";

    public static final String SHA = "de385704cc0edfd39ccfa521d9d5500da18cc331";
    //API
    /**
     * 新建(POST)、获取(GET)、删除(DELETE)文件：()中指的是使用对应的请求方式
     * %s =>仓库所属空间地址(企业、组织或个人的地址path)  (owner)
     * %s => 仓库路径(repo)
     * %s => 文件的路径(path)
     */
    public static final String API_CREATE_POST = "https://gitee.com/api/v5/repos/%s/%s/contents/%s";


    /**
     * 生成创建(获取、删除)的指定文件路径
     *
     * @param name
     * @return
     */
    public static String createUploadFileUrl(String name) {
        //拼接存储的图片名称
        String fileName = name + "." + "png";
        //填充请求路径
        String url = String.format(UploadImgUtil.API_CREATE_POST,
                UploadImgUtil.OWNER,
                UploadImgUtil.REPO,
                UploadImgUtil.PATH + fileName);
        return url;
    }

    /**
     * 获取创建文件的请求体map集合：access_token、message、content
     *
     * @param multipartFile 文件字节数组
     * @return 封装成map的请求体集合
     */
    public static Map<String, Object> getUploadBodyMap(byte[] multipartFile) {
        HashMap<String, Object> bodyMap = new HashMap<>(3);
        bodyMap.put("access_token", UploadImgUtil.ACCESS_TOKEN);
        bodyMap.put("message", UploadImgUtil.ADD_MESSAGE);
        bodyMap.put("content", Base64.encode(multipartFile));
        bodyMap.put("sha", SHA);
        return bodyMap;
    }

    public static String put(String url, Map<String, Object> formData) {
        HttpRequest request = HttpRequest.put(url);
        request.form(formData);
        String result = request.execute().body();
        return result;
    }

    public static String post(String base64String) {
        Random r = new Random();
        String name = "map";
        Integer num = r.nextInt(0x7fffffff);
        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64String);

        // 将字节数组保存为PNG图像
        String targetURL = UploadImgUtil.createUploadFileUrl(name + num);
        //请求体封装
        Map<String, Object> uploadBodyMap = UploadImgUtil.getUploadBodyMap(imageBytes);
        //借助HttpUtil工具类发送POST请求
        String JSONResult = HttpUtil.post(targetURL, uploadBodyMap);
        //解析响应JSON字符串
        JSONObject jsonObj = JSONUtil.parseObj(JSONResult);
        System.out.println(jsonObj);
        //请求失败
        if (jsonObj == null || jsonObj.getObj("commit") == null) {
            return null;
        }
        //请求成功：返回下载地址
        JSONObject content = JSONUtil.parseObj(jsonObj.getObj("content"));

        return String.valueOf(content.getObj("download_url"));
    }

    public static void save(String base64String, String mapName) {
        String outputImagePath = STATIC_PATH
                + mapName + ".png"; // 替换为输出的PNG图片路径
        try {
            // 解码Base64字符串
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64String);

            // 将字节数组保存为PNG图像
            FileOutputStream outputStream = new FileOutputStream(outputImagePath);
            outputStream.write(imageBytes);
            outputStream.close();

            //System.out.println("PNG image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
