package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.common.PromotionState;
import com.wasoko.promotion.service.dto.response.PromotionDetailsResponseDto;
import com.wasoko.promotion.service.entities.*;
import com.wasoko.promotion.service.exception.PromotionNotFoundException;
import com.wasoko.promotion.service.repository.PromotionOfferMapRepository;
import com.wasoko.promotion.service.repository.PromotionRepository;
import com.wasoko.promotion.service.service.impl.PromotionDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.wasoko.promotion.service.common.BenefitsType.FIXED_PRICE;
import static com.wasoko.promotion.service.common.ConditionType.UNCONDITIONAL;
import static com.wasoko.promotion.service.common.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The type Promotion detail service impl test.
 */
class PromotionDetailServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionOfferMapRepository promotionOfferMapRepository;

    @InjectMocks
    private PromotionDetailServiceImpl promotionDetailService;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Promotion offer map.
     */
    @Test
    @DisplayName("Should Get Promotion By Id Successfully")
    void testGetPromotionDetailsFound() {
        Promotion mockedPromotion = createPromotion(DEFAULT_PROMOTION_ID);
        when(promotionRepository.findActiveOrApprovedPromotionWithinDateRange(DEFAULT_PROMOTION_ID)).thenReturn(Optional.of(mockedPromotion));
        PromotionOfferMap PromotionOfferMap = new PromotionOfferMap();
        PromotionOfferMap.setPromotion(mockedPromotion);
        when(promotionOfferMapRepository.findByOffer_IdIn(Set.of(OFFER_ID_1))).thenReturn(List.of(PromotionOfferMap));
        PromotionOfferMap promotionOfferMap = new PromotionOfferMap();
        promotionOfferMap.setOffer(new CatalogOffer());
        promotionOfferMap.getOffer().setId(OFFER_ID_1);
        promotionOfferMap.setPromotion(mockedPromotion);
        when(promotionOfferMapRepository.findByPromotion_Id(DEFAULT_PROMOTION_ID)).thenReturn(Collections.singletonList(promotionOfferMap));
        RootResponseDTO responseDTO = promotionDetailService.getPromotion(DEFAULT_PROMOTION_ID);
        verify(promotionRepository, times(1)).findActiveOrApprovedPromotionWithinDateRange(DEFAULT_PROMOTION_ID);
        assertNotNull(responseDTO);
        assertTrue(responseDTO.getData() instanceof PromotionDetailsResponseDto);
        PromotionDetailsResponseDto promotionDetailsResponseDto = (PromotionDetailsResponseDto) responseDTO.getData();
        assertEquals(PROMOTION_NAME, promotionDetailsResponseDto.getName());
    }

    /**
     * Test get promotion not found.
     */
    @Test
    void testGetPromotionDetailsNotFound() {
        when(promotionRepository.findById(DEFAULT_PROMOTION_ID)).thenReturn(Optional.empty());
        assertThrows(PromotionNotFoundException.class, () -> promotionDetailService.getPromotion(DEFAULT_PROMOTION_ID));
        verify(promotionRepository, times(1)).findActiveOrApprovedPromotionWithinDateRange(DEFAULT_PROMOTION_ID);
    }

    private Promotion createPromotion(Long promotionId) {
        return Promotion.builder()
                .id(promotionId)
                .name(PROMOTION_NAME)
                .description(PROMOTION_DESCRIPTION)
                .maxUserApplications(0)
                .slug(PROMOTION_SLUG)
                .state(PromotionState.LIVE.name())
                .tnc(PROMOTION_TNC)
                .type(PROMOTION_TYPE)
                .startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                .endDatetime(LocalDateTime.now(ZoneOffset.UTC))
                .promotionBenefits(createPromotionBenefit())
                .promotionCreative(createPromotionCreative())
                .promotionCondition(createPromotionCondition())
                .promotionLimit(createPromotionLimit())
                .build();
    }

    private PromotionBenefit createPromotionBenefit() {
        return PromotionBenefit.builder()
                .type(FIXED_PRICE.name())
                .value(PROMOTION_BENEFIT_VALUE)
                .build();
    }

    private PromotionCreative createPromotionCreative() {
        return PromotionCreative.builder()
                .banner(PROMOTION_BANNER)
                .teaser1(PROMOTION_TEASER)
                .build();
    }

    private PromotionCondition createPromotionCondition() {
        return PromotionCondition.builder()
                .type(UNCONDITIONAL.name())
                .value(PROMOTION_CONDITION_VALUE)
                .build();
    }

    private PromotionLimit createPromotionLimit() {
        return PromotionLimit.builder()
                .limitPerCustomer(PROMOTION_LIMIT_PER_CUSTOMER)
                .maxCount(PROMOTION_LIMIT_MAX_COUNT)
                .build();
    }
}