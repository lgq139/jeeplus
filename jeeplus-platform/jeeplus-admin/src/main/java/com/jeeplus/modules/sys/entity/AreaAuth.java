package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.TreeEntity;

/**
 * 区域授权
 */
@TableName("sys_area_auth")
public class AreaAuth extends TreeEntity<AreaAuth> {

	private static final long serialVersionUID = 1L;
	/** 区划名称 */
	private String name;
	/** 区域编码 */
	private String code;
	/** 区划简码 */
	private String sortCode;
	/** 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县） */
	private String level;
	/** 授权建立场景：1：授权 0：不授权 */
	private String authCatalog;
	/** 授权发布场景 1：授权 0：不授权 */
	private String authDeploy;

	public AreaAuth(){
		super();
	}

	public AreaAuth(String id){
		super(id);
	}

	@Override
    public AreaAuth getParent() {
		return parent;
	}

	@Override
	public void setParent(AreaAuth parent) {
		this.parent = parent;
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

	public String getSortCode() {
		return sortCode;
	}

	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getAuthCatalog() {
		return authCatalog;
	}

	public void setAuthCatalog(String authCatalog) {
		this.authCatalog = authCatalog;
	}

	public String getAuthDeploy() {
		return authDeploy;
	}

	public void setAuthDeploy(String authDeploy) {
		this.authDeploy = authDeploy;
	}
}
