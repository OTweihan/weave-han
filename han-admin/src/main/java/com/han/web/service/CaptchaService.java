package com.han.web.service;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.han.common.core.constant.Constants;
import com.han.common.core.constant.GlobalConstants;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.SpringUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.reflect.ReflectUtils;
import com.han.common.mail.config.properties.MailProperties;
import com.han.common.mail.utils.MailUtils;
import com.han.common.ratelimiter.annotation.RateLimiter;
import com.han.common.ratelimiter.enums.LimitType;
import com.han.common.redis.utils.RedisUtils;
import com.han.common.web.config.properties.CaptchaProperties;
import com.han.common.web.enums.CaptchaType;
import com.han.web.domain.vo.CaptchaVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-16
 * @Description: 验证码服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaProperties captchaProperties;
    private final MailProperties mailProperties;

    /**
     * 发送短信验证码
     * <p>
     * 限制频率：每分钟同一手机号只能发送一次
     *
     * @param phonenumber 用户手机号
     * @throws ServiceException 短信发送失败时抛出
     */
    @RateLimiter(key = "#phonenumber", time = 60, count = 1)
    public void sendSmsCode(String phonenumber) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + phonenumber;
        // 生成4位随机数字验证码
        String code = RandomUtil.randomNumbers(4);
        // 存入Redis，设置过期时间
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));

        // 验证码模板id 自行处理 (查数据库或写死均可)
        String templateId = "";
        LinkedHashMap<String, String> map = new LinkedHashMap<>(1);
        map.put("code", code);

        // 获取短信发送工厂并发送
        SmsBlend smsBlend = SmsFactory.getSmsBlend("config1");
        SmsResponse smsResponse = smsBlend.sendMessage(phonenumber, templateId, map);

        if (!smsResponse.isSuccess()) {
            log.error("短信验证码发送异常 => {}", smsResponse);
            throw new ServiceException(smsResponse.getData().toString());
        }
    }

    /**
     * 发送邮箱验证码
     * <p>
     * 校验系统邮箱开关，并通过AOP代理调用实现限流
     *
     * @param email 邮箱地址
     * @throws ServiceException 邮箱功能未开启或发送失败时抛出
     */
    public void sendEmailCode(String email) {
        if (!mailProperties.getEnabled()) {
            throw new ServiceException("当前系统没有开启邮箱功能！");
        }
        // 使用AOP代理调用，确保内部方法的@RateLimiter注解生效
        SpringUtils.getAopProxy(this).sendEmailCodeChecked(email);
    }

    /**
     * 发送邮箱验证码（实际执行方法）
     * <p>
     * 限制频率：每分钟同一邮箱只能发送一次
     * 独立方法避免验证码关闭之后仍然走限流
     *
     * @param email 邮箱地址
     */
    @RateLimiter(key = "#email", time = 60, count = 1)
    public void sendEmailCodeChecked(String email) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + email;
        // 生成4位随机数字验证码
        String code = RandomUtil.randomNumbers(4);
        // 存入Redis，设置过期时间
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        try {
            // 发送邮件
            MailUtils.sendText(email, "登录验证码", "您本次验证码为：" + code + "，有效性为" + Constants.CAPTCHA_EXPIRATION + "分钟，请尽快填写。");
        } catch (Exception e) {
            log.error("短信验证码发送异常 => {}", e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 生成图形验证码
     * <p>
     * 校验验证码开关，并通过AOP代理调用生成逻辑
     *
     * @return 验证码对象 {@link CaptchaVo}
     */
    public CaptchaVo createCaptcha() {
        boolean captchaEnabled = captchaProperties.getEnable();
        if (!captchaEnabled) {
            CaptchaVo captchaVo = new CaptchaVo();
            captchaVo.setCaptchaEnabled(false);
            return captchaVo;
        }
        // 使用AOP代理调用，确保内部方法的@RateLimiter注解生效
        return SpringUtils.getAopProxy(this).createCaptchaChecked();
    }

    /**
     * 生成图形验证码（实际执行方法）
     * <p>
     * 限制频率：每分钟同一IP限制请求10次
     * 包含验证码生成、数学计算（如果是数学类型）、Redis缓存存储等逻辑
     *
     * @return 验证码对象，包含UUID和Base64图片
     */
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    public CaptchaVo createCaptchaChecked() {
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;

        // 获取配置的验证码类型和参数
        CaptchaType captchaType = captchaProperties.getType();
        CodeGenerator codeGenerator;
        if (CaptchaType.MATH == captchaType) {
            // 数学类型：长度为参与运算的数字个数
            codeGenerator = ReflectUtils.newInstance(captchaType.getClazz(), captchaProperties.getNumberLength(), false);
        } else {
            // 字符类型：长度为字符个数
            codeGenerator = ReflectUtils.newInstance(captchaType.getClazz(), captchaProperties.getCharLength());
        }

        // 获取验证码实现类（如CircleCaptcha, LineCaptcha等）
        AbstractCaptcha captcha = SpringUtils.getBean(captchaProperties.getCategory().getClazz());
        captcha.setGenerator(codeGenerator);
        captcha.createCode();

        // 如果是数学验证码，使用SpEL表达式处理验证码结果（例如 "1+1=" -> "2"）
        String code = captcha.getCode();
        if (CaptchaType.MATH == captchaType) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(StringUtils.remove(code, "="));
            code = exp.getValue(String.class);
        }

        // 存入Redis，设置过期时间
        RedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));

        // 构建返回对象
        CaptchaVo captchaVo = new CaptchaVo();
        captchaVo.setUuid(uuid);
        captchaVo.setImg(captcha.getImageBase64());
        return captchaVo;
    }
}
