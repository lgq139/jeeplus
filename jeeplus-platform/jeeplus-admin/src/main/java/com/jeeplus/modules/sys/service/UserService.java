package com.jeeplus.modules.sys.service;

import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.common.utils.Encodes;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.ext.service.CrudService;
import com.jeeplus.core.security.Digests;
import com.jeeplus.core.security.shiro.session.SessionDAO;
import com.jeeplus.core.service.ServiceException;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.mapper.UserMapper;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 用户管理
 */
@Service
@Transactional(readOnly = true)
public class UserService  extends CrudService<UserMapper, User> {

	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;

	@Autowired
	private SessionDAO sessionDao;
	public SessionDAO getSessionDao() {
		return sessionDao;
	}
	/**
	 * 获取用户
	 * @param id
	 * @return
	 */
    public User get(String id) {
		return UserUtils.get(id);
	}

	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName) {
		return UserUtils.getByLoginName(loginName);
	}


	/**
	 * 通过部门ID获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUserByOfficeId(String officeId) {
		List<User> list = (List<User>)CacheUtils.get(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId);
		if (list == null){
			User user = new User();
			user.setOffice(new Office(officeId));
			list = baseMapper.findUserByOfficeId(user);
			CacheUtils.put(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId, list);
		}
		return list;
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		// 清除原用户机构用户缓存
		User oldUser = getById(user.getId());
		if (!Objects.isNull(oldUser)) {
			if (StringUtils.isNotBlank(oldUser.getOrgCode())){
				CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + oldUser.getOrgCode());
			}
			// 更新用户数据
			updateById(user);
		} else {
			save(user);
			if (CollectionUtils.isNotEmpty(user.getRoleList())){
				baseMapper.insertUserRole(user);
			}
		}
		if (StringUtils.isNotBlank(user.getId())) {
			// 更新用户与角色关联
			baseMapper.deleteUserRole(user);
			if (CollectionUtils.isNotEmpty(user.getRoleList())) {
				baseMapper.insertUserRole(user);
			} else {
				throw new ServiceException(user.getLoginName() + "没有设置角色！");
			}
		}
		// 清除用户缓存
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void updateUserInfo(User user) {
		baseMapper.updateUserInfo(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		baseMapper.deleteUserRole(user);
		baseMapper.deleteById(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		User user = new User(id);
		user.setPassword(entryptPassword(newPassword));
		baseMapper.updatePasswordById(user);
		// 清除用户缓存
		user.setLoginName(loginName);
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void updateUserLoginInfo(User user) {
		// 保存上次登录信息
		user.setOldLoginIp(user.getLoginIp());
		user.setOldLoginDate(user.getLoginDate());
		// 更新本次登录信息
		user.setLoginIp(UserUtils.getSession().getHost());
		user.setLoginDate(new Date());
		baseMapper.updateLoginInfo(user);
	}

	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}

	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}

	/**
	 * 获得活动会话
	 * @return
	 */
	public Collection<Session> getActiveSessions(){
		return sessionDao.getActiveSessions(false);
	}

}
