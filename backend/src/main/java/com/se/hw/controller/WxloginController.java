package com.se.hw.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.se.hw.common.Result;
import com.se.hw.common.WechatUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

import com.se.hw.service.IWxloginService;
import com.se.hw.entity.Wxlogin;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author SE2304
 * @since 2023-04-27
 */
@RestController
@RequestMapping("/wxlogin")
public class WxloginController {

    @Resource
    private IWxloginService wxloginService;

    @PostMapping
    public Boolean save(@RequestBody Wxlogin wxlogin) {
        return wxloginService.saveOrUpdate(wxlogin);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return wxloginService.removeById(id);
    }

    @GetMapping
    public List<Wxlogin> findAll() {
        return wxloginService.list();
    }

    @GetMapping("/{id}")
    public List<Wxlogin> findOne(@PathVariable Integer id) {
        return wxloginService.list();
    }

    @GetMapping("/page")
    public Page<Wxlogin> findPage(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize) {
        return wxloginService.page(new Page<>(pageNum, pageSize));
    }

    @GetMapping("/request")
    @ResponseBody
    public Result wx_request(@RequestParam(value = "code", required = false) String code,
                             @RequestParam(value = "rawData", required = false) String rawData,
                             @RequestParam(value = "signature", required = false) String signature,
                             @RequestParam(value = "encrypteData", required = false) String encrypteData,
                             @RequestParam(value = "iv", required = false) String iv) {
        // 用户非敏感信息：rawData
        // 签名：signature
        //   JSONObject rawDataJson = JSON.parseObject(rawData);
        // 1.接收小程序发送的code
        // 2.开发者服务器 登录凭证校验接口 appi + appsecret + code
        JSONObject SessionKeyOpenId = WechatUtil.getSessionKeyOrOpenId(code);
        // 3.接收微信接口服务 获取返回的参数
        String openid = SessionKeyOpenId.getString("openid");
        String sessionKey = SessionKeyOpenId.getString("session_key");

        // 4.校验签名 小程序发送的签名signature与服务器端生成的签名signature2 = sha1(rawData + sessionKey)
//        String signature2 = DigestUtils.sha1Hex(rawData + sessionKey);
//        if (!signature.equals(signature2)) {
//            return Result.error(0, "校验失败");
//        }
        // uuid生成唯一key，用于维护微信小程序用户与服务端的会话
        String skey = UUID.randomUUID().toString();
        wxloginService.saveOrUpdate(new Wxlogin(skey, sessionKey, openid));
        return Result.success(1, skey);
    }

    @PostMapping("/login")
    public Result wx_login(@RequestParam(value = "", required = false) String skey) {
        Wxlogin wxlogin = wxloginService.getById(skey);
        if (wxlogin != null) {
            return Result.success(1);
        }
        return Result.error(100, "login error");
    }

}

