package com.jeeplus.modules.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.MenuService;
import com.jeeplus.modules.sys.service.RoleService;
import com.jeeplus.modules.sys.service.UserService;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/sys/role")
public class RoleController extends BaseController {

	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService userService;
	@Autowired
	private MenuService menuService;

	@ModelAttribute("role")
	public Role get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return roleService.get(id);
		}else{
			return new Role();
		}
	}


	@GetMapping("list")
	public AjaxJson data(Role role, HttpServletRequest request, HttpServletResponse response) {
		LambdaQueryChainWrapper<Role> wrapper = roleService.lambdaQuery();
		wrapper.like(StringUtils.isNotBlank(role.getName()), Role::getName, role.getName());
		wrapper.like(StringUtils.isNotBlank(role.getEnname()), Role::getEnname, role.getEnname());
		wrapper.eq(StringUtils.isNotBlank(role.getRoleType()), Role::getRoleType, role.getRoleType());
		//
		wrapper.orderByAsc(Role::getName);
		IPage<Role> page = wrapper.page(new Page<>(Long.parseLong(getPageNoParameter(request)),
				Long.parseLong(getPageSizeParameter(request))));
		return AjaxJson.success().data(page);
	}



	@RequiresPermissions(value={"sys:role:view","sys:role:add","sys:role:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(Role role) {

		String newDataRuleIds = "";
		if (role != null) {
			if (StringUtils.isNotBlank(role.getDataRuleIds())) {
				for (String id : role.getDataRuleIds().split(",")) {
					newDataRuleIds = newDataRuleIds + "dataRule-" + id + ",";
				}
			}
			if (newDataRuleIds.length() > 1) {
				role.setDataRuleIds(newDataRuleIds.substring(0, newDataRuleIds.length() - 1));
			}
			role.setMenuIdList(roleService.queryAllNotChildrenMenuId(role.getId()));
		}


		return AjaxJson.success().put("role", role);
	}


	@RequiresPermissions(value={"sys:role:assign","sys:role:auth","sys:role:add","sys:role:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(Role role) {
		if(!UserUtils.getUser().isAdmin() && role.getSysData().equals(JeePlusProperites.YES)){
			return AjaxJson.error("越权操作，只有超级管理员才能修改此数据！");
		}
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(role);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		if (!"true".equals(checkName(role.getOldName(), role.getName()))){
			return AjaxJson.error("保存角色'" + role.getName() + "'失败, 角色名已存在");
		}
		if (!"true".equals(checkEnname(role.getOldEnname(), role.getEnname()))){
			return AjaxJson.error("保存角色'" + role.getName() + "'失败, 英文名已存在");
		}
		if(StringUtils.isNotBlank(role.getDataRuleIds())){
			String dataRuleIds = role.getDataRuleIds();
			String newDataRuleIds= "";
			String[]ruleIds = dataRuleIds.split(",");
			for(String ruleId:ruleIds){
				if(ruleId.startsWith("dataRule-")){
					newDataRuleIds = newDataRuleIds + ruleId.substring(9) + ",";
				}
			}
			if(newDataRuleIds.length() > 1){
				role.setDataRuleIds(newDataRuleIds.substring(0, newDataRuleIds.length()-1));
			}

		}
		roleService.saveRole(role);
		return AjaxJson.success("保存角色'" + role.getName() + "'成功");
	}

	/**
	 * 删除角色
	 */
	@RequiresPermissions("sys:role:del")
	@DeleteMapping("delete")
	public AjaxJson delete( String ids) {
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		StringBuffer msg = new StringBuffer();
		for(String id : ids.split(",")){
			Role role = roleService.get(id);
			if(!UserUtils.getUser().isAdmin() && role.getSysData().equals(JeePlusProperites.YES)){
				msg.append( "越权操作，只有超级管理员才能修改["+role.getName()+"]数据！<br/>");
			}else{
				roleService.deleteRole(role);
				msg.append( "删除角色["+role.getName()+"]成功<br/>");

			}
		}
		return AjaxJson.success(msg.toString());
	}

	/**
	 * 获取所属角色用户
	 * @return
	 */
	@RequiresPermissions("sys:role:assign")
	@GetMapping("assign")
	public AjaxJson assign(User user, HttpServletRequest request, HttpServletResponse response) {
//		Page<User> page = userService.findPage(new Page<User>(request, response), user);
//		return AjaxJson.success().put("page", page);
		return AjaxJson.success();
	}



	/**
	 * 角色分配 -- 从角色中移除用户
	 * @param userId
	 * @param roleId
	 * @return
	 */
	@RequiresPermissions("sys:role:assign")
	@DeleteMapping("outrole")
	public AjaxJson outrole(String userId, String roleId) {
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		Role role = roleService.get(roleId);
		User user = userService.get(userId);
		if (UserUtils.getUser().getId().equals(userId) && !UserUtils.getUser().isAdmin()) {
			return AjaxJson.error("无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
		}else {
			if (user.getRoleList().size() <= 1){
				return AjaxJson.error("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。");
			}else{
				Boolean flag = roleService.outUserInRole(role, user);
				if (!flag) {
					return AjaxJson.error("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
				}else {
					return AjaxJson.success("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
				}
			}
		}
	}

	/**
	 * 角色分配
	 * @param role
	 * @return
	 */
	@RequiresPermissions("sys:role:assign")
	@PostMapping("assignrole")
	public AjaxJson assignRole(Role role, String[] ids) {
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		StringBuilder msg = new StringBuilder();
		int newNum = 0;
		for (int i = 0; i < ids.length; i++) {
			User user = roleService.assignUserToRole(role, userService.get(ids[i]));
			if (null != user) {
				msg.append("<br/>新增用户【" + user.getName() + "】到角色【" + role.getName() + "】！");
				newNum++;
			}
		}
		return AjaxJson.success("已成功分配 "+newNum+" 个用户"+msg);
	}

	/**
	 * 验证角色名是否有效
	 * @param oldName
	 * @param name
	 * @return
	 */
	public String checkName(String oldName, String name) {
		if (name!=null && name.equals(oldName)) {
			return "true";
		} else if (name!=null && roleService.getRoleByName(name) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 验证角色英文名是否有效
	 * @return
	 */
	public String checkEnname(String oldEnname, String enname) {
		if (enname!=null && enname.equals(oldEnname)) {
			return "true";
		} else if (enname!=null && roleService.getRoleByEnname(enname) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 *
	 * @return
	 */
	@PostMapping("pushRole")
	public AjaxJson syncPushRole() {
		roleService.pushRole();
		return AjaxJson.success("数据同步成功");
	}

}
