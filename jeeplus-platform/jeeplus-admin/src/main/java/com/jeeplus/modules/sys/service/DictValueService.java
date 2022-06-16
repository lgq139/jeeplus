package com.jeeplus.modules.sys.service;

import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.core.ext.service.CrudService;
import com.jeeplus.core.utils.TreeUtils;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.mapper.DictValueMapper;
import com.jeeplus.modules.sys.utils.DictUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DictValueService extends CrudService<DictValueMapper, DictValue> {

    /**
     * 获取树形结构的字典值列表
     *
     * @param dictTypeId 字典类型
     * @return
     */
    public List<DictValue> treeData(String dictTypeId) {
        List<DictValue> dictValueList = lambdaQuery()
                .eq(DictValue::getDictTypeId, dictTypeId)
                .orderByAsc(DictValue::getSort)
                .list();
        if (CollectionUtils.isNotEmpty(dictValueList)) {
            return TreeUtils.tree(dictValueList, "0");
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = false)
    @Override
    public boolean saveOrUpdate(DictValue entity) {
        if ("0".equals(entity.getParentId())) {
            entity.setParentIds("0,");
        } else {
            DictValue dictValue = getById(entity.getParentId());
            if (dictValue != null) {
                entity.setParentIds(dictValue.getParentIds() + dictValue.getId() + ",");
            }
        }
        boolean flag = super.saveOrUpdate(entity);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
        return flag;
    }

    @Transactional(readOnly = false)
    @Override
    public boolean save(DictValue entity) {
        boolean flag = super.save(entity);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
        return flag;
    }

    @Transactional(readOnly = false)
    @Override
    public boolean removeById(DictValue entity) {
        boolean flag = super.removeById(entity);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
        return flag;
    }

    @Transactional(readOnly = false)
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        boolean flag = super.removeByIds(idList);
        CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
        return flag;
    }
}
