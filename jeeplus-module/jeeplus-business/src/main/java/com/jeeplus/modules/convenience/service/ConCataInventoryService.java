package com.jeeplus.modules.convenience.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.convenience.entity.ConCataInventory;
import com.jeeplus.modules.convenience.mapper.ConCataInventoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConCataInventoryService extends ServiceImpl<ConCataInventoryMapper, ConCataInventory>  {
}
