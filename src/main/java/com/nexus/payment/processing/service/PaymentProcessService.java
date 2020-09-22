package com.nexus.payment.processing.service;

import org.springframework.data.util.Pair;

/**
 * The payment process service to perform following
 * 1. Find and throw error if client not found.
 * 2. Identifies and marks client as violator if they have multiple active payment method.
 * 3. If client is not a violator process unpaid orders
 */
public interface PaymentProcessService {

    Pair<PaymentProcessStatus, String> processPayment(Long clientId);

}
