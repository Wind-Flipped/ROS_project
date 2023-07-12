package com.se.hw.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Wxlogin implements Serializable {

    private static final long serialVersionUID = 1L;

    private String session3rd;

    private String sessionkey;

    private String openid;

}
