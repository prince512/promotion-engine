package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.dto.request.OfferDetails;
import com.wasoko.promotion.service.dto.request.PromotionValidationRequestDto;
import com.wasoko.promotion.service.dto.response.OfferPromotionDto;
import com.wasoko.promotion.service.dto.response.OfferPromotionResponse;
import com.wasoko.promotion.service.entities.*;
import com.wasoko.promotion.service.repository.PromotionOfferMapRepository;
import com.wasoko.promotion.service.service.impl.PromotionValidationServiceImpl;
import com.wasoko.promotion.service.strategies.benefits.BenefitFactory;
import com.wasoko.promotion.service.strategies.conditions.ConditionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.wasoko.promotion.service.common.BenefitsType.FIXED_PRICE;
import static com.wasoko.promotion.service.common.ConditionType.UNCONDITIONAL;
import static com.wasoko.promotion.service.common.PromotionState.LIVE;
import static com.wasoko.promotion.service.common.TestConstants.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

/**
 * The type Promotion validation service impl test.
 */
public class PromotionValidationServiceImplTest {

    @InjectMocks
    private PromotionValidationServiceImpl promotionValidationService;

    @Mock
    private PromotionOfferMapRepository promotionOfferMapRepository;

    @Mock
    private ConditionFactory conditionFactory;

    @Mock
    private BenefitFactory benefitFactory;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test validate promotions with no promotions.
     */
    @Test
    public void testNoPromotionsFoundForValidation() {
        PromotionValidationRequestDto requestDto = new PromotionValidationRequestDto();
        ArrayList<OfferDetails> offerDetailsList = new ArrayList<>();
        offerDetailsList.add(new OfferDetails(OFFER_ID_1, ORIGINAL_PRICE, QUANTITY));
        requestDto.setOfferDetails(offerDetailsList);

        // Mock the behavior of promotionOfferMapRepository to return an empty list
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(new ArrayList<>());

        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(requestDto);
        // Perform assertions on the response
        OfferPromotionResponse offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;

        // Check that the response is not null
        assert offerPromotionResponse != null;
        assertNotNull(offerPromotionResponse);
        // Check that the response is an instance of OfferPromotionResponse
        assertEquals(offerDetailsList.size(), offerPromotionResponse.getOfferPromotionDtoList().size());
        for (OfferPromotionDto offerPromotionDto : offerPromotionResponse.getOfferPromotionDtoList()) {
            assertEquals(ORIGINAL_PRICE, offerPromotionDto.getOriginalPrice());
            assertEquals(ORIGINAL_PRICE, offerPromotionDto.getDiscountedPrice());
        }

        // Verify that relevant methods were called
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());
        verifyNoMoreInteractions(promotionOfferMapRepository, conditionFactory, benefitFactory);
    }

    /**
     * Test validate promotions with empty promotions.
     */
    @Test
    public void testEmptyPromotionsFoundForValidation() {
        // Create a mock PromotionValidationRequestDto with sample offer details
        PromotionValidationRequestDto requestDto = new PromotionValidationRequestDto();
        ArrayList<OfferDetails> offerDetailsList = new ArrayList<>();
        // Add OfferDetails to the list as needed
        offerDetailsList.add(new OfferDetails(OFFER_ID_1, ORIGINAL_PRICE, QUANTITY));
        requestDto.setOfferDetails(offerDetailsList);

        // Mock the behavior of promotionOfferMapRepository to return an empty list
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(new ArrayList<>());

        // Call the method to be tested
        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(requestDto);

        // Perform assertions on the response
        OfferPromotionResponse offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        assertEquals(offerDetailsList.size(), offerPromotionResponse.getOfferPromotionDtoList().size());
        for (OfferPromotionDto offerPromotionDto : offerPromotionResponse.getOfferPromotionDtoList()) {
            assertEquals(ORIGINAL_PRICE, offerPromotionDto.getOriginalPrice());
            assertEquals(ORIGINAL_PRICE, offerPromotionDto.getDiscountedPrice());
        }

        // Verify that relevant methods were called
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());
        verifyNoMoreInteractions(promotionOfferMapRepository, conditionFactory, benefitFactory);
    }

    /**
     * Test validate promotions with promotions 1.
     */
    @Test
    public void testNoOfferPromotionMatchedForValidation() {
        // Create a mock PromotionValidationRequestDto with sample offer details
        PromotionValidationRequestDto requestDto = new PromotionValidationRequestDto();
        ArrayList<OfferDetails> offerDetailsList = new ArrayList<>();
        // Add OfferDetails to the list as needed
        offerDetailsList.add(new OfferDetails(OFFER_ID_1, ORIGINAL_PRICE, QUANTITY));
        requestDto.setOfferDetails(offerDetailsList);
        requestDto.setServiceabilityAreaId(SERVICEABILITY_AREA_ID);
        requestDto.setCustomerId(CUSTOMER_ID);

        // Create mock promotion data
        PromotionOfferMap promotionOfferMap = new PromotionOfferMap();
        promotionOfferMap.setId(1L);

        List<PromotionRedemption> promotionRedemptionList = new ArrayList<>();
        Promotion promotion = new Promotion();
        promotion.setId(PROMO_ID_1);
        promotion.setName(PROMOTION_NAME_1);
        promotion.setPromotionBenefits(
                PromotionBenefit.builder().value(20F)
                        .build());
        promotion.setPromotionCondition(
                PromotionCondition.builder().type(UNCONDITIONAL.name()).value(PROMOTION_CONDITION_VALUE)
                        .build());
        promotionOfferMap.setPromotion(promotion);

        PromotionRedemption promotionRedemption = new PromotionRedemption();
        promotionRedemption.setPromotion(promotion);
        promotionRedemption.setQuantity(QUANTITY + 2);
        promotionRedemption.setId(2L);
        promotionRedemption.setCustomerId(UUID.fromString(SERVICEABILITY_AREA_ID));
        promotionRedemption.setOrderId(UUID.fromString(ORDER_ID));
        promotionRedemption.setDiscountAvailed(200F);
        promotionRedemption.setOfferId(1L);
        promotionRedemptionList.add(promotionRedemption);
        promotion.setPromotionRedemptionList(promotionRedemptionList);

        CatalogOffer catalogOffer = new CatalogOffer();
        catalogOffer.setId(2L);
        promotionOfferMap.setOffer(catalogOffer);
        // Set other promotion data as needed

        // Mock the behavior of promotionOfferMapRepository to return a list with the mock promotion data
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(
                Collections.singletonList(promotionOfferMap));

        // Call the method to be tested
        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(requestDto);

        // Perform assertions on the response
        OfferPromotionResponse offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        assertEquals(offerDetailsList.size(), offerPromotionResponse.getOfferPromotionDtoList().size());

        // Assuming there's only one promotion in the mock data
        OfferPromotionDto offerPromotionDto = offerPromotionResponse.getOfferPromotionDtoList().get(0);
        assertEquals(ORIGINAL_PRICE, offerPromotionDto.getOriginalPrice());
        assertEquals(ORIGINAL_PRICE, offerPromotionDto.getDiscountedPrice());
        // Verify that relevant methods were called
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());

    }

    /**
     * Test validate promotions with promotions 2.
     */
    @Test
    public void testNoConditionMatchForValidation() {
        // Create a mock PromotionValidationRequestDto with sample offer details
        PromotionValidationRequestDto requestDto = new PromotionValidationRequestDto();
        ArrayList<OfferDetails> offerDetailsList = new ArrayList<>();
        // Add OfferDetails to the list as needed
        offerDetailsList.add(new OfferDetails(OFFER_ID_1, ORIGINAL_PRICE, QUANTITY));
        requestDto.setOfferDetails(offerDetailsList);
        requestDto.setServiceabilityAreaId(SERVICEABILITY_AREA_ID);
        requestDto.setCustomerId(CUSTOMER_ID);

        // Create mock promotion data
        PromotionOfferMap promotionOfferMap = new PromotionOfferMap();
        promotionOfferMap.setId(1L);

        List<PromotionRedemption> promotionRedemptionList = new ArrayList<>();
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName(PROMOTION_NAME);
        promotion.setState(LIVE.name());
        promotion.setStartDatetime(LocalDateTime.now(ZoneOffset.UTC));
        promotion.setEndDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L));
        promotion.setPromotionBenefits(PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE)
                .build());
        promotion.setPromotionCondition(
                PromotionCondition.builder().type(UNCONDITIONAL.name()).value(PROMOTION_CONDITION_VALUE)
                        .build());
        promotionOfferMap.setPromotion(promotion);

        PromotionRedemption promotionRedemption = new PromotionRedemption();
        promotionRedemption.setPromotion(promotion);
        promotionRedemption.setQuantity(QUANTITY + 2);
        promotionRedemption.setId(2L);
        promotionRedemption.setCustomerId(UUID.fromString(SERVICEABILITY_AREA_ID));
        promotionRedemption.setOrderId(UUID.fromString(ORDER_ID));
        promotionRedemption.setDiscountAvailed(200F);
        promotionRedemption.setOfferId(OFFER_ID_1);
        promotionRedemptionList.add(promotionRedemption);
        promotion.setPromotionRedemptionList(promotionRedemptionList);

        CatalogOffer catalogOffer = new CatalogOffer();
        catalogOffer.setId(OFFER_ID_1);
        promotionOfferMap.setOffer(catalogOffer);
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(
                Collections.singletonList(promotionOfferMap));

        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(requestDto);

        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());
        OfferPromotionResponse offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        assertEquals(offerDetailsList.size(), offerPromotionResponse.getOfferPromotionDtoList().size());
        OfferPromotionDto offerPromotionDto = offerPromotionResponse.getOfferPromotionDtoList().get(0);
        assertEquals(OFFER_ID_1, offerPromotionDto.getOfferId());
        assertEquals(ORIGINAL_PRICE, offerPromotionDto.getOriginalPrice());
        assertEquals(ORIGINAL_PRICE, offerPromotionDto.getDiscountedPrice());
    }

    /**
     * Test validate promotions happy path.
     */
    @Test
    void testValidationsForUnconditionalPromotion() {
        // Mock data
        // Create a mock PromotionValidationRequestDto with sample offer details
        PromotionValidationRequestDto requestDto = new PromotionValidationRequestDto();
        ArrayList<OfferDetails> offerDetailsList = new ArrayList<>();
        // Add OfferDetails to the list as needed
        offerDetailsList.add(new OfferDetails(OFFER_ID_1, ORIGINAL_PRICE, QUANTITY));
        requestDto.setOfferDetails(offerDetailsList);
        requestDto.setServiceabilityAreaId(UUID.randomUUID().toString());
        requestDto.setCustomerId(UUID.randomUUID().toString());

        // Create mock promotion data
        PromotionOfferMap promotionOfferMap = new PromotionOfferMap();
        promotionOfferMap.setId(1L);

        List<PromotionRedemption> promotionRedemptionList = new ArrayList<>();
        Promotion promotion = new Promotion();
        promotion.setId(PROMO_ID_1);
        promotion.setName(PROMOTION_NAME + 1);
        promotion.setMaxGlobalDiscount(new BigDecimal(20));
        promotion.setMaxUserApplications(MAX_USER_APPLICATIONS);
        promotion.setMaxGlobalApplications(MAX_GLOBAL_APPLICATIONS);
        promotion.setMaxBasketApplications(MAX_BASKET_APPLICATIONS);
        promotion.setMaxDiscount(MAX_DISCOUNT);
        promotion.setState(LIVE.name());
        promotion.setStartDatetime(LocalDateTime.now(ZoneOffset.UTC));
        promotion.setEndDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L));

        List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices = new ArrayList<>();
        promotionBenefitFixedPrices.add(PromotionBenefitFixedPrice.builder().fixedPrice(10F)
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(20F).isActive(true).build()).build());
        promotion.setPromotionBenefitFixedPrices(promotionBenefitFixedPrices);

        promotion.setPromotionBenefits(PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE)
             .type(FIXED_PRICE.name()).build());
        promotion.setPromotionCondition(
                PromotionCondition.builder().type(UNCONDITIONAL.name()).value(PROMOTION_CONDITION_VALUE)
                        .build());
        promotion.setPromotionLimit(
                PromotionLimit.builder().limitPerCustomer(LIMIT_PER_CUSTOMER).maxCount(MAX_COUNT).build());

        List<PromotionRedemption> promotionRedemptions = new ArrayList<>();
        promotionRedemptions.add(PromotionRedemption.builder().orderId(UUID.fromString(ORDER_ID))
                .customerId(UUID.fromString(CUSTOMER_ID)).offerId(OFFER_ID_1)
                .discountAvailed(DISCOUNT_AVAIL_1).quantity(QUANTITY + 2).build());
        promotion.setPromotionRedemptionList(promotionRedemptions);
        promotionOfferMap.setPromotion(promotion);

        PromotionRedemption promotionRedemption = new PromotionRedemption();
        promotionRedemption.setPromotion(promotion);
        promotionRedemption.setQuantity(QUANTITY + 2);
        promotionRedemption.setId(2L);
        promotionRedemption.setCustomerId(UUID.fromString(CUSTOMER_ID));
        promotionRedemption.setOrderId(UUID.fromString(ORDER_ID));
        promotionRedemption.setDiscountAvailed(DISCOUNT_AVAIL_1);
        promotionRedemption.setOfferId(OFFER_ID_1);
        promotionRedemptionList.add(promotionRedemption);
        promotion.setPromotionRedemptionList(promotionRedemptionList);

        CatalogOffer catalogOffer = new CatalogOffer();
        catalogOffer.setId(OFFER_ID_1);
        promotionOfferMap.setOffer(catalogOffer);
        // Mock repository response
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(
                Collections.singletonList(promotionOfferMap));

        // Mock factory methods
        when(conditionFactory.checkCondition(anyInt(), anyDouble(), any(), anyInt())).thenReturn(true);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(anyDouble());

        // Perform the validation
        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(requestDto);

        // Assertions
        OfferPromotionResponse offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        List<OfferPromotionDto> offers = offerPromotionResponse.getOfferPromotionDtoList();
        OfferPromotionDto offerPromotionDto = offers.get(0);

        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());
        verify(conditionFactory, times(1)).checkCondition(anyInt(), anyDouble(), any(), anyInt());
        verify(benefitFactory, times(1)).applyBenefit(anyDouble(), anyDouble(), any());

        // Assertions based on the mock data and behavior
        assertAll("Validating Promotion Result",
                () -> assertEquals(1, offers.size(), "There should be one offer promotion in the result"),
                () -> assertEquals(requestDto.getOfferDetails().get(0).getOfferId(),
                        offerPromotionDto.getOfferId(), "Offer ID should match"),
                () -> assertEquals(offerDetailsList.get(0).getPrice(), offerPromotionDto.getOriginalPrice(),
                        "Original Price should match"),
                () -> assertEquals(90.0, offerPromotionDto.getDiscountedPrice(),
                        "Discounted Price should match")
        );
    }

    /**
     * Test validate promotions happy path with fixed benefits.
     */
    @Test
    void testValidationsForFixedBenefitPromotion() {
        // Mock data
        // Create a mock PromotionValidationRequestDto with sample offer details
        PromotionValidationRequestDto requestDto = new PromotionValidationRequestDto();
        ArrayList<OfferDetails> offerDetailsList = new ArrayList<>();
        // Add OfferDetails to the list as needed
        offerDetailsList.add(new OfferDetails(OFFER_ID_1, ORIGINAL_PRICE, QUANTITY));
        requestDto.setOfferDetails(offerDetailsList);
        requestDto.setServiceabilityAreaId(UUID.randomUUID().toString());
        requestDto.setCustomerId(UUID.randomUUID().toString());

        // Create mock promotion data
        PromotionOfferMap promotionOfferMap = new PromotionOfferMap();
        promotionOfferMap.setId(1L);

        List<PromotionRedemption> promotionRedemptionList = new ArrayList<>();
        Promotion promotion = new Promotion();
        promotion.setId(PROMO_ID_1);
        promotion.setName(PROMOTION_NAME + 1);
        promotion.setMaxGlobalDiscount(new BigDecimal(20));
        promotion.setMaxUserApplications(MAX_USER_APPLICATIONS);
        promotion.setMaxGlobalApplications(MAX_GLOBAL_APPLICATIONS);
        promotion.setMaxBasketApplications(MAX_BASKET_APPLICATIONS);
        promotion.setMaxDiscount(MAX_DISCOUNT);
        promotion.setState(LIVE.name());
        promotion.setStartDatetime(LocalDateTime.now(ZoneOffset.UTC));
        promotion.setEndDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L));
        List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices = new ArrayList<>();
        promotion.setPromotionBenefitFixedPrices(promotionBenefitFixedPrices);
        promotion.setPromotionBenefits(PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE)
            .type(FIXED_PRICE.name()).build());
        promotion.setPromotionCondition(
                PromotionCondition.builder().type(UNCONDITIONAL.name()).value(PROMOTION_CONDITION_VALUE)
                        .build());
        promotion.setPromotionLimit(
                PromotionLimit.builder().limitPerCustomer(LIMIT_PER_CUSTOMER).maxCount(MAX_COUNT).build());
        List<PromotionRedemption> promotionRedemptions = new ArrayList<>();
        promotionRedemptions.add(PromotionRedemption.builder().orderId(UUID.fromString(ORDER_ID))
                .customerId(UUID.fromString(CUSTOMER_ID)).offerId(OFFER_ID_1)
                .discountAvailed(DISCOUNT_AVAIL_1 - 5).quantity(QUANTITY + 2).build());
        promotion.setPromotionRedemptionList(promotionRedemptions);
        promotionOfferMap.setPromotion(promotion);

        PromotionRedemption promotionRedemption = new PromotionRedemption();
        promotionRedemption.setPromotion(promotion);
        promotionRedemption.setQuantity(QUANTITY + 2);
        promotionRedemption.setId(2L);
        promotionRedemption.setCustomerId(UUID.fromString(CUSTOMER_ID));
        promotionRedemption.setOrderId(UUID.fromString(ORDER_ID));
        promotionRedemption.setDiscountAvailed(DISCOUNT_AVAIL_1 - 5);
        promotionRedemption.setOfferId(OFFER_ID_1);
        promotionRedemptionList.add(promotionRedemption);
        promotion.setPromotionRedemptionList(promotionRedemptionList);

        CatalogOffer catalogOffer = new CatalogOffer();
        catalogOffer.setId(OFFER_ID_1);
        promotionOfferMap.setOffer(catalogOffer);
        // Mock repository response
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(
                Collections.singletonList(promotionOfferMap));

        // Mock factory methods
        when(conditionFactory.checkCondition(anyInt(), anyDouble(), any(), anyInt())).thenReturn(true);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(anyDouble());

        // Perform the validation
        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(requestDto);

        // Assertions
        OfferPromotionResponse offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        List<OfferPromotionDto> offers = offerPromotionResponse.getOfferPromotionDtoList();
        OfferPromotionDto offerPromotionDto = offers.get(0);

        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());
        verify(conditionFactory, times(1)).checkCondition(anyInt(), anyDouble(), any(), anyInt());
        verify(benefitFactory, times(1)).applyBenefit(anyDouble(), anyDouble(), any());

        // Assertions based on the mock data and behavior
        assertAll("Validating Promotion Result",
                () -> assertEquals(1, offers.size(), "There should be one offer promotion in the result"),
                () -> assertEquals(requestDto.getOfferDetails().get(0).getOfferId(),
                        offerPromotionDto.getOfferId(), "Offer ID should match"),
                () -> assertEquals(offerDetailsList.get(0).getPrice(), offerPromotionDto.getOriginalPrice(),
                        "Original Price should match"),
                () -> assertEquals(85.0, offerPromotionDto.getDiscountedPrice(),
                        "Discounted Price should match")
        );
    }

    /**
     * Test Scenario 1: Validation with promotion constraints met and customer quantity exhausted.
     * Test Scenario 2 : The customer constraint is not reached when the maximum discount is reached as a promotion constraint.
     */
    @Test
    public void testPromotionValidationWithMultipleConstraintCustomer() {

        // Test 1: Customer Availed Redemption Count constraint is not reached, but the promotion limit has been reached.
        PromotionValidationRequestDto mockRequestDto = createMockRequestDto();
        // Usage:
        Promotion promotionA = createPromotion(PROMO_ID_1, new BigDecimal(60), 0,
                LIMIT_PER_CUSTOMER + 6, MAX_COUNT, OFFER_ID_1, DISCOUNT_AVAIL_1 + 5, QUANTITY + 2);
        Promotion promotionB = createPromotion(PROMO_ID_2, new BigDecimal(60), 0,
                LIMIT_PER_CUSTOMER + 6, MAX_COUNT, OFFER_ID_1, DISCOUNT_AVAIL_1 + 5, QUANTITY + 2);

        PromotionOfferMap promotionOfferMapA = createPromotionOfferMap(2L, 200F, promotionA);
        PromotionOfferMap promotionOfferMapB = createPromotionOfferMap(1L, 300F, promotionB);

        PromotionRedemption promotionRedemptionA = createPromotionRedemption(OFFER_ID_1,
                DISCOUNT_AVAIL_1, QUANTITY, promotionA);
        PromotionRedemption promotionRedemptionB = createPromotionRedemption(OFFER_ID_2,
                DISCOUNT_AVAIL_1, QUANTITY, promotionB);
        promotionA.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));
        promotionB.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));
        List<PromotionOfferMap> promotionOfferMap = new ArrayList<>();
        promotionOfferMap.add(promotionOfferMapA);
        promotionOfferMap.add(promotionOfferMapB);

        // Mock repository response
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(
                promotionOfferMap);
        // Mock factory methods
        when(conditionFactory.checkCondition(anyInt(), anyDouble(), any(), anyInt())).thenReturn(true);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(anyDouble());
        // Perform the validation
        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(mockRequestDto);

        OfferPromotionResponse offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        List<OfferPromotionDto> offerPromotionDtoList = offerPromotionResponse.getOfferPromotionDtoList();

        assertEmptyOfferPromotionDto(offerPromotionDtoList.get(0));

        // Test 2 : The customer constraint is not reached when the maximum discount is reached as a promotion constraint.
        mockRequestDto = createMockRequestDto();
        // Usage:
        promotionA = createPromotion(PROMO_ID_1, new BigDecimal(60), 0,
                LIMIT_PER_CUSTOMER + 6, MAX_COUNT, OFFER_ID_1, DISCOUNT_AVAIL_1 + 5, QUANTITY + 2);
        promotionB = createPromotion(PROMO_ID_2, new BigDecimal(60), 0,
                LIMIT_PER_CUSTOMER + 6, MAX_COUNT, OFFER_ID_1, DISCOUNT_AVAIL_1 + 5, QUANTITY + 2);

        promotionOfferMapA = createPromotionOfferMap(2L, 200F, promotionA);
        promotionOfferMapB = createPromotionOfferMap(1L, 300F, promotionB);

        promotionRedemptionA = createPromotionRedemption(OFFER_ID_1,
                DISCOUNT_AVAIL_1 + 100, QUANTITY, promotionA);
        promotionRedemptionB = createPromotionRedemption(OFFER_ID_2,
                DISCOUNT_AVAIL_1 + 100, QUANTITY, promotionB);
        promotionA.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));
        promotionB.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));
        promotionOfferMap = new ArrayList<>();
        promotionOfferMap.add(promotionOfferMapA);
        promotionOfferMap.add(promotionOfferMapB);

        // Mock repository response
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(
                promotionOfferMap);
        // Mock factory methods
        when(conditionFactory.checkCondition(anyInt(), anyDouble(), any(), anyInt())).thenReturn(true);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(anyDouble());
        // Perform the validation
        responseDTO = promotionValidationService.validatePromotions(mockRequestDto);

        offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        offerPromotionDtoList = offerPromotionResponse.getOfferPromotionDtoList();
        assertEmptyOfferPromotionDto(offerPromotionDtoList.get(0));

    }

    /**
     * Test Scenario 2: Validation with Customer maximum discount limit reached for promotions.
     */
    @Test
    public void testPromotionValidationWithCustomerMaxDiscountLimitReached() {
        PromotionValidationRequestDto mockRequestDto = createMockRequestDto();
        // Usage:
        Promotion promotionA = createPromotion(PROMO_ID_1, new BigDecimal(80), 0,
                LIMIT_PER_CUSTOMER, MAX_COUNT + 3, OFFER_ID_1, DISCOUNT_AVAIL_1 + 5, QUANTITY + 2);
        Promotion promotionB = createPromotion(PROMO_ID_2, new BigDecimal(300), MAX_USER_APPLICATIONS,
                LIMIT_PER_CUSTOMER + 6, MAX_COUNT + 5, OFFER_ID_2, DISCOUNT_AVAIL_1 + 5, QUANTITY + 2);

        PromotionOfferMap promotionOfferMapA = createPromotionOfferMap(2L, 200F, promotionA);
        PromotionOfferMap promotionOfferMapB = createPromotionOfferMap(1L, 300F, promotionB);

        PromotionRedemption promotionRedemptionA = createPromotionRedemption(OFFER_ID_1,
                DISCOUNT_AVAIL_1, QUANTITY + 2, promotionA);
        PromotionRedemption promotionRedemptionB = createPromotionRedemption(OFFER_ID_2,
                DISCOUNT_AVAIL_1 + 50, QUANTITY + 2, promotionB);
        promotionA.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));
        promotionB.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));

        List<PromotionOfferMap> promotionOfferMap = new ArrayList<>();
        promotionOfferMap.add(promotionOfferMapA);
        promotionOfferMap.add(promotionOfferMapB);

        // Mock repository response
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(
                promotionOfferMap);
        // Mock factory methods
        when(conditionFactory.checkCondition(anyInt(), anyDouble(), any(), anyInt())).thenReturn(true);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(anyDouble());
        // Perform the validation
        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(mockRequestDto);

        OfferPromotionResponse offerPromotionResponse =
                (responseDTO.getData() instanceof OfferPromotionResponse)
                        ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        List<OfferPromotionDto> offerPromotionDtoList = offerPromotionResponse.getOfferPromotionDtoList();
        assertEmptyOfferPromotionDto(offerPromotionDtoList.get(0));

        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());

    }

    /**
     * Test Scenario 3, Both the Customer's maximum discount limit and the global promotion limit have been reached.
     */
    @Test
    public void testPromotionValidationWithCustomerAndGlobalLimitReached() {
        // Create a mock request DTO.
        PromotionValidationRequestDto mockRequestDto = createMockRequestDto();

        // Create Promotion A and Promotion B
        Promotion promotionA = createPromotion(PROMO_ID_1, new BigDecimal(60), 0,
                LIMIT_PER_CUSTOMER, MAX_COUNT, OFFER_ID_1, DISCOUNT_AVAIL_1 + 5, QUANTITY);
        Promotion promotionB = createPromotion(PROMO_ID_2, new BigDecimal(300), MAX_USER_APPLICATIONS,
                LIMIT_PER_CUSTOMER, MAX_COUNT, OFFER_ID_2, DISCOUNT_AVAIL_1, QUANTITY);

        // Create PromotionOfferMap objects for the promotions
        PromotionOfferMap promotionOfferMapA = createPromotionOfferMap(2L, 200F, promotionA);
        PromotionOfferMap promotionOfferMapB = createPromotionOfferMap(1L, 300F, promotionB);

        // Create PromotionRedemption objects for the promotions
        PromotionRedemption promotionRedemptionA = createPromotionRedemption(OFFER_ID_1,
                DISCOUNT_AVAIL_1, QUANTITY, promotionA);
        PromotionRedemption promotionRedemptionB = createPromotionRedemption(OFFER_ID_2,
                DISCOUNT_AVAIL_1 + 50, QUANTITY, promotionB);

        // Associate the PromotionRedemption objects with their respective promotions
        promotionA.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));
        promotionB.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));

        // Create a list of PromotionOfferMap objects
        List<PromotionOfferMap> promotionOfferMap = new ArrayList<>();
        promotionOfferMap.add(promotionOfferMapA);
        promotionOfferMap.add(promotionOfferMapB);

        // Mock repository response
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(promotionOfferMap);

        // Mock factory methods
        when(conditionFactory.checkCondition(anyInt(), anyDouble(), any(), anyInt())).thenReturn(true);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(anyDouble());

        // Perform the validation
        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(mockRequestDto);

        // Extract OfferPromotionResponse from the response DTO
        OfferPromotionResponse offerPromotionResponse = (responseDTO.getData() instanceof OfferPromotionResponse)
                ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        List<OfferPromotionDto> offerPromotionDtoList = offerPromotionResponse.getOfferPromotionDtoList();
        assertEmptyOfferPromotionDto(offerPromotionDtoList.get(0));

        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());
    }

    /**
     * Test Scenario 4, Global Applications and Global Discount constraints are not met.
     */
    @Test
    public void testPromotionValidationGlobalLimitReached() {
        // Create a mock request DTO.
        PromotionValidationRequestDto mockRequestDto = createMockRequestDto();

        // Create Promotion A and Promotion B
        Promotion promotionA = createPromotion(PROMO_ID_1, new BigDecimal(50), 0,
                LIMIT_PER_CUSTOMER, MAX_COUNT, OFFER_ID_1, DISCOUNT_AVAIL_1 + 5, QUANTITY);
        Promotion promotionB = createPromotion(PROMO_ID_2, new BigDecimal(300), MAX_USER_APPLICATIONS,
                LIMIT_PER_CUSTOMER, MAX_COUNT, OFFER_ID_2, DISCOUNT_AVAIL_1, QUANTITY);

        // Create PromotionOfferMap objects for the promotions
        PromotionOfferMap promotionOfferMapA = createPromotionOfferMap(2L, 200F, promotionA);
        PromotionOfferMap promotionOfferMapB = createPromotionOfferMap(1L, 300F, promotionB);

        // Create PromotionRedemption objects for the promotions
        PromotionRedemption promotionRedemptionA = createPromotionRedemption(OFFER_ID_1,
                DISCOUNT_AVAIL_1, QUANTITY, promotionA);
        PromotionRedemption promotionRedemptionB = createPromotionRedemption(OFFER_ID_2,
                DISCOUNT_AVAIL_1 + 50, QUANTITY, promotionB);

        // Associate the PromotionRedemption objects with their respective promotions
        promotionA.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));
        promotionB.setPromotionRedemptionList(Arrays.asList(promotionRedemptionA, promotionRedemptionB));

        // Create a list of PromotionOfferMap objects
        List<PromotionOfferMap> promotionOfferMap = new ArrayList<>();
        promotionOfferMap.add(promotionOfferMapA);
        promotionOfferMap.add(promotionOfferMapB);

        // Mock repository response
        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(promotionOfferMap);

        // Mock factory methods
        when(conditionFactory.checkCondition(anyInt(), anyDouble(), any(), anyInt())).thenReturn(true);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(anyDouble());

        // Perform the validation
        RootResponseDTO responseDTO = promotionValidationService.validatePromotions(mockRequestDto);

        // Extract OfferPromotionResponse from the response DTO
        OfferPromotionResponse offerPromotionResponse = (responseDTO.getData() instanceof OfferPromotionResponse)
                ? (OfferPromotionResponse) responseDTO.getData() : null;
        assert offerPromotionResponse != null;
        List<OfferPromotionDto> offerPromotionDtoList = offerPromotionResponse.getOfferPromotionDtoList();
       assertEmptyOfferPromotionDto(offerPromotionDtoList.get(0));

        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());
    }

    private PromotionValidationRequestDto createMockRequestDto() {
        PromotionValidationRequestDto requestDto = new PromotionValidationRequestDto();
        ArrayList<OfferDetails> offerDetailsList = new ArrayList<>();
        offerDetailsList.add(new OfferDetails(OFFER_ID_1, ORIGINAL_PRICE, QUANTITY));
        offerDetailsList.add(new OfferDetails(OFFER_ID_2, ORIGINAL_PRICE, QUANTITY));
        requestDto.setOfferDetails(offerDetailsList);
        requestDto.setServiceabilityAreaId(SERVICEABILITY_AREA_ID);
        requestDto.setCustomerId(CUSTOMER_ID);

        return requestDto;
    }

    // Method to create a Promotion
    private Promotion createPromotion(long promoId, BigDecimal maxGlobalDiscount, int maxUserApplications,
                                      int limitPerCustomer,
                                      int maxCount, Long offerId, Float discountAvailed, int quantity) {
        Promotion promotion = new Promotion();
        promotion.setId(promoId);
        promotion.setName(PROMOTION_NAME);
        promotion.setMaxGlobalDiscount(maxGlobalDiscount);
        promotion.setMaxUserApplications(maxUserApplications);
        promotion.setMaxGlobalApplications(1);
        promotion.setMaxDiscount(MAX_DISCOUNT);

        List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices = new ArrayList<>();
        promotion.setPromotionBenefitFixedPrices(promotionBenefitFixedPrices);

        PromotionBenefit promotionBenefit = PromotionBenefit.builder()
                .value(PROMOTION_BENEFIT_VALUE)

                .type(FIXED_PRICE.name())
                .build();
        promotion.setPromotionBenefits(promotionBenefit);

        PromotionCondition promotionCondition = PromotionCondition.builder()
                .type(UNCONDITIONAL.name())
                .value(PROMOTION_CONDITION_VALUE)
                .build();
        promotion.setPromotionCondition(promotionCondition);

        PromotionLimit promotionLimit = PromotionLimit.builder()
                .limitPerCustomer(limitPerCustomer)
                .maxCount(maxCount)
                .build();

        promotion.setPromotionLimit(promotionLimit);
        List<PromotionRedemption> promotionRedemptions = new ArrayList<>();
        promotionRedemptions.add(PromotionRedemption.builder()
                .orderId(UUID.fromString(ORDER_ID))
                .customerId(UUID.fromString(CUSTOMER_ID))
                .offerId(offerId)
                .discountAvailed(discountAvailed)
                .quantity(quantity)
                .build());
        promotion.setPromotionRedemptionList(promotionRedemptions);

        return promotion;
    }

    private PromotionOfferMap createPromotionOfferMap(long id, float price, Promotion promotion) {
        return PromotionOfferMap.builder()
                .id(id)
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(price).build())
                .promotion(promotion)
                .build();
    }

    private PromotionRedemption createPromotionRedemption(Long offerId, Float discountAvailed, int quantity,
                                                          Promotion promotion) {
        PromotionRedemption promotionRedemption = new PromotionRedemption();
        promotionRedemption.setId(2L);
        promotionRedemption.setCustomerId(UUID.fromString(CUSTOMER_ID));
        promotionRedemption.setOrderId(UUID.fromString(ORDER_ID));
        promotionRedemption.setOfferId(offerId);
        promotionRedemption.setDiscountAvailed(discountAvailed);
        promotionRedemption.setQuantity(quantity);
        promotionRedemption.setPromotion(promotion);

        return promotionRedemption;
    }

    private void assertEmptyOfferPromotionDto(OfferPromotionDto offerPromotionDto) {
        assertAll("OfferPromotionDto",
                () -> assertEquals("", offerPromotionDto.getPromoId(), "Promo ID should be empty for empty promotion"),
                () -> assertEquals("", offerPromotionDto.getName(), "Promotion Name should be empty for empty promotion"),
                () -> assertEquals(1L, offerPromotionDto.getOfferId(), "Offer ID should be 0"),
                () -> assertEquals(100.0, offerPromotionDto.getOriginalPrice(), "Original Price should be 0.0"),
                () -> assertEquals(100.0, offerPromotionDto.getDiscountedPrice(), "Discounted Price should be 0.0"),
                () -> assertEquals("", offerPromotionDto.getType(), "Type should be empty"),
                () -> assertEquals(0.0, offerPromotionDto.getCalculatedDiscount(), "Calculated Discount should be 0.0"),
                () -> assertEquals("0.00%", offerPromotionDto.getCalculatedDiscountPercentage(), "Calculated Discount Percentage should be empty"),
                () -> assertEquals("", offerPromotionDto.getEndDate(), "End Date should be empty")
        );
    }


}