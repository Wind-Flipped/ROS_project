package com.se.hw.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.se.hw.common.Result;
import com.se.hw.common.TestUtil;
import com.se.hw.common.WechatUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

import com.se.hw.service.IUserService;
import com.se.hw.entity.User;


/**
 * <p>
 * 微信用户信息 前端控制器
 * </p>
 *
 * @author SE2304
 * @since 2023-05-24
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping
    public Boolean save(@RequestBody User user) {
        return userService.saveOrUpdate(user);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return userService.removeById(id);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.list();
    }

    @GetMapping("/{id}")
    public List<User> findOne(@PathVariable Integer id) {
        return userService.list();
    }

    @GetMapping("/page")
    public Page<User> findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
        return userService.page(new Page<>(pageNum, pageSize));
    }

    @GetMapping("/login")
    public Result user_login(@RequestParam(value = "code", required = false) String code) {
        // 用户非敏感信息：rawData
        // 签名：signature
        //JSONObject rawDataJson = JSON.parseObject(rawData);
        // 1.接收小程序发送的code
        // 2.开发者服务器 登录凭证校验接口 appi + appsecret + code
        TestUtil.log(code);
        JSONObject SessionKeyOpenId = WechatUtil.getSessionKeyOrOpenId(code);
        // 3.接收微信接口服务 获取返回的参数
        String openid = SessionKeyOpenId.getString("openid");
        String sessionKey = SessionKeyOpenId.getString("session_key");

        // 4.校验签名 小程序发送的签名signature与服务器端生成的签名signature2 = sha1(rawData + sessionKey)
//        String signature2 = DigestUtils.sha1Hex(rawData + sessionKey);
//        if (!signature.equals(signature2)) {
//            return Result.error(500, "签名校验失败");
//        }
        // 5.根据返回的User实体类，判断用户是否是新用户，是的话，将用户信息存到数据库；不是的话，更新最新登录时间
        User user = userService.getById(openid);
        TestUtil.log(openid);
        // uuid生成唯一key，用于维护微信小程序用户与服务端的会话
        String skey = UUID.randomUUID().toString();
        if (user == null) {
            // 用户信息入库
            user = new User();
            user.setOpenId(openid);
            user.setSkey(skey);
            user.setUserName("ikun");
            user.setAvatar("https://gitee.com/hd20373463/pictures/raw/master/default.png");
            user.setGender(1);
            user.setCreateTime(new Date());
            user.setLastVisitTime(new Date());
            user.setSessionKey(sessionKey);
            userService.saveOrUpdate(user);
        } else {
            // 已存在，更新用户登录时间
            user.setLastVisitTime(new Date());
            // 重新设置会话skey
            user.setSkey(skey);
            userService.updateById(user);
        }
        //encrypteData比rowData多了appid和openid
        //JSONObject userInfo = WechatUtil.getUserInfo(encrypteData, sessionKey, iv);
        return Result.success(100, user);
    }

    @GetMapping("/getUserInfo")
    public Result getUserInfo(@RequestParam Integer openId) {
        User user = userService.getById(openId);
        if (user == null) {
            return Result.error(404,"Can't find the user");
        }
        return Result.success(100, user);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Map<String, Object> request) {
        User user = req2user(request);
        if (user == null) {
            return Result.error(400, "can't find the user");
        }
        userService.updateById(user);
       return Result.success(100,user);
    }

    private User req2user(Map<String, Object> req) {
        Map<String, Object> request = (Map<String, Object>) req.get("user");
        String openId = (String) request.get("openId");
        User user = userService.getById(openId);
        if (userService.getById(user.getOpenId()) == null) {
            return null;
        }
        return new User((String) request.get("openId"), (String) request.get("skey"), user.getCreateTime(), user.getLastVisitTime(),
                (String) request.get("sessionKey"), (String) request.get("avatar"), (Integer) request.get("gender"), (String) request.get("userName"));
    }
}

