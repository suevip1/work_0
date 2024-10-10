package com.dtstack.engine.master.config;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-12-21 09:55
 * 覆写 RedisCache，以便支持模糊清除
 */
public class CustomizedRedisCache extends RedisCache {
    private static final String WILD_CARD = "*";
    private final String name;
    private final RedisCacheWriter cacheWriter;
    private final ConversionService conversionService;

    protected CustomizedRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        this.name = name;
        this.cacheWriter = cacheWriter;
        this.conversionService = cacheConfig.getConversionService();
    }

    @Override
    public void evict(Object key) {
        if (key instanceof String) {
            String keyString = key.toString();
            if (keyString.endsWith(WILD_CARD)) {
                evictLikeSuffix(keyString);
                return;
            }
            if (keyString.startsWith(WILD_CARD)) {
                evictLikePrefix(keyString);
                return;
            }
        }
        super.evict(key);
    }

    /**
     * 前缀匹配
     *
     * @param key
     */
    public void evictLikePrefix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }

    /**
     * 后缀匹配
     *
     * @param key
     */
    public void evictLikeSuffix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }
}
