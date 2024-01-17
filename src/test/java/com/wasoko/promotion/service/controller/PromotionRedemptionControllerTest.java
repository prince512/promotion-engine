package com.wasoko.promotion.service.controller;

import static com.wasoko.promotion.service.common.Constants.AUTHORIZATION_KEY;
import static com.wasoko.promotion.service.common.Constants.AUTHORIZATION_VALUE;
import static com.wasoko.promotion.service.common.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasoko.promotion.service.dto.request.PromotionRedemptionRequestDto;
import com.wasoko.promotion.service.dto.request.RedemptionOfferDto;
import com.wasoko.promotion.service.service.PromotionRedemptionService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(PromotionRedemptionController.class)
public class PromotionRedemptionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PromotionRedemptionService promotionRedemptionService;

  @Autowired
  private ObjectMapper objectMapper;
  /**
   * Test applying promotion redemption requests.
   *
   * This test method covers various scenarios for applying promotion redemption requests, including valid application,
   * attempts to apply to non-existing redemptions, and attempts to apply with duplicate offer IDs.
   *
   * @throws Exception if there is an error during the test execution.
   */
  @Test
  public void testApplyRedemption() throws Exception {
    // Test Case 1: Valid application (Expecting OK)
    PromotionRedemptionRequestDto promotionRedemptionRequestDto = createPromotionRedemptionRequestDto();
    String inputInJson = objectMapper.writeValueAsString(promotionRedemptionRequestDto);
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .put(PROMOTIONS_BASE_URI + "/" + "redemption")
        .accept(MediaType.APPLICATION_JSON)
        .content(inputInJson)
        .contentType(MediaType.APPLICATION_JSON);
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    // Test Case 2: Attempt to update non-existing redemption (Expecting NOT_FOUND)
    testPromotionRedemption("redemption1", promotionRedemptionRequestDto, HttpStatus.NOT_FOUND);
    // Test Case 3: Attempt to update with duplicate offer IDs (Expecting BAD_REQUEST)
    List<RedemptionOfferDto> offers = promotionRedemptionRequestDto.getOffers();
    offers.get(1).setOfferId(OFFER_ID_1);
    testPromotionRedemption("redemption", promotionRedemptionRequestDto, HttpStatus.BAD_REQUEST);
    testPromotionRedemption("redemption", promotionRedemptionRequestDto, HttpStatus.BAD_REQUEST);
  }

  private void testPromotionRedemption(String redemption,
      PromotionRedemptionRequestDto promotionRedemptionRequestDto, HttpStatus httpStatus)
      throws Exception {
    mockMvc.perform(put(PROMOTIONS_BASE_URI + "/" + redemption)
            .content(objectMapper.writeValueAsString(promotionRedemptionRequestDto))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().is(httpStatus.value()));
  }

  private PromotionRedemptionRequestDto createPromotionRedemptionRequestDto() {
    PromotionRedemptionRequestDto requestDto = new PromotionRedemptionRequestDto();
    requestDto.setCustomerId(CUSTOMER_ID);
    requestDto.setOrderId(ORDER_ID);

    RedemptionOfferDto offerDto1 = new RedemptionOfferDto();
    offerDto1.setOfferId(OFFER_ID_1);
    offerDto1.setPromoId(PROMO_ID_1);
    offerDto1.setQuantity(QUANTITY_1);
    offerDto1.setDiscountAvailed(DISCOUNT_AVAIL_1);

    RedemptionOfferDto offerDto2 = new RedemptionOfferDto();
    offerDto2.setOfferId(OFFER_ID_2);
    offerDto2.setPromoId(PROMO_ID_2);
    offerDto2.setQuantity(QUANTITY_2);
    offerDto2.setDiscountAvailed(DISCOUNT_AVAIL_2);

    requestDto.setOffers(List.of(offerDto1, offerDto2));

    return requestDto;
  }

}