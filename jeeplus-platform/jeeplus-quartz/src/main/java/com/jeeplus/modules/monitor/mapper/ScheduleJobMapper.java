/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.monitor.entity.ScheduleJob;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;;

/**
 * 定时任务MAPPER接口
 *
 * @author lgf
 * @version 2017-02-04
 */
@Mapper
@Repository
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {


}
