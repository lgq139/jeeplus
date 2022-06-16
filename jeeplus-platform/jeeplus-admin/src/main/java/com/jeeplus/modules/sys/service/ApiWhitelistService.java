package com.jeeplus.modules.sys.service;

import cn.hutool.core.date.DateTime;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.core.ext.service.CrudService;
import com.jeeplus.modules.sys.entity.ApiWhitelist;
import com.jeeplus.modules.sys.mapper.ApiWhitelistMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 白名单Service
 *
 * @author FEAR
 * @version 2021-08-19
 */
@Service
@Transactional(readOnly = true)
public class ApiWhitelistService extends CrudService<ApiWhitelistMapper, ApiWhitelist> {

    private static final Logger log = LoggerFactory.getLogger(ApiWhitelistService.class);

    private static final String KEY = "api_whitelist";


    @Override
    @Transactional(readOnly = false)
    public boolean save(ApiWhitelist entity) {
        if (entity.getDescription() == null) {
            entity.setExpiredDate(DateTime.of("9999-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        }
        boolean flag = super.save(entity);
        clearCache();
        return flag;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean removeById(ApiWhitelist apiWhitelist) {
        boolean flag = super.removeById(apiWhitelist);
        clearCache();
        return flag;
    }

    /**
     * 判断请求IP是否在白名单中
     */
    public boolean pass(String ip) {
        List<String> whitelist = getWhitelist();
        return whitelist.contains(ip);
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        CacheUtils.remove(KEY);
    }

    @SuppressWarnings("unchecked")
    public List<String> getWhitelist() {
        List<String> whitelist = (List<String>) CacheUtils.get(KEY);
        if (whitelist == null) {
            List<ApiWhitelist> ips = list();
            whitelist = ips.stream().map(ApiWhitelist::getIp).collect(Collectors.toList());
            whitelist.add("localhost");
            whitelist.add("127.0.0.1");
            whitelist.add("0:0:0:0:0:0:0:1");
            CacheUtils.put(KEY, whitelist);
        }
        return whitelist;
    }

}
