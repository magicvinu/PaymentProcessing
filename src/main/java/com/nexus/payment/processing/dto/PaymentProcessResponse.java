package com.nexus.payment.processing.dto;

import com.nexus.payment.processing.model.OrderDetails;
import java.util.List;
import lombok.Data;

/** REST Payment Process API response dto */
@Data
public class PaymentProcessResponse {
  private String response;
  private List<OrderDetails> orderDetailsList;
}
