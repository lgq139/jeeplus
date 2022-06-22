package com.jeeplus.modules.convenience.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.DataEntity;
import lombok.Data;

/**
 * 便民目录维护、目录认领
 */
@Data
@TableName("con_cata_maintain")
public class ConCataMaintain extends DataEntity<ConCataMaintain> {

    private static final long serialVersionUID = 1L;

    /** 导入文件uuid */
    private String importFileUuid;
    /** 导入目录uuid */
    private String importCataUuid;
    /** 目录基本编码 */
    private String baseCode;
    /** 目录名称 */
    private String cataName;
    /** 事项类型 */
    private String cataType;
    /** 行使层级 */
    private String cataLevel;
    /** 目录版本号 */
    private Integer cataVersion;
    /** 停用、启用 */
    private String enableStatus;
    /** 导入目录状态（如：在用） */
    private String importStatus;
    /** 是否最大版本（1：是 0否） */
    private String maxVersion;

}
