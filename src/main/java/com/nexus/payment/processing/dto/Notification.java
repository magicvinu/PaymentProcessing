package com.nexus.payment.processing.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Notification {

  private int category;
  private String code;
  private String source;
  private String severity;
  private String type;
  private String message;
}
