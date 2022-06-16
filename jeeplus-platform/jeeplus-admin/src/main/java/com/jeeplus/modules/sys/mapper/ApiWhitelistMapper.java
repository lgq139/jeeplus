package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.ApiWhitelist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 白名单MAPPER接口
 * @author FEAR
 * @version 2021-08-19
 */
@Mapper
@Repository
public interface ApiWhitelistMapper extends BaseMapper<ApiWhitelist> {

}
