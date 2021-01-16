package com.nexus.payment.processing.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderDetails {

  private Long clientId;
  private Long paymentRecordId;
  private Long orderId;
  private String clientName;
  private BigDecimal totalAmount;
  private String paymentRecordStatus;
  private String processDate;
}
