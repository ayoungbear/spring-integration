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

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.function.SingletonSupplier;

/**
 * CaffeineCache 缓存变动监听，输出相关日志，比如过期等。
 *
 * @author yangzexiong
 */
public class SimpleCaffeineCacheListener implements RemovalListener {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCaffeineCacheListener.class);

    private static final Supplier<SimpleCaffeineCacheListener> INSTANCE = SingletonSupplier
            .of(SimpleCaffeineCacheListener::new);

    public static SimpleCaffeineCacheListener newInstance() {
        return INSTANCE.get();
    }

    @Override
    public void onRemoval(Object key, Object value, RemovalCause cause) {
        logger.info("cache {} key={} value={}", cause, key, value);
    }

}
