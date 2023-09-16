package com.github.ayoungbear.spring.integration.cache.caffeine;

import org.springframework.cache.interceptor.CacheOperationInvocationContext;

/**
 * CaffeineCache 缓存配置信息解析。
 *
 * @author yangzexiong
 * @date 2023/6/5
 */
@FunctionalInterface
public interface CaffeineCacheOperationConfigResolver {

    /**
     * 根据缓存执行上下文获取 CaffeineCache 相关信息
     *
     * @param name cache name
     * @param context Representation of the context of the invocation of a cache operation
     * @return
     */
    CaffeineCacheOperationConfig resolveConfig(String name, CacheOperationInvocationContext<?> context);

}
