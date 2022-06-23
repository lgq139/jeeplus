package com.jeeplus.modules.convenience.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.DataEntity;
import lombok.Data;

/**
 * 便民目录导入文件信息
 */

@Data
@TableName("con_cata_import_file")
public class ConCataImportFile extends DataEntity<ConCataImportFile> {

    private static final long serialVersionUID = 1L;

    private String uuid;
    /** 导入数量 */
    private Integer importCount;
    /** 操作部门编码 */
    private String importOrgCode;
    /** 操作部门名称 */
    private String importOrgName;
    /** 操作层级 */
    private String importLevel;
    /** 操作人员姓名 */
    private String importUserName;
    /** 导入状态（1：正常  0：暂存） */
    private String importStatus;


}
