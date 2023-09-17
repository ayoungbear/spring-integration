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

import javax.annotation.Nullable;

/**
 * CaffeineCacheConfigUtils
 *
 * @author yangzexiong
 */
public class CaffeineCacheConfigUtils {

    /**
     * The name of the cache resolver bean.
     */
    public static final String CACHE_RESOLVER_BEAN_NAME = "spring.cache.caffeine.CaffeineCacheResolver";

    /**
     * merge {@link CaffeineCacheOperationConfig}.
     *
     * @param source the source config
     * @param target the config to merge
     * @return the source config
     */
    public static <C extends CaffeineCacheOperationConfig> C mergeConfig(C source,
            @Nullable CaffeineCacheOperationConfig target) {
        if (target == null) {
            return source;
        }
        if (target.getMaximumSize() != null) {
            source.setMaximumSize(target.getMaximumSize());
        }
        if (target.getExpireAfterAccess() != null) {
            source.setExpireAfterAccess(target.getExpireAfterAccess());
        }
        if (target.getExpireAfterWrite() != null) {
            source.setExpireAfterWrite(target.getExpireAfterWrite());
        }
        if (target.getRefreshAfterWrite() != null) {
            source.setRefreshAfterWrite(target.getRefreshAfterWrite());
        }
        if (target.getExecutor() != null) {
            source.setExecutor(target.getExecutor());
        }
        source.setSoftRef(target.getSoftRef());
        return source;
    }

}
