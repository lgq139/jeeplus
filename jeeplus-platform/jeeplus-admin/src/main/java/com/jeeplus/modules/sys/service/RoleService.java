package com.jeeplus.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jeeplus.core.oauth.OauthRole;
import com.jeeplus.core.oauth.RemoteOauth2ApiService;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.mapper.RoleMapper;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    @Autowired
    private UserService userService;

    public Role get(String id) {
        return baseMapper.get(id);
    }

    public Role getRoleByName(String name) {
        return lambdaQuery().eq(Role::getName, name).one();
    }

    public Role getRoleByEnname(String enname) {
        return lambdaQuery().eq(Role::getEnname, enname).one();
    }

    /**
     * 查询角色的所有无下属菜单ID
     * @param id
     * @return
     */
    public List<String> queryAllNotChildrenMenuId(String id){
        if(StringUtils.isNotEmpty(id)){
            return baseMapper.queryAllNotChildrenMenuId(id);
        }
        return Lists.newArrayList();
    }

    public List<Role> findAllRole() {
        return UserUtils.getRoleList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveRole(Role role) {
        if (StringUtils.isBlank(role.getId())) {
            save(role);
        } else {
            updateById(role);
        }
        // 更新角色与菜单关联
        baseMapper.deleteRoleMenu(role);
        if (role.getMenuList().size() > 0) {
            baseMapper.insertRoleMenu(role);
        }

        // 更新角色与数据权限关联
        baseMapper.deleteRoleDataRule(role);
        if (role.getDataRuleList().size() > 0) {
            baseMapper.insertRoleDataRule(role);
        }
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);

        pushRole();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Role role) {
        baseMapper.deleteRoleMenu(role);
        baseMapper.deleteRoleDataRule(role);
        removeById(role);
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);

        pushRole();
    }

    /**
     * 解除用户的 某个角色
     * @param role 角色
     * @param user 用户
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean outUserInRole(Role role, User user) {
        List<Role> roles = user.getRoleList();
        for (Role e : roles) {
            if (e.getId().equals(role.getId())) {
                roles.remove(e);
                userService.saveUser(user);
                return true;
            }
        }
        return false;
    }

    /**
     * 给用户分配角色
     * @param role 角色
     * @param user 用户
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public User assignUserToRole(Role role, User user) {
        if (user == null) {
            return null;
        }
        List<String> roleIds = user.getRoleIdList();
        if (roleIds.contains(role.getId())) {
            return null;
        }
        user.getRoleList().add(role);
        userService.saveUser(user);
        return user;
    }

    @Autowired
    private RemoteOauth2ApiService remoteOauth2ApiService;

    public void pushRole() {
        List<Role> roleList = list();
        remoteOauth2ApiService.pushRoles(
                roleList.stream().filter(it -> "1".equals(it.getRoleType())) // 非标准角色
                        .map(it -> new OauthRole(it.getId(), it.getName()).setRemarks(it.getRemarks()))
                        .collect(Collectors.toList()));
    }


}
