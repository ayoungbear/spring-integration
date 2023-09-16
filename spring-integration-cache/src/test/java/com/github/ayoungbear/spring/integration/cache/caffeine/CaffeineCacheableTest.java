package com.github.ayoungbear.spring.integration.cache.caffeine;

import com.github.ayoungbear.BaseSpringTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * CaffeineCacheableTest
 *
 * @author yangzexiong
 * @date 2023/2/3
 */
@TestPropertySource(properties = {"logging.level.root=debug", "spring.cache.caffeine.refreshAfterWrite=500",
        "spring.cache.caffeine.executor=cacheExecutor", "test.refreshTime=1000"})
@ContextConfiguration(classes = {CaffeineCacheTestConfiguration.class})
public class CaffeineCacheableTest extends BaseSpringTest {

    @Autowired
    private CaffeineCacheTestService caffeineCacheTestService;

    @Before
    public void setUp() throws Exception {
        CaffeineCacheTestService.COUNT.clear();
    }

    @Test
    public void testDefaultCaffeineConfig() {
        Assert.assertEquals(1L, caffeineCacheTestService.getDefault("key1"));
        Assert.assertEquals(1L, caffeineCacheTestService.getDefault("key1"));
        Assert.assertEquals(1L, caffeineCacheTestService.getDefaultUseSameCache("key1"));
        Assert.assertEquals(1L, caffeineCacheTestService.getDefault("key2"));

        sleep(600);
        Assert.assertEquals(1L, caffeineCacheTestService.getDefault("key2"));
        sleep(50);
        Assert.assertEquals(2L, caffeineCacheTestService.getDefault("key2"));
        Assert.assertEquals(2L, caffeineCacheTestService.getDefaultUseSameCache("key2"));

        sleep(1001);
        Assert.assertEquals(2L, caffeineCacheTestService.getDefault("key1"));
        Assert.assertEquals(2L, caffeineCacheTestService.getDefault("key1"));
        Assert.assertEquals(2L, caffeineCacheTestService.getDefaultUseSameCache("key1"));

        sleep(1001);
        Assert.assertEquals(3L, caffeineCacheTestService.getDefaultUseSameCache("key1"));
        Assert.assertEquals(3L, caffeineCacheTestService.getDefault("key1"));
    }

    @Test
    public void testCaffeineCacheableExpr() {
        Assert.assertEquals(1L, caffeineCacheTestService.getValue("key1", 300, "100"));
        sleep(301);
        Assert.assertEquals(2L, caffeineCacheTestService.getValue("key1", 300, "100"));
        sleep(50);
        Assert.assertEquals(2L, caffeineCacheTestService.getValue("key1", 500, "100"));
        sleep(301);
        Assert.assertEquals(2L, caffeineCacheTestService.getValue("key1", 300, "500"));
        sleep(301);
        Assert.assertEquals(3L, caffeineCacheTestService.getValue("key1", 33333, "800"));
        sleep(50);
        Assert.assertEquals(3L, caffeineCacheTestService.getValue("key1", 33333, "800"));
        sleep(1501);
        Assert.assertEquals(3L, caffeineCacheTestService.getValue("key1", 1000, "500"));
        sleep(50);
        Assert.assertEquals(4L, caffeineCacheTestService.getValue("key1", 1000, "500"));
        sleep(1011);
        Assert.assertEquals(5L, caffeineCacheTestService.getValue("key1", 2000, "1"));
        sleep(50);
        Assert.assertEquals(5L, caffeineCacheTestService.getValue("key1", 2000, "1"));
        sleep(1000);
        Assert.assertEquals(5L, caffeineCacheTestService.getValue("key1", 2000, "1"));
        sleep(50);
        Assert.assertEquals(6L, caffeineCacheTestService.getValue("key1", 2000, "1"));
    }

    @Test
    public void testSpringCacheAnnotation() {
        Assert.assertEquals(1L, caffeineCacheTestService.getCache("100"));
        Assert.assertEquals(1L, caffeineCacheTestService.getCache("100"));
        caffeineCacheTestService.evictCache("100");
        Assert.assertEquals(2L, caffeineCacheTestService.getCache("100"));
        sleep(700);
        Assert.assertEquals(2L, caffeineCacheTestService.getCache("100"));
        sleep(50);
        Assert.assertEquals(3L, caffeineCacheTestService.getCache("100"));
        caffeineCacheTestService.putCache("100");
        Assert.assertEquals(2333L, caffeineCacheTestService.getCache("100"));
        sleep(1001);
        Assert.assertEquals(4L, caffeineCacheTestService.getCache("100"));
    }

}
