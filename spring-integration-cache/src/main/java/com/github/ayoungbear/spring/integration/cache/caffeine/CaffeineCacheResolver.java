package com.github.ayoungbear.spring.integration.cache.caffeine;

import com.github.ayoungbear.spring.integration.cache.CacheKey;
import com.github.ayoungbear.spring.integration.cache.CacheValueInvoker;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.util.ClassUtils;

/**
 * Determine the {@link org.springframework.cache.caffeine.CaffeineCache} instance(s)
 * to use for an intercepted method invocation.
 *
 * @author yangzexiong
 * @date 2023/2/2
 */
public class CaffeineCacheResolver implements CacheResolver {

    private final ConcurrentMap<String, com.github.benmanes.caffeine.cache.Cache
            <CacheKey<Object, CaffeineCacheOperationContext>, Object>> cacheMap = new ConcurrentHashMap<>();

    private final CaffeineCacheOperationConfigResolver caffeineCacheOperationConfigResolver;

    public CaffeineCacheResolver(CaffeineCacheOperationConfigResolver cacheOperationContextResolver) {
        this.caffeineCacheOperationConfigResolver = Objects
                .requireNonNull(cacheOperationContextResolver, "config resolver must not be null");
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = context.getOperation().getCacheNames();
        if (CollectionUtils.isEmpty(cacheNames)) {
            cacheNames = Collections.singletonList(getDefaultCacheName(context));
        }
        return cacheNames.stream()
                .map(name -> getCaffeineCache(name, context))
                .collect(Collectors.toList());
    }

    /**
     * Get the default cache name by {@link CacheOperationInvocationContext}.
     *
     * @param context
     * @return the default cache name
     */
    protected String getDefaultCacheName(CacheOperationInvocationContext<?> context) {
        Method specificMethod = ClassUtils
                .getMostSpecificMethod(context.getMethod(), ClassUtils.getUserClass(context.getTarget()));
        return specificMethod.toGenericString();
    }

    /**
     * Build a common {@link CaffeineCache} instance for the specified cache name and config.
     *
     * @param name
     * @param config
     * @return
     */
    protected com.github.benmanes.caffeine.cache.Cache getNativeCaffeineCache(String name,
            CaffeineCacheOperationConfig config) {
        com.github.benmanes.caffeine.cache.Cache
                <CacheKey<Object, CaffeineCacheOperationContext>, Object> localCache = this.cacheMap
                .computeIfAbsent(name, cacheName -> createNativeCaffeineCache(cacheName, config));
        // refresh cache policy
        if (config.getMaximumSize() != null) {
            localCache.policy().eviction().ifPresent(eviction -> {
                if (eviction.getMaximum() < config.getMaximumSize()) {
                    eviction.setMaximum(config.getMaximumSize());
                }
            });
        }
        if (config.getRefreshAfterWrite() != null) {
            localCache.policy().refreshAfterWrite().ifPresent(expiration -> {
                long refreshAfterWriteNanos = TimeUnit.MILLISECONDS.toNanos(config.getRefreshAfterWrite());
                if (expiration.getExpiresAfter(TimeUnit.NANOSECONDS) > refreshAfterWriteNanos) {
                    expiration.setExpiresAfter(refreshAfterWriteNanos, TimeUnit.NANOSECONDS);
                }
            });
        }
        return localCache;
    }

    private Cache getCaffeineCache(String name, CacheOperationInvocationContext<?> context) {
        CaffeineCacheOperationConfig config = caffeineCacheOperationConfigResolver.resolveConfig(name, context);
        CaffeineCacheOperationContext caffeineCacheOperationContext = CaffeineCacheConfigUtils
                .mergeConfig(new CaffeineCacheOperationContext(), config);
        caffeineCacheOperationContext
                .setCacheLoader(new CacheValueInvoker(context, caffeineCacheOperationContext.getSoftRef()));

        com.github.benmanes.caffeine.cache.Cache<CacheKey, Object> localCache = getNativeCaffeineCache(name, config);
        return new WrappedCaffeineCache(name, localCache, caffeineCacheOperationContext);
    }

    private com.github.benmanes.caffeine.cache.Cache createNativeCaffeineCache(String name,
            CaffeineCacheOperationConfig config) {
        Caffeine caffeine = Caffeine.newBuilder()
                .maximumSize(Optional.ofNullable(config.getMaximumSize()).filter(m -> m > 0).orElse(Integer.MAX_VALUE))
                .refreshAfterWrite(Optional.ofNullable(config.getRefreshAfterWrite()).filter(r -> r > 0)
                        .map(TimeUnit.MILLISECONDS::toNanos).orElse(Long.MAX_VALUE), TimeUnit.NANOSECONDS)
                .expireAfter(CacheKeyExpiry.newInstance())
                .removalListener(SimpleCaffeineCacheListener.newInstance());
        if (config.getExecutor() != null) {
            // unsupported redefine executor
            caffeine.executor(config.getExecutor());
        }
        if (config.getSoftRef()) {
            // caffeine.softValues();
        }
        return caffeine.build(CacheValueInvokerLoader.newInstance());
    }

}
