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

package com.github.ayoungbear.spring.integration.cache;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.util.Assert;

/**
 * CacheValueInvoker
 *
 * @author yangzexiong
 * @see org.springframework.cache.interceptor.CacheOperationInvocationContext
 */
public class CacheValueInvoker implements CacheOperationInvoker {

    private static final Logger logger = LoggerFactory.getLogger(CacheValueInvoker.class);
    private static final Object[] EMPTY = new Object[0];

    private final Method method;
    private final Object target;
    private final Supplier<Object[]> argsSupplier;

    public CacheValueInvoker(CacheOperationInvocationContext<?> context, boolean softRefArgs) {
        this(context.getMethod(), context.getTarget(), context.getArgs(), softRefArgs);
    }

    public CacheValueInvoker(Method method, Object target, Object[] args, boolean softRefArgs) {
        Assert.notNull(method, "cacheMethod must not be null");
        Assert.notNull(target, "target must not be null");
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        this.method = (!Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(method, targetClass) : method);
        this.target = target;
        Object[] arguments = args == null ? EMPTY : args;
        SoftReference<Object[]> softReference = new SoftReference<>(arguments);
        this.argsSupplier = softRefArgs ? softReference::get : () -> arguments;
    }

    @Override
    public Object invoke() throws ThrowableWrapper {
        Object[] arguments = argsSupplier.get();
        if (arguments == null) {
            logger.warn("Args soft references has bean garbage collection and the cached method is [{}]", method);
            return null;
        }
        try {
            return method.invoke(target, arguments);
        } catch (Throwable ex) {
            throw new ThrowableWrapper(ex);
        }
    }

}
