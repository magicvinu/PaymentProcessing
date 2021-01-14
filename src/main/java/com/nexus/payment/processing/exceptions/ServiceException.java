package com.nexus.payment.processing.exceptions;

import lombok.Getter;
import org.springframework.util.Assert;

import java.util.*;


@Getter
public abstract class ServiceException extends RuntimeException {

    private final Set<ErrorMetaData> errorMetaData;
    private final UUID exceptionId;

    protected ServiceException(String message, Throwable cause, ErrorMetaData errorMetaData) {
        this(message, cause, Optional.ofNullable(errorMetaData)
                .map(emd -> Set.of(emd)).orElse(Collections.emptySet()));
//        super(message, cause);
//        Optional.ofNullable(errorMetaData)
//                .map(emd -> Set.of(emd)).orElse(Collections.emptySet());
//        exceptionId = UUID.randomUUID();

//        Assert.notNull(errorMetaData, "Faults cannot be null");
//        this.errorMetaData = Collections.unmodifiableSet(errorMetaData);

    }

    /**
     * @param message
     * @param cause
     * @param errorMetaData
     */
    protected ServiceException(String message, Throwable cause, Set<ErrorMetaData> errorMetaData) {

        super(message, cause);

        exceptionId = UUID.randomUUID();

        Assert.notNull(errorMetaData, "Faults cannot be null");
        this.errorMetaData = Collections.unmodifiableSet(errorMetaData);

    }

    protected ServiceException(String message, Throwable cause) {
        this(message, cause, Collections.emptySet());
    }

    @Override
    public String getMessage() {
        return String.format("%s Exception id:%s, faults:%s",
                super.getMessage(), exceptionId, errorMetaData.toArray());
    }


}

