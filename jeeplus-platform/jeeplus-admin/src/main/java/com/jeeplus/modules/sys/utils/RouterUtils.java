package com.jeeplus.modules.sys.utils;

import com.google.common.collect.Lists;
import com.jeeplus.modules.sys.entity.Menu;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class RouterUtils {


    public static List<Menu> getRoutersByMenu() {
        Menu menu = UserUtils.getTopMenu();
        menu = getChildOfTree(menu, 0, UserUtils.getMenuList());
        return menu.getChildren();
    }

    private static Menu getChildOfTree(Menu menuItem1, int level, List<Menu> menuList) {

        Menu menuItem = new Menu();
             BeanUtils.copyProperties(menuItem1,menuItem);
            menuItem.setChildren(Lists.newArrayList());
            for (Menu child : menuList) {
                if ( child.getParentId().equals(menuItem.getId())) {
                    menuItem.getChildren().add(getChildOfTree(child, level + 1, menuList));
                }
            }


        return menuItem;
    }

}
