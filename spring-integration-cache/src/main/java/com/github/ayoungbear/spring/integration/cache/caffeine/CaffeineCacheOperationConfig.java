package com.github.ayoungbear.spring.integration.cache.caffeine;

import java.util.concurrent.Executor;

/**
 * CaffeineCache 缓存配置信息。
 *
 * @author yangzexiong
 * @date 2023/6/5
 */
public class CaffeineCacheOperationConfig {

    /**
     * 缓存大小
     */
    private Integer maximumSize;
    /**
     * 缓存过期时间-操作后（ms）
     */
    private Integer expireAfterAccess;
    /**
     * 缓存过期时间-操作后（ms）
     */
    private Integer expireAfterWrite;
    /**
     * 缓存刷新时间（ms）
     */
    private Integer refreshAfterWrite;
    /**
     * 缓存异步执行器
     */
    private Executor executor;
    /**
     * 是否使用软引用
     */
    private boolean softRef;

    public Integer getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(Integer maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Integer getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(Integer expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
    }

    public Integer getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(Integer expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public Integer getRefreshAfterWrite() {
        return refreshAfterWrite;
    }

    public void setRefreshAfterWrite(Integer refreshAfterWrite) {
        this.refreshAfterWrite = refreshAfterWrite;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public boolean getSoftRef() {
        return softRef;
    }

    public void setSoftRef(boolean softRef) {
        this.softRef = softRef;
    }

}
