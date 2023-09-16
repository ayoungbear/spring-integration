package com.github.ayoungbear.spring.integration.cache.caffeine;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AliasFor;

/**
 * Extends annotation indicating that the result of invoking a method (or all methods in a class)
 * can be cached with {@link com.github.benmanes.caffeine.cache.Cache}.
 *
 * <p>the example:</p>
 * <pre class="code">
 * &#064;Service
 * &#064;CaffeineCacheConfig(maximumSize = "100", expireAfterWrite = "60000")
 * public class MyService {
 *
 *     &#064;CaffeineCacheable(refreshAfterWrite = "30000")
 *     public Response getValue(Request request) {
 *          // load value from db or remote cache
 *          return response;
 *     }
 * }</pre>
 *
 * <p>The SpEL expression evaluates against a dedicated context that provides the
 * following meta-data:
 * </p>
 * <ul>
 * <li>{@code #root.method}, {@code #root.target} for
 * references to the {@link Method method}, target object, and
 * affected cache(s) respectively.</li>
 * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
 * ({@code #root.targetClass}) are also available.</li>
 * <li>Method arguments can be accessed by index. For instance the second argument
 * can be accessed via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
 * can also be accessed by name if that information is available.</li>
 * </ul>
 *
 * @author yangzexiong
 * @date 2023/2/3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Cacheable(cacheResolver = CaffeineCacheConfigUtils.CACHE_RESOLVER_BEAN_NAME)
@CaffeineCacheConfig
public @interface CaffeineCacheable {

    /**
     * Alias for {@link #cacheNames}.
     * By default use the method name.
     *
     * @see Method#toGenericString()
     */
    @AliasFor(annotation = Cacheable.class, attribute = "cacheNames")
    String[] value() default {};

    /**
     * Names of the caches in which method invocation results are stored.
     * <p>Names may be used to determine the target cache (or caches), matching
     * the qualifier value or bean name of a specific bean definition.</p>
     *
     * @see #value
     * @see CacheConfig#cacheNames
     * @since 4.2
     */
    @AliasFor(annotation = Cacheable.class, attribute = "value")
    String[] cacheNames() default {};

    /**
     * Spring Expression Language (SpEL) expression for computing the key dynamically.
     * <p>Default is {@code ""}, meaning all method parameters are considered as a key,
     * unless a custom {@link #keyGenerator} has been configured.</p>
     * <p>The SpEL expression evaluates against a dedicated context that provides the
     * following meta-data:
     * <ul>
     * <li>{@code #root.method}, {@code #root.target}, and {@code #root.caches} for
     * references to the {@link Method method}, target object, and
     * affected cache(s) respectively.</li>
     * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
     * ({@code #root.targetClass}) are also available.
     * <li>Method arguments can be accessed by index. For instance the second argument
     * can be accessed via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
     * can also be accessed by name if that information is available.</li>
     * </ul>
     */
    @AliasFor(annotation = Cacheable.class, attribute = "key")
    String key() default "";

    /**
     * The bean name of the custom {@link org.springframework.cache.interceptor.KeyGenerator}
     * to use.
     * <p>Mutually exclusive with the {@link #key} attribute.</p>
     *
     * @see CacheConfig#keyGenerator
     */
    @AliasFor(annotation = Cacheable.class, attribute = "keyGenerator")
    String keyGenerator() default "";

    /**
     * Spring Expression Language (SpEL) expression used for making the method
     * caching conditional.
     * <p>Default is {@code ""}, meaning the method result is always cached.</p>
     * <p>The SpEL expression evaluates against a dedicated context that provides the
     * following meta-data:</p>
     * <ul>
     * <li>{@code #root.method}, {@code #root.target}, and {@code #root.caches} for
     * references to the {@link Method method}, target object, and
     * affected cache(s) respectively.</li>
     * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
     * ({@code #root.targetClass}) are also available.</li>
     * <li>Method arguments can be accessed by index. For instance the second argument
     * can be accessed via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
     * can also be accessed by name if that information is available.</li>
     * </ul>
     */
    @AliasFor(annotation = Cacheable.class, attribute = "condition")
    String condition() default "";

    /**
     * Spring Expression Language (SpEL) expression used to veto method caching.
     * <p>Unlike {@link #condition}, this expression is evaluated after the method
     * has been called and can therefore refer to the {@code result}.</p>
     * <p>Default is {@code ""}, meaning that caching is never vetoed.</p>
     * <p>The SpEL expression evaluates against a dedicated context that provides the
     * following meta-data:</p>
     * <ul>
     * <li>{@code #result} for a reference to the result of the method invocation. For
     * supported wrappers such as {@code Optional}, {@code #result} refers to the actual
     * object, not the wrapper</li>
     * <li>{@code #root.method}, {@code #root.target}, and {@code #root.caches} for
     * references to the {@link Method method}, target object, and
     * affected cache(s) respectively.</li>
     * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
     * ({@code #root.targetClass}) are also available.</li>
     * <li>Method arguments can be accessed by index. For instance the second argument
     * can be accessed via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
     * can also be accessed by name if that information is available.</li>
     * </ul>
     *
     * @since 3.2
     */
    @AliasFor(annotation = Cacheable.class, attribute = "unless")
    String unless() default "";

    /**
     * Synchronize the invocation of the underlying method if several threads are
     * attempting to load a value for the same key. The synchronization leads to
     * a couple of limitations:
     * <ol>
     * <li>{@link #unless()} is not supported</li>
     * <li>Only one cache may be specified</li>
     * <li>No other cache-related operation can be combined</li>
     * </ol>
     * This is effectively a hint and the actual cache provider that you are
     * using may not support it in a synchronized fashion. Check your provider
     * documentation for more details on the actual semantics.
     *
     * @see org.springframework.cache.Cache#get(Object, Callable)
     * @since 4.3
     */
    @AliasFor(annotation = Cacheable.class, attribute = "sync")
    boolean sync() default false;

    /**
     * 缓存大小配置，可以直接配置 {@code "1000"}，结果需为 int 类型。
     * 支持 Spring Expression Language (SpEL) expression，表达式用法同 Spring Cache。
     * 默认为空表示不配置，即不设置大小或者取默认配置的大小 {@link CaffeineCacheProperties#getMaximumSize()} ()}。
     *
     * 如果多个同名 {@link org.springframework.cache.Cache} 配置了不同大小，则取最大值。
     */
    @AliasFor(annotation = CaffeineCacheConfig.class, attribute = "maximumSize")
    String maximumSize() default "";

    /**
     * 缓存读写操作后过期时间（ms），可以直接配置 {@code "1000"}，结果需为 int 类型。
     * 支持 Spring Expression Language (SpEL) expression，表达式用法同 Spring Cache。
     * 默认为空表示不配置，即不设置过期或者取默认配置的过期时间 {@link CaffeineCacheProperties#getExpireAfterAccess()}。
     */
    @AliasFor(annotation = CaffeineCacheConfig.class, attribute = "expireAfterAccess")
    String expireAfterAccess() default "";

    /**
     * 缓存写操作后过期时间（ms），可以直接配置 {@code "1000"}，结果需为 int 类型。
     * 支持 Spring Expression Language (SpEL) expression，表达式用法同 Spring Cache。
     * 默认为空表示不配置，即不设置过期或者取默认配置的过期时间 {@link CaffeineCacheProperties#getExpireAfterWrite()}。
     */
    @AliasFor(annotation = CaffeineCacheConfig.class, attribute = "expireAfterWrite")
    String expireAfterWrite() default "";

    /**
     * 缓存异步刷新时间（ms），可以直接配置 {@code "1000"}，结果需为 int 类型。
     * 支持 Spring Expression Language (SpEL) expression，表达式用法同 Spring Cache。
     * 默认为空表示不配置，即不设置刷新或者取默认配置的刷新时间 {@link CaffeineCacheProperties#getRefreshAfterWrite()}。
     *
     * 异步刷新时间需要比过期时间小才能生效。
     * 如果多个同名 {@link org.springframework.cache.Cache} 配置了不同刷新时间，则取最大值；
     * 现在 CaffeineCache 暂不支持根据 key 动态设置刷新时间。
     */
    @AliasFor(annotation = CaffeineCacheConfig.class, attribute = "refreshAfterWrite")
    String refreshAfterWrite() default "";

    /**
     * 缓存异步执行器，例如异步刷新缓存场景下使用。
     * 默认为空表示不配置，默认使用 {@link ForkJoinPool#commonPool()} 或者默认配置的 {@link java.util.concurrent.Executor}，
     * 通过 {@link CaffeineCacheProperties#getExecutor()} 配置默认的线程池bean。
     */
    @AliasFor(annotation = CaffeineCacheConfig.class, attribute = "executor")
    String executor() default "";

    /**
     * 配置是否对引用的方法参数启用软引用 {@link java.lang.ref.SoftReference}。
     * 因为异步刷新需要缓存请求参数 {@link org.springframework.cache.interceptor.CacheOperationInvocationContext#getArgs()}，
     * 可能存在参数对象比较大的情况。
     *
     * 如果配置成软引用，当内存不足时参数对象会被回收，此时无法再刷新缓存并直接返回 {@code null}，相当于缓存失效并同步重新加载缓存。
     * 如果缓存方法的请求参数对象不大，比如只有一个 {@link String}，可以不用考虑开启。
     */
    @AliasFor(annotation = CaffeineCacheConfig.class, attribute = "softRef")
    boolean softRef() default false;

}
