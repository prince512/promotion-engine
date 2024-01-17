package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NoArgsConstructor;

/**
 * The persistent class for the promotion_promotion database table.
 *
 */
@Entity
@Table(name="promotion_promotion")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Promotion  {

    @Id
    private Long id;
    @Column(name="campaign_manager")
    private String campaignManager;
    @Column(name = "description")
    private String description;
    @Column(name="end_datetime")
    private LocalDateTime endDatetime;
    @Column(name = "exclusive")
    private Boolean exclusive;
    @Column(name="max_basket_applications")
    private Integer maxBasketApplications;
    @Column(name="max_discount")
    private Float maxDiscount;
    @Column(name="max_global_applications")
    private Integer maxGlobalApplications;
    @Column(name="max_global_discount")
    private BigDecimal maxGlobalDiscount;
    @Column(name="max_user_applications")
    private Integer maxUserApplications;
    @Column(name = "name")
    private String name;
    @Column(name = "priority")
    private Integer priority;
    @Column(name = "slug")
    private String slug;
    @Column(name="start_datetime")
    private LocalDateTime startDatetime;
    @Column(name = "state")
    private String state;
    @Column(name = "tnc")
    private String tnc;
    @Column(name = "type")
    private String type;
    @OneToOne(mappedBy="promotion", fetch = FetchType.EAGER)
    private PromotionBenefit promotionBenefits;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creative_id")
    private PromotionCreative promotionCreative;
    @OneToOne(mappedBy="promotion", fetch = FetchType.EAGER)
    private PromotionCondition promotionCondition;
    @OneToOne(mappedBy="promotion", fetch = FetchType.EAGER)
    private PromotionLimit promotionLimit;
    @OneToMany(mappedBy="promotion", fetch = FetchType.EAGER)
    private List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices;
    @OneToMany(mappedBy="promotion", fetch = FetchType.EAGER)
    private List<PromotionRedemption> promotionRedemptionList;

}