package com.jeeplus.core.ext.persistence;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeeplus.config.swagger.IgnoreSwaggerParameter;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 数据Entity类
 *
 * @author jeeplus
 * @version 2017-05-16
 */
public abstract class DataEntity<T> extends BaseEntity<T> {

    private static final long serialVersionUID = 1L;

    /** 备注 */
    protected String remarks;
    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    @IgnoreSwaggerParameter
    protected String createBy;
    /** 创建日期 */
    @TableField(fill = FieldFill.INSERT)
    @IgnoreSwaggerParameter
    protected Date createDate;
    /** 更新者 */
    @TableField(fill = FieldFill.UPDATE)
    @IgnoreSwaggerParameter
    protected String updateBy;
    /** 更新日期 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @IgnoreSwaggerParameter
    protected Date updateDate;
    /** 删除标记（0：正常；1：删除；2：审核） */
//    @TableLogic(value = "0", delval = "1")
    @IgnoreSwaggerParameter
    protected String delFlag;

    public DataEntity() {
        super();
        this.delFlag = DEL_FLAG_NORMAL;
    }

    public DataEntity(String id) {
        super(id);
    }

    @Length(min = 0, max = 255)
    public String getRemarks() {
        return remarks;
    }

    @SuppressWarnings("unchecked")
    public T setRemarks(String remarks) {
        this.remarks = remarks;
        return (T) this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @JsonIgnore
    @Length(min = 1, max = 1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

}
