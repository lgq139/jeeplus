package com.jeeplus.modules.convenience.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.convenience.entity.ConCataInventory;
import com.jeeplus.modules.convenience.entity.ConCataMaintain;
import com.jeeplus.modules.convenience.mapper.ConCataInventoryMapper;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConCataInventoryService extends ServiceImpl<ConCataInventoryMapper, ConCataInventory>  {

    public boolean saveCata(ConCataMaintain cataMaintain) {
        ConCataInventory cataInventory = new ConCataInventory();
        User user = UserUtils.getUser();
        cataInventory.setBaseCode(cataMaintain.getBaseCode());
        cataInventory.setCataName(cataMaintain.getCataName());
        cataInventory.setCataType(cataMaintain.getCataType());
        cataInventory.setCataLevel(cataMaintain.getCataLevel());
        cataInventory.setCataVersion(cataMaintain.getCataVersion());
        cataInventory.setOrgCode(user.getOrgCode());
        cataInventory.setOrgName(user.getOrgName());
        return super.save(cataInventory);
    }
}
