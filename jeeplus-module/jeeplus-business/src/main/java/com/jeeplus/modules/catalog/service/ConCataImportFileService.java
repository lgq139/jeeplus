package com.jeeplus.modules.catalog.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.catalog.entity.ConCataImportFile;
import com.jeeplus.modules.catalog.mapper.ConCataImportFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConCataImportFileService extends ServiceImpl<ConCataImportFileMapper, ConCataImportFile>{
}
