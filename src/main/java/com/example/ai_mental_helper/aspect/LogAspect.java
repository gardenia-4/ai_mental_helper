package com.example.ai_mental_helper.aspect;

import cn.hutool.json.JSONUtil;
import com.example.ai_mental_helper.annotation.LogAnnotation;
import com.example.ai_mental_helper.entity.SysLog;
import com.example.ai_mental_helper.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogService sysLogService;

    // 拦截所有贴了 @LogAnnotation 注解的方法
    @Around("@annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint point, LogAnnotation logAnnotation) throws Throwable {
        long beginTime = System.currentTimeMillis();

        Object result = null;
        String exceptionMsg = null;

        try {
            // 执行目标业务方法
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            exceptionMsg = e.getMessage();
            throw e; // 抛出异常，交给全局异常处理器
        } finally {
            // 计算耗时（毫秒）
            long time = System.currentTimeMillis() - beginTime;

            // 异步保存日志
            saveSysLog(point, logAnnotation, time, exceptionMsg);
        }
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, LogAnnotation logAnnotation, long time, String exceptionMsg) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLog sysLog = new SysLog();

        // 1. 抓取注解上的操作描述
        if (logAnnotation != null) {
            sysLog.setOperation(logAnnotation.operation());
        }

        // 2. 抓取类名与方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        // 3. 抓取请求参数转为 JSON 字符串
        Object[] args = joinPoint.getArgs();
        try {
            String params = JSONUtil.toJsonStr(args);
            // 长度保护防止文本过长撑爆数据库
            sysLog.setParams(params.length() > 1000 ? params.substring(0, 1000) : params);
        } catch (Exception e) {
            sysLog.setParams("参数解析失败");
        }

        // 4. 获取 Request 上下文，抓取 IP 和 Token 解析出来的用户名
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            sysLog.setIp(request.getRemoteAddr());

            // 从拦截器塞入的 Request Attribute 中获取当前用户名
            Object currentUsername = request.getAttribute("currentUsername");
            sysLog.setUsername(currentUsername != null ? currentUsername.toString() : "ANONYMOUS(未登录)");
        }

        sysLog.setExecutionTime(time);
        sysLog.setExceptionMsg(exceptionMsg);

        // 5. 异步入库
        sysLogService.saveLogAsync(sysLog);
    }
}