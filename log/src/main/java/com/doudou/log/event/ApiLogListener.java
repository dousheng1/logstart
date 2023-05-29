package com.doudou.log.event;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.URLUtil;
import com.doudou.log.config.ServerInfo;
import com.doudou.log.constant.EventConstant;
import com.doudou.log.entity.LogApi;
import com.doudou.log.service.ApiLogService;
import com.doudou.log.utils.WebUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 异步监听日志事件
 *
 * @author Chill
 */
@Slf4j
@AllArgsConstructor
public class ApiLogListener {

    private final ApiLogService logApiService;
    private final ServerInfo serverInfo;

    @Async
    @Order
    @EventListener(ApiLogEvent.class)
    public void saveApiLog(ApiLogEvent event) {
        Map<String, Object> source = (Map<String, Object>) event.getSource();
        LogApi logApi = (LogApi) source.get(EventConstant.EVENT_LOG);
        HttpServletRequest request = (HttpServletRequest) source.get(EventConstant.EVENT_REQUEST);
        //logApi.setServiceId(bladeProperties.getName());
        logApi.setServerHost(serverInfo.getHostName());
        logApi.setServerIp(serverInfo.getIpWithPort());
        //logApi.setEnv(bladeProperties.getEnv());
        logApi.setRemoteIp(WebUtil.getIP(request));
        logApi.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
        logApi.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        logApi.setMethod(request.getMethod());
        logApi.setParams(WebUtil.getRequestParamString(request));
        // logApi.setCreateBy(UserContextHolder.getAccountId());
        // logApi.setCreateByCode(UserContextHolder.getAccountName());
        // logApi.setCreateByName(UserContextHolder.getUserName());
        logApi.setCreateTime(DateUtil.date());
        logApiService.save(logApi);
    }

}
