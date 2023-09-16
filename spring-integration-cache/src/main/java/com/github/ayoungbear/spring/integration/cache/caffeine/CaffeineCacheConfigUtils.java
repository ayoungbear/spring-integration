package com.github.ayoungbear.spring.integration.cache.caffeine;

import javax.annotation.Nullable;

/**
 * CaffeineCacheConfigUtils
 *
 * @author yangzexiong
 * @date 2023/2/3
 */
public abstract class CaffeineCacheConfigUtils {

    /**
     * The name of the cache resolver bean.
     */
    public static final String CACHE_RESOLVER_BEAN_NAME = "spring.cache.caffeine.CaffeineCacheResolver";

    /**
     * merge {@link CaffeineCacheOperationConfig}.
     *
     * @param source the source config
     * @param target the config to merge
     * @return the source config
     */
    public static <C extends CaffeineCacheOperationConfig> C mergeConfig(C source,
            @Nullable CaffeineCacheOperationConfig target) {
        if (target == null) {
            return source;
        }
        if (target.getMaximumSize() != null) {
            source.setMaximumSize(target.getMaximumSize());
        }
        if (target.getExpireAfterAccess() != null) {
            source.setExpireAfterAccess(target.getExpireAfterAccess());
        }
        if (target.getExpireAfterWrite() != null) {
            source.setExpireAfterWrite(target.getExpireAfterWrite());
        }
        if (target.getRefreshAfterWrite() != null) {
            source.setRefreshAfterWrite(target.getRefreshAfterWrite());
        }
        if (target.getExecutor() != null) {
            source.setExecutor(target.getExecutor());
        }
        source.setSoftRef(target.getSoftRef());
        return source;
    }

}
