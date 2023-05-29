package com.doudou.log.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.doudou.log.utils.WebUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Spring boot 控制器 请求日志，方便代码调试
 *
 */
@Slf4j
@Aspect
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(value = "sg.log.request.enabled", havingValue = "true", matchIfMissing = true)
public class RequestLogAspect {

    private ObjectMapper objectMapper;

    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * AOP 环切 控制器 R 返回值
     *
     * @param point JoinPoint
     * @return Object
     * @throws Throwable 异常
     */
    @Around(
//            "execution(!static com.doudou.demo.common.R *(..)) && " +
//                    "(@within(org.springframework.stereotype.Controller) || " +
//                    "@within(org.springframework.web.bind.annotation.RestController))"
                    "(@within(org.springframework.stereotype.Controller) || " +
                    "@within(org.springframework.web.bind.annotation.RestController))"
    )
    public Object aroundApi(ProceedingJoinPoint point) throws Throwable {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        Object[] args = point.getArgs();
        // 请求参数处理
        final Map<String, Object> paraMap = new HashMap<>(16);
        for (int i = 0; i < args.length; i++) {
            // 读取方法参数
            MethodParameter methodParam = new SynthesizingMethodParameter(method, i);
            methodParam.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
            // PathVariable 参数跳过
            PathVariable pathVariable = methodParam.getParameterAnnotation(PathVariable.class);
            if (pathVariable != null) {
                continue;
            }
            RequestBody requestBody = methodParam.getParameterAnnotation(RequestBody.class);
            String parameterName = methodParam.getParameterName();
            Object value = args[i];
            // 如果是body的json则是对象
            if (requestBody != null) {
                if (value == null) {
                    paraMap.put(parameterName, null);
                } else if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
                    paraMap.put(parameterName, value);
                } else {
                    paraMap.putAll(BeanUtil.beanToMap(value));
                }
                continue;
            }
            // 处理 参数
            if (value instanceof HttpServletRequest) {
                paraMap.putAll(((HttpServletRequest) value).getParameterMap());
                continue;
            } else if (value instanceof WebRequest) {
                paraMap.putAll(((WebRequest) value).getParameterMap());
                continue;
            } else if (value instanceof HttpServletResponse) {
                continue;
            } else if (value instanceof MultipartFile) {
                MultipartFile multipartFile = (MultipartFile) value;
                String name = multipartFile.getName();
                String fileName = multipartFile.getOriginalFilename();
                paraMap.put(name, fileName);
                continue;
            }
            // 参数名
            RequestParam requestParam = methodParam.getParameterAnnotation(RequestParam.class);
            String paraName = parameterName;
            if (requestParam != null && StrUtil.isNotBlank(requestParam.value())) {
                paraName = requestParam.value();
            }
            if (value == null) {
                paraMap.put(paraName, null);
            } else if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
                paraMap.put(paraName, value);
            } else if (value instanceof InputStream) {
                paraMap.put(paraName, "InputStream");
            } else if (value instanceof InputStreamSource) {
                paraMap.put(paraName, "InputStreamSource");
            } else if (canJsonSerialize(value)) {
                // 判断模型能被 json 序列化，则添加
                paraMap.put(paraName, value);
            } else {
                paraMap.put(paraName, "【注意】不能序列化为json");
            }
        }
        HttpServletRequest request = WebUtil.getRequest();
        String requestURI = Objects.requireNonNull(request).getRequestURI();
        String requestMethod = request.getMethod();

        // 构建成一条长 日志，避免并发下日志错乱
        StringBuilder beforeReqLog = new StringBuilder(300);
        // 日志参数
        List<Object> beforeReqArgs = new ArrayList<>();
        beforeReqLog.append("\n\n================  Request Start  ================\n");
        // 打印路由
        beforeReqLog.append("===> {}: {}");
        beforeReqArgs.add(requestMethod);
        beforeReqArgs.add(requestURI);
        // 请求参数
        if (paraMap.isEmpty()) {
            beforeReqLog.append("\n");
        } else {
            beforeReqLog.append(" Parameters: {}\n");
            beforeReqArgs.add(JSONUtil.toJsonStr(paraMap));
        }
        // 打印请求头
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            String headerValue = request.getHeader(headerName);
            beforeReqLog.append("===Headers===  {} : {}\n");
            beforeReqArgs.add(headerName);
            beforeReqArgs.add(headerValue);
        }
        beforeReqLog.append("================  Request End   ================\n");
        // 打印执行时间
        long startNs = System.nanoTime();
        log.info(beforeReqLog.toString(), beforeReqArgs.toArray());
        // aop 执行后的日志
        StringBuilder afterReqLog = new StringBuilder(200);
        // 日志参数
        List<Object> afterReqArgs = new ArrayList<>();
        afterReqLog.append("\n\n================  Response Start  ================\n");
        try {
            Object result = point.proceed();
            // 打印返回结构体
            afterReqLog.append("===Result===  {}\n");
            afterReqArgs.add(JSONUtil.toJsonStr(result));
            return result;
        } finally {
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            afterReqLog.append("<=== {}: {} ({} ms)\n");
            afterReqArgs.add(requestMethod);
            afterReqArgs.add(requestURI);
            afterReqArgs.add(tookMs);
            afterReqLog.append("================  Response End   ================\n");
            log.info(afterReqLog.toString(), afterReqArgs.toArray());
        }
    }

    private boolean canJsonSerialize(Object value) {
        try {
            objectMapper.writeValueAsBytes(value);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}