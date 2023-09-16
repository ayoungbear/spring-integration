package com.github.ayoungbear.spring.integration.cache.caffeine;

import com.github.ayoungbear.spring.integration.cache.CacheExpressionRootObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

/**
 * 注解式 CaffeineCache 缓存配置信息解析，根据注解配置覆盖默认配置。
 *
 * @author yangzexiong
 * @date 2023/6/5
 * @see CaffeineCacheConfig
 */
public class AnnotationCaffeineCacheConfigResolver extends DefaultCaffeineCacheConfigResolver {

    private final CaffeineCacheConfigExpressionEvaluator evaluator = new CaffeineCacheConfigExpressionEvaluator();

    @Override
    public CaffeineCacheOperationConfig resolveConfig(String name, CacheOperationInvocationContext<?> context) {
        CaffeineCacheOperationConfig defaultConfig = super.resolveConfig(name, context);

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(context.getTarget());
        Method method = (!Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(context.getMethod(),
                targetClass) : context.getMethod());

        CacheExpressionRootObject rootObject = new CacheExpressionRootObject(method, context.getArgs(),
                context.getTarget(), targetClass);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(rootObject, method,
                context.getArgs(), evaluator.getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }

        CaffeineCacheOperationConfig classConfig = resolveConfig(targetClass, evaluationContext);
        CaffeineCacheOperationConfig methodConfig = resolveConfig(method, evaluationContext);

        CaffeineCacheOperationConfig config = CaffeineCacheConfigUtils.mergeConfig(defaultConfig, classConfig);
        return CaffeineCacheConfigUtils.mergeConfig(config, methodConfig);
    }

    private CaffeineCacheOperationConfig resolveConfig(AnnotatedElement element,
            MethodBasedEvaluationContext evaluationContext) {
        CaffeineCacheConfig caffeineCacheConfig = AnnotatedElementUtils
                .getMergedAnnotation(element, CaffeineCacheConfig.class);
        if (caffeineCacheConfig == null) {
            return null;
        }
        AnnotatedElementKey elementKey = new AnnotatedElementKey(element, null);
        CaffeineCacheOperationConfig config = new CaffeineCacheOperationConfig();
        if (StringUtils.hasText(caffeineCacheConfig.maximumSize())) {
            config.setMaximumSize(
                    evaluator.evaluateInteger(caffeineCacheConfig.maximumSize(), elementKey, evaluationContext));
        }
        if (StringUtils.hasText(caffeineCacheConfig.expireAfterWrite())) {
            config.setExpireAfterWrite(
                    evaluator.evaluateInteger(caffeineCacheConfig.expireAfterWrite(), elementKey, evaluationContext));
        }
        if (StringUtils.hasText(caffeineCacheConfig.expireAfterAccess())) {
            config.setExpireAfterAccess(
                    evaluator.evaluateInteger(caffeineCacheConfig.expireAfterAccess(), elementKey, evaluationContext));
        }
        if (StringUtils.hasText(caffeineCacheConfig.refreshAfterWrite())) {
            config.setRefreshAfterWrite(
                    evaluator.evaluateInteger(caffeineCacheConfig.refreshAfterWrite(), elementKey, evaluationContext));
        }
        if (StringUtils.hasText(caffeineCacheConfig.executor())) {
            config.setExecutor(findExecutor(caffeineCacheConfig.executor()));
        }
        config.setSoftRef(caffeineCacheConfig.softRef());
        return config;
    }

}
