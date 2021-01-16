package com.nexus.payment.processing.exceptions;

public class ClientViolatorException extends RuntimeException {

  public ClientViolatorException(Long clientId) {

    super(
        String.format(
            "Client with Id %d have multiple payment record and marked as violator", clientId));
  }
}
