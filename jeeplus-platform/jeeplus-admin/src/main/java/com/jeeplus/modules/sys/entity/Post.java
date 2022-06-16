package com.jeeplus.modules.sys.entity;

import com.jeeplus.core.ext.persistence.DataEntity;

import javax.validation.constraints.NotNull;


/**
 * 岗位Entity
 */
public class Post extends DataEntity<Post> {
	/** 岗位名称 */
	private String name;
	/** 岗位编码 */
	private String code;
	/** 岗位类型 */
	private String type;
	/** 岗位状态 */
	private String status;
	/** 岗位排序 */
	private Integer sort;

	public Post() {
		super();
	}

	public Post(String id){
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@NotNull(message="岗位排序不能为空")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

}
