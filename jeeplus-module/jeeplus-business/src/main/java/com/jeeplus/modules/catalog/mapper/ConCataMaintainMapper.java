package com.jeeplus.modules.catalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.catalog.entity.ConCataMaintain;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ConCataMaintainMapper extends BaseMapper<ConCataMaintain> {
}
