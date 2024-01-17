package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.entities.*;
import com.wasoko.promotion.service.repository.PromotionRepository;
import com.wasoko.promotion.service.repository.PromotionSegmentRepository;
import com.wasoko.promotion.service.service.impl.PromotionCustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.wasoko.promotion.service.common.ConditionType.UNCONDITIONAL;
import static com.wasoko.promotion.service.common.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The type Promotion customer service impl test.
 */
class PromotionCustomerServiceImplTest {
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private PromotionSegmentRepository promotionSegmentRepository;
    @InjectMocks
    private PromotionCustomerServiceImpl promotionCustomerService;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test get promotion with valid serviceability area id and customer id.
     */
    @Test
    void testGetPromotionWithValidServiceabilityAreaIdAndCustomerID() {
        createMockPromotionSegmentRepository(DEFAULT_PROMOTIONS_FOR_CUSTOMER);
        createMockPromotionRepository(DEFAULT_PROMOTIONS_FOR_CUSTOMER, DEFAULT_PROMOTION_ID);
        RootResponseDTO responseDTO = promotionCustomerService.getPromotionsForCustomerAndServiceabilityArea(
                SERVICEABILITY_AREA_ID, CUSTOMER_ID);

        verify(promotionRepository, times(1)).findAllByIdIn(DEFAULT_PROMOTIONS_FOR_CUSTOMER);
        assertNotNull(responseDTO);
        assertTrue(responseDTO.isStatus());
        List<?> data = (List<?>) responseDTO.getData();
        assertFalse(data.isEmpty());
    }

    /**
     * Test get promotion with valid serviceability area id and customer id.
     */
    @Test
    void testEmptyPromotionCreativeForValidServiceabilityAndCustomer() {
        createMockPromotionSegmentRepository(DEFAULT_PROMOTIONS_FOR_CUSTOMER);
        when(promotionRepository.findAllByIdIn(DEFAULT_PROMOTIONS_FOR_CUSTOMER)).thenReturn(Collections.singletonList(
                Promotion.builder()
                        .id(DEFAULT_PROMOTION_ID)
                        .promotionCreative(PromotionCreative.builder()
                                .build())
                        .promotionLimit(PromotionLimit.builder()
                                .id(PROMOTION_LIMIT_ID)
                                .limitPerCustomer(LIMIT_PER_CUSTOMER)
                                .maxCount(MAX_COUNT)
                                .offer(CatalogOffer.builder().build())
                                .build())
                        .promotionCondition(PromotionCondition.builder()
                                .value(PROMOTION_CONDITION_VALUE)
                                .type(UNCONDITIONAL.name())
                                .build())
                        .maxGlobalApplications(10).maxUserApplications(10).maxDiscount(100F).maxGlobalDiscount(new BigDecimal(100))
                        .promotionRedemptionList(Collections.singletonList(PromotionRedemption.builder().quantity(QUANTITY_1).customerId(UUID.fromString(CUSTOMER_ID)).orderId(UUID.fromString(ORDER_ID)).discountAvailed(DISCOUNT_AVAIL_2).promotion(Promotion.builder().id(9L).build()).build()))

                        .build()));

        RootResponseDTO responseDTO = promotionCustomerService.getPromotionsForCustomerAndServiceabilityArea(
                SERVICEABILITY_AREA_ID, CUSTOMER_ID);

        verify(promotionRepository, times(1)).findAllByIdIn(DEFAULT_PROMOTIONS_FOR_CUSTOMER);
        assertNotNull(responseDTO);
        assertTrue(responseDTO.isStatus());
        List<?> data = (List<?>) responseDTO.getData();
        assertFalse(data.isEmpty());
    }

    /**
     * Test get promotion with invalid serviceability area id and customer id.
     */
    @Test
    void testGetPromotionWithInvalidServiceabilityAreaIdAndCustomerID() {
        createMockPromotionSegmentRepository(DEFAULT_PROMOTIONS_FOR_CUSTOMER);
        when(promotionRepository.findAllByIdIn(DEFAULT_PROMOTIONS_FOR_CUSTOMER)).thenReturn(Collections.emptyList());

        RootResponseDTO responseDTO = promotionCustomerService.getPromotionsForCustomerAndServiceabilityArea(
                SERVICEABILITY_AREA_ID, CUSTOMER_ID);

        verify(promotionRepository, times(1)).findAllByIdIn(DEFAULT_PROMOTIONS_FOR_CUSTOMER);
        List<?> data = (List<?>) responseDTO.getData();
        assertTrue(data.isEmpty());
    }

    private void createMockPromotionSegmentRepository(List<Long> promotionsForCustomer) {
        when(promotionSegmentRepository.findPromotionsByServiceabilityAreaIdAndCustomerID(
                UUID.fromString(SERVICEABILITY_AREA_ID), UUID.fromString(CUSTOMER_ID)))
                .thenReturn(promotionsForCustomer);
    }

    private void createMockPromotionRepository(List<Long> promotionsForCustomer, Long promotionId) {
        when(promotionRepository.findAllByIdIn(promotionsForCustomer)).thenReturn(Collections.singletonList(
                Promotion.builder()
                        .id(promotionId)
                        .promotionCreative(PromotionCreative.builder()
                                .banner(BANNER)
                                .teaser1(TEASER1)
                                .teaser2(TEASER2)
                                .teaserImageUrl(TEASER_IMAGE_URL)
                                .bannerImageUrl(PROMOTION_BANNER)
                                .build())
                        .promotionRedemptionList(Collections.singletonList(PromotionRedemption.builder().quantity(QUANTITY_1).customerId(UUID.fromString(CUSTOMER_ID)).orderId(UUID.fromString(ORDER_ID)).discountAvailed(DISCOUNT_AVAIL_2).promotion(Promotion.builder().id(9L).build()).build()))
                        .promotionLimit(PromotionLimit.builder()
                                .id(PROMOTION_LIMIT_ID)
                                .limitPerCustomer(LIMIT_PER_CUSTOMER)
                                .maxCount(MAX_COUNT)
                                .offer(CatalogOffer.builder().build())
                                .build())
                        .maxGlobalApplications(10).maxUserApplications(10).maxDiscount(100F).maxGlobalDiscount(new BigDecimal(100))
                        .promotionCondition(PromotionCondition.builder()
                                .value(PROMOTION_CONDITION_VALUE)
                                .type(UNCONDITIONAL.name())
                                .build())
                        .build()));
    }
}
