package com.jeeplus.modules.sys.security;

import cn.hutool.extra.spring.SpringUtil;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.modules.sys.entity.Menu;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.security.shiro.JWTToken;
import com.jeeplus.modules.sys.security.util.JWTUtil;
import com.jeeplus.modules.sys.service.UserService;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 系统安全认证实现类
 */
@Service
public class SystemAuthorizingRealm extends AuthorizingRealm {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private UserService userService;

	@Autowired
	private JeePlusProperites jeePlusProperites;

	/**
	 * 大坑！，必须重写此方法，不然Shiro会报错
	 */
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JWTToken;
	}

	/**
	 * 认证回调函数, 登录时调用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) {

		String token = (String) authcToken.getCredentials();
		// 解密获得username，用于和数据库进行对比
		String loginName = JWTUtil.getLoginName(token);
		if (loginName == null) {
			throw new AuthenticationException("无效的token");
		}

		// 校验用户
		User user = getUserService().getUserByLoginName(loginName);
		if (user == null){
			throw new AuthenticationException("用户不存在.");
		}


		if (JWTUtil.verify(token) == 1) {
			throw new AuthenticationException("token已经过期");
		} else if (JWTUtil.verify(token) == 2) {
			throw new AuthenticationException("用户名或者密码错误");
		}



//		return new SimpleAuthenticationInfo(token, token, "my_realm");
		// 校验登录验证码
//		if ( LoginController.isValidateCodeLogin(token.getLoginName(), false, false)){
//			Session session = UserUtils.getSession();
//			String code = (String)session.getAttribute(ValidateCodeServlet.VALIDATE_CODE);
//			if (token.getCaptcha() == null || !token.getCaptcha().toUpperCase().equals(code)){
//				throw new AuthenticationException("msg:验证码错误, 请重试.");
//			}
//		}

		// 校验用户名密码
		if (JeePlusProperites.NO.equals(user.getLoginFlag())) {
			throw new AuthenticationException("该已帐号禁止登录.");
		}
		// byte[] salt = Encodes.decodeHex(user.getPassword().substring(0, 16));
		return new SimpleAuthenticationInfo(token, token, getName());
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		String username = JWTUtil.getLoginName(principals.toString());
		// 获取当前已登录的用户
		User user = getUserService().getUserByLoginName(username);
		if (user != null) {
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			List<Menu> list = UserUtils.getMenuList();
			for (Menu menu : list){
				if (StringUtils.isNotBlank(menu.getPermission())){
					// 添加基于Permission的权限信息
					for (String permission : StringUtils.split(menu.getPermission(),",")){
						info.addStringPermission(permission);
					}
				}
			}
			// 添加用户权限
			info.addStringPermission("user");
			// 添加用户角色信息
			for (Role role : user.getRoleList()){
				info.addRole(role.getEnname());
			}
			return info;
		} else {
			return null;
		}
	}

	@Override
	protected void checkPermission(Permission permission, AuthorizationInfo info) {
		authorizationValidate(permission);
		super.checkPermission(permission, info);
	}

	@Override
	protected boolean[] isPermitted(List<Permission> permissions, AuthorizationInfo info) {
		if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
        		authorizationValidate(permission);
            }
        }
		return super.isPermitted(permissions, info);
	}

	@Override
	public boolean isPermitted(PrincipalCollection principals, Permission permission) {
		authorizationValidate(permission);
		return super.isPermitted(principals, permission);
	}

	@Override
	protected boolean isPermittedAll(Collection<Permission> permissions, AuthorizationInfo info) {
		if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
            	authorizationValidate(permission);
            }
        }
		return super.isPermittedAll(permissions, info);
	}

	/**
	 * 授权验证方法
	 * @param permission
	 */
	private void authorizationValidate(Permission permission){
		// 模块授权预留接口
	}


	@Override
	public String getAuthorizationCacheName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * 获取系统业务对象
	 */
	public UserService getUserService() {
		if (userService == null){
			userService = SpringUtil.getBean(UserService.class);
		}
		return userService;
	}


}
