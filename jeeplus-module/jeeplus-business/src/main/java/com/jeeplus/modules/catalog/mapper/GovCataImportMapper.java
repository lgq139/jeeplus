package com.jeeplus.modules.catalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.catalog.entity.GovCataImport;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GovCataImportMapper extends BaseMapper<GovCataImport> {
}
