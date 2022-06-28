package com.jeeplus.modules.catalog.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.catalog.entity.ConCataImport;
import com.jeeplus.modules.catalog.entity.ConCataMaintain;
import com.jeeplus.modules.catalog.mapper.ConCataMaintainMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConCataMaintainService extends ServiceImpl<ConCataMaintainMapper, ConCataMaintain>  {

    @Transactional(readOnly = false)
    public boolean saveImportCata(ConCataImport conCataImport) {
        ConCataMaintain cataMaintain = new ConCataMaintain();
        cataMaintain.setImportFileUuid(conCataImport.getImportFileUuid());
        cataMaintain.setImportCataId(conCataImport.getId());
        cataMaintain.setBaseCode(conCataImport.getBaseCode());
        cataMaintain.setCataName(conCataImport.getCataName());
        cataMaintain.setCataType(conCataImport.getCataType());
        cataMaintain.setCataLevel(conCataImport.getCataLevel());
        cataMaintain.setCataVersion(conCataImport.getCataVersion());
        cataMaintain.setImportStatus(conCataImport.getImportStatus());
        return saveCata(cataMaintain);
    }

    @Transactional(readOnly = false)
    public boolean saveCata(ConCataMaintain conCataMaintain) {
        ConCataMaintain entity = super.lambdaQuery()
                .eq(ConCataMaintain::getBaseCode,conCataMaintain.getBaseCode())
                .eq(ConCataMaintain::getMaxVersion,"1").one();
        if (entity != null) {
            if (entity.getCataVersion() > conCataMaintain.getCataVersion()) {
                conCataMaintain.setMaxVersion("0");
            }else {
                entity.setMaxVersion("0");
                super.updateById(entity);
                conCataMaintain.setMaxVersion("1");
            }
        }else {
            conCataMaintain.setMaxVersion("1");
        }
        conCataMaintain.setEnableStatus("1");
        return super.save(conCataMaintain);
    }

}
