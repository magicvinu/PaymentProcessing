package com.nexus.payment.processing.exceptions;

/**
 * Throw this class when client is not found in the database.
 */
public class ClientNotFoundException extends ServiceException {

    public ClientNotFoundException(Long clientId, Throwable cause, ErrorMetaData errorMetaData) {

        super(String.format("Client with Id %d not found", clientId), cause, errorMetaData);
    }
}