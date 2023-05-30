//package com.doudou.log.config;
//
//
//import com.doudou.log.error.DDErrorAttributes;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.boot.autoconfigure.condition.SearchStrategy;
//import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
//import org.springframework.boot.web.servlet.error.ErrorAttributes;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.DispatcherServlet;
//
//import javax.servlet.Servlet;
//@Configuration
////@AllArgsConstructor
//@ConditionalOnWebApplication
//@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
//@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
//public class ErrorMvcAutoConfiguration {
//
////    private final ServerProperties serverProperties;
//
//    @Bean
//    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
//    public DefaultErrorAttributes errorAttributes() {
//        return new DDErrorAttributes();
//    }
//
////    @Bean
////    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
////    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes) {
////        return new DDErrorController(errorAttributes, serverProperties.getError());
////    }
//
//}
