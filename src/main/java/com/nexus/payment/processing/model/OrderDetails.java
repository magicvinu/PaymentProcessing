package com.nexus.payment.processing.model;

import lombok.Data;

import java.math.BigDecimal;

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


