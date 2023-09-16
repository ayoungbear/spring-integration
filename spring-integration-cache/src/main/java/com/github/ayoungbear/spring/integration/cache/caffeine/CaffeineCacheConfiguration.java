package com.github.ayoungbear.spring.integration.cache.caffeine;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * Spring CaffeineCache 缓存增强配置类。
 *
 * @author yangzexiong
 * @date 2023/2/3
 * @see CaffeineCacheConfig
 * @see CaffeineCacheable
 * @see CaffeineCacheResolver
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(CaffeineCacheProperties.class)
public class CaffeineCacheConfiguration {

    private final CaffeineCacheProperties properties;

    public CaffeineCacheConfiguration(CaffeineCacheProperties properties) {
        this.properties = properties;
    }

    @Bean(CaffeineCacheConfigUtils.CACHE_RESOLVER_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheResolver caffeineCacheResolver() {
        return new CaffeineCacheResolver(defaultConfigResolver());
    }

    @Bean
    @Role(BeanDefinition.ROLE_SUPPORT)
    public CaffeineCacheOperationConfigResolver defaultConfigResolver() {
        AnnotationCaffeineCacheConfigResolver configResolver = new AnnotationCaffeineCacheConfigResolver();
        configResolver.setMaximumSize(properties.getMaximumSize());
        configResolver.setExpireAfterAccess(properties.getExpireAfterAccess());
        configResolver.setExpireAfterWrite(properties.getExpireAfterWrite());
        configResolver.setRefreshAfterWrite(properties.getRefreshAfterWrite());
        configResolver.setExecutorName(properties.getExecutor());
        return configResolver;
    }

}
