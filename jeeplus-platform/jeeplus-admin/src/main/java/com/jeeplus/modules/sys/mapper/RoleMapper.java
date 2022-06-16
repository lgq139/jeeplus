package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoleMapper extends BaseMapper<Role> {
	/**
	 *
	 * @param id
	 * @return
	 */
	Role get(String id);
	/**
	 * 查询角色的所有无下属菜单ID
	 * @param id
	 * @return
	 */
	List<String> queryAllNotChildrenMenuId(String id);
	/**
	 * 维护角色与菜单权限关系
	 * @param role
	 * @return
	 */
	int deleteRoleMenu(Role role);

	int insertRoleMenu(Role role);

	/**
	 * 维护角色与数据权限关系
	 * @param role
	 * @return
	 */
	int deleteRoleDataRule(Role role);

	int insertRoleDataRule(Role role);

}
