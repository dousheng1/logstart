package com.doudou.log.error;

import com.doudou.log.publisher.ErrorLogPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DDErrorAttributes extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
		String requestUri = this.getAttr(webRequest, "javax.servlet.error.request_uri");
		Integer status = this.getAttr(webRequest, "javax.servlet.error.status_code");
		Throwable error = getError(webRequest);
		Map<String, Object> map = new HashMap<>(3);
		if (error == null) {
			log.error("URL:{} error status:{}", requestUri, status);
			map.put("data", status);
			map.put("message", "系统未知异常[HttpStatus]:" + status);
		} else {
			log.error(String.format("URL:%s error status:%d", requestUri, status), error);
			map.put("data", status);
			map.put("message", error.getMessage());
		}
		//发送服务异常事件
		ErrorLogPublisher.publishEvent(error, requestUri);
		return map;
	}

	@Nullable
	private <T> T getAttr(WebRequest webRequest, String name) {
		return (T) webRequest.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

}
