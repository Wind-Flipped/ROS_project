package com.se.hw.service.impl;

import com.se.hw.entity.User;
import com.se.hw.mapper.UserMapper;
import com.se.hw.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author SE2304
 * @since 2023-04-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
