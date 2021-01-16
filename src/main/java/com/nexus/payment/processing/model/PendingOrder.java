package com.nexus.payment.processing.model;

import lombok.Data;

/** Unpaid order model */
@Data
public class PendingOrder {

  private Long orderId;
  private Long clientId;
  private Long paymentRecordId;
  private String paymentMethodStatus;
  private String paymentRecordStatus;
  private String paymentRecordProcessDate;
}
