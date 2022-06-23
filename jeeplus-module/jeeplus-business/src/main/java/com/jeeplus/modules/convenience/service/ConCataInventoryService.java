package com.jeeplus.modules.convenience.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.convenience.entity.ConCataInventory;
import com.jeeplus.modules.convenience.entity.ConCataMaintain;
import com.jeeplus.modules.convenience.mapper.ConCataInventoryMapper;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConCataInventoryService extends ServiceImpl<ConCataInventoryMapper, ConCataInventory>  {

    @Autowired
    private ConCataInventoryMapper conCataInventoryMapper;

    @Transactional(readOnly = false)
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

    public List<Object> getBaseCodeList(String orgCode) {
        return conCataInventoryMapper.getBaseCodeList(orgCode);
    }
}
