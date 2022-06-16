package com.jeeplus.modules.sys.utils;

import com.google.common.collect.Lists;
import com.jeeplus.modules.sys.entity.Menu;

import java.util.List;

public class MenuUtils {

    public static List<Menu> getMenus() {
        Menu menu = UserUtils.getTopMenu();
        getChildOfTree(menu, 0, UserUtils.getMenuList());
        return menu.getChildren();
    }

    private static Menu getChildOfTree(Menu menuItem, int level, List<Menu> menuList) {


        if ("1".equals(menuItem.getIsShow())) {// 如果是父节点且显示
            menuItem.setChildren(Lists.newArrayList());
            for (Menu child : menuList) {
                if (child.getParentId().equals(menuItem.getId()) && !"2".equals(child.getType()) && "1".equals(child.getIsShow())) {
                    menuItem.getChildren().add(getChildOfTree(child, level + 1, menuList));
                }
            }

        }

        return menuItem;
    }


}
