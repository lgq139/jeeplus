package com.jeeplus.modules.catalog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import com.jeeplus.core.ext.persistence.DataEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 便民目录清单
 */
@Data
@TableName("approve_cata_import")
public class GovCataImport extends DataEntity<GovCataImport> {

    private static final long serialVersionUID = 1L;

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
    /** 目录版本号 */
    @ExcelField(title = "版本号（必填,且为正数）",sort = 5)
    @NotNull
    private Integer cataVersion;
    /** 部门编码 */
    @NotBlank
    @ExcelField(title = "部门编码（必填）",sort = 6)
    private String orgCode;
    /** 部门名称 */
    @NotBlank
    @ExcelField(title = "部门名称（必填）",sort = 7)
    private String orgName;
    /** 目录状态（如：1:在用  0:取消） */
    @NotBlank
    @ExcelField(title = "目录状态（必填）",sort = 8)
    private String status;
    /** 法律法规 */
    @NotBlank
    @ExcelField(title = "法律法规",sort = 9)
    private String byLaws;

    /** 查验结果 */
    private String checkResult;
    /** 导入报告 */
    private String importReport;
    /** 是否有效数据（0：否  1是） */
    private String isValid;

}
