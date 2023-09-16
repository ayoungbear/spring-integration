package com.github.ayoungbear.spring.integration.cache.caffeine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * CaffeineCacheConfigExpressionEvaluator
 *
 * @author yangzexiong
 * @date 2023/6/6
 */
public class CaffeineCacheConfigExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    public Integer evaluateInteger(String expression, AnnotatedElementKey elementKey,
            EvaluationContext evaluationContext) {
        return getExpression(this.expressionCache, elementKey, expression).getValue(evaluationContext, Integer.class);
    }

    @Override
    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return super.getParameterNameDiscoverer();
    }

}
