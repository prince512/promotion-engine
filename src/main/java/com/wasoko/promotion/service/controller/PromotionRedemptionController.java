package com.wasoko.promotion.service.controller;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.dto.request.PromotionRedemptionRequestDto;
import com.wasoko.promotion.service.service.PromotionRedemptionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Promotion redemption controller.
 */
@RestController
@RequestMapping("/promotions/redemption")
@Slf4j
@Validated
public class PromotionRedemptionController {
    private final PromotionRedemptionService promotionRedemptionService;

    /**
     * Instantiates a new Promotion redemption controller.
     *
     * @param promotionRedemptionService        the promotion redemption service
     */
    @Autowired
    public PromotionRedemptionController(PromotionRedemptionService promotionRedemptionService) {
        this.promotionRedemptionService = promotionRedemptionService;
    }

    /**
     * Apply redemption response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PutMapping
    public ResponseEntity<RootResponseDTO> applyRedemption(@RequestBody @Valid PromotionRedemptionRequestDto request) {

        return ResponseEntity.ok().body(promotionRedemptionService.applyRedemption(request));

    }
}
