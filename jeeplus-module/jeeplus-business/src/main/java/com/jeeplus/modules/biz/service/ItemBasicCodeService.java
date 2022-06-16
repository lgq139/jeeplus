package com.jeeplus.modules.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.biz.entity.ItemBasicCode;
import com.jeeplus.modules.biz.mapper.ItemBasicCodeMapper;
import com.jeeplus.modules.sys.utils.DictUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 四级四同目录Service
 *
 * @author FEAR
 * @version 2021-11-09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ItemBasicCodeService extends ServiceImpl<ItemBasicCodeMapper, ItemBasicCode> {

    private static final Logger log = LoggerFactory.getLogger(ItemBasicCodeService.class);

    @Deprecated
    public List<ItemBasicCode> listOfBasicMaterial() {
        LambdaQueryWrapper<ItemBasicCode> wrapper = Wrappers.lambdaQuery(ItemBasicCode.class);
        // TODO:
        return baseMapper.findListOfItemMaterial(wrapper);
    }

    @Override
    public boolean saveOrUpdate(ItemBasicCode entity) {
        // 设置阶段的Label标签
        if (StringUtils.isNotBlank(entity.getStageCode())) {
            entity.setStageName(DictUtils.getDictLabel(entity.getStageCode(), "basic_item_stage", null));
        }
        return super.saveOrUpdate(entity);
    }
}
