package com.github.ayoungbear.spring.integration.cache.caffeine;

import java.util.concurrent.ForkJoinPool;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Caffeine 缓存默认配置。
 *
 * @author yangzexiong
 * @date 2023/2/21
 */
@ConfigurationProperties(prefix = "spring.cache.caffeine")
public class CaffeineCacheProperties {

    /**
     * 缓存大小
     */
    private Integer maximumSize;
    /**
     * 读写操作后过期时间（ms）
     */
    private Integer expireAfterAccess;
    /**
     * 写操作后过期时间（ms）
     */
    private Integer expireAfterWrite;
    /**
     * 缓存刷新时间（ms），要比过期时间短才有效
     */
    private Integer refreshAfterWrite;
    /**
     * 异步任务执行用线程池对象name，需要注册到spring上下文中。
     * 默认使用 {@link ForkJoinPool#commonPool()}
     */
    private String executor;

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

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

}
