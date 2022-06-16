package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeeplus.core.ext.persistence.TreeEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * 菜单Entity
 */
@TableName("sys_menu")
public class Menu extends TreeEntity<Menu> {

	private String name;
	/** 链接 */
	private String href;
	/** 目标（ mainFrame、_blank、_self、_parent、_top） */
	private String target;
	/** 图标 */
	private String icon;
	/** 是否在菜单中显示（1：显示；0：不显示） */
	private String isShow;
	/** 按钮类型 */
	@TableField("menu_type")
	private String type;
	/** 权限标识 */
	private String permission;

	private  String backgroundType;

	@TableField(exist = false)
	private List<DataRule> dataRuleList;
	@TableField(exist = false)
	private String userId;

	public Menu(){
		super();
		this.sort = 30;
		this.isShow = "1";
		this.type="1";
	}

	public Menu(String id){
		super(id);
	}

	@Override
	public Menu getParent() {
		return parent;
	}

	@Override
	public void setParent(Menu parent) {
		this.parent = parent;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=0, max=2000)
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Length(min=0, max=20)
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@Length(min=0, max=100)
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Length(min=1, max=1)
	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	@Length(min=0, max=200)
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@JsonIgnore
	public static void sortList(List<Menu> list, List<Menu> sourcelist, String parentId, boolean cascade) {
		for (int i = 0; i < sourcelist.size(); i++) {
			Menu e = sourcelist.get(i);
			if (StringUtils.isNotBlank(e.getParentId()) && e.getParentId().equals(parentId)) {
				list.add(e);
				if (cascade) {
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j = 0; j < sourcelist.size(); j++) {
						Menu child = sourcelist.get(j);
						if (StringUtils.isNotBlank(child.getParentId()) && child.getParentId().equals(e.getId())) {
							sortList(list, sourcelist, e.getId(), true);
							break;
						}
					}
				}
			}
		}
	}

	@JsonIgnore
	public static String getRootId(){
		return "1";
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<DataRule> getDataRuleList() {
		return dataRuleList;
	}

	public void setDataRuleList(List<DataRule> dataRuleList) {
		this.dataRuleList = dataRuleList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBackgroundType() {
		return backgroundType;
	}

	public void setBackgroundType(String backgroundType) {
		this.backgroundType = backgroundType;
	}

}
