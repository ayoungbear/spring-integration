package com.github.ayoungbear.spring.integration.cache.caffeine;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.function.SingletonSupplier;

/**
 * SimpleCaffeineCacheListener
 *
 * @author yangzexiong
 * @date 2023/6/2
 */
public class SimpleCaffeineCacheListener implements RemovalListener {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCaffeineCacheListener.class);

    private static final Supplier<SimpleCaffeineCacheListener> INSTANCE = SingletonSupplier
            .of(SimpleCaffeineCacheListener::new);

    public static SimpleCaffeineCacheListener newInstance() {
        return INSTANCE.get();
    }

    @Override
    public void onRemoval(Object key, Object value, RemovalCause cause) {
        logger.info("cache {} key={} value={}", cause, key, value);
    }

}
