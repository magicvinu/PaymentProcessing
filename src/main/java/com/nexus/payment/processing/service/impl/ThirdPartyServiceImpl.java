package com.nexus.payment.processing.service.impl;

import com.nexus.payment.processing.model.UnpaidOrder;
import com.nexus.payment.processing.service.PaymentAPIStatus;
import com.nexus.payment.processing.service.ThirdPartyService;
import org.springframework.stereotype.Service;

@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {
    @Override
    public PaymentAPIStatus paymentApi(UnpaidOrder unpaidOrder) {
        return PaymentAPIStatus.SUCCESS;
    }
}
