package com.jeeplus.modules.sys.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class IndustryStructureImportModel {

    @ExcelProperty("CODE_ID")
    private String id;
    @ExcelProperty("CODE_NAME")
    private String label;
    @ExcelProperty("CODE_VALUE")
    private String value;
    @ExcelProperty("CODE_PARENT")
    private String parentId;

}
