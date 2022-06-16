package com.jeeplus.config.web;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.service.BaseService;
import com.jeeplus.modules.sys.security.SystemAuthorizingRealm;
import com.jeeplus.modules.sys.service.ApiClientService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口安全校验
 */
public class ApiInterceptor extends BaseService implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiInterceptor.class);

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        String principal = (String) subject.getPrincipal();
        if (principal != null) {
            return true;
        }
        String clientId = request.getParameter("client_id");
        String secret = apiClientService.getSecretByClientId(clientId);
        String time = request.getParameter("time");
        String sign = request.getParameter("sign");
        if (StringUtils.isAnyEmpty(clientId, secret, time, sign)) {
            log.warn("密钥校验失败, 缺少参数 clientId: {}, time: {}, sign: {}", clientId, time, sign);
            error(response, "密钥校验失败, 缺少参数");
            return false;
        }
        String trueSign = DigestUtil.md5Hex(clientId + secret + time);
        if (!sign.equals(trueSign)) {
            log.warn("密钥校验失败 clientId: {}, time: {}, sign: {}", clientId, time, sign);
            error(response, null);
            return false;
        }
        return true;
    }

    private void error(HttpServletResponse response, String message) throws JsonProcessingException {
        if (StringUtils.isBlank(message)) {
            message = "无效的密钥";
        }
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
