/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.ApiClient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * api密钥MAPPER接口
 * @author FEAR
 * @version 2021-04-01
 */
@Mapper
@Repository
public interface ApiClientMapper extends BaseMapper<ApiClient> {

}
