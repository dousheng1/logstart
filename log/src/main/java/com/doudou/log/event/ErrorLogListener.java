package com.doudou.log.event;


import cn.hutool.core.date.DateUtil;
import com.doudou.log.config.ServerInfo;
import com.doudou.log.constant.EventConstant;
import com.doudou.log.entity.LogError;
import com.doudou.log.service.ErrorLogService;
import com.doudou.log.utils.WebUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 异步监听错误日志事件
 *
 * @author Chill
 */
@Slf4j
@AllArgsConstructor
public class ErrorLogListener {

	private final ErrorLogService logErrorService;
	private final ServerInfo serverInfo;

	@Async
	@Order
	@EventListener(ErrorLogEvent.class)
	public void saveErrorLog(ErrorLogEvent event) {
		Map<String, Object> source = (Map<String, Object>) event.getSource();
		LogError logError = (LogError) source.get(EventConstant.EVENT_LOG);
		HttpServletRequest request = (HttpServletRequest) source.get(EventConstant.EVENT_REQUEST);
		logError.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
		logError.setMethod(request.getMethod());
		logError.setParams(WebUtil.getRequestParamString(request));
		//logError.setServiceId(bladeProperties.getName());
		logError.setServerHost(serverInfo.getHostName());
		logError.setServerIp(serverInfo.getIpWithPort());
		//logError.setEnv(bladeProperties.getEnv());
//		logError.setCreateBy(UserContextHolder.getAccountId());
//		logError.setCreateByCode(UserContextHolder.getAccountName());
//		logError.setCreateByName(UserContextHolder.getUserName());
		logError.setCreateTime(DateUtil.date());
		logErrorService.save(logError);
	}

}
