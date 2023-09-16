package com.github.ayoungbear.spring.integration.cache.caffeine;

import com.github.ayoungbear.spring.integration.cache.CacheKey;
import com.github.benmanes.caffeine.cache.Expiry;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.function.SingletonSupplier;

/**
 * CaffeineCache 根据 {@link CacheKey} 的过期策略。
 *
 * @author yangzexiong
 * @date 2023/6/2
 * @see CacheKey
 * @see CaffeineCacheOperationContext
 */
public class CacheKeyExpiry implements Expiry<CacheKey<Object, CaffeineCacheOperationContext>, Object> {

    private static final Supplier<CacheKeyExpiry> INSTANCE = SingletonSupplier.of(CacheKeyExpiry::new);

    private static final Logger logger = LoggerFactory.getLogger(CacheKeyExpiry.class);

    public static CacheKeyExpiry newInstance() {
        return INSTANCE.get();
    }

    @Override
    public long expireAfterCreate(@NonNull CacheKey<Object, CaffeineCacheOperationContext> key, @NonNull Object value,
            long currentTime) {
        CaffeineCacheOperationContext context = key.getContext();
        long expireTime = Long.MAX_VALUE;
        if (context.getExpireAfterWrite() != null && context.getExpireAfterWrite() > 0) {
            expireTime = Math.min(expireTime, TimeUnit.MILLISECONDS.toNanos(context.getExpireAfterWrite()));
        }
        if (context.getExpireAfterAccess() != null && context.getExpireAfterAccess() > 0) {
            expireTime = Math.min(expireTime, TimeUnit.MILLISECONDS.toNanos(context.getExpireAfterAccess()));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("key '{}' expire time after [create] time is [{}ms]", key,
                    TimeUnit.NANOSECONDS.toMillis(expireTime));
        }
        return expireTime;
    }

    @Override
    public long expireAfterUpdate(@NonNull CacheKey<Object, CaffeineCacheOperationContext> key, @NonNull Object value,
            long currentTime, @NonNegative long currentDuration) {
        CaffeineCacheOperationContext context = key.getContext();
        long expireTime = currentDuration;
        if (context.getExpireAfterWrite() != null && context.getExpireAfterWrite() > 0) {
            expireTime = TimeUnit.MILLISECONDS.toNanos(context.getExpireAfterWrite());
        }
        if (context.getExpireAfterAccess() != null && context.getExpireAfterAccess() > 0) {
            expireTime = Math.min(expireTime, TimeUnit.MILLISECONDS.toNanos(context.getExpireAfterAccess()));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("key '{}' expire time after [update] time is [{}ms]", key,
                    TimeUnit.NANOSECONDS.toMillis(expireTime));
        }
        return expireTime;
    }

    @Override
    public long expireAfterRead(@NonNull CacheKey<Object, CaffeineCacheOperationContext> key, @NonNull Object value,
            long currentTime, @NonNegative long currentDuration) {
        CaffeineCacheOperationContext context = key.getContext();
        long expireTime = currentDuration;
        if (context.getExpireAfterAccess() != null && context.getExpireAfterAccess() > 0) {
            expireTime = TimeUnit.MILLISECONDS.toNanos(context.getExpireAfterAccess());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("key '{}' expire time after [read] is [{}ms]", key, TimeUnit.NANOSECONDS.toMillis(expireTime));
        }
        return expireTime;
    }

}
