package com.jeeplus.modules.sys.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Lists;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.core.ext.persistence.Node;
import com.jeeplus.modules.sys.entity.*;
import com.jeeplus.modules.sys.mapper.DataRuleMapper;
import com.jeeplus.modules.sys.mapper.MenuMapper;
import com.jeeplus.modules.sys.mapper.UserMapper;
import com.jeeplus.modules.sys.security.util.JWTUtil;
import com.jeeplus.modules.sys.service.BspAreaService;
import com.jeeplus.modules.sys.service.MenuService;
import com.jeeplus.modules.sys.service.OfficeService;
import com.jeeplus.modules.sys.service.RoleService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.List;
import java.util.Map;

/**
 * 用户工具类
 * @author jeeplus
 * @version 2016-12-05
 */
public class UserUtils {


	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";

	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_TOP_MENU = "topMenu";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_DATA_RULE_LIST = "dataRuleList";
	public static final String CACHE_AREA_LIST = "areaList";
	public static final String CACHE_OFFICE_LIST = "officeList";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";

	public static final String CACHE_SPLIT = "_user_id_";
    public static final String CACHE_GRADE_ = "grade_";

	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	public static User get(String id){
		User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user ==  null){
			user = SpringUtil.getBean(UserMapper.class).selectById(id);
			if (user == null){
				return null;
			}
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}

	public static String getUserNameById(String id){
		User user = get(id);
		if(user == null){
			return "";
		}else{
			return user.getName();
		}
	}


	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static User getByLoginName(String loginName){
		User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null){
			user = SpringUtil.getBean(UserMapper.class).getByLoginName(new User (null, loginName));
			if (user == null){
				return null;
			}
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}

	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache(){
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_DATA_RULE_LIST);
		removeCache(CACHE_TOP_MENU);
		removeCache(CACHE_MENU_LIST);
		removeCache(CACHE_AREA_LIST);
		removeCache(CACHE_OFFICE_LIST);
		removeCache(CACHE_OFFICE_ALL_LIST);
		UserUtils.clearCache(getUser());
	}

	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(User user){
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
		if (user.getOffice() != null && user.getOffice().getId() != null){
			CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
		}
	}

	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getUser(){
		String token = getToken();
		if (token!=null){
			User user = getByLoginName(JWTUtil.getLoginName(token));
			if (user != null){
				return user;
			}
			return new User ();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User ();
	}

	/**
	 * 获取当前用户角色列表
	 * @return
	 */
	public static List<Role> getRoleList(){
		@SuppressWarnings("unchecked")
		List<Role> roleList = (List<Role>)getCache(CACHE_ROLE_LIST);
		if (roleList == null){
			User user = getUser();
			if (user.isAdmin()){
				roleList = SpringUtil.getBean(RoleService.class)
						.lambdaQuery().orderByAsc(Role::getName).list();
			}else{
				roleList = user.getRoleList();
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}

	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static List<Menu> getMenuList(){
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			User user = getUser();
			if (user.isAdmin()) {
				menuList = SpringUtil.getBean(MenuService.class).lambdaQuery()
						.orderByAsc(Menu::getSort)
						.list();
			} else {
				Menu m = new Menu();
				m.setUserId(user.getId());
				menuList = SpringUtil.getBean(MenuMapper.class).findByUserId(m);
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}

	/**
	 * 获取当前用户授权数据权限
	 * @return
	 */
	public static List<DataRule> getDataRuleList(){
		@SuppressWarnings("unchecked")
		List<DataRule> dataRuleList = (List<DataRule>)getCache(CACHE_DATA_RULE_LIST);
		if (dataRuleList == null) {
			User user = getUser();
			if (user.isAdmin()) {
				dataRuleList = Lists.newArrayList();
			} else {
				dataRuleList = SpringUtil.getBean(DataRuleMapper.class).findByUserId(user);
			}
			putCache(CACHE_DATA_RULE_LIST, dataRuleList);
		}
		return dataRuleList;
	}

	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static Menu getTopMenu() {
		Menu topMenu = (Menu) getCache(CACHE_TOP_MENU);
		if (topMenu == null) {
			topMenu = SpringUtil.getBean(MenuMapper.class).get("1");
			putCache(CACHE_TOP_MENU, topMenu);
		}
		return  topMenu;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
		if (officeList == null){
			User user = getUser();
			if (user.isAdmin()){
				officeList = SpringUtil.getBean(OfficeService.class).list();
			}else{
//				Office office = new Office ();
//				BaseService.dataRuleFilter(office);
//				officeList = SpringContextHolder.getBean(OfficeMapper.class).findList(office);
			}
			putCache(CACHE_OFFICE_LIST, officeList);
		}
		return officeList;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeAllList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_ALL_LIST);
		if (officeList == null){
			officeList = SpringUtil.getBean(OfficeService.class).list();
		}
		return officeList;
	}

	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject(){
		return SecurityUtils.getSubject();
	}

	/**
	 * 获取当前登录者对象
	 */
	public static String getToken(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Object token = subject.getPrincipal();
			if (token != null){
				return token.toString();
			}
		}catch (UnavailableSecurityManagerException e) {

		}catch (InvalidSessionException e){

		}
		return null;
	}

	public static Session getSession(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
//			subject.logout();
		} catch (InvalidSessionException e){

		}
		return null;
	}


	// 根据key，获取和当前用户的缓存
	public static Object getCache(String key) {
		return CacheUtils.get(USER_CACHE, key + CACHE_SPLIT + getUser().getId());
	}

	//设置当前用户的缓存
	public static void putCache(String key, Object value) {
		CacheUtils.put(USER_CACHE, key + CACHE_SPLIT + getUser().getId(), value);
	}

	//清除当前用户的缓存
	public static void removeCache(String key) {
		CacheUtils.remove(USER_CACHE, key + CACHE_SPLIT + getUser().getId());
	}

	public static boolean hasPermission(String permission) {
		return SecurityUtils.getSubject().isPermitted(permission);
	}


    /**
     * 根据区划代码获取行政级别
     * @param regionCode
     * @return
     */
    public static String getGradeByRegionCode(String regionCode) {
        String grade = (String)CacheUtils.get(USER_CACHE, CACHE_GRADE_ + regionCode);
        if (grade == null){
            Map<String, Node> regionMap = SpringUtil.getBean(BspAreaService.class).getRegionMap();
            grade = regionMap.get(regionCode).getGrade();
            CacheUtils.put(USER_CACHE, CACHE_GRADE_ + regionCode, grade);
        }
        return grade;
    }
}
