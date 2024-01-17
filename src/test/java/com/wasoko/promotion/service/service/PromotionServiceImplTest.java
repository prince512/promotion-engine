package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.common.BenefitsType;
import com.wasoko.promotion.service.dto.request.Offer;
import com.wasoko.promotion.service.dto.request.PromotionRequest;
import com.wasoko.promotion.service.dto.response.OfferPromotionDetailsResponse;
import com.wasoko.promotion.service.entities.*;
import com.wasoko.promotion.service.repository.PromotionBenefitFixedPriceRepository;
import com.wasoko.promotion.service.repository.PromotionOfferMapRepository;
import com.wasoko.promotion.service.service.impl.PromotionServiceImpl;
import com.wasoko.promotion.service.strategies.benefits.BenefitFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.wasoko.promotion.service.common.BenefitsType.FIXED_PRICE;
import static com.wasoko.promotion.service.common.ConditionType.UNCONDITIONAL;
import static com.wasoko.promotion.service.common.PromotionState.LIVE;
import static com.wasoko.promotion.service.common.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * The type Promotion service impl test.
 */
public class PromotionServiceImplTest {

    @Mock
    private PromotionOfferMapRepository promotionOfferMapRepository;
    @Mock
    private PromotionBenefitFixedPriceRepository promotionBenefitFixedPriceRepository;
    @InjectMocks
    private PromotionServiceImpl promotionService;
    @Mock
    private BenefitFactory benefitFactory;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test no promotions found for offers.
     */
    @Test
    public void testNoPromotionsFoundForOffers() {
        List<Offer> offers = Arrays.asList(
                Offer.builder().offerId(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build(),
                Offer.builder().offerId(OFFER_ID_2).originalPrice(ORIGINAL_PRICE).build());

        PromotionRequest promotionRequest = new PromotionRequest(new ArrayList<>(offers));

        when(promotionOfferMapRepository.findByOffer_IdIn(anySet())).thenReturn(
                Collections.emptyList());
        RootResponseDTO responseDTO = promotionService.getPromotions(promotionRequest);

        assertTrue(responseDTO.isStatus());
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(anySet());
        verifyNoMoreInteractions(promotionBenefitFixedPriceRepository);

        OfferPromotionDetailsResponse offerPromotionDetailsResponse = getPromotionOfferDetailsResponse(
                responseDTO);
        assertEquals(offerPromotionDetailsResponse.getPromotions().getDiscountedPrice(),
                ORIGINAL_PRICE);
        assertEquals(offerPromotionDetailsResponse.getPromotions().getDiscountPercentage(), "0%");
        assertNull(offerPromotionDetailsResponse.getPromotions().getPromotionName());
        assertNull(offerPromotionDetailsResponse.getPromotions().getPromotionType());
        assertEquals(offerPromotionDetailsResponse.getOriginalPrice(), ORIGINAL_PRICE);
        assertEquals(offerPromotionDetailsResponse.getOfferId(), OFFER_ID_1);

    }

    /**
     * Test get best promotion for offer.
     */
    @Test
    public void testGetBestPromotionForOffer() {
        // Create test data
        Offer offer = Offer.builder().offerId(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build();
        PromotionRequest promotionRequest = PromotionRequest.builder()
                .offers(Collections.singletonList(offer)).build();
        List<PromotionOfferMap> promotionOfferMapList = new ArrayList<>();

        promotionOfferMapList.add(PromotionOfferMap.builder()
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())
                .promotion(Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                        .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).id(PROMO_ID_1).name(PROMOTION_NAME).type(PROMOTION_TYPE)
                        .promotionBenefits(PromotionBenefit.builder().promotion(createPromotion())
                                .value(PROMOTION_BENEFIT_VALUE).type(BenefitsType.PERCENTAGE.name()).build())
                        .build()).build());

        // Mock repository and factory behavior
        when(promotionOfferMapRepository.findByOffer_IdIn(any())).thenReturn(promotionOfferMapList);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(
                (double) PROMOTION_BENEFIT_VALUE);
        RootResponseDTO responseDTO = promotionService.getPromotions(promotionRequest);

        // Verify that the repository methods and factory method were called
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(any());
        verify(benefitFactory, times(1)).applyBenefit(anyDouble(), anyDouble(), any());

        OfferPromotionDetailsResponse offerPromotionDetailsResponse = getPromotionOfferDetailsResponse(
                responseDTO);
        assertEquals(offerPromotionDetailsResponse.getPromotions().getDiscountedPrice(),
                PROMOTION_BENEFIT_VALUE);
        assertEquals(offerPromotionDetailsResponse.getPromotions().getDiscountPercentage(), "21.00%");
        assertEquals(offerPromotionDetailsResponse.getPromotions().getPromotionName(), PROMOTION_NAME);
        assertEquals(offerPromotionDetailsResponse.getPromotions().getPromotionType(), PROMOTION_TYPE);
        assertEquals(offerPromotionDetailsResponse.getOriginalPrice(), ORIGINAL_PRICE);
        assertEquals(offerPromotionDetailsResponse.getOfferId(), OFFER_ID_1);
    }

    private OfferPromotionDetailsResponse getPromotionOfferDetailsResponse(
            RootResponseDTO responseDTO) {
        List<?> promotionOfferDetailsResponseList = (List<?>) responseDTO.getData();
        boolean allElementsArePromotionOfferDetailsResponse = promotionOfferDetailsResponseList.stream()
                .allMatch(element -> element instanceof OfferPromotionDetailsResponse);
        assertTrue(allElementsArePromotionOfferDetailsResponse);
        // Assertions
        OfferPromotionDetailsResponse offerPromotionDetailsResponse =
                promotionOfferDetailsResponseList.get(0) instanceof OfferPromotionDetailsResponse
                        ? (OfferPromotionDetailsResponse) promotionOfferDetailsResponseList.get(0) : null;
        assert Objects.requireNonNull(offerPromotionDetailsResponse).getPromotions() != null;
        return offerPromotionDetailsResponse;
    }

    /**
     * Test get promotions for offer with fixed price benefit.
     */
    @Test
    public void testGetNoPromotionsFoundForOfferWithFixedPriceBenefit() {
        // Create test data
        Offer offer = Offer.builder().offerId(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build();
        PromotionRequest promotionRequest = PromotionRequest.builder()
                .offers(Collections.singletonList(offer)).build();

        List<PromotionOfferMap> promotionOfferMapList = new ArrayList<>();

        promotionOfferMapList.add(PromotionOfferMap.builder()
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())
                .promotion(Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                        .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).promotionBenefits(
                                PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE).type(FIXED_PRICE.name()).build())
                        .build()).build());
        // Mock repository and factory behavior
        when(promotionOfferMapRepository.findByOffer_IdIn(any())).thenReturn(promotionOfferMapList);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(
                (double) PROMOTION_BENEFIT_VALUE);
        RootResponseDTO responseDTO = promotionService.getPromotions(promotionRequest);
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(any());
        OfferPromotionDetailsResponse offerPromotionDetailsResponse = getPromotionOfferDetailsResponse(
                responseDTO);
        assert offerPromotionDetailsResponse.getPromotions() != null;
        assert offerPromotionDetailsResponse.getPromotions().getDiscountedPrice() == 100.0;
        assert offerPromotionDetailsResponse.getPromotions().getDiscount() == 0.0;

    }

    /**
     * Test get promotions for offer with multiple fixed price benefits.
     */
    @Test
    public void testGetPromotionsForOfferWithMultipleFixedPriceBenefits() {
        // Create test data
        Offer offer = Offer.builder().offerId(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build();
        PromotionRequest promotionRequest = PromotionRequest.builder()
                .offers(Collections.singletonList(offer)).build();

        List<PromotionOfferMap> promotionOfferMapList = new ArrayList<>();

        Promotion promotion = Promotion.builder().build();

        PromotionBenefit promotionBenefit = PromotionBenefit.builder().type(null)
                .value(PROMOTION_BENEFIT_VALUE)
                .type(FIXED_PRICE.name()).build();

        promotion.setPromotionBenefits(promotionBenefit);
        List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices = new ArrayList<>();

        promotionBenefitFixedPrices.add(PromotionBenefitFixedPrice.builder().fixedPrice(FIXED_BENEFIT)
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())
                .build());

        promotionBenefitFixedPrices.add(
                PromotionBenefitFixedPrice.builder().fixedPrice(FIXED_BENEFIT + 20)
                        .offer(
                                CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())
                        .build());

        promotion.setPromotionBenefitFixedPrices(promotionBenefitFixedPrices);

        promotionOfferMapList.add(PromotionOfferMap.builder()
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())

                .promotion(Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                        .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).promotionBenefitFixedPrices(promotionBenefitFixedPrices)
                        .promotionBenefits(
                                PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE).type(FIXED_PRICE.name())
                                        .build()).build()).build());

        // Mock repository and factory behavior
        when(promotionOfferMapRepository.findByOffer_IdIn(any())).thenReturn(promotionOfferMapList);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(
                (double) PROMOTION_BENEFIT_VALUE);
        RootResponseDTO responseDTO = promotionService.getPromotions(promotionRequest);
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(any());
        OfferPromotionDetailsResponse offerPromotionDetailsResponse = getPromotionOfferDetailsResponse(
                responseDTO);
        assertNotNull(offerPromotionDetailsResponse);
        assertNotNull(offerPromotionDetailsResponse.getPromotions());
        assertEquals(offerPromotionDetailsResponse.getPromotions().getDiscountedPrice(),
                ORIGINAL_PRICE);
        assertEquals(offerPromotionDetailsResponse.getOfferId(), OFFER_ID_1);

    }

    @Test
    public void testGetBestPromotionForMultiplePromotions() {
        // Create test data
        Offer offer = Offer.builder().offerId(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build();
        PromotionRequest promotionRequest = PromotionRequest.builder()
                .offers(Collections.singletonList(offer)).build();
        List<PromotionOfferMap> promotionOfferMapList = new ArrayList<>();

        promotionOfferMapList.add(PromotionOfferMap.builder()
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())
                .promotion(Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                        .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).id(PROMO_ID_1).name(PROMOTION_NAME).promotionBenefits(
                                PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE).type(FIXED_PRICE.name()).build())
                        .build()).build());

        promotionOfferMapList.add(PromotionOfferMap.builder()
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())
                .promotion(Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                        .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).id(PROMO_ID_2).type(PROMOTION_TYPE).name(PROMOTION_NAME)
                        .promotionBenefits(PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE + 10)
                                .type(BenefitsType.PERCENTAGE.name()).build()).build()).build());

        // Mock repository and factory behavior
        when(promotionOfferMapRepository.findByOffer_IdIn(any())).thenReturn(promotionOfferMapList);
        // when( promotionBenefitFixedPriceRepository.findByPromotionIn(any())).thenReturn()
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(
                (double) PROMOTION_BENEFIT_VALUE);
        RootResponseDTO responseDTO = promotionService.getPromotions(promotionRequest);

        // Verify that the repository methods and factory method were called
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(any());
        // Assertions
        OfferPromotionDetailsResponse offerPromotionDetailsResponse = getPromotionOfferDetailsResponse(
                responseDTO);
        assert offerPromotionDetailsResponse.getPromotions() != null;
        assertAll("Validating Promotion Result",
                () -> assertEquals(offerPromotionDetailsResponse.getOfferId(), offer.getOfferId(),
                        "Offer ID should match"), () -> assertEquals(offer.getOriginalPrice(),
                        offerPromotionDetailsResponse.getOriginalPrice(), "Original Price should be match"),
                () -> assertEquals("test", offerPromotionDetailsResponse.getPromotions().getPromotionName(),
                        "Promotion Name should be ot empty"),
                () -> assertEquals(offerPromotionDetailsResponse.getPromotions().getPromotionId(), 1,
                        "Promotion id  should match"),
                () -> assertEquals(offerPromotionDetailsResponse.getPromotions().getDiscountedPrice(),
                        PROMOTION_BENEFIT_VALUE, "Discounted Price should match"));
    }

    @Test
    public void testGetBestPromotionForOfferDoesNotExist() {
        // Create test data
        Offer offer = Offer.builder().offerId(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build();
        PromotionRequest promotionRequest = PromotionRequest.builder()
                .offers(Collections.singletonList(offer)).build();
        List<PromotionOfferMap> promotionOfferMapList = new ArrayList<>();

        promotionOfferMapList.add(PromotionOfferMap.builder()
                .offer(CatalogOffer.builder().id(3L).price(CATALOG_PRICE).isActive(true).build()).promotion(
                        Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                                .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).name(PROMOTION_NAME).promotionBenefits(
                                        PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE).type(FIXED_PRICE.name())
                                                .build()).build()).build());

        promotionOfferMapList.add(PromotionOfferMap.builder()
                .offer(CatalogOffer.builder().id(5L).price(CATALOG_PRICE).isActive(true).build()).promotion(
                        Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                                .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).id(PROMO_ID_1).type(PROMOTION_TYPE).name(PROMOTION_NAME)
                                .promotionBenefits(PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE)
                                        .type(BenefitsType.PERCENTAGE.name()).build()).build()).build());

        // Mock repository and factory behavior
        when(promotionOfferMapRepository.findByOffer_IdIn(any())).thenReturn(promotionOfferMapList);
        when(benefitFactory.applyBenefit(anyDouble(), anyDouble(), any())).thenReturn(
                (double) PROMOTION_BENEFIT_VALUE);
        RootResponseDTO responseDTO = promotionService.getPromotions(promotionRequest);

        // Verify that the repository methods and factory method were called
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(any());
        // Assertions
        OfferPromotionDetailsResponse offerPromotionDetailsResponse = getPromotionOfferDetailsResponse(
                responseDTO);
        assert offerPromotionDetailsResponse.getPromotions() != null;
        assertAll("Validating Promotion Result",
                () -> assertEquals(offerPromotionDetailsResponse.getOfferId(), offer.getOfferId(),
                        "Offer ID should match"), () -> assertEquals(offer.getOriginalPrice(),
                        offerPromotionDetailsResponse.getOriginalPrice(), "Original price should be match"),
                () -> assertNull(offerPromotionDetailsResponse.getPromotions().getPromotionName(),
                        "Promotion Name should be empty"),
                () -> assertNull(offerPromotionDetailsResponse.getPromotions().getPromotionId(),
                        "Promotion id should be empty"));
    }

    @Test
    public void testGetPromotionsForFixedBenefits() {
        // Create test data
        Offer offer = Offer.builder().offerId(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build();

        PromotionRequest promotionRequest = new PromotionRequest();
        promotionRequest.setOffers(Collections.singletonList(offer));

        List<PromotionOfferMap> promotionOfferMapList = new ArrayList<>();

        Promotion promotion = Promotion.builder().build();

        PromotionBenefit promotionBenefit = new PromotionBenefit();
        promotionBenefit.setType(null);
        promotionBenefit.setValue(PROMOTION_BENEFIT_VALUE);
       promotion.setPromotionBenefits(promotionBenefit);
        List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices = new ArrayList<>();
        promotionBenefitFixedPrices.add(PromotionBenefitFixedPrice.builder().fixedPrice(FIXED_BENEFIT)
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())
                .promotion(createPromotion()).build());

        promotion.setPromotionBenefitFixedPrices(promotionBenefitFixedPrices);
        promotionOfferMapList.add(PromotionOfferMap.builder()
                .offer(CatalogOffer.builder().id(OFFER_ID_1).price(CATALOG_PRICE).isActive(true).build())
                .promotion(
                        Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                                .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).id(1L).promotionBenefitFixedPrices(promotionBenefitFixedPrices)
                                .promotionBenefits(
                                        PromotionBenefit.builder().value(PROMOTION_BENEFIT_VALUE).type(FIXED_PRICE.name())
                                                .build()).build()).build());
        // Mock repository and factory behavior
        when(promotionOfferMapRepository.findByOffer_IdIn(any())).thenReturn(promotionOfferMapList);
        when(promotionBenefitFixedPriceRepository.findByPromotionIn(any())).thenReturn(
                promotionBenefitFixedPrices);

        // Call the method to test
        RootResponseDTO responseDTO = promotionService.getPromotions(promotionRequest);

        // Assertions
        OfferPromotionDetailsResponse offerPromotionDetailsResponse = getPromotionOfferDetailsResponse(
                responseDTO);
        // Verify that the repository methods and factory method were called
        verify(promotionOfferMapRepository, times(1)).findByOffer_IdIn(any());
        // Assertions
        assert offerPromotionDetailsResponse.getPromotions() != null;
        assert offerPromotionDetailsResponse.getPromotions().getDiscountedPrice() == 0.0;

        assertAll("Validating Promotion Result",
                () -> assertEquals(offerPromotionDetailsResponse.getOfferId(), offer.getOfferId(),
                        "Offer ID should match"), () -> assertEquals(offer.getOriginalPrice(),
                        offerPromotionDetailsResponse.getOriginalPrice(), "Original price ID should be match"),
                () -> assertNull(offerPromotionDetailsResponse.getPromotions().getPromotionName(),
                        "Promotion Name should be empty"),
                () -> assertEquals(offerPromotionDetailsResponse.getPromotions().getPromotionId(),
                        PROMO_ID_1, "Promotion id  should be match"));
    }

    private Promotion createPromotion() {
        Promotion mockedPromotion = Promotion.builder().state(LIVE.name()).startDatetime(LocalDateTime.now(ZoneOffset.UTC))
                .endDatetime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2L)).id(PROMO_ID_1).name(PROMOTION_NAME_1)
                .description(PROMOTION_DESCRIPTION).startDatetime(LocalDateTime.now())
                .endDatetime(LocalDateTime.now()).maxUserApplications(MAX_USER_APPLICATIONS)
                .slug(PROMOTION_SLUG).state(LIVE.name()).tnc(PROMOTION_TNC).type(PROMOTION_TYPE)
                .build();
        mockedPromotion.setPromotionBenefits(
                PromotionBenefit.builder().type(FIXED_PRICE.name()).value(PROMOTION_BENEFIT_VALUE).build());
        mockedPromotion.setPromotionCreative(
                PromotionCreative.builder().banner(PROMOTION_BANNER).teaser1(PROMOTION_TEASER).build());
        mockedPromotion.setPromotionCondition(
                PromotionCondition.builder().type(UNCONDITIONAL.name()).value(0).build());
        mockedPromotion.setPromotionLimit(
                PromotionLimit.builder().limitPerCustomer(LIMIT_PER_CUSTOMER).maxCount(MAX_COUNT).build());
        return mockedPromotion;
    }
}