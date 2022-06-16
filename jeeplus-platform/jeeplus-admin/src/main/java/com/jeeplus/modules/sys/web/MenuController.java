package com.jeeplus.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.Menu;
import com.jeeplus.modules.sys.service.MenuService;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/menu")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    @ModelAttribute("menu")
    public Menu get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return menuService.get(id);
        } else {
            return new Menu();
        }
    }

    @GetMapping("list")
    public AjaxJson list() {
         List<Menu> menus = Lists.newArrayList();
        for(Menu menu: menuService.findAllMenu()){
            if(!"1".equals(menu.getId())){
                menus.add(menu);
            }
        }
        return AjaxJson.success().put("menuList", menus);
    }

    @GetMapping("queryById")
    public AjaxJson queryById(Menu menu) {
        if (menu.getParent() == null || menu.getParent().getId() == null) {
            menu.setParent(new Menu(Menu.getRootId()));
        }
        menu.setParent(menuService.get(menu.getParent().getId()));
        return AjaxJson.success().data(menu);
    }

    @RequiresPermissions(value = {"sys:menu:add", "sys:menu:edit"}, logical = Logical.OR)
    @PostMapping("save")
    public AjaxJson save(Menu menu) {
        if (!UserUtils.getUser().isAdmin()) {
            return AjaxJson.error("越权操作，只有超级管理员才能添加或修改数据！");
        }
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }
        /**
         * 后台hibernate-validation插件校验
         */
        String errMsg = beanValidator(menu);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }

        // 获取排序号，最末节点排序号+30
        if (StringUtils.isBlank(menu.getId())) {
            List<Menu> list = Lists.newArrayList();
            List<Menu> sourcelist = menuService.findAllMenu();
            Menu.sortList(list, sourcelist, menu.getParentId(), false);
            if (list.size() > 0) {
                menu.setSort(list.get(list.size() - 1).getSort() + 30);
            }
        }
        menuService.saveMenu(menu);

        return AjaxJson.success("保存成功!");
    }

    @RequiresPermissions("sys:menu:del")
    @PostMapping("delete")
    public AjaxJson delete(Menu menu) {
        AjaxJson j = new AjaxJson();
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作");
        }
        menuService.delete(menu);
        return AjaxJson.success("删除成功!");
    }


    /**
     * 修改菜单排序
     */
    @RequiresPermissions("sys:menu:updateSort")
    @PostMapping("sort")
    public AjaxJson sort(String id1, int sort1, String id2, int sort2) {
        AjaxJson j = new AjaxJson();
        if (jeePlusProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }
        Menu menu = new Menu();
        menu.setId(id1);
        menu.setSort(sort1);
        menuService.updateSort(menu);
        menu.setId(id2);
        menu.setSort(sort2);
        menuService.updateSort(menu);
        return AjaxJson.success("排序成功！");
    }

    public List<Menu> getTreeMenu(List<Menu> list, String extId, String isShowHid) {
        Menu menu = menuService.get("1");
        List rootTree =  formatListToTree (menu, list, extId, isShowHid);
        return rootTree;
    }


    /**
     * 以root为根节点, 将allList从线性列表转为树形列表
     *
     * @param root 根节点, 为空抛出空指针异常
     * @param allList 所有需要参与构造为树的列表
     * @param extId 需要排除在树之外的节点(子节点一并被排除)
     * @return java.util.List<Menu>
     * @Author 滕鑫源
     * @Date 2020/10/23 17:04
     **/
    public List<Menu> formatListToTree(Menu root, List<Menu> allList, String extId, String isShowHide) {
        String rootId = root.getId();

        // 最终的树形态
        List<Menu> trees = Lists.newArrayList();

        // 把需要构造树的所有列表, 根据以父id作为key, 整理为列表
        Map<String, List<Menu>> treeMap = Maps.newHashMap();
        for (Menu menu : allList) {
            List<Menu> menus = treeMap.get(menu.getParentId());
            if (menus == null) {
                menus = Lists.newLinkedList();
            }

            // 剔除排除项, 构造treeMap, 转递归为线性操作
            if (StringUtils.isBlank(extId) ||  (!extId.equals(menu.getId()) && menu.getParentIds().indexOf("," + extId + ",") == -1)){
                if (isShowHide != null && isShowHide.equals(JeePlusProperites.NO) && menu.getIsShow().equals(JeePlusProperites.NO)) {
                    continue;
                }
                menus.add(menu);
                treeMap.put(menu.getParentId(), menus);
            }

        }

        // 没有给定的子树, 返回空树
        if (treeMap.get(rootId) == null || treeMap.get(rootId).isEmpty()) {
            return trees;
        }

        // 开始递归格式化
        List<Menu> children = treeMap.get(rootId);
        for (Menu parent : children) {
            formatFillChildren(parent, treeMap);
            trees.add(parent);
        }
        if (StringUtils.equals(rootId, "0")) {
            return children;
        } else {
            root.setChildren(trees);
            return Lists.newArrayList(root);
        }
    }

    /**
     * 从treeMap中取出子节点填入parent, 并递归此操作
     *
     * @param parent
     * @param treeMap
     * @return void
     * @Author 滕鑫源
     * @Date 2020/9/30 16:33
     **/
    private void formatFillChildren(Menu parent, Map<String, List<Menu>> treeMap) {
        List<Menu> children = treeMap.get(parent.getId());
        parent.setChildren(children);
        if (children != null && !children.isEmpty()) {
            for (Menu child : children) {
                formatFillChildren(child, treeMap);
            }
        }
    }
    /**
     * 显示的菜单包含功能菜单
     * isShowHide是否显示隐藏菜单
     *
     * @param extId
     * @return
     */
    @RequiresPermissions("user")
    @GetMapping("treeData")
    public AjaxJson treeData( @RequestParam(required = false) String extId, @RequestParam(required = false) String isShowHide) {
        List<Menu> list = menuService.findAllMenu();
        List<Menu> rootTree = getTreeMenu(list, extId, isShowHide);
        return AjaxJson.success().data(rootTree);
    }

    /**
     * 不显示功能菜单
     *
     * @return
     */
    @RequiresPermissions("user")
    @GetMapping("treeData2")
    public AjaxJson treeData2() {
        List<Menu> list = menuService.findAllMenu();
        List<Menu> rootTree = getTreeMenu(list, "", "");
        return AjaxJson.success().data(rootTree.get(0).getChildren());
    }

}
