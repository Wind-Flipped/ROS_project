package com.se.hw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author SE2304
 * @since 2023-05-27
 */
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Map implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String url;

    private String welcome;

    private String rosname;

    private String bg;

}
