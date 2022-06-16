package com.jeeplus.config.web;

import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CSRF Referer验证拦截器
 *
 * @author jeeplus
 * @version 2017-8-19
 */
public class ReferrerInterceptor extends BaseService implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ReferrerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String referrer = request.getHeader("referer");
        String url = StringUtils.joinWith("", request.getScheme(), "://", request.getServerName());
        if (referrer == null || "".equals(referrer) || referrer.lastIndexOf(url) == 0) {
            return true;
        } else {
            logger.warn("CSRF Referer验证失败，Referer：{}，url：{}", referrer, url);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
