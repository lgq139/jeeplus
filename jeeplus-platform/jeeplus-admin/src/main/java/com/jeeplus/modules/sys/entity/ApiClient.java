package com.jeeplus.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.DataEntity;

/**
 * api密钥Entity
 *
 * @author FEAR
 * @version 2021-04-01
 */
@TableName("sys_api_client")
public class ApiClient extends DataEntity<ApiClient> {

    private static final long serialVersionUID = 1L;
    /** 密钥 */
    private String secret;
    /** 系统名称 */
    private String systemName;
    /** 描述 */
    private String description;

    public ApiClient() {
        super();
    }

    public ApiClient(String id) {
        super(id);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
