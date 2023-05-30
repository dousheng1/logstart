package com.doudou.log.error;

import com.doudou.log.utils.JacksonUtil;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class DDErrorController extends BasicErrorController {

    public DDErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
//		Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        Map<String, Object> body = getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));
        HttpStatus status = getStatus(request);
        response.setStatus(status.value());
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setObjectMapper(JacksonUtil.getInstance());
        view.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        return new ModelAndView(view, body);
    }

}
