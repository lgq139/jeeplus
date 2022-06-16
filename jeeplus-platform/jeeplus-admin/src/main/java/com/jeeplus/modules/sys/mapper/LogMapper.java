package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.Log;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

;

/**
 * 日志MAPPER接口
 */
@Mapper
@Repository
public interface LogMapper extends BaseMapper<Log> {

	void empty();
}
