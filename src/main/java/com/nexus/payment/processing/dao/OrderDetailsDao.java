package com.nexus.payment.processing.dao;

import com.nexus.payment.processing.model.OrderDetails;

import java.util.List;

/**
 * Repository class to get details of order for a client
 */
public interface OrderDetailsDao {
    List<OrderDetails> getOrderDetailsFor(Long clientId);

}
