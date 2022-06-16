package com.jeeplus.core.oauth;

import java.io.Serializable;
import java.util.List;

/**
 * 认证中心获取到的用户
 *
 * @author FEAR
 */
public class OauthUser implements Serializable {
    /** 用户ID */
    private String id;
    /** 登录账号 */
    private String loginName;
    /** 用户名 */
    private String name;
    /** 机构编码 */
    private String orgCode;
    /** 机构名称 */
    private String orgName;
    /** 区划编码 */
    private String regionCode;
    /** 一体化平台(bsp)编码 */
    private String regionShortCode;
    /** 区划名称 */
    private String regionName;
    /** 手机号 */
    private String mobile;

    private List<OauthRole> roles;
    /** 标准角色列表 */
    private List<OauthRole> pubRoles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<OauthRole> getRoles() {
        return roles;
    }

    public void setRoles(List<OauthRole> roles) {
        this.roles = roles;
    }

    public String getRegionShortCode() {
        return regionShortCode;
    }

    public void setRegionShortCode(String regionShortCode) {
        this.regionShortCode = regionShortCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<OauthRole> getPubRoles() {
        return pubRoles;
    }

    public void setPubRoles(List<OauthRole> pubRoles) {
        this.pubRoles = pubRoles;
    }
}
