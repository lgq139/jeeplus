package com.jeeplus.core.persistence;

public abstract class TenantEntity<T> extends DataEntity<T> {
    /**
     * 租户
     */
    protected String tenantId = "DEFAULT_TENANT";

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
