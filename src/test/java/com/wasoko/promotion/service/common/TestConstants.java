package com.wasoko.promotion.service.common;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestConstants {

    // Serviceability Constants
    public static final String SERVICEABILITY_AREA_ID = "1072f955-80f0-4c2e-b209-994aa80d2016";
    public static final String CUSTOMER_ID = "17bf7e5f-55df-4a5e-a5f4-1cc05a3f9d9f";

    // Default Promotion Constants
    public static final Long DEFAULT_PROMOTION_ID = 9L;
    public static final List<Long> DEFAULT_PROMOTIONS_FOR_CUSTOMER = Arrays.asList(1L, 2L);

    // Banner and Teaser Constants
    public static final String BANNER = "banner";
    public static final String TEASER1 = "teaser1";
    public static final String TEASER2 = "teaser2";
    public static final String TEASER_IMAGE_URL = "teaserImageUrl";

    // Promotion Limit Constants
    public static final Long PROMOTION_LIMIT_ID = 1L;
    public static final int LIMIT_PER_CUSTOMER = 3;
    public static final int MAX_COUNT = 5;

    // Fixed Benefit Constants
    public static final Float FIXED_BENEFIT = 10F;

    // Promotion Condition Constants
    public static final int PROMOTION_CONDITION_VALUE = 20;

    // Promotion Details Constants
    public static final String PROMOTION_NAME = "test";
    public static final String PROMOTION_DESCRIPTION = "test";
    public static final String PROMOTION_SLUG = "test";
    public static final String PROMOTION_TNC = "tnc";
    public static final String PROMOTION_TYPE = "FlashSale";
    public static final String PROMOTION_BANNER = "682452.jpeg";
    public static final String PROMOTION_TEASER = "test teaser 2";
    public static final float PROMOTION_BENEFIT_VALUE = 21.0F;
    public static final int PROMOTION_BENEFIT_MAX_AFFECTED_ITEMS = 5;
    public static final int PROMOTION_LIMIT_PER_CUSTOMER = 2;
    public static final int PROMOTION_LIMIT_MAX_COUNT = 2;

    // Order and Offer Constants
    public static final String ORDER_ID = UUID.randomUUID().toString();
    public static final Long OFFER_ID_1 = 1L;
    public static final Long PROMO_ID_1 = 1L;
    public static final Integer QUANTITY_1 = 2;
    public static final Float DISCOUNT_AVAIL_1 = 10F;
    public static final Long OFFER_ID_2 = 2L;
    public static final Long PROMO_ID_2 = 2L;
    public static final Integer QUANTITY_2 = 2;
    public static final Float DISCOUNT_AVAIL_2 = 10F;

    // Promotion Names
    public static final String PROMOTION_NAME_1 = "Promotion 1";

    // Catalog Offer Constants
    public static final Long CATALOG_OFFER_ID = 1L;
    public static final double ORIGINAL_PRICE = 100.0;
    public static final Float CATALOG_PRICE = 100F;
    public static final int QUANTITY = 1;

    // Promotion Application Limits
    public static final int MAX_USER_APPLICATIONS = 5;
    public static final int MAX_GLOBAL_APPLICATIONS = 10;
    public static final int MAX_BASKET_APPLICATIONS = 2;
    public static final float MAX_DISCOUNT = 100F;

    // Base URI for Promotions
    public static final String PROMOTIONS_BASE_URI = "/promotions";

    // Empty String
    public static final String EMPTY_STRING = "";

    // Customer Endpoint and Invalid IDs
    public static final String CUSTOMER_ENDPOINT = "customer";
    public static final String INVALID_SERVICEABILITY_AREA_ID = SERVICEABILITY_AREA_ID + "invalid";
    public static final String INVALID_CUSTOMER_ID = CUSTOMER_ID + "invalid";
    public static final String PROMOTION_VALIDATE_END_POINT = "validate";
    private TestConstants() {
        throw new IllegalStateException("Constants class should not be initialized");
    }
}