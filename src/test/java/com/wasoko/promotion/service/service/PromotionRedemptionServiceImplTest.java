package com.wasoko.promotion.service.service;

import static com.wasoko.promotion.service.common.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wasoko.promotion.service.dto.request.PromotionRedemptionRequestDto;
import com.wasoko.promotion.service.dto.request.RedemptionOfferDto;
import com.wasoko.promotion.service.entities.Promotion;
import com.wasoko.promotion.service.exception.InvalidPromotionException;
import com.wasoko.promotion.service.repository.PromotionRedemptionRepository;
import com.wasoko.promotion.service.repository.PromotionRepository;
import com.wasoko.promotion.service.service.impl.PromotionRedemptionServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * The type Promotion redemption service impl test.
 */
class PromotionRedemptionServiceImplTest {

  @Mock
  private PromotionRedemptionRepository promotionRedemptionRepository;
  @Mock
  private PromotionRepository promotionRepository;
  @InjectMocks
  private PromotionRedemptionServiceImpl promotionRedemptionService;

  /**
   * Sets up.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Test apply redemption.
   */
  @Test
  void testApplyRedemption() {
    // Arrange
    PromotionRedemptionRequestDto requestDto = createSamplePromotionRedemptionRequestDto();

    when(promotionRepository.findById(anyLong())).thenReturn(Optional.of(new Promotion()));

    // Act and Assert
    assertDoesNotThrow(() -> promotionRedemptionService.applyRedemption(requestDto));
    verify(promotionRedemptionRepository, times(1)).saveAll(anyList());
  }

  /**
   * Test apply redemption with invalid promotion.
   */
  @Test
  void testApplyRedemptionWithInvalidPromotion() {
    // Arrange
    PromotionRedemptionRequestDto requestDto = createSamplePromotionRedemptionRequestDto();
    RedemptionOfferDto offerDto = requestDto.getOffers().get(0);
    offerDto.setPromoId(3L); // This is an invalid promoId

    when(promotionRepository.findById(3L)).thenReturn(Optional.empty());

    // Act and Assert
    InvalidPromotionException exception = assertThrows(InvalidPromotionException.class,
        () -> promotionRedemptionService.applyRedemption(requestDto));

    assertTrue(exception.getMessage().contains("invalid promotion IDs"));
    verify(promotionRedemptionRepository, never()).saveAll(anyList());
  }

  private PromotionRedemptionRequestDto createSamplePromotionRedemptionRequestDto() {
    PromotionRedemptionRequestDto requestDto = new PromotionRedemptionRequestDto();
    requestDto.setCustomerId(CUSTOMER_ID);
    requestDto.setOrderId(ORDER_ID);

    RedemptionOfferDto offerDto1 = RedemptionOfferDto.builder().offerId(OFFER_ID_1)
        .promoId(PROMO_ID_1).quantity(QUANTITY_1).discountAvailed(DISCOUNT_AVAIL_1).build();

    RedemptionOfferDto offerDto2 = RedemptionOfferDto.builder().offerId(OFFER_ID_2)
        .promoId(PROMO_ID_2).quantity(QUANTITY_2).discountAvailed(DISCOUNT_AVAIL_2).build();

    requestDto.setOffers(List.of(offerDto1, offerDto2));

    return requestDto;
  }

}
