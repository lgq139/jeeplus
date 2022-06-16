package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_role_menu")
public class RoleMenuRelation implements Serializable {

    private String roleId;

    private String menuId;

}
