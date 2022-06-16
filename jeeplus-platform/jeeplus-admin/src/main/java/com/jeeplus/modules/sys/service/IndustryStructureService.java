package com.jeeplus.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.core.utils.TreeUtils;
import com.jeeplus.modules.sys.entity.IndustryStructure;
import com.jeeplus.modules.sys.mapper.IndustryStructureMapper;
import com.jeeplus.modules.sys.utils.DictUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class IndustryStructureService extends ServiceImpl<IndustryStructureMapper, IndustryStructure> {

    public List<IndustryStructure> treeData() {
        List<IndustryStructure> list = list();
        if (CollectionUtils.isNotEmpty(list)) {
            return TreeUtils.tree(list, "0");
        }
        return Collections.emptyList();
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        CacheUtils.remove(DictUtils.CACHE_INDUSTRY_STRUCTURE_MAP);
        CacheUtils.put(DictUtils.CACHE_INDUSTRY_STRUCTURE_MAP, lambdaQuery()
                .orderByAsc(IndustryStructure::getId)
                .list());
    }

}
