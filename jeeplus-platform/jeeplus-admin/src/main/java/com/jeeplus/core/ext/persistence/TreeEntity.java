package com.jeeplus.core.ext.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.jeeplus.core.persistence.Tree;

import java.util.List;

/**
 * 数据Entity类
 *
 * @author jeeplus
 * @version 2017-05-16
 */
public abstract class TreeEntity<T> extends DataEntity<T> implements Tree<T> {

	/** 父级编号 */
	@TableField(exist = false)
	protected T parent;
	protected String parentId;
	/** 所有父级编号 */
	protected String parentIds = "";
	/** 排序 */
	protected Integer sort;
	/** 子级 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	protected List<T> children = Lists.newArrayList();

	public TreeEntity() {
		super();
		this.sort = 10;
	}

	public TreeEntity(String id) {
		super(id);
	}

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 *
	 * @return
	 */
	public abstract T getParent();

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 *
	 * @return
	 */
	public abstract void setParent(T parent);

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	@Override
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public List<T> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<T> children) {
		this.children = children;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
