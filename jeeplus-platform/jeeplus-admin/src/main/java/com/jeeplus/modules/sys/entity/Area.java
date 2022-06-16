package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.TreeEntity;
import org.hibernate.validator.constraints.Length;

@TableName("sys_area")
public class Area extends TreeEntity<Area> {

	/** 区域编码 */
	private String code;
	/** 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县） */
	private String type;

	@Override
    public Area getParent() {
		return parent;
	}

	@Override
	public void setParent(Area parent) {
		this.parent = parent;
	}

	@Length(min = 1, max = 1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min = 1, max = 100)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}
}
