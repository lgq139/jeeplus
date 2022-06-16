package com.jeeplus.modules.sys.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.Industry;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IndustryMapper extends BaseMapper<Industry> {
}
