package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.DataEntity;

import java.util.Date;

/**
 * 白名单Entity
 */
@TableName("sys_api_whitelist")
public class ApiWhitelist extends DataEntity<ApiWhitelist> {

	private static final long serialVersionUID = 1L;
	private String systemName;		// 系统名称
	private String ip;		// IP地址
	private Date expiredDate;		// 过期时间
	private String description;		// 描述

	public ApiWhitelist() {
		super();
	}

	public ApiWhitelist(String id){
		super(id);
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
