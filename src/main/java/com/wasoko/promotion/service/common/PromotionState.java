package com.wasoko.promotion.service.common;

/**
 * Represents the status of a promotional campaign.
 */
public enum PromotionState {
    /**
     * Draft status indicates that the promotion is being prepared but not yet ready for use.
     */
    DRAFT,

    /**
     * Ready for Approval status indicates that the promotion is completed and awaiting approval.
     */
    READY_FOR_APPROVAL,

    /**
     * Approved status indicates that the promotion has been reviewed and approved for activation.
     */
    APPROVED,

    /**
     * Active status indicates that the promotion is currently active and in use.
     */
    LIVE,

    /**
     * Inactive status indicates that the promotion is no longer active but is not stopped or suspended.
     */
    INACTIVE,

    /**
     * Stopped status indicates that the promotion has been intentionally stopped and is no longer active.
     */
    STOPPED,

    /**
     * Suspended status indicates that the promotion has been temporarily suspended and is not active.
     */
    SUSPENDED
}
