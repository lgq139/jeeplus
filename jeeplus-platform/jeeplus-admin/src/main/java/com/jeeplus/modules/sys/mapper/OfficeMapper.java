package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.Office;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 机构MAPPER接口
 */
@Mapper
@Repository
public interface OfficeMapper extends BaseMapper<Office> {

}
