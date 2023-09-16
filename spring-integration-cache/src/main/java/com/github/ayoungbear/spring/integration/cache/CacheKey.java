package com.github.ayoungbear.spring.integration.cache;

import java.util.Objects;

/**
 * Cache key with context.
 *
 * @author yangzexiong
 * @date 2023/2/17
 */
public class CacheKey<K, C> {

    protected final K key;

    private C context;

    public CacheKey(K key) {
        this(key, null);
    }

    public CacheKey(K key, C context) {
        this.key = Objects.requireNonNull(key);
        this.context = context;
    }

    public static <K, C> CacheKey<K, C> of(K key) {
        return new CacheKey<>(key);
    }

    public static <K, C> CacheKey<K, C> of(K key, C context) {
        return new CacheKey<>(key, context);
    }

    public K getKey() {
        return key;
    }

    public C getContext() {
        return context;
    }

    public void setContext(C context) {
        this.context = context;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CacheKey ? ((CacheKey) obj).key.equals(this.key) : this.key.equals(obj);
    }

    @Override
    public String toString() {
        return key.toString();
    }

}
