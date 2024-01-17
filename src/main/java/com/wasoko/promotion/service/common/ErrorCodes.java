package com.wasoko.promotion.service.common;

import lombok.Getter;

/**
 * The enum Error codes.
 *
 * This enumeration defines error codes that are used to identify specific error scenarios in the application.
 * Each error code is associated with a unique numeric value.
 *
 * Additionally, each error code is accompanied by a brief description of its meaning and context.
 *
 * @author Prince
 */
@Getter
public enum ErrorCodes {

    /**
     * Promo er 001 error codes.
     * Indicates an error related to Promotion with code ER-001: General error in promotion processing.
     */
    PROMO_ER_001(1),

    /**
     * Promo er 002 error codes.
     * Indicates an error related to Promotion with code ER-002: Bad request, no data found.
     */
    PROMO_ER_002(2),

    /**
     * Promo er 003 error codes.
     * Indicates an error related to Promotion with code ER-003: Unable to read HTTP message.
     */
    PROMO_ER_003(3),

    /**
     * Promo er 004 error codes.
     * Indicates an error related to Promotion with code ER-004: Unexpected error occurred during promotion evaluation.
     */
    PROMO_ER_004(4),

    /**
     * Promo er 005 error codes.
     * Indicates an error related to Promotion with code ER-005.
     */
    PROMO_ER_005(5),

    /**
     * Promo er 006 error codes.
     * Indicates an error related to Promotion with code ER-006.
     */
    PROMO_ER_006(6),

    /**
     * Promo er 007 error codes.
     * Indicates an error related to Promotion with code ER-007: Business exception occurred.
     */
    PROMO_ER_007(7),

    /**
     * Promo er 008 error codes.
     * Indicates an error related to Promotion with code ER-008: Resource not found.
     */
    PROMO_ER_008(8);

    private final int errorCode;

    ErrorCodes(int errorCode){
        this.errorCode = errorCode;
    }

}
