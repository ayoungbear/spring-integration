# [spring-integration-cache](https://github.com/ayoungbear/spring-integration/tree/main/spring-integration-cache)

[Spring Cache](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache)
针对缓存抽象提供了一套缓存操作的注解：

[@Cacheable](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-annotations-cacheable):
触发缓存填充。

[@CacheEvict](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-annotations-evict):
触发缓存清除。

[@CachePut](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-annotations-put):
在不干扰方法执行的情况下更新缓存。

[@Caching](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-annotations-caching):
将应用于方法的多个缓存操作重新分组。

[@CacheConfig](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-annotations-config):
在类级别共享一些常见的与缓存相关的设置。

同时也提供了一些缓存实现，比如基于 ConcurrentMap 的、基于 [ehcache](https://www.ehcache.org/) 的、基于
[caffeine](https://github.com/ben-manes/caffeine/wiki)、基于 [redis](https://github.com/redis/redis)
的等。

但因为**缓存抽象不包含具体实现的特性**
（比如过期刷新），想要使用这些特性就必须要去配置相应缓存实现的 [CacheManager](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-store-configuration)
，使用起来可能会比较繁琐。

因此针对不同实现进行了一些封装，提供一些简便的 api 或工具，让使用更加方便快捷。

## [spring caffeine cache](https://github.com/ayoungbear/spring-integration/tree/main/spring-integration-cache/src/main/java/com/github/ayoungbear/spring/integration/cache/caffeine)

Spring caffeine cache
是对 [Spring Annotation-based Caching](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache)
（abstraction）& [Caffeine Cache](https://github.com/ben-manes/caffeine/wiki)
（implementation）的集成增强，为了简化开发和提升效率：

**一个注解实现 `本地缓存 + 容量控制 + 缓存过期 + 异步刷新` 的功能**。

比如一个查询配置的方法想添加本地缓存，假设设置缓存大小为100、写入后过期时间为10分钟、缓存每5分钟后有请求异步刷新一次数据，只需要添加一个注解 [@CaffeineCacheable](https://github.com/ayoungbear/spring-integration/blob/main/spring-integration-cache/src/main/java/com/github/ayoungbear/spring/integration/cache/caffeine/CaffeineCacheable.java)。

```java

@Repository
public class MyConfigRepositoryImpl implements MyConfigRepository {

    @Override
    @CaffeineCacheable(maximumSize = "100", expireAfterWrite = "600000", refreshAfterWrite = "300000")
    public MyConfig getMyConfig(String configId) {
        // load config from db or remote cache
        return myConfig;
    }
}
```

### 使用说明

#### [@EnableCaffeineCaching](https://github.com/ayoungbear/spring-integration/blob/main/spring-integration-cache/src/main/java/com/github/ayoungbear/spring/integration/cache/caffeine/EnableCaffeineCaching.java)

用于开启 Spring caffeine cache 功能特性，主要是注册配置相关对象等。

同时也会开启 [Spring Annotation-based Caching](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache)
功能（@EnableCaching）。

```java

@Configuration
@EnableCaffeineCaching
public class CacheConfiguration {
    // cache config definition
}
```

#### [@CaffeineCacheConfig](https://github.com/ayoungbear/spring-integration/blob/main/spring-integration-cache/src/main/java/com/github/ayoungbear/spring/integration/cache/caffeine/CaffeineCacheConfig.java)

用于在在类级别共享 caffeine 缓存相关的设置，比如设置该类下的默认过期时间、默认刷新时间等。

类比下就是 @CacheConfig 之于 @Cacheable，相当于 @CaffeineCacheConfig 之于 @CaffeineCacheable。

#### [@CaffeineCacheable](https://github.com/ayoungbear/spring-integration/blob/main/spring-integration-cache/src/main/java/com/github/ayoungbear/spring/integration/cache/caffeine/CaffeineCacheable.java)

对 Spring
原生 [@Cacheable](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-annotations-cacheable)
的扩展注解，支持了 [caffeine](https://github.com/ben-manes/caffeine/wiki) 的相关特性。

直接使用该注解即可实现 [caffeine](https://github.com/ben-manes/caffeine/wiki) 本地缓存的特性，还可以结合
SpEL 动态指定缓存过期时间等属性。

```java
/**
 * 假设配置 cache.maxSize=1000
 * 指定默认配置 大小取配置变量'cache.maxSize'，过期刷新时间为5分钟，使用bean name为'myExecutor'的线程池执行异步刷新
 */
@Repository
@CaffeineCacheConfig(maximumSize = "@environment.getProperty('cache.maxSize')", refreshAfterWrite = "300000",
        executor = "myExecutor")
public class MyConfigRepositoryImpl implements MyConfigRepository {

    /**
     * 缓存大小1000，10分钟过期，30s刷新
     */
    @Override
    @CaffeineCacheable(expireAfterWrite = "600000", refreshAfterWrite = "30000")
    public MyConfig getMyConfig(String configId) {
        // load config from db or remote cache
        return myConfig;
    }

    /**
     * 缓存大小100，若 productId 是'MASTER'则30分钟过期，其他则是10分钟过期，5分钟刷新
     */
    @Override
    @CaffeineCacheable(maximumSize = "100", expireAfterWrite = "'MASTER'.equals(#productId)?1800000:600000")
    public ProductConfig getAppConfig(String productId) {
        // load config from db or remote cache
        return myConfig;
    }
}
```

相关属性说明：

- `value`：缓存名称，相当于 cache 的 key，支持不配置该属性。默认为缓存方法
  Method.toGenericString。
- `cacheNames`：缓存名称，相当于 cache 的 key，value 的别名属性。
- `keyGenerator`：缓存 key 生成接口实现对应的 name，根据方法和参数等生成缓存 key（与 key 互斥）。默认使用
  SimpleKeyGenerator。
- `condition`：缓存条件，可通过 SpEL 表达式指定需要缓存的情况，返回 true 表示需要缓存。默认为 true。
- `unless`：缓存反条件，可通过 SpEL 表达式指定不需要缓存的情况，返回 true 表示不需要缓存。默认为 false。
- `sync`：缓存加载是否同步执行。默认为 false。
- `maximumSize`：缓存容量，超过容量后会根据淘汰策略剔除不常用的 kv，可通过 SpEL 表达式指定，返回 int
  类型。默认不限制（Integer.MAX_VALUE），或者取默认配置 **`spring.cache.caffeine.maximumSize`**。
  _如果不同方法使用了相同的 cache（同
  cacheName），但设置了不同的容量大小，那取较大值（比如method1设置size=100，method2设置size=200，最终size=200）。_
- `expireAfterAccess`；缓存读取操作后数据的过期时间（ms），可通过 SpEL 表达式指定，返回 int
  类型。默认不过期，或者取默认配置 **`spring.cache.caffeine.expireAfterAccess`** 。
- `expireAfterWrite`：缓存写入后数据的过期时间（ms），可通过 SpEL 表达式指定，返回 int
  类型。默认不过期，或者取默认配置 **`spring.cache.caffeine.expireAfterWrite`**。
- `refreshAfterWrite`：缓存数据异步刷新的时间（ms），可通过 SpEL 表达式指定，返回 int
  类型。默认不刷新，或者取默认配置 **`spring.cache.caffeine.refreshAfterWrite`**。_如果不同方法使用了相同的
  cache（同
  cacheName），但设置了不同的过期时间，那取较小值（比如method1设置为100s，method2设置为200s，最终刷新时间间隔是100s）。缓存过期支持不同
  key 配置不同过期时间，但缓存刷新 refreshAfterWrite 不支持根据 key 动态变化，如果设置有冲突则取较小值；原因是
  caffeine 目前还没支持动态刷新（后续可能会支持），官方也有相关 issue
  有兴趣可以了解：https://github.com/ben-manes/caffeine/issues/504。_
- `executor`：缓存异步处理（刷新）时使用的异步执行器线程池 name。默认为 ForkJoinPool.commonPool()
  ，或者取默认配置 **`spring.cache.caffeine.executor`**。_executor 不支持覆盖，如果不同方法使用了相同的
  cache（同 cacheName），但设置了不同的 executor，以最先初始化 cache 的为准。_
- `softRef`：是否开启软引用（可以使 GC 在内存不足时，回收这些缓存对象释放内存）。默认为 false。
  _一般应用场景不需要考虑该参数，如果方法参数是个大对象才考虑是否开启（因为异步刷新需要缓存请求参数），但这种一般都可以通过优化写法解决。_

## 原理

组件是在 [Spring Annotation-based Caching](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache)
的基础上做的扩展增强，通过定制 CacheResolver 实现具体缓存的相关特性。

参考：[Custom Cache Resolution](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-annotations-cacheable-cache-resolver)
、[Using Custom Annotations](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache-annotation-stereotype)

Spring cache 的基本原理还是 AOP，通过动态代理增强缓存相关的操作。

