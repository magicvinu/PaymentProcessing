package com.nexus.payment.processing.model;

import java.math.BigDecimal;
import lombok.Data;

/** Unpaid order model */
@Data
public class UnpaidOrder {

  private Long paymentRecordId;
  private BigDecimal totalAmount;
  private String creditCardNumber;
  private String authCode;
  private Long clientId;
}
