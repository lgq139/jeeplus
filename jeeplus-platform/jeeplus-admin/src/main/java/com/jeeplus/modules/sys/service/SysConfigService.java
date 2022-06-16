package com.jeeplus.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.modules.sys.entity.SysConfig;
import com.jeeplus.modules.sys.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> {

	public SysConfig get(String id) {
		if (CacheUtils.get("sys.config", id) == null) {
			CacheUtils.put("sys.config", id, getById(id));
		}
		return (SysConfig) CacheUtils.get("sys.config", id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean save(SysConfig sysConfig) {
		boolean res =save(sysConfig);
		CacheUtils.remove("sys.config", sysConfig.getId());
		return res;
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(SysConfig sysConfig) {
		super.removeById(sysConfig);
		CacheUtils.remove("sys.config", sysConfig.getId());
	}

}
