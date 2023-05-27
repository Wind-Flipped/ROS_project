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
        TestUtil.log("success!");
        return new Result(code, null, null);
    }

    public static Result success(int code, Object data) {
        TestUtil.log("success! " + data.toString());
        return new Result(code, null, data);
    }

    public static Result error(int code) {
        TestUtil.log("error!");
        return new Result(code, null, null);
    }

    public static Result error(int code, String msg) {
        Result result = new Result(code, msg, null);
        TestUtil.log("error! " + result);
        return result;
    }

    public Result code(int code) {
        this.code = code;
        return this;
    }

    public Result message(String msg) {
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}