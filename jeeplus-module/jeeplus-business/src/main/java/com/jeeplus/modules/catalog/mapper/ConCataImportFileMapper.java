package com.jeeplus.modules.catalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.catalog.entity.ConCataImportFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ConCataImportFileMapper extends BaseMapper<ConCataImportFile> {
}
