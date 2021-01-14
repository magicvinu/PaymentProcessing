package com.nexus.payment.processing.dto;

import com.nexus.payment.processing.exceptions.ErrorCode;
import com.nexus.payment.processing.exceptions.ErrorMetaData;
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
