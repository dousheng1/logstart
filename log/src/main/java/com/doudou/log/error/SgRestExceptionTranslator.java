package com.doudou.log.error;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.doudou.log.publisher.ErrorLogPublisher;
import com.doudou.log.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 未知异常转译和发送，方便监听，对未知异常统一处理。Order 排序优先级低
 *
 * @author Chill
 */
@Slf4j
@Order
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@RestControllerAdvice
public class SgRestExceptionTranslator {


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleError(Throwable e) {
        log.error("服务器异常", e);
        //发送服务异常事件
        ErrorLogPublisher.publishEvent(e, URLUtil.getPath(WebUtil.getRequest().getRequestURI()));

        Map<String, Object> map = new HashMap<>(3);
        map.put("data", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        map.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        map.put("message", ObjectUtil.isEmpty(e.getMessage()) ? "服务器异常" : e.getMessage());
        return map;
    }

}
