package com.doudou.log.config;

import com.doudou.log.aspect.ApiLogAspect;
import com.doudou.log.event.ApiLogListener;
import com.doudou.log.event.ErrorLogListener;
import com.doudou.log.publisher.ApiLogPublisher;
import com.doudou.log.service.ApiLogService;
import com.doudou.log.service.ErrorLogService;
import com.doudou.log.service.impl.LogApiServiceImpl;
import com.doudou.log.service.impl.LogErrorServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * 日志工具自动配置
 *
 * @author Chill
 */
@Configuration
@Import({ServerInfo.class, ApiLogPublisher.class, ErrorMvcAutoConfiguration.class})
@ConditionalOnWebApplication
public class LogToolAutoConfiguration {

    @Bean
    public ApiLogAspect apiLogAspect() {
        return new ApiLogAspect();
    }

    @Bean
    public ApiLogService apiLogService() {
        return new LogApiServiceImpl();
    }

    @Bean
    public ErrorLogService errorLogService() {
        return new LogErrorServiceImpl();
    }



    @Bean
    @ConditionalOnMissingBean(name = "apiLogListener")
    public ApiLogListener apiLogListener(ApiLogService logApiService, ServerInfo serverInfo) {
        return new ApiLogListener(logApiService, serverInfo);
    }

    @Bean
    @ConditionalOnMissingBean(name = "errorEventListener")
    public ErrorLogListener errorEventListener(ErrorLogService logErrorService, ServerInfo serverInfo) {
        return new ErrorLogListener(logErrorService, serverInfo);
    }

}
