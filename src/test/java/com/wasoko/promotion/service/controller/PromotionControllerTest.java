package com.wasoko.promotion.service.controller;

import static com.wasoko.promotion.service.common.Constants.AUTHORIZATION_KEY;
import static com.wasoko.promotion.service.common.Constants.AUTHORIZATION_VALUE;
import static com.wasoko.promotion.service.common.Constants.SPACE;
import static com.wasoko.promotion.service.common.TestConstants.CATALOG_OFFER_ID;
import static com.wasoko.promotion.service.common.TestConstants.CATALOG_PRICE;
import static com.wasoko.promotion.service.common.TestConstants.CUSTOMER_ENDPOINT;
import static com.wasoko.promotion.service.common.TestConstants.CUSTOMER_ID;
import static com.wasoko.promotion.service.common.TestConstants.DEFAULT_PROMOTION_ID;
import static com.wasoko.promotion.service.common.TestConstants.INVALID_CUSTOMER_ID;
import static com.wasoko.promotion.service.common.TestConstants.INVALID_SERVICEABILITY_AREA_ID;
import static com.wasoko.promotion.service.common.TestConstants.OFFER_ID_1;
import static com.wasoko.promotion.service.common.TestConstants.ORIGINAL_PRICE;
import static com.wasoko.promotion.service.common.TestConstants.PROMOTIONS_BASE_URI;
import static com.wasoko.promotion.service.common.TestConstants.PROMOTION_VALIDATE_END_POINT;
import static com.wasoko.promotion.service.common.TestConstants.QUANTITY;
import static com.wasoko.promotion.service.common.TestConstants.SERVICEABILITY_AREA_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasoko.promotion.service.dto.request.Offer;
import com.wasoko.promotion.service.dto.request.OfferDetails;
import com.wasoko.promotion.service.dto.request.PromotionRequest;
import com.wasoko.promotion.service.dto.request.PromotionValidationRequestDto;
import com.wasoko.promotion.service.service.PromotionCustomerService;
import com.wasoko.promotion.service.service.PromotionDetailService;
import com.wasoko.promotion.service.service.PromotionService;
import com.wasoko.promotion.service.service.PromotionValidationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.junit.Before;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * The type Promotion controller test.
 */
@WebMvcTest(PromotionController.class)
public class PromotionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  /**
   * The Promotion service.
   */
  @MockBean
  private PromotionService promotionService;

  @MockBean

  private PromotionCustomerService promotionCustomerService;
  @MockBean

  private PromotionValidationService promotionValidationService;
  @MockBean

  private PromotionDetailService promotionDetailService;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Sets up.
   */
  @Before
  public void setUp() {
    PromotionController promotionController = new PromotionController(promotionService,
        promotionCustomerService, promotionValidationService, promotionDetailService);
    mockMvc = MockMvcBuilders.standaloneSetup(promotionController).build();
  }

  /**
   * Test get best promotions price. This test verifies that the best promotions price is returned
   * successfully.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetBestPromotionsPrice() throws Exception {

    PromotionRequest promotionRequest = PromotionRequest.builder()
        .offers(Collections.singletonList(
            Offer.builder().offerId(CATALOG_OFFER_ID).originalPrice(ORIGINAL_PRICE)
                .build())).build();

    String inputInJson = objectMapper.writeValueAsString(promotionRequest);
    HttpHeaders headers = new HttpHeaders();
    headers.add(AUTHORIZATION_KEY, AUTHORIZATION_VALUE);
    RequestBuilder requestBuilder = post(PROMOTIONS_BASE_URI)
        .accept(MediaType.APPLICATION_JSON)
        .content(inputInJson)
        .headers(headers)
        .contentType(MediaType.APPLICATION_JSON);
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = mvcResult.getResponse();
    String tokenValue = mvcResult.getRequest().getHeader(AUTHORIZATION_KEY);
    Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    Assertions.assertEquals(tokenValue, AUTHORIZATION_VALUE);
  }

  @Test
  public void testAuthorizationFailure() throws Exception {
    PromotionRequest promotionRequest = PromotionRequest.builder()
        .offers(Collections.singletonList(
            Offer.builder().offerId(CATALOG_OFFER_ID).originalPrice(ORIGINAL_PRICE)
                .build())).build();

    String inputInJson = objectMapper.writeValueAsString(promotionRequest);
    RequestBuilder requestBuilder = post(PROMOTIONS_BASE_URI)
        .accept(MediaType.APPLICATION_JSON)
        .content(inputInJson)
        .contentType(MediaType.APPLICATION_JSON);
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    Assertions.assertNull(mvcResult.getRequest().getHeader(AUTHORIZATION_KEY));
  }

  /**
   * Test bad request scenarios for getting best promotions with empty or invalid price data.
   * <p>
   * This test method covers various scenarios where the request for getting best promotions
   * contains empty or invalid price data, resulting in bad requests.
   *
   * @throws Exception if there is an error during the test execution.
   */
  @Test
  public void testGetBadRequestBestPromotionsPriceEmptyRequest() throws Exception {
    // Test Case 1: Verify handling of an empty offer list in the PromotionRequest
    PromotionRequest promotionRequestEmptyList = PromotionRequest.builder()
        .offers(Collections.singletonList(Offer.builder().build()))
        .build();
    badRequestForPromotionValidation(objectMapper.writeValueAsString(promotionRequestEmptyList),
        PROMOTIONS_BASE_URI, HttpStatus.BAD_REQUEST);

    // Test Case 2: Verify handling of a negative original price in an offers
    PromotionRequest promotionRequestNegativePrice = PromotionRequest.builder()
        .offers(Collections.singletonList(Offer.builder().originalPrice(-ORIGINAL_PRICE).build()))
        .build();
    badRequestForPromotionValidation(objectMapper.writeValueAsString(promotionRequestNegativePrice),
        PROMOTIONS_BASE_URI, HttpStatus.BAD_REQUEST);

    // Test Case 3: Verify handling of a negative offer ID in an offers
    PromotionRequest promotionRequestNegativeOfferID = PromotionRequest.builder()
        .offers(Collections.singletonList(
            Offer.builder().offerId(-OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build()))
        .build();
    badRequestForPromotionValidation(
        objectMapper.writeValueAsString(promotionRequestNegativeOfferID), PROMOTIONS_BASE_URI,
        HttpStatus.BAD_REQUEST);

    // Test Case 4: Verify handling of duplicate offers in the PromotionRequest
    PromotionRequest promotionRequestDuplicateOffers = PromotionRequest.builder()
        .offers(Arrays.asList(
            Offer.builder().originalPrice(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build(),
            Offer.builder().originalPrice(OFFER_ID_1).originalPrice(ORIGINAL_PRICE).build()))
        .build();
    badRequestForPromotionValidation(
        objectMapper.writeValueAsString(promotionRequestDuplicateOffers), PROMOTIONS_BASE_URI,
        HttpStatus.BAD_REQUEST);

    // Test Case 5: Verify handling of an offer with an empty original price
    PromotionRequest promotionRequestEmptyPrice = PromotionRequest.builder()
        .offers(Collections.singletonList(Offer.builder().offerId(OFFER_ID_1).build()))
        .build();
    badRequestForPromotionValidation(objectMapper.writeValueAsString(promotionRequestEmptyPrice),
        PROMOTIONS_BASE_URI, HttpStatus.BAD_REQUEST);

    // Test Case 6: Verify handling of an offer with a negative original price
    PromotionRequest promotionRequestNegativePrice2 = PromotionRequest.builder()
        .offers(Collections.singletonList(Offer.builder().offerId(OFFER_ID_1)
            .originalPrice(-ORIGINAL_PRICE).build()))
        .build();
    badRequestForPromotionValidation(
        objectMapper.writeValueAsString(promotionRequestNegativePrice2), PROMOTIONS_BASE_URI,
        HttpStatus.BAD_REQUEST);

    // Test Case 7: Verify handling of offers with negative offer IDs
    PromotionRequest promotionRequestNegativeOfferID2 = PromotionRequest.builder()
        .offers(Collections.singletonList(Offer.builder().offerId(-OFFER_ID_1)
            .originalPrice(ORIGINAL_PRICE).build()))
        .build();
    badRequestForPromotionValidation(
        objectMapper.writeValueAsString(promotionRequestNegativeOfferID2), PROMOTIONS_BASE_URI,
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Test get promotions for customer. This test verifies that promotions for a customer are
   * retrieved successfully.
   *
   * @throws Exception the exception
   */
  @Test
  void testGetPromotionsForCustomer() throws Exception {

    String customerEndpoint = PROMOTIONS_BASE_URI + "/" + CUSTOMER_ENDPOINT;
    // Test with valid data
    performRequestAndExpectStatus(get(customerEndpoint)
        .param("serviceabilityAreaId", SERVICEABILITY_AREA_ID)
        .param("customerId", CUSTOMER_ID), HttpStatus.OK);

    // Test with SPACE (Not Found)
    performRequestAndExpectStatus(get(PROMOTIONS_BASE_URI + "/" + SPACE)
        .param("serviceabilityAreaId", SERVICEABILITY_AREA_ID)
        .param("customerId", CUSTOMER_ID), HttpStatus.NOT_FOUND);
    performRequestAndExpectStatus(get(SPACE)
        .param("serviceabilityAreaId", SERVICEABILITY_AREA_ID)
        .param("customerId", CUSTOMER_ID), HttpStatus.NOT_FOUND);

    // Test with invalid serviceabilityAreaId (Bad Request Error)
    performRequestAndExpectStatus(get(customerEndpoint)
        .param("serviceabilityAreaId", INVALID_SERVICEABILITY_AREA_ID)
        .param("customerId", CUSTOMER_ID), HttpStatus.BAD_REQUEST);

    // Test with invalid customerId (Bad Request Error)
    performRequestAndExpectStatus(get(customerEndpoint)
        .param("serviceabilityAreaId", SERVICEABILITY_AREA_ID)
        .param("customerId", INVALID_CUSTOMER_ID), HttpStatus.BAD_REQUEST);
  }

  private void performRequestAndExpectStatus(MockHttpServletRequestBuilder baseUri,
      HttpStatus httpStatus) throws Exception {
    mockMvc.perform(baseUri
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().is(httpStatus.value()));
  }

  /**
   * Test retrieving promotion details by ID.
   * <p>
   * This test method verifies the behavior of the promotion details retrieval endpoint for both
   * valid and invalid promotion IDs.
   *
   * @throws Exception if there is an error during the test execution.
   */
  @Test
  void testGetPromotionDetails() throws Exception {
    // Test Case 1: Valid Promotion ID
    performRequestAndExpectStatus(get(PROMOTIONS_BASE_URI + "/" + DEFAULT_PROMOTION_ID),
        HttpStatus.OK);

    // Test Case 2: Invalid (Negative) Promotion ID
    performRequestAndExpectStatus(get(PROMOTIONS_BASE_URI + "/" + -DEFAULT_PROMOTION_ID),
        HttpStatus.BAD_REQUEST);

    // Test Case 3: Invalid (Non-numeric) Promotion ID
    performRequestAndExpectStatus(get(PROMOTIONS_BASE_URI + "/" + "xyz"), HttpStatus.BAD_REQUEST);
  }

  /**
   * Test the validation of promotion requests.
   * <p>
   * This test method covers various scenarios for validating promotion requests, including valid
   * input, duplicate offer IDs, invalid UUIDs for customer and serviceability area, and invalid
   * endpoints.
   *
   * @throws Exception if there is an error during the test execution.
   */
  @Test
  public void testValidatePromotion() throws Exception {
    // Test Case 1: Valid input
    ArrayList<OfferDetails> offerDetails = new ArrayList<>();
    offerDetails.add(
        OfferDetails.builder().offerId(CATALOG_OFFER_ID).price(ORIGINAL_PRICE).quantity(QUANTITY)
            .build());
    PromotionValidationRequestDto promotionValidationRequestDto = PromotionValidationRequestDto.builder()
        .offerDetails(offerDetails).build();
    promotionValidationRequestDto.setCustomerId(UUID.randomUUID().toString());
    promotionValidationRequestDto.setServiceabilityAreaId(UUID.randomUUID().toString());
    performValidationRequest(promotionValidationRequestDto, PROMOTION_VALIDATE_END_POINT,
        HttpStatus.OK);

    // Test Case 2: Duplicate offer ID (Expecting BAD_REQUEST)
    offerDetails.add(
        OfferDetails.builder().offerId(OFFER_ID_1).price(CATALOG_PRICE).quantity(QUANTITY).build());
    performValidationRequest(promotionValidationRequestDto, PROMOTION_VALIDATE_END_POINT,
        HttpStatus.BAD_REQUEST);

    // Test Case 3: Invalid UUIDs for customer and serviceability area (Expecting BAD_REQUEST)
    promotionValidationRequestDto.setCustomerId(UUID.randomUUID() + "InvalidUUID");
    promotionValidationRequestDto.setServiceabilityAreaId(UUID.randomUUID() + "InvalidUUID");
    performValidationRequest(promotionValidationRequestDto, PROMOTION_VALIDATE_END_POINT,
        HttpStatus.BAD_REQUEST);

    // Test Case 4: Invalid endpoint (Expecting NOT_FOUND)
    performValidationRequest(promotionValidationRequestDto, "invalidEndPoint/abc/abc",
        HttpStatus.NOT_FOUND);

    // Test Case 5: Invalid endpoint (Expecting METHOD_NOT_ALLOWED)
    performValidationRequest(promotionValidationRequestDto, "invalidEndPoint",
        HttpStatus.METHOD_NOT_ALLOWED);

    // Test Case 6: Negative offer ID (Expecting BAD_REQUEST)
    offerDetails.add(
        OfferDetails.builder().offerId(-OFFER_ID_1).price(CATALOG_PRICE).quantity(QUANTITY)
            .build());
    performValidationRequest(promotionValidationRequestDto, PROMOTION_VALIDATE_END_POINT,
        HttpStatus.BAD_REQUEST);

    // Test Case 7: Negative quantity (Expecting BAD_REQUEST)
    offerDetails.add(
        OfferDetails.builder().offerId(OFFER_ID_1).price(CATALOG_PRICE).quantity(-QUANTITY)
            .build());
    performValidationRequest(promotionValidationRequestDto, PROMOTION_VALIDATE_END_POINT,
        HttpStatus.BAD_REQUEST);

    // Test Case 8: Negative price (Expecting BAD_REQUEST)
    offerDetails.add(
        OfferDetails.builder().offerId(OFFER_ID_1).price(-CATALOG_PRICE).quantity(QUANTITY)
            .build());
    performValidationRequest(promotionValidationRequestDto, PROMOTION_VALIDATE_END_POINT,
        HttpStatus.BAD_REQUEST);
  }

  private void performValidationRequest(PromotionValidationRequestDto requestDto, String endpoint,
      HttpStatus expectedStatus) throws Exception {
    String requestJson = objectMapper.writeValueAsString(requestDto);
    mockMvc.perform(MockMvcRequestBuilders.post(PROMOTIONS_BASE_URI + "/" + endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(MockMvcResultMatchers.status().is(expectedStatus.value()));
  }

  /**
   * Perform a POST request to the specified base URI with the given object mapper content and
   * headers, and assert that the response status matches the expected HTTP status code.
   *
   * @param objectMapper The content to be included in the request body, typically in JSON format.
   * @param baseURI      The base URI where the POST request will be sent.
   * @param httpStatus   The expected HTTP status code to be asserted against the response.
   * @throws Exception if there is an error during the request or if the response status does not
   *                   match the expected status.
   */
  private void badRequestForPromotionValidation(String objectMapper, String baseURI,
      HttpStatus httpStatus) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(AUTHORIZATION_KEY, AUTHORIZATION_VALUE);
    RequestBuilder requestBuilder = post(baseURI)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper)
        .headers(headers)
        .contentType(MediaType.APPLICATION_JSON);
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();
    Assertions.assertEquals(httpStatus.value(), response.getStatus());
  }
}