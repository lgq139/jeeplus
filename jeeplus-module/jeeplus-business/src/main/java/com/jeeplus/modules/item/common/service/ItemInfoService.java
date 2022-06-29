package com.jeeplus.modules.item.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.item.common.entity.ItemInfo;
import com.jeeplus.modules.item.common.mapper.ItemInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ItemInfoService extends ServiceImpl<ItemInfoMapper, ItemInfo> {

}
