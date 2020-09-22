package com.nexus.payment.processing.exception;

/**
 * Throw this class when client is not found in the database.
 */
public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long clientId) {

        super(String.format("Client with Id %d not found", clientId));
    }
}