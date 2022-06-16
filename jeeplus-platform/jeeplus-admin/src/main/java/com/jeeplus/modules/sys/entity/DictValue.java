/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.TreeEntity;

/**
 * 数据字典Entity
 * @author FEAR
 *
 */
@TableName("sys_dict_value")
public class DictValue extends TreeEntity<DictValue> {

	private static final long serialVersionUID = 1L;
	/** 类型 */
	private String dictType;
	/** 类型ID */
	private String dictTypeId;
	/** 标签名 */
	private String label;
	/** 键值 */
	private String value;

	public DictValue() {
		super();
	}

	public DictValue(String id){
		super(id);
	}

	public String getDictType() {
		return dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}

	public String getDictTypeId() {
		return dictTypeId;
	}

	public void setDictTypeId(String dictTypeId) {
		this.dictTypeId = dictTypeId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public DictValue getParent() {
		return parent;
	}

	@Override
	public void setParent(DictValue parent) {
		this.parent = parent;
	}
}
