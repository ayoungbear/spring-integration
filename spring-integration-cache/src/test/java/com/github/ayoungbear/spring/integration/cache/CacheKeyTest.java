package com.github.ayoungbear.spring.integration.cache;

import com.github.ayoungbear.spring.integration.cache.caffeine.WrappedCaffeineCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Assert;
import org.junit.Test;

/**
 * CacheKeyTest
 *
 * @author yangzexiong
 * @date 2023/6/7
 */
public class CacheKeyTest {

    @Test
    public void testCacheKey() {
        Assert.assertNotEquals("111", CacheKey.of("111"));
        Assert.assertEquals(CacheKey.of("111"), "111");
        Assert.assertEquals(CacheKey.of("111").hashCode(), "111".hashCode());
        Assert.assertEquals(CacheKey.of("111"), CacheKey.of("111", 1212121));
        Assert.assertEquals("111", CacheKey.of("111").getKey());
        Assert.assertEquals(111, CacheKey.of("111", 111).getContext().intValue());
    }

    @Test
    public void testWrappedCaffeineCache() {
        WrappedCaffeineCache cache = new WrappedCaffeineCache("test", Caffeine.newBuilder().build(),
                "context");
        cache.put(CacheKey.of("1", 123), "111");
        cache.put(CacheKey.of("2", 456), "222");

        Assert.assertEquals("context", cache.getContext());
        Assert.assertNotNull(cache.toString());

        Assert.assertEquals("111", cache.get("1").get());
        Assert.assertEquals("111", cache.get(CacheKey.of("1")).get());
        Assert.assertEquals("111", cache.get(CacheKey.of("1", 333)).get());
        Assert.assertEquals("222", cache.get("2").get());
        Assert.assertEquals("222", cache.get(CacheKey.of("2")).get());
        Assert.assertEquals("222", cache.get(CacheKey.of("2", 333)).get());

        cache.put(CacheKey.of("2", 666), "333");
        Assert.assertEquals("333", cache.get("2", String.class));
        Assert.assertEquals("333", cache.get(CacheKey.of("2")).get());
        Assert.assertEquals("333", cache.get(CacheKey.of("2", 333)).get());

        cache.putValue("3", "1234");
        Assert.assertEquals("1234", cache.get("3", String.class));
        Assert.assertEquals("1234", cache.get(CacheKey.of("3")).get());
        Assert.assertEquals("1234", cache.get(CacheKey.of("3", 333)).get());

        Assert.assertEquals("1234", cache.putIfAbsent("3", "222222").get());

        Assert.assertTrue(cache.evictIfPresent("3"));
        Assert.assertTrue(cache.evictIfPresent(CacheKey.of("2", 212121)));
        cache.evict("1");
        Assert.assertFalse(cache.evictIfPresent("1"));
    }

}
