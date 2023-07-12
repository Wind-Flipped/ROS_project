package com.se.hw.service.impl;

import com.se.hw.entity.Point;
import com.se.hw.mapper.PointMapper;
import com.se.hw.service.IPointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author SE2304
 * @since 2023-05-26
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements IPointService {

}
