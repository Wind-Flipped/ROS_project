package com.se.hw.mapper;

import com.se.hw.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 微信用户信息 Mapper 接口
 * </p>
 *
 * @author SE2304
 * @since 2023-05-24
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
