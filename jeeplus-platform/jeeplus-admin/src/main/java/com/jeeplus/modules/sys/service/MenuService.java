package com.jeeplus.modules.sys.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.modules.sys.entity.DataRule;
import com.jeeplus.modules.sys.entity.Menu;
import com.jeeplus.modules.sys.entity.RoleDataRuleRelation;
import com.jeeplus.modules.sys.entity.RoleMenuRelation;
import com.jeeplus.modules.sys.mapper.MenuMapper;
import com.jeeplus.modules.sys.utils.LogUtils;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单.
 */
@Service
@Transactional(readOnly = true)
public class MenuService extends ServiceImpl<MenuMapper, Menu> {

    @Autowired
    private DataRuleService dataRuleService;
    @Autowired
    private RoleMenuRelationService roleMenuRelationService;
    @Autowired
    private RoleDataRuleRelationService roleDataRuleRelationService;

    public Menu get(String id) {
        return getById(id);
    }

    public List<Menu> findAllMenu() {
        return UserUtils.getMenuList();
    }

    public List<Menu> getChildren(String parentId) {
        return getBaseMapper().getChildren(parentId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(Menu menu) {
        // 获取父节点实体
        if (Objects.isNull(menu.getParent())) {
            menu.setParent(this.get(menu.getParentId()));
            menu.setParentId(menu.getParent().getId());
        }
        // 获取修改前的parentIds，用于更新子节点的parentIds
        String oldParentIds = menu.getParentIds();
        // 设置新的父节点串
        if (Objects.nonNull(menu.getParent())) {
            menu.setParentIds(menu.getParent().getParentIds() + menu.getParent().getId() + ",");
        }
        // 保存或更新实体
        super.saveOrUpdate(menu);
        // 更新子节点 parentIds
        List<Menu> list = lambdaQuery().like(Menu::getParentIds, StrUtil.format(",{},", menu.getId())).list();
        for (Menu e : list) {
            e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
            lambdaUpdate().set(Menu::getParentId, e.getParentId())
                    .set(Menu::getParentIds, e.getParentIds())
                    .eq(Menu::getId, e.getId())
                    .update();
        }
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        UserUtils.removeCache(UserUtils.CACHE_TOP_MENU);
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSort(Menu menu) {
        lambdaUpdate().set(Menu::getSort, menu.getSort()).eq(Menu::getId, menu.getId()).update();
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        UserUtils.removeCache(UserUtils.CACHE_TOP_MENU);
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Menu menu) {
        List<String> menuIds = lambdaQuery().select(Menu::getId).eq(Menu::getId, menu.getId())
                .or()
                .like(Menu::getParentIds, "," + menu.getId() + ",")
                .list().stream()
                .map(Menu::getId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(menuIds)) {
            // 解除菜单角色关联
            roleMenuRelationService.lambdaUpdate().in(RoleMenuRelation::getMenuId, menuIds).remove();
            // 删除菜单关联的数据权限数据，以及解除角色数据权限关联
            List<String> dataRuleIds = dataRuleService.lambdaQuery()
                    .in(DataRule::getMenuId, menuIds)
                    .list().stream()
                    .map(DataRule::getId)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dataRuleIds)) {
                roleDataRuleRelationService.lambdaUpdate().in(RoleDataRuleRelation::getDataruleId, dataRuleIds).remove();
                dataRuleService.lambdaUpdate().in(DataRule::getMenuId, menuIds).remove();
            }
        }
        // 删除菜单
        lambdaUpdate().eq(Menu::getId, menu.getId()).or().like(Menu::getParentIds, menu.getId()).remove();
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_TOP_MENU);
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }


}
