package com.jeeplus.core.oauth;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StandardRole implements Serializable {

    public static final String ALL = "all";
    public static final String DELETE = "delete";
    public static final String INSTALL = "install";
    public static final String UPDATE = "update";

    /**
     * 同步类型：取值为：
     * “all”：所有角色
     * “delete”：删除角色
     * “insert”：新增角色
     * “update”：更新角色
     */
    private String syncType;
    /**
     * 角色列表信息
     */
    private List<Role> roles;

    @Data
    public static class Role {
        /** 角色id */
        private String id;
        /** 角色编码 */
        private String code;
        /** 角色名称 */
        private String name;
        /** 角色描述 */
        private String  roleDesc;
        /** 是否可用（1：可用；2：不可用） */
        private String  useable;
    }
}
