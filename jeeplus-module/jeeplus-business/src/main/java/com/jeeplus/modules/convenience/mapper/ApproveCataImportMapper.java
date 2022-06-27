package com.jeeplus.modules.convenience.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.convenience.entity.ApproveCataImport;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApproveCataImportMapper extends BaseMapper<ApproveCataImport> {
}
