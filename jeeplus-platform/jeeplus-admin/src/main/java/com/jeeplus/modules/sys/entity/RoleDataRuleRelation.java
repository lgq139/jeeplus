package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_role_datarule")
public class RoleDataRuleRelation implements Serializable {

    private String roleId;

    private String dataruleId;

}
