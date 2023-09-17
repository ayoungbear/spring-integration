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

import com.google.common.util.concurrent.AtomicLongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.validation.annotation.Validated;

/**
 * CaffeineCacheTestService
 *
 * @author yangzexiong
 */
@Validated
@CaffeineCacheConfig(maximumSize = "100", expireAfterWrite = "1000")
public class CaffeineCacheTestService {

    public static final AtomicLongMap<String> COUNT = AtomicLongMap.create();

    private static final Logger logger = LoggerFactory.getLogger(CaffeineCacheTestService.class);

    @CaffeineCacheable
    public long getDefault(String key) {
        logger.info("load getDefault");
        return COUNT.incrementAndGet(key);
    }

    @CaffeineCacheable(cacheNames = "public long com.github.ayoungbear.spring.integration.cache.caffeine.CaffeineCacheTestService.getDefault(java.lang.String)")
    public long getDefaultUseSameCache(String key) {
        logger.info("load getDefaultName");
        return COUNT.incrementAndGet(key);
    }

    @CaffeineCacheable(key = "#key", expireAfterAccess = "#expireAfterAccess", refreshAfterWrite = "T(java.lang.Integer).parseInt(#refreshAfterWrite)+1000")
    public long getValue(String key, Integer expireAfterAccess, String refreshAfterWrite) {
        logger.info("load getValue");
        return COUNT.incrementAndGet(key);
    }

    @CaffeineCacheable(cacheNames = "test", key = "@caffeineCacheTestService.getKey(#key)",
            refreshAfterWrite = "@caffeineCacheTestService.refreshAfterWrite(#key)", softRef = true, sync = true)
    public long getCache(String key) {
        return COUNT.incrementAndGet(key);
    }

    @CacheEvict(cacheNames = "test", key = "'test-'+#key", cacheResolver = CaffeineCacheConfigUtils.CACHE_RESOLVER_BEAN_NAME)
    public void evictCache(String key) {
    }

    @CachePut(cacheNames = "test", key = "'test-'+#key", cacheResolver = CaffeineCacheConfigUtils.CACHE_RESOLVER_BEAN_NAME)
    public long putCache(String key) {
        return 2333L;
    }

    public String getKey(String key) {
        return "test-" + key;
    }

    public Integer refreshAfterWrite(String id) {
        return Integer.parseInt(id) + 500;
    }

}
