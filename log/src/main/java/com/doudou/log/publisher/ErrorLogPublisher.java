package com.doudou.log.publisher;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.doudou.log.constant.EventConstant;
import com.doudou.log.entity.LogError;
import com.doudou.log.event.ErrorLogEvent;
import com.doudou.log.utils.WebUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorLogPublisher implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static void publishEvent(Throwable error, String requestUri) {
		HttpServletRequest request = WebUtil.getRequest();
		LogError logError = new LogError();
		logError.setRequestUri(requestUri);
		if (ObjectUtil.isNotEmpty(error)) {
			logError.setStackTrace(ExceptionUtil.stacktraceToString(error));
			logError.setExceptionName(error.getClass().getName());
			logError.setMessage(error.getMessage());
			StackTraceElement[] elements = error.getStackTrace();
			if (ObjectUtil.isNotEmpty(elements)) {
				StackTraceElement element = elements[0];
				logError.setMethodName(element.getMethodName());
				logError.setMethodClass(element.getClassName());
				logError.setFileName(element.getFileName());
				logError.setLineNumber(element.getLineNumber());
			}
		}
		Map<String, Object> event = new HashMap<>(16);
		event.put(EventConstant.EVENT_LOG, logError);
		event.put(EventConstant.EVENT_REQUEST, request);
		applicationContext.publishEvent(new ErrorLogEvent(event));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ErrorLogPublisher.applicationContext = applicationContext;
	}
}
