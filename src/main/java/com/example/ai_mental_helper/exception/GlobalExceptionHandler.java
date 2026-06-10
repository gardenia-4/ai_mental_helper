package com.example.ai_mental_helper.exception;

import com.example.ai_mental_helper.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获系统未知的任意 RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        // 在后台控制台打印详细错误，方便调试
        log.error("系统运行时发生异常: ", e);
        // 向前端返回包装后的友好提示 [cite: 16]
        return Result.error(500, "服务器出了点小差，请稍后再试或联系管理员");
    }

    /**
     * 捕获系统全局最高级别的 Exception 异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统全局捕获未知严重错误: ", e);
        return Result.error(500, "系统内部未知系统故障");
    }


    // 请把这个方法复制加到你之前的 GlobalExceptionHandler 类中
    @org.springframework.web.bind.annotation.ExceptionHandler(CustomException.class)
    public Result<?> handleCustomException(CustomException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
}