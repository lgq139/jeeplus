package com.jeeplus.config.web;

import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.service.BaseService;
import com.jeeplus.modules.sys.service.ApiWhitelistService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

public class WhitelistInterceptor extends BaseService implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WhitelistInterceptor.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiWhitelistService apiWhitelistService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!Objects.isNull(SecurityUtils.getSubject().getPrincipal())) {
            return true;
        }
        String clientIP = ServletUtil.getClientIP(request);
        List<String> whitelist = apiWhitelistService.getWhitelist();
        if (!whitelist.contains(clientIP)) {
            log.warn("白名单校验失败, 当前请求IP: {}", clientIP);
            error(response, null);
            return false;
        }
        return true;
    }

    private void error(HttpServletResponse response, String message) throws JsonProcessingException {
        message = StringUtils.defaultIfEmpty(message, "白名单校验失败, 无请求权限");
        AjaxJson error = AjaxJson.error(message);
        response.setCharacterEncoding("utf-8");
        ServletUtil.write(response, objectMapper.writeValueAsString(error), "application/json");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
