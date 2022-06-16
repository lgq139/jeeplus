package com.jeeplus.modules.sys.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import com.jeeplus.core.ext.persistence.DataEntity;

/**
 * 数据字典Entity
 * @author lgf
 * @version 2017-01-16
 */
@TableName("sys_dict_type")
public class DictType extends DataEntity<DictType> {

	private static final long serialVersionUID = 1L;
	/** 类型 */
	private String type;
	/** 描述 */
	private String description;
	/** 字典类型 1列表, 2树 */
	private String layout;
	/** 是否对外开放 1开放, 2不开放 */
	private String open;
	/** 字典分组，系统字典，默认分组 */
	private String dictGroup;

	@TableField(exist = false)
	private List<DictValue> dictValueList = Lists.newArrayList();
	@TableField(exist = false)
	private List<DictTreeValue> dictTreeValueList = Lists.newArrayList();

	public DictType() {
		super();
	}

	public DictType(String id){
		super(id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getDictGroup() {
		return dictGroup;
	}

	public void setDictGroup(String dictGroup) {
		this.dictGroup = dictGroup;
	}

	public List<DictValue> getDictValueList() {
		return dictValueList;
	}

	public void setDictValueList(List<DictValue> dictValueList) {
		this.dictValueList = dictValueList;
	}

	public List<DictTreeValue> getDictTreeValueList() {
		return dictTreeValueList;
	}

	public void setDictTreeValueList(List<DictTreeValue> dictTreeValueList) {
		this.dictTreeValueList = dictTreeValueList;
	}
}
