package com.jeeplus.modules.sys.service;

import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.core.ext.persistence.BaseEntity;
import com.jeeplus.core.ext.service.CrudService;
import com.jeeplus.modules.sys.entity.ApiClient;
import com.jeeplus.modules.sys.mapper.ApiClientMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * api密钥Service
 *
 * @author FEAR
 * @version 2021-04-01
 */
@Service
@Transactional(readOnly = true)
public class ApiClientService extends CrudService<ApiClientMapper, ApiClient> {

    private static final Logger log = LoggerFactory.getLogger(ApiClientService.class);

    private static final String KEY = "api_client";

    @Override
    public boolean saveOrUpdate(ApiClient entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.setId(RandomStringUtils.randomAlphanumeric(10));
            entity.setSecret(RandomStringUtils.randomAlphanumeric(20));
        }
        boolean flag = super.saveOrUpdate(entity);
        clearCache();
        return flag;
    }

    @Override
    public boolean removeById(ApiClient entity) {
        boolean flag = super.removeById(entity);
        clearCache();
        return flag;
    }

    public String getSecretByClientId(String clientId) {
        ApiClient apiClient = getClientMap().get(clientId);
        if (apiClient == null) {
            return null;
        }
        return apiClient.getSecret();
    }

    public void refreshCache() {
        clearCache();
        getClientMap();
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        CacheUtils.remove(KEY);
    }

    @SuppressWarnings("unchecked")
    private Map<String, ApiClient> getClientMap() {
        Map<String, ApiClient> clientMap = (Map<String, ApiClient>) CacheUtils.get(KEY);
        if (clientMap == null) {
            List<ApiClient> clients = list();
            clientMap = clients.stream().collect(Collectors.toMap(BaseEntity::getId, i -> i));
            CacheUtils.put(KEY, clientMap);
        }
        return clientMap;
    }
}
