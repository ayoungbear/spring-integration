package com.github.ayoungbear.spring.integration.cache;

import java.lang.reflect.Method;

/**
 * Class describing the root object used during the expression evaluation.
 *
 * @author yangzexiong
 * @date 2023/2/21
 * @see class org.springframework.cache.interceptor.CacheExpressionRootObject
 */
public class CacheExpressionRootObject {

    private final Method method;

    private final Object[] args;

    private final Object target;

    private final Class<?> targetClass;

    public CacheExpressionRootObject(Method method, Object[] args, Object target, Class<?> targetClass) {
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.args = args;
    }

    public Method getMethod() {
        return this.method;
    }

    public String getMethodName() {
        return this.method.getName();
    }

    public Object[] getArgs() {
        return this.args;
    }

    public Object getTarget() {
        return this.target;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }
}
