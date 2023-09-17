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

package com.github.ayoungbear.spring.integration.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * 缓存配置相关 SpEL 表达式解析。
 *
 * @author yangzexiong
 */
public class CacheConfigExpressionEvaluator extends CachedExpressionEvaluator {

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
