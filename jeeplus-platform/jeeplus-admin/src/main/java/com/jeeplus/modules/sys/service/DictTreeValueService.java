package com.jeeplus.modules.sys.service;


import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.ext.service.CrudService;
import com.jeeplus.core.utils.TreeUtils;
import com.jeeplus.modules.sys.entity.DictTreeValue;
import com.jeeplus.modules.sys.mapper.DictTreeValueMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DictTreeValueService extends CrudService<DictTreeValueMapper, DictTreeValue> {

    /**
     * 获取树形结构的字典值列表
     *
     * @param dictTypeId 字典类型
     * @return
     */
    public List<DictTreeValue> treeData(String dictTypeId) {
        List<DictTreeValue> dictTreeValueList = lambdaQuery().eq(DictTreeValue::getDictTypeId, dictTypeId).list();
        if (CollectionUtils.isNotEmpty(dictTreeValueList)) {
            return TreeUtils.tree(dictTreeValueList, "0");
        }
        return Collections.emptyList();
    }

    @Override
    public boolean saveOrUpdate(DictTreeValue entity) {
        if ("0".equals(entity.getParentId())) {
            entity.setParentIds("0,");
        } else {
            DictTreeValue dictTreeValue = getById(entity.getParentId());
            if (dictTreeValue != null) {
                entity.setParentIds(dictTreeValue.getParentIds() + dictTreeValue.getId() + ",");
            }
        }
        return super.saveOrUpdate(entity);
    }
}
