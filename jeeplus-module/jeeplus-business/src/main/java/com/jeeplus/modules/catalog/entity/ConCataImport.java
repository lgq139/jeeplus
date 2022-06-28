package com.jeeplus.modules.catalog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import com.jeeplus.core.ext.persistence.DataEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 导入便民目录信息
 */
@TableName("con_cata_import")
public class ConCataImport extends DataEntity<ConCataImport> {

    private static final long serialVersionUID = 1L;

    /** 导入文件id */
    private String importFileUuid;
    /** 目录基本编码 */
    @ExcelField(title = "基本编码（必填）",sort = 1)
    @NotBlank
    private String baseCode;
    /** 目录名称 */
    @NotBlank
    @ExcelField(title = "目录名称（必填）",sort = 2)
    private String cataName;
    /** 事项类型 */
    @NotBlank
    @ExcelField(title = "事项类型（必填）",dictType="con_item_type",sort = 3)
    private String cataType;
    /** 行使层级 */
    @NotBlank
    @ExcelField(title = "行使层级（必填）",dictType="item_level",sort = 4)
    private String cataLevel;
    /** 查验结果 */
    private String checkResult;
    /** 导入报告 */
    private String importReport;
    /** 目录版本号 */
    @ExcelField(title = "版本号（必填,且为正数）",sort = 5)
    @NotNull
    private Integer cataVersion;
    /** 导入目录状态（如：1:在用  0:取消） */
    @ExcelField(title = "目录状态（必填）",sort = 6)
    @NotBlank
    private String importStatus;
    @ExcelField(title = "备注",sort = 7)
    private String remarks;
    /** 是否有效数据（0：否  1是） */
    private String isValid;

    public String getImportFileUuid() {
        return importFileUuid;
    }

    public void setImportFileUuid(String importFileUuid) {
        this.importFileUuid = importFileUuid;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getCataName() {
        return cataName;
    }

    public void setCataName(String cataName) {
        this.cataName = cataName;
    }

    public String getCataType() {
        return cataType;
    }

    public void setCataType(String cataType) {
        this.cataType = cataType;
    }

    public String getCataLevel() {
        return cataLevel;
    }

    public void setCataLevel(String cataLevel) {
        this.cataLevel = cataLevel;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getImportReport() {
        return importReport;
    }

    public void setImportReport(String importReport) {
        this.importReport = importReport;
    }

    public Integer getCataVersion() {
        return cataVersion;
    }

    public void setCataVersion(Integer cataVersion) {
        this.cataVersion = cataVersion;
    }

    public String getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(String importStatus) {
        this.importStatus = importStatus;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public ConCataImport setRemarks(String remarks) {
        this.remarks = remarks;
        return null;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
