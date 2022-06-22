package com.jeeplus.modules.convenience.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.convenience.entity.ConCataImportFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ConCataImportFileMapper extends BaseMapper<ConCataImportFile> {
}
