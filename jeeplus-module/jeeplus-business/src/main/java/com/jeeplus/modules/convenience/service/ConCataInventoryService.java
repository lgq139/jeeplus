package com.jeeplus.modules.convenience.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.convenience.entity.ApproveCataImport;
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

    //便民目录认领
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
        cataInventory.setStatus(cataMaintain.getImportStatus());
        cataInventory.setType("2");
        return super.save(cataInventory);
    }

    //本部门认领目录编码集合
    public List<Object> getBaseCodeList(String orgCode) {
        return conCataInventoryMapper.getBaseCodeList(orgCode);
    }

    //政务目录导入保存
    @Transactional(readOnly = false)
    public boolean saveApproveCata(ApproveCataImport approveCataImport) {
        ConCataInventory cataInventory = new ConCataInventory();
        cataInventory.setBaseCode(approveCataImport.getBaseCode());
        cataInventory.setCataName(approveCataImport.getCataName());
        cataInventory.setCataType(approveCataImport.getCataType());
        cataInventory.setCataLevel(approveCataImport.getCataLevel());
        cataInventory.setCataVersion(approveCataImport.getCataVersion());
        cataInventory.setOrgCode(approveCataImport.getOrgCode());
        cataInventory.setOrgName(approveCataImport.getOrgName());
        cataInventory.setStatus(approveCataImport.getStatus());
        cataInventory.setByLaws(approveCataImport.getByLaws());
        cataInventory.setType("1");
        return super.save(cataInventory);
    }
}
