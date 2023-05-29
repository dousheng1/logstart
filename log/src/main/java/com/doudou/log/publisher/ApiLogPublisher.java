package com.doudou.log.publisher;


import cn.hutool.extra.spring.SpringUtil;
import com.doudou.log.annotation.ApiLog;
import com.doudou.log.constant.EventConstant;
import com.doudou.log.entity.LogApi;
import com.doudou.log.event.ApiLogEvent;
import com.doudou.log.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * API日志信息事件发送
 *
 * @author Chill
 */
public class ApiLogPublisher {

    public static void publishEvent(String methodName, String methodClass, ApiLog apiLog, long time) {
        HttpServletRequest request = WebUtil.getRequest();
        LogApi logApi = new LogApi();
        logApi.setType(EventConstant.LOG_NORMAL_TYPE);
        logApi.setTitle(apiLog.value());
        logApi.setTime(String.valueOf(time));
        logApi.setMethodClass(methodClass);
        logApi.setMethodName(methodName);
        Map<String, Object> event = new HashMap<>(16);
        event.put(EventConstant.EVENT_LOG, logApi);
        event.put(EventConstant.EVENT_REQUEST, request);
        SpringUtil.getApplicationContext().publishEvent(new ApiLogEvent(event));
    }

}
