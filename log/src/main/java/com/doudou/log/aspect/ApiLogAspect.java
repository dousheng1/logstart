package com.doudou.log.aspect;

import cn.hutool.core.util.URLUtil;
import com.doudou.log.annotation.ApiLog;
import com.doudou.log.publisher.ApiLogPublisher;
import com.doudou.log.publisher.ErrorLogPublisher;
import com.doudou.log.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 操作日志使用spring event异步入库
 *
 * @author
 */
@Slf4j
@Aspect
public class ApiLogAspect {


    @Around("@annotation(apiLog)")
    public Object around(ProceedingJoinPoint point, ApiLog apiLog) throws Throwable {
        //获取类名
        String className = point.getTarget().getClass().getName();
        //获取方法
        String methodName = point.getSignature().getName();
        // 发送异步日志事件
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //记录日志
        ApiLogPublisher.publishEvent(methodName, className, apiLog, time);
//        ErrorLogPublisher.publishEvent();
        return result;
    }

    @AfterThrowing(value = "@annotation(apiLog)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e, ApiLog apiLog) {
//        String className = joinPoint.getTarget().getClass().getName();
//        String methodName = joinPoint.getSignature().getName();
        ErrorLogPublisher.publishEvent(e, URLUtil.getPath(WebUtil.getRequest().getRequestURI()));
    }
}
