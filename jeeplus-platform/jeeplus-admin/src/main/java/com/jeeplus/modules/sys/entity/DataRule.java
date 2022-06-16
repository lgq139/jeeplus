package com.jeeplus.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.DataEntity;
import org.apache.commons.lang3.StringEscapeUtils;

import com.jeeplus.common.utils.StringUtils;

/**
 * 数据权限Entity
 * @author lgf
 * @version 2017-04-02
 */
@TableName("sys_datarule")
public class DataRule extends DataEntity<DataRule> {
	/** 所属菜单 */
	private String menuId;
	/** 数据规则名称 */
	private String name;
	/** 实体类名 */
	private String className;
	/** 规则字段 */
	@TableField("t_field")
	private String field;
	/** 规则条件 */
	@TableField("t_express")
	private String express;
	/** 规则值 */
	@TableField("t_value")
	private String value;
	/** 自定义sql */
	private String sqlSegment;

	public DataRule() {
		super();
	}

	public DataRule(String id){
		super(id);
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSqlSegment() {
		return sqlSegment;
	}

	public void setSqlSegment(String sqlSegment) {
		this.sqlSegment = sqlSegment;
	}

	public String getDataScopeSql() {
		StringBuffer sqlBuffer = new StringBuffer();
		if (StringUtils.isNotBlank(field) && StringUtils.isNotBlank(value)) {
			sqlBuffer.append(" AND " + field + " " + StringEscapeUtils.unescapeHtml4(express) + " " + value + " ");
		}
		if (StringUtils.isNotBlank(sqlSegment)) {
			sqlBuffer.append(" AND " + StringEscapeUtils.unescapeHtml4(sqlSegment) + " ");
		}

		return sqlBuffer.toString();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
