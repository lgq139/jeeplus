import {post, get, deleted} from '../../tool'

/**
 * 获取任务详情
 * @param jobId 任务ID
 * @return {*}
 */
export function getScheduleJob(jobId) {
  return get(`/quartz/scheduleJob/queryById?id=${jobId}`)
}

export function listScheduleJob(params) {
  return get('/quartz/scheduleJob/list', params)
}

/**
 * 保存更新任务定义
 * @param params
 * @return {*}
 */
export function saveScheduleJob(params) {
  return post('/quartz/scheduleJob/save', params)
}

/**
 * 删除任务
 * @param params - {ids: ''}
 * @return {*}
 */
export function deleteScheduleJob(params) {
  return post('/quartz/scheduleJob/delete', params)
}

/**
 * 启动/恢复任务执行
 * @param jobId 任务ID
 * @return {*}
 */
export function resumeScheduleJob(jobId) {
  return post(`/quartz/scheduleJob/resume?id=${jobId}`)
}

/**
 * 暂停任务执行
 * @param jobId 任务ID
 */
export function stopScheduleJob(jobId) {
  return post(`/quartz/scheduleJob/stop?id=${jobId}`);
}

/**
 * 立即执行一次任务
 * @param jobId 任务ID
 */
export function startNowScheduleJob(jobId) {
  return post(`/quartz/scheduleJob/startNow?id=${jobId}`)
}


//----------------- 日志

export function listScheduleJobLog(params) {
  return get('/monitor/jobLog/list', params)
}

export function cleanScheduleJobLogByJobId(jobId) {
  return get(`/monitor/jobLog/clean?jobId=${jobId}`)
}
