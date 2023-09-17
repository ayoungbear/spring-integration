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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Enables Spring's annotation-driven cache management capability and provides enhanced ability for {@link
 * com.github.benmanes.caffeine.cache.Cache}.
 * To be used together with @{@link org.springframework.context.annotation.Configuration Configuration}
 * classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableCaffeineCaching
 * public class CaffeineCacheConfiguration {
 *
 *     &#064;Bean
 *     public Executor cacheExecutor() {
 *         // configure a executor to asynchronously reload the cache value
 *         // set the default executor by using property spring.cache.caffeine.executor=cacheExecutor
 *         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 *         executor.setMaxPoolSize(100);
 *         executor.setCorePoolSize(100);
 *         executor.setThreadNamePrefix("cache-worker-");
 *         executor.setQueueCapacity(1000);
 *         return executor;
 *     }
 *
 * }</pre>
 *
 * @author yangzexiong
 * @see CaffeineCacheConfiguration
 * @see CaffeineCacheable
 * @see CaffeineCacheConfig
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CaffeineCacheConfiguration.class)
public @interface EnableCaffeineCaching {

}
