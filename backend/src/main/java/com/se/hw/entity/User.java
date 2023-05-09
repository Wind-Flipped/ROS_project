package com.se.hw.entity;

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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String pwd;

    private String email;

    private String username;


}
