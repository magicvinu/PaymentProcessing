package com.nexus.payment.processing.service;

import com.nexus.payment.processing.model.OrderDetails;
import java.util.List;

public interface OrderDetailService {
  List<OrderDetails> getOrderDetails(Long clientId);
}
