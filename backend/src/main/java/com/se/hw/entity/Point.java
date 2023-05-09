package com.se.hw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author SE2304
 * @since 2023-04-27
 */
@Getter
@Setter
public class Point implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Double xAxis;

    private Double yAxis;

    private Integer status;

    private Integer mapId;


}
