package com.jeeplus.modules.sys.security.shiro;

import com.jeeplus.common.utils.CookieUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.core.web.GlobalErrorController;
import com.jeeplus.modules.sys.security.util.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JeePlusProperites jeePlusProperites;

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Token字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = getToken(req);
        return authorization != null && !"null".equals(authorization)&& !"".equals(authorization);
    }

    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = getToken(httpServletRequest);

        JWTToken token = new JWTToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 获取token，支持三种方式, 请求参数、header、cookie， 优先级依次降低，以请求参数中的优先级最高。
     * @param httpServletRequest
     * @return
     */
    private String getToken(HttpServletRequest httpServletRequest){
        String token0 = httpServletRequest.getParameter(JWTUtil.TOKEN);
        String token1 = httpServletRequest.getHeader(JWTUtil.TOKEN);
        String token2 = CookieUtils.getCookie(httpServletRequest, JWTUtil.TOKEN);
        if(StringUtils.isNotBlank(token0)){
            return token0;
        }
        if(StringUtils.isNotBlank(token1)){
            return token1;
        }
        if(StringUtils.isNotBlank(token2)){
            return token2;
        }
       return null;
    }


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
               return executeLogin(request, response);
            } catch (AuthenticationException e) {
                GlobalErrorController.response401(request, response);//登录超时，需要刷新token
            }catch (Exception e){
               GlobalErrorController.response4021(request, response);//没有登录，需要登录
            }
        }else {
            GlobalErrorController.response4021(request, response);//没有登录，需要登录
        }

        return false;

    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }


}
