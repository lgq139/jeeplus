package com.jeeplus.modules.convenience.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.DataEntity;
import lombok.Data;

@Data
@TableName("con_cata_inventory")
public class ConCataInventory extends DataEntity<ConCataInventory> {

    private static final long serialVersionUID = 1L;

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
    /** 是否最大版本（1：是 0否） */
    private String maxVersion;
    /** 部门编码 */
    private String orgCode;
    /** 部门名称 */
    private String orgName;

}
