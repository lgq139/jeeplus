package com.jeeplus.modules.catalog.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.catalog.entity.GovCataImport;
import com.jeeplus.modules.catalog.mapper.GovCataImportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GovCataImportService extends ServiceImpl<GovCataImportMapper, GovCataImport>  {
}
