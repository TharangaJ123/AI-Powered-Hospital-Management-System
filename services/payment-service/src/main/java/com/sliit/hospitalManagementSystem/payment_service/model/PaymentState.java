package com.sliit.hospitalManagementSystem.payment_service.model;

public enum PaymentState {
	// Payment has been initiated but not yet finalized
	PENDING,
	// Payment was completed successfully
	SUCCESS,
	// Payment process failed or was canceled
	FAILED
}

