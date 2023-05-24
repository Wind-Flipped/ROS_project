package com.se.hw.entity;

import java.io.Serializable;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 微信用户信息
 * </p>
 *
 * @author SE2304
 * @since 2023-05-24
 */
@Getter
@Setter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * open_id
     */
    @TableId(value = "open_id",type = IdType.INPUT)
    private String openId;

    /**
     * skey
     */
    private String skey;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 最后登录时间
     */
    @TableField("last_visit_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastVisitTime;

    /**
     * session_key
     */
    private String sessionKey;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 网名
     */
    private String name;


}
