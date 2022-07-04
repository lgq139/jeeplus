package com.jeeplus.modules.theme.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.DataEntity;

import java.util.Date;

/**
 * 主题表;
 *
 * @author : http://www.chiner.pro
 * @date : 2022-7-2
 */
@TableName("j_theme")
public class JTheme extends DataEntity<JTheme> {
    /**
     * 主键
     */
    private static final long serialVersionUID = 1L;
    /**
     * 主题名称
     */
    private String themeName;
    /**
     * 主题编码
     */
    private String themeCode;
    /**
     * 服务对象（字典值）
     */
    private String serviceObject;
    /**
     * 排序
     */
    private Integer sortOrder;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateDate;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 删除标记
     */
    private String delFlag;

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeCode() {
        return themeCode;
    }

    public void setThemeCode(String themeCode) {
        this.themeCode = themeCode;
    }

    public String getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(String serviceObject) {
        this.serviceObject = serviceObject;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String getUpdateBy() {
        return updateBy;
    }

    @Override
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public Date getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public JTheme setRemarks(String remarks) {
        this.remarks = remarks;
        return null;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
