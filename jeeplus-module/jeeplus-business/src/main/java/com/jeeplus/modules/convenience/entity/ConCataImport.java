package com.jeeplus.modules.convenience.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import com.jeeplus.core.ext.persistence.DataEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 导入便民目录信息
 */
@Data
@TableName("con_cata_import")
public class ConCataImport extends DataEntity<ConCataImport> {

    private static final long serialVersionUID = 1L;

    private String uuid;
    /** 导入文件id */
    private String importFileUuid;
    /** 目录基本编码 */
    @ExcelField(title = "基本编码（必填）",sort = 0)
    @NotBlank
    private String baseCode;
    /** 目录名称 */
    @NotBlank
    @ExcelField(title = "目录名称（必填）",sort = 1)
    private String cataName;
    /** 事项类型 */
    @NotBlank
    @ExcelField(title = "事项类型（必填）",sort = 2)
    private String cataType;
    /** 行使层级 */
    @NotBlank
    @ExcelField(title = "行使层级（必填）",sort = 3)
    private String cataLevel;
    /** 查验结果 */
    private String checkResult;
    /** 导入报告 */
    private String importReport;
    /** 目录版本号 */
    @ExcelField(title = "版本号（必填,且为正数）")
    @NotNull
    private Integer cataVersion;
   /* @ExcelField(title = "备注")
    private String remarks;*/

}
