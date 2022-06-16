package com.jeeplus.modules.monitor.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.monitor.entity.ScheduleJobLog;
import com.jeeplus.modules.monitor.mapper.ScheduleJobLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleJobLogService extends ServiceImpl<ScheduleJobLogMapper, ScheduleJobLog> {

    @Transactional(rollbackFor = Exception.class)
    public void clean() {
        baseMapper.cleanJobInfo();
    }

    @Transactional(rollbackFor = Exception.class)
    public void cleanByJobId(String jobId) {
        lambdaUpdate().eq(ScheduleJobLog::getJobId, jobId).remove();
    }

}
