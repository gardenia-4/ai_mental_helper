package com.example.ai_mental_helper.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;     // 状态码：200-成功，401-未认证，403-权限拦截，500-系统错误 [cite: 17]
    private String message;   // 提示信息
    private T data;           // 承载的数据体

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}