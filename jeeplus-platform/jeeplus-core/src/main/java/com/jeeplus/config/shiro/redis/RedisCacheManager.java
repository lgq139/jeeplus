/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.config.shiro.redis;

import net.sf.ehcache.CacheException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by jeeplus on 2018/2/20.
 */
public class RedisCacheManager implements CacheManager {

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${spring.redis.expireTime}")
    private long redisExpireTime;

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new RedisCache<K, V>(redisExpireTime, redisTemplate);// 为了简化代码的编写，此处直接new一个Cache
    }

}