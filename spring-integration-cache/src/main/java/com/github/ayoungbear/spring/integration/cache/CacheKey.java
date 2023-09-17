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

package com.github.ayoungbear.spring.integration.cache;

import java.util.Objects;

/**
 * Cache key with context.
 *
 * @author yangzexiong
 */
public class CacheKey<K, C> {

    protected final K key;

    private C context;

    public CacheKey(K key) {
        this(key, null);
    }

    public CacheKey(K key, C context) {
        this.key = Objects.requireNonNull(key);
        this.context = context;
    }

    public static <K, C> CacheKey<K, C> of(K key) {
        return new CacheKey<>(key);
    }

    public static <K, C> CacheKey<K, C> of(K key, C context) {
        return new CacheKey<>(key, context);
    }

    public K getKey() {
        return key;
    }

    public C getContext() {
        return context;
    }

    public void setContext(C context) {
        this.context = context;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CacheKey ? ((CacheKey) obj).key.equals(this.key) : this.key.equals(obj);
    }

    @Override
    public String toString() {
        return key.toString();
    }

}
