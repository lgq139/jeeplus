/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Lists;
import com.jeeplus.config.shiro.ShiroConfig;
import com.jeeplus.config.web.ApiInterceptor;
import com.jeeplus.config.web.LogInterceptor;
import com.jeeplus.config.web.ReferrerInterceptor;
import com.jeeplus.config.web.WhitelistInterceptor;
import com.jeeplus.core.mapper.JsonMapper;
//import com.jeeplus.modules.flowable.interceptor.FlowableHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by jeeplus on 2017/9/28.
 */
@ConditionalOnBean(ShiroConfig.class)
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DispatcherServlet dispatcherServlet;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/act/**").addResourceLocations("classpath:/act/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
        // registry.addInterceptor(referrerInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(apiInterceptor()).addPathPatterns("/openApi/**");
        // registry.addInterceptor(whitelistInterceptor()).addPathPatterns("/openApi/**");
    }


    @Bean
    public LogInterceptor logInterceptor(){
        return new LogInterceptor();
    }

    @Bean
    public ApiInterceptor apiInterceptor() {
        return new ApiInterceptor();
    }

    @Bean
    public WhitelistInterceptor whitelistInterceptor() {
        return new WhitelistInterceptor();
    }

    @Bean
    public ReferrerInterceptor referrerInterceptor() {
        return new ReferrerInterceptor();
    }

    @Bean
    public ServletRegistrationBean apiServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
        //注入上传配置到自己注册的ServletRegistrationBean
        bean.addUrlMappings("/service/*");
        bean.setName("ModelRestServlet");
        return bean;
    }
    @Bean
    public ServletRegistrationBean restServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
        //注入上传配置到自己注册的ServletRegistrationBean
        bean.addUrlMappings("/rest/*");
        bean.setName("RestServlet");
        return bean;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        List<MediaType> supportedMediaTypes = Lists.newArrayList();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        //formHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(formHttpMessageConverter);


        StringHttpMessageConverter stringHttpMessageConverter =new StringHttpMessageConverter();
        stringHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(stringHttpMessageConverter);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setPrettyPrint(false);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        mappingJackson2HttpMessageConverter.setObjectMapper(new JsonMapper());
        converters.add(mappingJackson2HttpMessageConverter);

        ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        List<MediaType> byteSupportedMediaTypes = Lists.newArrayList();
        byteSupportedMediaTypes.add(MediaType.ALL);
        byteSupportedMediaTypes.add(MediaType.IMAGE_PNG);
        byteSupportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        byteSupportedMediaTypes.add(MediaType.IMAGE_GIF);
        byteSupportedMediaTypes.add(MediaType.IMAGE_JPEG);
        byteSupportedMediaTypes.add(MediaType.valueOf("image/*"));
        byteArrayHttpMessageConverter.setSupportedMediaTypes(byteSupportedMediaTypes);
        converters.add(byteArrayHttpMessageConverter);


    }


    /**
     * druidServlet注册
     */
    @Bean
    public ServletRegistrationBean druidServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new StatViewServlet());
        registration.addUrlMappings("/druid/*");
        return registration;
    }



}
