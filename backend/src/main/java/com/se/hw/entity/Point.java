package com.se.hw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author SE2304
 * @since 2023-05-26
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Point implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Float xAxis;

    private Float yAxis;

    private Integer status;

    private Integer mapId;

    private Float zAxis;

    private Float oriX;

    private Float oriY;

    private Float oriZ;

    private Float oriW;

}
