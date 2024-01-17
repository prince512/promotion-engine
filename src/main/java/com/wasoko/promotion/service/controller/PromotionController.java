package com.wasoko.promotion.service.controller;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.dto.request.PromotionRequest;
import com.wasoko.promotion.service.dto.request.PromotionValidationRequestDto;
import com.wasoko.promotion.service.service.PromotionCustomerService;
import com.wasoko.promotion.service.service.PromotionDetailService;
import com.wasoko.promotion.service.service.PromotionService;
import com.wasoko.promotion.service.service.PromotionValidationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class that handles Promotion-related endpoints.
 */
@RestController
@RequestMapping("/promotions")
@Slf4j
@Validated
public class PromotionController {
    private final PromotionService promotionService;
    private final PromotionCustomerService promotionCustomerService;
    private final PromotionValidationService promotionValidationService;
    private final PromotionDetailService promotionDetailService;

    /**
     * Instantiates a new Promotion controller.
     *
     * @param promotionService           the promotion service
     * @param promotionCustomerService   the promotion customer service
     * @param promotionValidationService the promotion validation service
     * @param promotionDetailService     the promotion detail service
     */
    @Autowired
    public PromotionController(PromotionService promotionService, PromotionCustomerService promotionCustomerService, PromotionValidationService promotionValidationService, PromotionDetailService promotionDetailService) {
        this.promotionService = promotionService;
        this.promotionCustomerService = promotionCustomerService;
        this.promotionValidationService = promotionValidationService;
        this.promotionDetailService = promotionDetailService;
    }

    /**
     * Gets all promotions.
     *
     * @param promotionRequest the offer request
     * @return return the best promotion for given offers
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RootResponseDTO> getAllPromotionsPrice(
            @RequestBody @Valid PromotionRequest promotionRequest) {
        return ResponseEntity.ok().body(promotionService.getPromotions(promotionRequest));

    }


    /**
     * Gets promotions for customer.
     *
     * @param serviceabilityAreaId the serviceability area id
     * @param customerId           the customer id
     * @return the promotions for customer
     */
    @GetMapping("/customer")
    public ResponseEntity<RootResponseDTO> getPromotionsForCustomer(
            @UUID(allowNil = false, letterCase = UUID.LetterCase.INSENSITIVE, message = "Given serviceability area is not a valid UUID") @Valid @RequestParam(required = false) String serviceabilityAreaId,
            @UUID(allowNil = false, letterCase = UUID.LetterCase.INSENSITIVE, message = "Given customerId is not a valid UUID") @Valid @RequestParam(required = false) String customerId) {

        return ResponseEntity.ok()
                .body(promotionCustomerService.getPromotionsForCustomerAndServiceabilityArea(
                        serviceabilityAreaId, customerId));

    }


    /**
     * Validate Promotion.
     *
     * @param promotionValidationRequestDto the promotion validation request
     * @return the response entity
     */
    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RootResponseDTO> validate(
            @RequestBody @Valid PromotionValidationRequestDto promotionValidationRequestDto) {
        return ResponseEntity.ok().body(promotionValidationService.validatePromotions(
                promotionValidationRequestDto));

    }


    /**
     * Gets promotion.
     *
     * @param id the id
     * @return the promotion details with given promotion id
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RootResponseDTO> getPromotion(
            @Positive(message = "Id should be positive")
            @NotNull(message = "Id cannot be empty") @PathVariable @Valid Long id) {

        return ResponseEntity.ok().body(ResponseEntity.ok(promotionDetailService.getPromotion(id)).getBody());

    }
}