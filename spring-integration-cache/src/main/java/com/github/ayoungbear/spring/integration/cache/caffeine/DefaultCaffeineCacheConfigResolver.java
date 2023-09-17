/**
 * Copyright [2023] [yangzexiong]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ayoungbear.spring.integration.cache.caffeine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import javax.annotation.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.util.StringUtils;

/**
 * 默认的 CaffeineCache 缓存配置信息解析。
 *
 * @author yangzexiong
 */
public class DefaultCaffeineCacheConfigResolver implements BeanFactoryAware, CaffeineCacheOperationConfigResolver {

    private final ConcurrentMap<String, Executor> executorMap = new ConcurrentHashMap<>();

    protected BeanFactory beanFactory;

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
     * 缓存刷新时间，要比过期时间短才有效
     */
    private Integer refreshAfterWrite;
    /**
     * 异步任务执行用线程池对象name，需要注册到spring上下文中。
     * 默认使用 {@link ForkJoinPool#commonPool()}
     */
    private String executorName;

    @Override
    public CaffeineCacheOperationConfig resolveConfig(String name, CacheOperationInvocationContext<?> context) {
        CaffeineCacheOperationConfig defaultConfig = new CaffeineCacheOperationConfig();
        defaultConfig.setMaximumSize(maximumSize);
        defaultConfig.setExpireAfterAccess(expireAfterAccess);
        defaultConfig.setExpireAfterWrite(expireAfterWrite);
        defaultConfig.setRefreshAfterWrite(refreshAfterWrite);
        if (StringUtils.hasText(executorName)) {
            defaultConfig.setExecutor(findExecutor(executorName));
        }
        return defaultConfig;
    }

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

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    protected Executor findExecutor(String name) {
        return executorMap.computeIfAbsent(name, key -> getQualifierBean(key, Executor.class));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Nullable
    public <T> T getBean(Class<T> type) {
        if (beanFactory == null) {
            return null;
        }
        return beanFactory.getBeanProvider(type).getIfAvailable();
    }

    @Nullable
    public <T> T getQualifierBean(String qualifier, Class<T> type) {
        if (StringUtils.hasText(qualifier)) {
            if (beanFactory == null) {
                throw new IllegalStateException(
                        "BeanFactory must be provided to access qualified bean '" + qualifier + "' of type '" + type
                                .getSimpleName() + "'");
            }
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, type, qualifier);
        }
        return null;
    }

}
