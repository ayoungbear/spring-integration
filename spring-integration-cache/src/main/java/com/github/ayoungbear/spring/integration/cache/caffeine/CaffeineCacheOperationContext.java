package com.github.ayoungbear.spring.integration.cache.caffeine;

import org.springframework.cache.interceptor.CacheOperationInvoker;

/**
 * CaffeineCache 缓存操作上下文。
 *
 * @author yangzexiong
 * @date 2023/2/20
 */
public class CaffeineCacheOperationContext extends CaffeineCacheOperationConfig {

    private CacheOperationInvoker cacheLoader;

    public CacheOperationInvoker getCacheLoader() {
        return cacheLoader;
    }

    public void setCacheLoader(CacheOperationInvoker cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

}
