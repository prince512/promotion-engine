package com.wasoko.promotion.service.common;

/**
 * The type Constants.
 */
public class Constants {

    /**
     * The constant ERROR_OCCURRED_WITH_PARAM.
     */
    public static final String ERROR_OCCURRED_WITH_PARAM = "Error has occurred - {}";

    /**
     * The constant ERRORS_OCCURRED_WITH_OVERALL_MSG.
     */
    public static final String ERRORS_OCCURRED_WITH_OVERALL_MSG = "Errors occurred: {}. Overall error message: {}";

    /**
     * The constant AUTHORIZATION.
     */
    public static final String AUTHORIZATION_KEY = "Authorization";
    /**
     * The constant AUTHORIZATION_VALUE.
     */
    public static final String AUTHORIZATION_VALUE = "$2a$12$AO9wJLpDAvpffEkqn4kDuuBVdSPfcUrzwv7yLHEc8vYtP.NDIXKF2";

    /**
     * The constant STANDARD_ERROR_MESSAGE.
     */
    public static final String STANDARD_ERROR_MESSAGE = "Invalid token provided. Please provide a correct token.";
    public static final String SPACE = "";
    public static final Integer MAX_VALUE_LIMIT = 999999999;
    private Constants() {
        throw new IllegalStateException("Constants class should not be initialized");
    }

}