package com.nexus.payment.processing.service.impl;

import com.nexus.payment.processing.dao.PaymentProcessDao;
import com.nexus.payment.processing.exceptions.ClientNotFoundException;
import com.nexus.payment.processing.exceptions.ErrorCode;
import com.nexus.payment.processing.exceptions.ErrorMetaData;
import com.nexus.payment.processing.model.PendingOrder;
import com.nexus.payment.processing.model.UnpaidOrder;
import com.nexus.payment.processing.service.PaymentAPIStatus;
import com.nexus.payment.processing.service.PaymentProcessService;
import com.nexus.payment.processing.service.PaymentProcessStatus;
import com.nexus.payment.processing.service.ThirdPartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The payment process service to perform following
 * 1. Find and throw error if client not found.
 * 2. Identifies and marks client as violator if they have multiple active payment method.
 * 3. If client is not a violator process unpaid orders
 */
@Service
@RequiredArgsConstructor(onConstructor=@__(@Autowired))
public class PaymentProcessServiceImpl implements PaymentProcessService {
    private final PaymentProcessDao paymentProcessDao;
    private final ThirdPartyService thirdPartyService;

    @Override
    @Transactional(timeout = 30)
    public Pair<PaymentProcessStatus, String> processPayment(Long clientId) {

        // Check if client is available
        CheckIfClientExists(clientId);

        // Find list of unpaid orders
            List<PendingOrder> unpaidOrderList = new ArrayList<>();
            List<PendingOrder> pendingOrdersList = paymentProcessDao.findPendingOrdersFor(clientId);
            if(pendingOrdersList.size() == 0){
                //No records to process for the client
                return Pair.of(PaymentProcessStatus.NO_RECORDS, "No pending orders for the client id= " + clientId);
            }else{
                // there are pending records which may have already been processed.
                pendingOrdersList.forEach(po -> {
                    // check if order is already processed.
                    if(!paymentProcessDao.findIfOrderIsAlreadyProcessed(po.getClientId(), po.getOrderId())){
                        // Add if pending order doesn't exists'
                        unpaidOrderList.add(po);
                    }
                });

            }

        if(unpaidOrderList.size() == 0){
            // No unpaid orders to process.
            return Pair.of(PaymentProcessStatus.NO_RECORDS, "No unpaid orders for the client id= " + clientId);
        }else {
            // client is not a violator
            // Insert pending orders
            paymentProcessDao.insertUnpaidOrders(unpaidOrderList.get(0));

            if(paymentProcessDao.checkIfClientIsAViolators(clientId)){
                // Check if current client is a violator.
                // Update client as violator if the client has multiple payments
                paymentProcessDao.updateClientAsViolators(clientId);
                return Pair.of(PaymentProcessStatus.VIOLATOR, String.format("Client id %d is a violator", clientId));
            }

            // Get the newly created pending order for a given client
            List<UnpaidOrder> newlyCreatedUnpaidOrderList = paymentProcessDao.getPendingOrdersDetailsFor(clientId);

            Map<Long, PaymentAPIStatus> paymentAPIStatusMap = newlyCreatedUnpaidOrderList.stream().collect(
                    Collectors.toMap(UnpaidOrder::getPaymentRecordId, thirdPartyService::paymentApi));
            // The better way is to use first element of unpaidOrderList, I left it that to show my awareness of stream and lamda.
            paymentProcessDao.updatePaymentRecordWithStatusFromPaymentApi(paymentAPIStatusMap);

            return Pair.of(PaymentProcessStatus.SUCCESS, "Order processed for Client id " + clientId);
        }

    }

    /**
     * Get a lock on the client which is under process. This ensure only one server/thread can get lock on the client record
     * @param clientId
     */
    private void CheckIfClientExists(Long clientId) {
        if(!paymentProcessDao.checkIfClientExistByLockingRecord(clientId)){
            // Client doesn't exists, Throw exception which will be caught and processed by Controller advice #PaymentProcessControllerAdvice.class
            throw new ClientNotFoundException(clientId, new Exception("Client not found"), ErrorMetaData.builder()
                    .code(ErrorCode.CLIENT_NOT_FOUND_ERROR)
                    .source(ErrorMetaData.Source.DATABASE)
                    .category(HttpStatus.NOT_FOUND.value()).build());
        }
    }
}
