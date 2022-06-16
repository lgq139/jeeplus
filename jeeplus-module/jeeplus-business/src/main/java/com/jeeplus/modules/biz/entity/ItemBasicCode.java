package com.jeeplus.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeeplus.core.ext.persistence.DataEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 四级四同目录
 *
 * @author FEAR
 * @version 2021-11-09
 */
@TableName("s_item_basic_code")
public class ItemBasicCode extends DataEntity<ItemBasicCode> {

    private static final long serialVersionUID = 1L;
    /** 事项目录名称 */
    private String name;
    /** 事项目录编码 */
    private String code;
    /** 事项目录名称（工建） */
    private String gjName;
    /** 事项目录编码（工建） */
    private String gjCode;
    /** 事项目录名称（发改） */
    private String fgName;
    /** 事项目录编码（发改） */
    private String fgCode;
    /** 业务指导部门编码 */
    private String superviseOrgCode;
    /** 业务指导部门名称 */
    private String superviseOrgName;
    /** 事项类型 */
    private String type;
    /** 行使层级 */
    private String approvalLevel;
    /** 表单数据获取方式 */
    private String formType;
    /** 表单ID */
    private String formId;
    /** 表单名称 */
    private String formName;
    /** 转发地址 */
    private String forwardUrl;
    /** 是否出证；需要制证的事项，将在制证后出现出件按钮；不需要出证的按钮，将在办结后出现出件按钮 */
    private String license;
    /** 标准阶段编码 */
    private String stageCode;
    /** 标准阶段名称 */
    private String stageName;
    @JsonIgnore
    @TableField(exist = false)
    private String sort;
    @TableField(exist = false)
    private boolean disabled = true;

    public ItemBasicCode() {
        super();
    }

    public ItemBasicCode(String id) {
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

    public String getGjName() {
        return gjName;
    }

    public void setGjName(String gjName) {
        this.gjName = gjName;
    }

    public String getGjCode() {
        return gjCode;
    }

    public void setGjCode(String gjCode) {
        this.gjCode = gjCode;
    }

    public String getFgName() {
        return fgName;
    }

    public void setFgName(String fgName) {
        this.fgName = fgName;
    }

    public String getFgCode() {
        return fgCode;
    }

    public void setFgCode(String fgCode) {
        this.fgCode = fgCode;
    }

    public String getSuperviseOrgCode() {
        return superviseOrgCode;
    }

    public void setSuperviseOrgCode(String superviseOrgCode) {
        this.superviseOrgCode = superviseOrgCode;
    }

    public String getSuperviseOrgName() {
        return superviseOrgName;
    }

    public void setSuperviseOrgName(String superviseOrgName) {
        this.superviseOrgName = superviseOrgName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(String approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getStageCode() {
        return stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ItemBasicCode that = (ItemBasicCode) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
