package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.jeeplus.modules.sys.entity.SysConfig;

@Mapper
@Repository
public interface SysConfigMapper extends BaseMapper<SysConfig> {

}
