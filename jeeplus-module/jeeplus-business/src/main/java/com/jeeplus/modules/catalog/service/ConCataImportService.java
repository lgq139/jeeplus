package com.jeeplus.modules.catalog.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.catalog.entity.ConCataImport;
import com.jeeplus.modules.catalog.mapper.ConCataImportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConCataImportService extends ServiceImpl<ConCataImportMapper, ConCataImport>  {
}
