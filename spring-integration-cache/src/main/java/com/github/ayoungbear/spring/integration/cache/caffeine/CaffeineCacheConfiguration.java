/**
 *    Copyright [2023] [yangzexiong]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
