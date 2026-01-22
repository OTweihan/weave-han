package com.han.common.ratelimiter.aspectj;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import com.han.common.core.constant.GlobalConstants;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.MessageUtils;
import com.han.common.core.utils.ServletUtils;
import com.han.common.core.utils.SpringUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.ratelimiter.annotation.RateLimiter;
import com.han.common.ratelimiter.enums.LimitType;
import com.han.common.redis.utils.RedisUtils;
import org.redisson.api.RateType;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 限流处理
 */
@Slf4j
@Aspect
public class RateLimiterAspect {

    /**
     * 定义spel表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();
    
    /**
     * 定义spel解析模版
     */
    private final ParserContext parserContext = new TemplateParserContext();

    /**
     * 方法参数解析器
     */
    private final ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();

    /**
     * SpEL 表达式缓存
     */
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();


    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        int timeout = rateLimiter.timeout();
        try {
            String combineKey = getCombineKey(rateLimiter, point);
            RateType rateType = RateType.OVERALL;
            if (rateLimiter.limitType() == LimitType.CLUSTER) {
                rateType = RateType.PER_CLIENT;
            }
            long number = RedisUtils.rateLimiter(combineKey, rateType, count, time, timeout);
            if (number == -1) {
                String message = rateLimiter.message();
                if (StringUtils.startsWith(message, "{") && StringUtils.endsWith(message, "}")) {
                    message = MessageUtils.message(StringUtils.substring(message, 1, message.length() - 1));
                }
                throw new ServiceException(message);
            }
            log.debug("限制令牌 => {}, 剩余令牌 => {}, 缓存key => '{}'", count, number, combineKey);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw e;
            } else {
                throw new RuntimeException("服务器限流异常，请稍候再试", e);
            }
        }
    }

    private String getCombineKey(RateLimiter rateLimiter, JoinPoint point) {
        String key = rateLimiter.key();
        // 判断 key 不为空 和 不是表达式
        if (StringUtils.isNotBlank(key) && StringUtils.containsAny(key, "#")) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method targetMethod = signature.getMethod();
            Object[] args = point.getArgs();
            MethodBasedEvaluationContext context =
                new MethodBasedEvaluationContext(point.getTarget(), targetMethod, args, pnd);
            context.setBeanResolver(new BeanFactoryResolver(SpringUtils.getBeanFactory()));
            Expression expression = expressionCache.computeIfAbsent(key, k -> {
                if (StringUtils.startsWith(k, parserContext.getExpressionPrefix())
                    && StringUtils.endsWith(k, parserContext.getExpressionSuffix())) {
                    return parser.parseExpression(k, parserContext);
                }
                return parser.parseExpression(k);
            });
            key = expression.getValue(context, String.class);
        }
        StringBuilder stringBuffer = new StringBuilder(GlobalConstants.RATE_LIMIT_KEY);
        HttpServletRequest request = ServletUtils.getRequest();
        if (request != null) {
            stringBuffer.append(request.getRequestURI()).append(":");
        } else {
            stringBuffer.append("default:");
        }
        if (rateLimiter.limitType() == LimitType.IP) {
            // 获取请求ip
            if (request != null) {
                stringBuffer.append(ServletUtils.getClientIP(request)).append(":");
            } else {
                stringBuffer.append("unknown:");
            }
        } else if (rateLimiter.limitType() == LimitType.CLUSTER) {
            // 获取客户端实例id
            stringBuffer.append(RedisUtils.getClient().getId()).append(":");
        }
        return stringBuffer.append(key).toString();
    }
}
