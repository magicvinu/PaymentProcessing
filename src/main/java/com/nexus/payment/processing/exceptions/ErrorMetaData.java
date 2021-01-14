package com.nexus.payment.processing.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMetaData {

    private int category;
    private ErrorCode code;
    private Source source;
    private Severity severity;
    private Type type;
    private String message;

    public enum Source{
        SOURCE1,
        SOURCE2,
        DATABASE
    }

    public enum Type{
        ERROR,
        WARNING
    }
    public enum Severity{
        HIGH,
        MEDIUM,
        LOW
    }

}
