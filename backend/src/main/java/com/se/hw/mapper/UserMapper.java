package com.se.hw.mapper;

import com.se.hw.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author SE2304
 * @since 2023-04-27
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
