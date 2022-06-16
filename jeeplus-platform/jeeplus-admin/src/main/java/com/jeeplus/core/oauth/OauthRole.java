package com.jeeplus.core.oauth;

public class OauthRole {
    /** 角色ID */
    private String roleId;
    /** 角色编码 */
    private String roleCode;
    /** 角色名称 */
    private String roleName;

    private String remarks;

    public OauthRole() {
    }

    public OauthRole(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public OauthRole(String roleId, String roleCode, String roleName) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public OauthRole setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public OauthRole setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public OauthRole setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public String getRemarks() {
        return remarks;
    }

    public OauthRole setRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }
}
