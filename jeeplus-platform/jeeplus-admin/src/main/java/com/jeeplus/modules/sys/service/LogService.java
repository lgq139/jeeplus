package com.jeeplus.modules.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.core.ext.service.CrudService;
import com.jeeplus.modules.sys.entity.Log;
import com.jeeplus.modules.sys.mapper.LogMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 日志Service
 * @author jeeplus
 * @version 2017-05-16
 */
@Service
@Transactional(readOnly = true)
public class LogService extends CrudService<LogMapper, Log> {

	@Autowired
	private LogMapper logMapper;

    public IPage<Log> findPage(Page<Log> page, Log log) {
		// 设置默认时间范围，默认当前月
		if (log.getBeginDate() == null){
			log.setBeginDate(DateUtils.setDays(DateUtils.parseDate(DateUtils.getDate()), 1));
		}
		if (log.getEndDate() == null){
			log.setEndDate(DateUtils.addMonths(log.getBeginDate(), 1));
		}
		LambdaQueryChainWrapper<Log> wrapper = this.lambdaQuery();
		wrapper.between(!Objects.isNull(log.getBeginDate()) && !Objects.isNull(log.getEndDate()),
				Log::getCreateDate, log.getBeginDate(), log.getEndDate());
		wrapper.eq(StringUtils.isNotBlank(log.getUserId()), Log::getUserId, log.getUserId());
		wrapper.like(StringUtils.isNotBlank(log.getTitle()), Log::getTitle, log.getTitle());
		wrapper.eq(StringUtils.isNotBlank(log.getCreateBy()), Log::getCreateBy, log.getCreateBy());
		wrapper.like(StringUtils.isNotBlank(log.getRequestUri()), Log::getRequestUri, log.getRequestUri());
		wrapper.eq(StringUtils.isNotBlank(log.getType()), Log::getType, log.getType());
		wrapper.orderByDesc(Log::getCreateDate);
		return wrapper.page(page);
	}

	/**
	 * 删除全部数据
	 */
	@Transactional(readOnly = false)
	public void empty(){
		logMapper.empty();
	}

}
