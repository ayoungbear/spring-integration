package com.github.ayoungbear;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试基类, 提供一些测试用的基础方法
 *
 * @author yangzexiong
 */
public abstract class BaseTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @BeforeClass
    public static void baseSetUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void baseTearDownAfterClass() throws Exception {
    }

    public static Thread run(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.start();
        return t;
    }

    public static Thread run(Runnable runnable, String name) {
        Thread t = new Thread(runnable, name);
        t.start();
        return t;
    }

    public static void sleep(long time, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long millis) {
        sleep(millis, TimeUnit.MILLISECONDS);
    }

    @Before
    public void baseSetUp() throws Exception {
    }

    @After
    public void baseTearDown() throws Exception {
    }

}
