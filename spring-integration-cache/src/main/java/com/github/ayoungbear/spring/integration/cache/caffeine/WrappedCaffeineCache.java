package com.github.ayoungbear.spring.integration.cache.caffeine;

import com.github.ayoungbear.spring.integration.cache.CacheKey;
import com.github.benmanes.caffeine.cache.Cache;
import java.util.concurrent.Callable;
import org.springframework.cache.caffeine.CaffeineCache;

/**
 * 扩展 {@link CaffeineCache} 使用特定上下文包装key，使key能附带一些信息。
 *
 * @author yangzexiong
 * @date 2023/2/20
 * @see CacheKey
 * @see CaffeineCache
 */
public class WrappedCaffeineCache<K, C> extends CaffeineCache {

    private final C context;

    public WrappedCaffeineCache(String name, Cache<? extends CacheKey<K, C>, Object> cache, C context) {
        super(name, toCache(cache));
        this.context = context;
    }

    private static Cache toCache(Cache<? extends CacheKey, Object> cache) {
        return cache;
    }

    public C getContext() {
        return context;
    }

    @Override
    public ValueWrapper get(Object key) {
        return toValueWrapper(lookup(toCacheKey(key)));
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return super.get(toCacheKey(key), valueLoader);
    }

    @Override
    protected Object lookup(Object key) {
        return super.lookup(toCacheKey(key));
    }

    @Override
    public void put(Object key, Object value) {
        super.put(toCacheKey(key), value);
    }

    public void putValue(K key, Object value) {
        put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return super.putIfAbsent(toCacheKey(key), value);
    }

    @Override
    public void evict(Object key) {
        super.evict(toCacheKey(key));
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return super.evictIfPresent(toCacheKey(key));
    }

    @Override
    public String toString() {
        return "WrappedCaffeineCache{" + "name=" + getName() + ",context=" + context + ",cache=" + getNativeCache()
                + '}';
    }

    private CacheKey toCacheKey(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return key instanceof CacheKey ? (CacheKey) key : CacheKey.of(key, context);
    }

}
