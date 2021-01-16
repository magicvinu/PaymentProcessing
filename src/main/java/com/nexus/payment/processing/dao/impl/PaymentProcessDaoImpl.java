package com.nexus.payment.processing.dao.impl;

import com.nexus.payment.processing.dao.PaymentProcessDao;
import com.nexus.payment.processing.exceptions.ClientViolatorException;
import com.nexus.payment.processing.model.Client;
import com.nexus.payment.processing.model.PendingOrder;
import com.nexus.payment.processing.model.UnpaidOrder;
import com.nexus.payment.processing.repository.ClientRepository;
import com.nexus.payment.processing.rowmapper.PendingOrderRowMapper;
import com.nexus.payment.processing.rowmapper.UnpaidOrderRowMapper;
import com.nexus.payment.processing.service.PaymentAPIStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

/** Repository to process payments. */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentProcessDaoImpl extends JdbcDaoSupport implements PaymentProcessDao {

  Logger log = Logger.getLogger(getClass().getName());
  private final DataSource dataSource;
  private final ClientRepository clientRepository;

  @PostConstruct
  private void initialize() {
    setDataSource(dataSource);
  }

  /**
   * Finds if client is available.
   *
   * @param clientId
   * @return
   */
  @Override
  public boolean checkIfClientExistByLockingRecord(Long clientId) {
    Optional<Client> clientOptional = clientRepository.findById(clientId);
    if (clientOptional.isPresent()) {
      Client client = clientOptional.get();
      if (client.isViolator()) {
        throw new ClientViolatorException(clientId);
      }
    }
    return clientOptional.isPresent();
  }

  /**
   * Checks if client is having multiple active payment methods
   *
   * @param clientId
   * @return
   */
  @Override
  public boolean checkIfClientIsAViolators(Long clientId) {

    String isClientAViolatorQuery =
        "SELECT count(*) >1 FROM CLIENT \n"
            + "INNER JOIN ORDERS ON CLIENT.CLIENT_ID= ORDERS.CLIENT_ID\n"
            + "INNER JOIN PAYMENT_METHOD ON PAYMENT_METHOD.PAYMENT_METHOD_ID = ORDERS.PAYMENT_METHOD_ID\n"
            + "WHERE CLIENT.VIOLATOR = FALSE AND PAYMENT_METHOD.STATUS='ACTIVE' AND CLIENT.CLIENT_ID=?";

    boolean isClientAViolator =
        getJdbcTemplate()
            .queryForObject(isClientAViolatorQuery, new Object[] {clientId}, Boolean.class);
    log.info("clientId = " + clientId + "is a violator = " + isClientAViolator);
    return isClientAViolator;
  }

  /**
   * Update client as a violator
   *
   * @param clientId
   */
  @Override
  public void updateClientAsViolators(Long clientId) {
    String updateViolator = "UPDATE CLIENT SET VIOLATOR=TRUE WHERE CLIENT_ID=?";
    getJdbcTemplate().update(updateViolator, new Object[] {clientId});
  }

  public boolean findIfOrderIsAlreadyProcessed(Long clientId, Long orderId) {
    String pendingOrderQuery =
        "SELECT count(*) >=1 FROM PENDING_ORDERS WHERE ORDER_ID=? AND ORDERS_CLIENT_ID=?"
            + " AND PAYMENT_RECORD_STATUS='SUCCESS' AND PAYMENT_RECORD_PROCESS_DATETIME IS NOT NULL";
    boolean orderAlreadyProcessed =
        getJdbcTemplate()
            .queryForObject(pendingOrderQuery, new Object[] {orderId, clientId}, Boolean.class);
    return orderAlreadyProcessed;
  }

  public List<PendingOrder> findPendingOrdersFor(Long clientId) {
    String pendingOrderQuery =
        "SELECT * FROM PENDING_ORDERS WHERE ((PAYMENT_RECORD_STATUS IS NULL AND PAYMENT_METHOD_STATUS= 'ACTIVE')\n"
            + "OR ( PAYMENT_METHOD_STATUS = 'ACTIVE' AND PAYMENT_RECORD_STATUS = 'FAIL' \n"
            + "AND TIMESTAMPDIFF(HOUR, PAYMENT_RECORD_PROCESS_DATETIME, CURRENT_TIMESTAMP)>=24 ))\n"
            + "AND ORDERS_CLIENT_ID=?";
    return getJdbcTemplate()
        .query(pendingOrderQuery, new Object[] {clientId}, new PendingOrderRowMapper());
  }

  /**
   * Add unpaid orders in payment_records table for a given client
   *
   * @param clientId
   */
  @Override
  public void insertPendingOrdersFor(Long clientId) {

    String insertPendingOrderQuery =
        "INSERT  INTO PAYMENT_RECORD (ORDER_ID, STATUS, PROCESS_DATETIME)\n"
            + "SELECT ORDER_ID,'ACTIVE', null FROM PENDING_ORDERS WHERE ((PAYMENT_RECORD_STATUS IS NULL AND PAYMENT_METHOD_STATUS= 'ACTIVE')\n"
            + "OR ( PAYMENT_METHOD_STATUS = 'ACTIVE' AND PAYMENT_RECORD_STATUS = 'FAIL'"
            + " AND TIMESTAMPDIFF(HOUR, PAYMENT_RECORD_PROCESS_DATETIME, CURRENT_TIMESTAMP)>=24 ))\n"
            + "AND ORDERS_CLIENT_ID=?";
    int insertRowCount = getJdbcTemplate().update(insertPendingOrderQuery, clientId);
    log.info("Number of records inserted " + insertRowCount);
  }

  @Override
  public void insertUnpaidOrders(PendingOrder pendingOrder) {
    String insertUnpaidOrderQuery =
        "INSERT  INTO PAYMENT_RECORD (ORDER_ID, STATUS, PROCESS_DATETIME) "
            + "VALUES(?,'ACTIVE', null)";
    int insertRowCount =
        getJdbcTemplate().update(insertUnpaidOrderQuery, pendingOrder.getOrderId());
    log.info("Number of records inserted " + insertRowCount);
  }
  /**
   * Find list of pending orders for a client
   *
   * @param clientId
   * @return
   */
  @Override
  public List<UnpaidOrder> getPendingOrdersDetailsFor(Long clientId) {
    String unpaidOrderQuery =
        "SELECT PAYMENT_RECORD.PAYMENT_RECORD_ID,TOTAL_AMOUNT,ENCRYPTED_CREDIT_CARD,ENCRYPTED_AUTHCODE,ORDERS.CLIENT_ID FROM PAYMENT_RECORD \n"
            + "INNER JOIN ORDERS ON ORDERS.ORDER_ID = PAYMENT_RECORD.ORDER_ID\n"
            + "INNER JOIN PAYMENT_METHOD ON PAYMENT_METHOD.PAYMENT_METHOD_ID = ORDERS.PAYMENT_METHOD_ID\n"
            + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PROCESS_DATETIME IS NULL AND ORDERS.CLIENT_ID=?";
    return getJdbcTemplate()
        .query(unpaidOrderQuery, new Object[] {clientId}, new UnpaidOrderRowMapper());
  }

  /**
   * Call third party API for processing unpaid orders for a given client
   *
   * @param paymentAPIStatusMap
   */
  @Override
  public void updatePaymentRecordWithStatusFromPaymentApi(
      Map<Long, PaymentAPIStatus> paymentAPIStatusMap) {

    String updatePaymentApiResponseStatusQuery =
        "UPDATE PAYMENT_RECORD SET STATUS=?, PROCESS_DATETIME=CURRENT_TIMESTAMP WHERE PAYMENT_RECORD_ID=?";
    paymentAPIStatusMap.keySet().stream()
        .forEach(
            x -> {
              getJdbcTemplate()
                  .update(
                      updatePaymentApiResponseStatusQuery,
                      paymentAPIStatusMap.get(x.longValue()).name(),
                      x.longValue());
            });
  }
}
