package com.jeeplus.modules.catalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.catalog.entity.ConCataImport;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ConCataImportMapper extends BaseMapper<ConCataImport> {
}
