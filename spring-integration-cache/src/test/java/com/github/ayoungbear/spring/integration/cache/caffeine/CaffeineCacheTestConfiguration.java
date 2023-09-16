package com.github.ayoungbear.spring.integration.cache.caffeine;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * CaffeineCacheTestConfiguration
 *
 * @author yangzexiong
 * @date 2023/2/13
 */
@Configuration
@EnableCaffeineCaching
public class CaffeineCacheTestConfiguration {

    @Bean
    public CaffeineCacheTestService caffeineCacheTestService() {
        return new CaffeineCacheTestService();
    }

    @Bean
    public Executor cacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(100);
        executor.setCorePoolSize(100);
        executor.setThreadNamePrefix("cache-worker-");
        executor.setQueueCapacity(1000);
        return executor;
    }

}
