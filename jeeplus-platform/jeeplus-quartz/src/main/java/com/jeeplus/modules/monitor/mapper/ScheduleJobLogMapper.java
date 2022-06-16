package com.jeeplus.modules.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.monitor.entity.ScheduleJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ScheduleJobLogMapper extends BaseMapper<ScheduleJobLog> {

    @Update("truncate table sys_schedule_log")
    void cleanJobInfo();

}
