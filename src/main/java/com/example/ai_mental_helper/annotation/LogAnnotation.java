package com.example.ai_mental_helper.annotation;

import java.lang.annotation.*;

/**
 * 10. 自定义系统审计日志注解
 */
@Target(ElementType.METHOD) // 作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
@Documented
public @interface LogAnnotation {
    String operation() default ""; // 记录操作描述，例如 "用户登录", "发布打卡"
}