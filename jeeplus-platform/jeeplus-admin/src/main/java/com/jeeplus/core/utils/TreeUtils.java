package com.jeeplus.core.utils;

import com.jeeplus.core.persistence.Tree;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TreeUtils {

    public static <E extends Tree<E>> List<E> tree(List<E> data, String parentId) {
        return TreeUtils.getChildren(parentId, data);
    }

    private static <E extends Tree<E>> List<E> getChildren(String parentId, List<E> roots) {
        List<E> collect = roots.stream().filter(item -> item.getParentId().equals(parentId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            roots.removeAll(collect);
            for (E item : collect) {
                item.setChildren(getChildren(item.getId(), roots));
            }
        } else {
            return roots;
        }
        return collect;
    }

}
