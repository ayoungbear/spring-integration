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

import com.github.ayoungbear.spring.integration.cache.CacheKey;
import com.github.benmanes.caffeine.cache.CacheLoader;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.util.function.SingletonSupplier;

/**
 * 通过 {@link CaffeineCacheOperationContext#getCacheLoader()} 实现的缓存加载。
 *
 * @author yangzexiong
 */
public class CacheValueInvokerLoader implements CacheLoader<CacheKey<Object, CaffeineCacheOperationContext>, Object> {

    private static final Logger logger = LoggerFactory.getLogger(CacheValueInvokerLoader.class);

    private static final Supplier<CacheValueInvokerLoader> INSTANCE = SingletonSupplier
            .of(CacheValueInvokerLoader::new);

    public static CacheValueInvokerLoader newInstance() {
        return INSTANCE.get();
    }

    @Override
    public Object load(CacheKey<Object, CaffeineCacheOperationContext> key) throws Exception {
        CaffeineCacheOperationContext caffeineCacheOperationContext = key.getContext();
        CacheOperationInvoker valueLoader = caffeineCacheOperationContext.getCacheLoader();
        if (valueLoader == null) {
            logger.warn("Unable to reload for key={}, return null then the mapping will be removed", key);
            return null;
        }
        return valueLoader.invoke();
    }

}
