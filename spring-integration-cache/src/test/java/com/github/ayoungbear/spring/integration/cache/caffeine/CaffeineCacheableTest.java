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
