package com.wasoko.promotion.service.aop.security;

import static com.wasoko.promotion.service.common.Constants.AUTHORIZATION_KEY;
import static com.wasoko.promotion.service.common.Constants.STANDARD_ERROR_MESSAGE;

import com.wasoko.promotion.service.exception.AuthenticationAndAuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * The type Base authorization aop.
 */
@Slf4j
@Aspect
@Configuration
public class BaseAuthorizationAOP {

    @Value("${rescue.authKey}")
    private String authKey;
    private final HttpServletRequest request;

    /**
     * Constructor for AuthorizationValidationAspect.
     *
     * @param request The HTTPServletRequest to access request headers.
     */
    public BaseAuthorizationAOP(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Perform authorization validation before executing controller methods.
     */
    @Before("execution(* com.wasoko.promotion.service.controller.*.*(..))")
    public void validateAuthorization() {
        String requestAuthKey = request.getHeader(AUTHORIZATION_KEY);
        log.trace("Authorization Token Validation: " + requestAuthKey);
        if (!authKey.equals(requestAuthKey)) {
            throw new AuthenticationAndAuthorizationException(STANDARD_ERROR_MESSAGE);
        }
    }
}
