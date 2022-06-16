package com.jeeplus.modules.sys.web;

import com.auth0.jwt.JWT;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.AESUtil;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.config.web.Servlets;
import com.jeeplus.core.mapper.JsonMapper;
import com.jeeplus.core.oauth.*;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.core.web.GlobalErrorController;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.entity.enums.LogType;
import com.jeeplus.modules.sys.security.util.JWTUtil;
import com.jeeplus.modules.sys.service.RoleService;
import com.jeeplus.modules.sys.service.UserService;
import com.jeeplus.modules.sys.utils.LogUtils;
import com.jeeplus.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 登录Controller
 *
 * @author jeeplus
 * @version 2016-5-31
 */
@RestController
@Api(tags = "登录管理")
public class Oauth2LoginController extends BaseController {


    @Autowired
    private JeePlusProperites jeePlusProperites;
    @Autowired
    private RemoteOauth2SSOService oauth2SSOService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Value("${oauth2.client_id}")
    private String clientId;
    @Value("${oauth2.client_secret}")
    private String clientSecret;

    @PostMapping("/sys/login")
    @ApiOperation("登录接口")
    public AjaxJson login(@RequestParam("code") String code, @RequestParam() String redirectUri) throws UnsupportedEncodingException {
        Map<String, Object> param = new HashMap<>();
        param.put("client_id", clientId);
        param.put("client_secret", clientSecret);
        param.put("code", code);
        param.put("redirect_uri", redirectUri);
        param.put("grant_type", "authorization_code");
        OauthResponseResult<OauthToken> result = oauth2SSOService.getToken(param);
        if (result.isSuccess() && !Objects.isNull(result.getData())) {
            OauthToken oauthToken = result.getData();
            String accessToken = oauthToken.getAccessToken();
            OauthResponseResult<String> oauthUserResult = oauth2SSOService.getUserInfo(Collections.singletonMap("access_token", accessToken));
            if (oauthUserResult.isSuccess()) {
                if (!Objects.isNull(oauthUserResult.getData())) {
                    String content = AESUtil.decoder16(oauthUserResult.getData(), clientSecret);
                    OauthUser oauthUser = (OauthUser) JsonMapper.fromJsonString(content, OauthUser.class);

                    userService.saveUser(convertToUser(oauthUser));
                    User user = UserUtils.getByLoginName(oauthUser.getLoginName());
                    assert user != null;
                    // 更新登录IP和时间
                    userService.updateUserLoginInfo(user);
                    // 记录登录日志
                    LogUtils.saveLog(Servlets.getRequest(), user, "系统登录", LogType.LOGIN.getType());
                    return Objects.requireNonNull(AjaxJson.success()
                            .put(JWTUtil.TOKEN, JWTUtil.createAccessToken(user.getLoginName(), user.getId())))
                            .put(JWTUtil.REFRESH_TOKEN, JWTUtil.createRefreshToken(user.getLoginName(), user.getId()));
                }
            }
        }
        return AjaxJson.error(result.getMsg());
    }

    public User convertToUser(OauthUser oauthUser) {
        User user = new User();
        user.setId(oauthUser.getId());
        user.setName(oauthUser.getName());
        user.setLoginName(oauthUser.getLoginName());
        user.setOrgCode(oauthUser.getOrgCode());
        user.setOrgName(oauthUser.getOrgName());
        user.setRegionCode(oauthUser.getRegionCode());
        user.setMobile(oauthUser.getMobile());
        user.setRegionName(oauthUser.getRegionName());
        if (CollectionUtils.isNotEmpty(oauthUser.getRoles())) {
            for (OauthRole oauthRole : oauthUser.getRoles()) {
                Role role = new Role();
                role.setId(oauthRole.getRoleId());
                role.setName(oauthRole.getRoleName());
                role.setRemarks(oauthRole.getRemarks());
                user.getRoleList().add(role);
            }
        }
        if (CollectionUtils.isNotEmpty(oauthUser.getPubRoles())) {
            for (OauthRole oauthRole : oauthUser.getPubRoles()) {
                Role role = new Role();
                role.setId(oauthRole.getRoleId());
                role.setName(oauthRole.getRoleName());
                role.setEnname(oauthRole.getRoleCode());
                user.getRoleList().add(role);
            }
        }
        if (CollectionUtils.isEmpty(user.getRoleList())) {
            // 若用户未分配角色的情况，则默认赋予橘色 default
            Role role = roleService.getRoleByEnname("default");
            user.getRoleList().add(role);
        }
        return user;
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


}
