package com.nexus.payment.processing.dao;

import com.nexus.payment.processing.model.PendingOrder;
import com.nexus.payment.processing.model.UnpaidOrder;
import com.nexus.payment.processing.service.PaymentAPIStatus;
import java.util.List;
import java.util.Map;

/** Repository to process payments. */
public interface PaymentProcessDao {

  boolean checkIfClientExistByLockingRecord(Long clientId);

  boolean checkIfClientIsAViolators(Long clientId);

  void updateClientAsViolators(Long clientId);

  boolean findIfOrderIsAlreadyProcessed(Long clientId, Long orderId);

  List<PendingOrder> findPendingOrdersFor(Long clientId);

  void insertPendingOrdersFor(Long clientId);

  void insertUnpaidOrders(PendingOrder pendingOrder);

  List<UnpaidOrder> getPendingOrdersDetailsFor(Long clientId);

  void updatePaymentRecordWithStatusFromPaymentApi(Map<Long, PaymentAPIStatus> paymentAPIStatusMap);
}
