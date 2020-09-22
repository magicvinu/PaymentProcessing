package com.nexus.payment.processing.service;

import com.nexus.payment.processing.model.UnpaidOrder;

public interface ThirdPartyService {

    PaymentAPIStatus paymentApi(UnpaidOrder unpaidOrder);

}
