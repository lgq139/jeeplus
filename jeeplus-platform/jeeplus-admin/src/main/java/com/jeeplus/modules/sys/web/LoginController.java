/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web;

import com.auth0.jwt.JWT;
import com.google.common.collect.Maps;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.core.web.GlobalErrorController;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.security.util.JWTUtil;
import com.jeeplus.modules.sys.service.UserService;
import com.jeeplus.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 登录Controller
 *
 * @author jeeplus
 * @version 2016-5-31
 */
//@RestController
public class LoginController extends BaseController {


    @Autowired
    JeePlusProperites jeePlusProperites;



    @PostMapping("/sys/login")
    @ApiOperation("登录接口")
    public AjaxJson login(@RequestParam("userName") String userName,
                          @RequestParam("password") String password) {
        AjaxJson j = new AjaxJson();
        User user = UserUtils.getByLoginName(userName);
        if (user != null && UserService.validatePassword(password, user.getPassword())) {

            if (JeePlusProperites.NO.equals(user.getLoginFlag())){
                j.setSuccess(false);
                j.setMsg("该用户已经被禁止登陆!");
            }else {
                j.setSuccess(true);
                j.put(JWTUtil.TOKEN, JWTUtil.createAccessToken(userName, user.getPassword()));
                j.put(JWTUtil.REFRESH_TOKEN, JWTUtil.createRefreshToken(userName, user.getPassword()));
            }

        } else {
            j.setSuccess(false);
            j.setMsg("用户名或者密码错误!");
        }
        return j;
    }



    @GetMapping("/sys/refreshToken")
    @ApiOperation("刷新token")
    public AjaxJson accessTokenRefresh(String refreshToken, HttpServletRequest request, HttpServletResponse response){

        if (JWTUtil.verify(refreshToken) == 1) {
            GlobalErrorController.response4022(request, response);

        }else if (JWTUtil.verify(refreshToken) == 2) {
            return AjaxJson.error("用户名密码错误");
        }

        String loginName = JWTUtil.getLoginName(refreshToken);
        String password = UserUtils.getByLoginName(loginName).getPassword();
        //创建新的accessToken
        String accessToken = JWTUtil.createAccessToken(loginName, password);

        //下面判断是否刷新 REFRESH_TOKEN，如果refreshToken 快过期了 需要重新生成一个替换掉
        long minTimeOfRefreshToken = 2*JeePlusProperites.newInstance().getEXPIRE_TIME();//REFRESH_TOKEN 有效时长是应该为accessToken有效时长的2倍
        Long refreshTokenExpirationTime = JWT.decode(refreshToken).getExpiresAt().getTime();//refreshToken创建的起始时间点
        //(refreshToken过期时间- 当前时间点) 表示refreshToken还剩余的有效时长，如果小于2倍accessToken时长 ，则刷新 REFRESH_TOKEN
        if(refreshTokenExpirationTime - System.currentTimeMillis() <= minTimeOfRefreshToken){
            //刷新refreshToken
            refreshToken = JWTUtil.createRefreshToken(loginName, password);
        }

        return AjaxJson.success().put(JWTUtil.TOKEN, accessToken).put(JWTUtil.REFRESH_TOKEN, refreshToken);
    }



    /**
     * 退出登录
     * @throws IOException
     */
    @ApiOperation("用户退出")
    @GetMapping("/sys/logout")
    public AjaxJson logout() {
        AjaxJson j = new AjaxJson();
        String token = UserUtils.getToken();
        if (StringUtils.isNotBlank(token)) {
            UserUtils.clearCache();
            UserUtils.getSubject().logout();
        }
        j.setMsg("退出成功");
        return j;
    }


    /**
     * 是否是验证码登录
     *
     * @param useruame 用户名
     * @param isFail   计数加1
     * @param clean    计数清零
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
        Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
        if (loginFailMap == null) {
            loginFailMap = Maps.newHashMap();
            CacheUtils.put("loginFailMap", loginFailMap);
        }
        Integer loginFailNum = loginFailMap.get(useruame);
        if (loginFailNum == null) {
            loginFailNum = 0;
        }
        if (isFail) {
            loginFailNum++;
            loginFailMap.put(useruame, loginFailNum);
        }
        if (clean) {
            loginFailMap.remove(useruame);
        }
        return loginFailNum >= 3;
    }


}
