package com.sliit.review_service.entity;

public enum ReviewStatus {
    // Review has been submitted but not yet reviewed by an admin
    PENDING,
    // Review has been verified and is visible to the public
    APPROVED,
    // Review has been rejected and will not be displayed
    REJECTED
}
