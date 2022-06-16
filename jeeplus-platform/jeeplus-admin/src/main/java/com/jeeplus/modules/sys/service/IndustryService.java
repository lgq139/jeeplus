package com.jeeplus.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.core.utils.TreeUtils;
import com.jeeplus.modules.sys.entity.Industry;
import com.jeeplus.modules.sys.mapper.IndustryMapper;
import com.jeeplus.modules.sys.utils.DictUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class IndustryService extends ServiceImpl<IndustryMapper, Industry> {

    /**
     * 加载树形数据
     */
    public List<Industry> treeData() {
       List<Industry> list = lambdaQuery().orderByAsc(Industry::getId).list();
       if (CollectionUtils.isNotEmpty(list)) {
           return TreeUtils.tree(list, "0");
       }
       return Collections.emptyList();
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        CacheUtils.remove(DictUtils.CACHE_INDUSTRY_MAP);
        CacheUtils.put(DictUtils.CACHE_INDUSTRY_MAP, lambdaQuery().orderByAsc(Industry::getId).list());
    }

}
