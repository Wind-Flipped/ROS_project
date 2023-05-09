package com.se.hw.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private int code;
    private String msg;
    private Object data;

    public static Result success(int code) {
        return new Result(code, null, null);
    }

    public static Result success(int code, Object data) {
        return new Result(code, null, data);
    }

    public static Result error(int code) {
        return new Result(code, null, null);
    }

    public static Result error(int code, String msg) {
        return new Result(code, msg, null);
    }

    public Result code(int code) {
        this.code = code;
        return this;
    }

    public Result message(String msg) {
        this.msg = msg;
        return this;
    }

}