package com.nexus.payment.processing;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexus.payment.processing.dao.PaymentProcessDao;
import com.nexus.payment.processing.repository.ClientRepository;
import com.nexus.payment.processing.service.PaymentAPIStatus;
import com.nexus.payment.processing.service.PaymentProcessStatus;
import com.nexus.payment.processing.service.ThirdPartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentProcessControllerTest {
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private ClientRepository clientRepository;
  @Autowired private MockMvc mockMvc;
  @Autowired private PaymentProcessDao paymentProcessDao;
  @MockBean private ThirdPartyService thirdPartyService;

  @BeforeEach
  void setUp() {}

  @Test
  void testHello() throws Exception {
    this.mockMvc
        .perform(get("/hello"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Hello World")));
  }

  @Test
  void testClientNotFound() throws Exception {
    this.mockMvc
        .perform(post("/processPayments/10"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            content().string(containsString("Client not found, Client with Id 10 not found")));
  }

  @Test
  void testExistingClientWhoIsAViolator() throws Exception {
    this.mockMvc
        .perform(post("/processPayments/9"))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content()
                .string(
                    containsString(
                        "Client with Id 9 have multiple payment record and marked as violator")));
  }

  @Test
  void testClientWhoIsAViolator() throws Exception {

    //        INSERT INTO CLIENT(CLIENT_ID, CLIENT_NAME) VALUES(6, 'Jaddu');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(8, '4356000000000008', 'Jaddu address' , 'ACTIVE');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(9, '4356000000000009', 'Jaddu address' , 'ACTIVE');

    int clientId = 6;
    Integer pendingOrdersBeforePaymentProcess =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert pendingOrdersBeforePaymentProcess == 0;
    this.mockMvc
        .perform(post("/processPayments/" + clientId))
        .andDo(print())
        .andExpect(status().isAccepted())
        .andExpect(content().string(containsString(PaymentProcessStatus.VIOLATOR.name())));
    Boolean violator =
        jdbcTemplate.queryForObject(
            "SELECT VIOLATOR FROM CLIENT WHERE CLIENT_ID=?",
            new Object[] {clientId},
            Boolean.class);
    assert violator == true;
    // There shouldn't be any new pending order for the violator.
    Integer newPendingOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert newPendingOrders == 1;
  }

  @Test
  void testProcessOrder_SuccessfullyPayedOrderAreNotConsideredAsUnpaidOrders() throws Exception {

    //        INSERT INTO CLIENT(CLIENT_ID, CLIENT_NAME) VALUES(1, 'George');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(1, '4356000000000001', 'George address' , 'ACTIVE');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(2, '4356000000000002', 'George address' , 'INACTIVE');
    //        -- George Active Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(1, 1, 1, 10.12, 'authcode1');
    //        -- George Inactive Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(2, 1, 2, 12.22, 'authcode2');
    //        -- George Successful payment record
    //        INSERT INTO PAYMENT_RECORD(ORDER_ID, STATUS, PROCESS_DATETIME) VALUES(1, 'SUCCESS',
    // CURRENT_TIMESTAMP);

    int clientId = 1;
    // We need to check if successfullyPaidOrderId is not added again as an unpaid order in
    // PAYMENT_RECORD AFTER PROCESSING
    Integer successfullyPaidOrderId =
        jdbcTemplate.queryForObject(
            "SELECT ORDER_ID FROM PENDING_ORDERS WHERE ORDERS_CLIENT_ID=? AND PAYMENT_RECORD_STATUS='SUCCESS'",
            new Object[] {clientId},
            Integer.class);
    Integer pendingOrdersBeforePaymentProcess =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert pendingOrdersBeforePaymentProcess == 0;
    this.mockMvc
        .perform(post("/processPayments/" + clientId))
        .andDo(print())
        .andExpect(status().isAccepted())
        .andExpect(content().string(containsString(PaymentProcessStatus.SUCCESS.name())));
    Integer newPendingOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=? AND PAYMENT_RECORD.ORDER_ID=?",
            new Object[] {clientId, successfullyPaidOrderId},
            Integer.class);
    assert newPendingOrders == 0;
  }

  @Test
  void testProcessOrder_DeclinedOrderAreNotConsideredAsUnpaidOrders() throws Exception {

    //        INSERT INTO CLIENT(CLIENT_ID, CLIENT_NAME) VALUES(3, 'Simon');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(5, '4356000000000005', 'Simon address' , 'ACTIVE');
    //        -- Simon Active Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(5, 3, 5, 29.18, 'authcode5');
    //        -- Simon DECLINE payment record For current Active payment method
    //        INSERT INTO PAYMENT_RECORD(ORDER_ID, STATUS, PROCESS_DATETIME) VALUES(5, 'DECLINE',
    // CURRENT_TIMESTAMP);

    int clientId = 3;
    // We need to check if declinedOrderId is not added again as an unpaid order in PAYMENT_RECORD
    // AFTER PROCESSING
    Integer declinedOrderId =
        jdbcTemplate.queryForObject(
            "SELECT ORDER_ID FROM PENDING_ORDERS WHERE ORDERS_CLIENT_ID=? AND PAYMENT_RECORD_STATUS='DECLINE'",
            new Object[] {clientId},
            Integer.class);
    Integer pendingOrdersBeforePaymentProcess =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert pendingOrdersBeforePaymentProcess == 0;
    this.mockMvc
        .perform(post("/processPayments/" + clientId))
        .andDo(print())
        .andExpect(status().isAccepted())
        .andExpect(content().string(containsString(PaymentProcessStatus.NO_RECORDS.name())));
    Integer newPendingOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=? AND PAYMENT_RECORD.ORDER_ID=?",
            new Object[] {clientId, declinedOrderId},
            Integer.class);
    assert newPendingOrders == 0;
  }

  @Test
  void testProcessOrder_FailedOrderMoreThan24HourOldAreConsideredUnpaidOrders() throws Exception {

    //        INSERT INTO CLIENT(CLIENT_ID, CLIENT_NAME) VALUES(4, 'Alan');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(6, '4356000000000006', 'Alan address' , 'ACTIVE');
    //        -- Alan Active Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(6, 4, 6, 29.18, 'authcode6');
    //        -- Alan Failed payment record For current Active payment method which was processed
    // more than 24 hr back.
    //        INSERT INTO PAYMENT_RECORD(ORDER_ID, STATUS, PROCESS_DATETIME) VALUES(6, 'FAIL',
    // CURRENT_TIMESTAMP - INTERVAL 25 HOUR);
    // Insert client who has failed order more than 24 hr old.
    int clientId = 4;
    when(thirdPartyService.paymentApi(ArgumentMatchers.any())).thenReturn(PaymentAPIStatus.SUCCESS);
    // We need to check if failedOrderIdMoreThan24HourOld is added again as an unpaid order in
    // PAYMENT_RECORD AFTER PROCESSING
    Integer failedOrderIdMoreThan24HourOld =
        jdbcTemplate.queryForObject(
            "SELECT ORDER_ID FROM PENDING_ORDERS WHERE ORDERS_CLIENT_ID=? AND PAYMENT_RECORD_STATUS='FAIL'",
            new Object[] {clientId},
            Integer.class);
    Integer pendingOrdersBeforePaymentProcess =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert pendingOrdersBeforePaymentProcess == 0;
    this.mockMvc
        .perform(post("/processPayments/" + clientId))
        .andDo(print())
        .andExpect(status().isAccepted())
        .andExpect(content().string(containsString(PaymentProcessStatus.SUCCESS.name())));
    Integer newPendingOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=? AND PAYMENT_RECORD.ORDER_ID=?",
            new Object[] {clientId, failedOrderIdMoreThan24HourOld},
            Integer.class);
    assert newPendingOrders == 0;
  }

  @Test
  void testProcessOrder_FailedOrderLessThan24HourOldAreNotConsideredAsUnpaidOrders()
      throws Exception {

    //        INSERT INTO CLIENT(CLIENT_ID, CLIENT_NAME) VALUES(5, 'Kevin');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(7, '4356000000000007', 'Kevin address' , 'ACTIVE');
    //        -- Kevin Active Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(7, 5, 7, 39.18, 'authcode7');
    //        -- Kevin Failed payment record For current Active payment method processed within last
    // 24 hr
    //        INSERT INTO PAYMENT_RECORD(ORDER_ID, STATUS, PROCESS_DATETIME) VALUES(7, 'FAIL',
    // CURRENT_TIMESTAMP);

    // Insert client who has failed order less than 24 hr old.
    int clientId = 5;
    // We need to check if failedOrderIdLessThan24HourOld is not added again as an unpaid order in
    // PAYMENT_RECORD AFTER PROCESSING
    Integer failedOrderIdLessThan24HourOld =
        jdbcTemplate.queryForObject(
            "SELECT ORDER_ID FROM PENDING_ORDERS WHERE ORDERS_CLIENT_ID=? AND PAYMENT_RECORD_STATUS='FAIL'",
            new Object[] {clientId},
            Integer.class);
    Integer pendingOrdersBeforePaymentProcess =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert pendingOrdersBeforePaymentProcess == 0;
    this.mockMvc
        .perform(post("/processPayments/" + clientId))
        .andDo(print())
        .andExpect(status().isAccepted())
        .andExpect(content().string(containsString(PaymentProcessStatus.NO_RECORDS.name())));
    Integer newPendingOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=? AND PAYMENT_RECORD.ORDER_ID=?",
            new Object[] {clientId, failedOrderIdLessThan24HourOld},
            Integer.class);
    assert newPendingOrders == 0;
  }

  @Test
  void testProcessOrderSuccess_WithSuccessfulThirdPartyAPICall() throws Exception {

    //        -- Client Alex has Failed order which is more than 24 old, Payment API process will
    // process order with SUCCESS status
    //        INSERT INTO CLIENT(CLIENT_ID, CLIENT_NAME) VALUES(2, 'Alex');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(3, '4356000000000003', 'Alex address' , 'ACTIVE');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(4, '4356000000000004', 'Alex address' , 'INACTIVE');
    //        -- Alex Active Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(3, 2, 3, 15.32, 'authcode3');
    //        -- Alex Inactive Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(4, 2, 4, 20.18, 'authcode4');

    // MOck third party API call with Success.
    when(thirdPartyService.paymentApi(ArgumentMatchers.any())).thenReturn(PaymentAPIStatus.SUCCESS);
    int clientId = 2;
    Integer pendingOrdersBeforePaymentProcess =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert pendingOrdersBeforePaymentProcess == 0;
    this.mockMvc
        .perform(post("/processPayments/" + clientId))
        .andDo(print())
        .andExpect(status().isAccepted())
        .andExpect(content().string(containsString(PaymentProcessStatus.SUCCESS.name())));

    // No Active records for the clients
    Integer newPendingOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert newPendingOrders == 0;

    // Check if Successful records added for the clients
    Integer successfulOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='SUCCESS' AND PAYMENT_RECORD.PROCESS_DATETIME IS NOT NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert successfulOrders == 1;
  }

  @Test
  void testProcessOrderSuccess_WithDeclinedThirdPartyAPICall() throws Exception {

    //        -- Client Jack has Failed order which is more than 24 old, Payment API process will
    // process order with DECLINE status
    //        INSERT INTO CLIENT(CLIENT_ID, CLIENT_NAME) VALUES(7, 'Jack');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(10, '4356000000000010', 'Jack address' , 'ACTIVE');
    //        -- Jack Active Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(10, 7, 10, 35.32, 'authcode10');
    //        -- Jack Failed payment record For current Active payment method which was processed
    // more than 24 hr back.
    //        INSERT INTO PAYMENT_RECORD(ORDER_ID, STATUS, PROCESS_DATETIME) VALUES(10, 'FAIL',
    // CURRENT_TIMESTAMP - INTERVAL 25 HOUR);

    // MOck third party API call with Declined.
    when(thirdPartyService.paymentApi(ArgumentMatchers.any())).thenReturn(PaymentAPIStatus.DECLINE);
    int clientId = 7;
    Integer pendingOrdersBeforePaymentProcess =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert pendingOrdersBeforePaymentProcess == 0;
    this.mockMvc
        .perform(post("/processPayments/" + clientId))
        .andDo(print())
        .andExpect(status().isAccepted())
        .andExpect(content().string(containsString(PaymentProcessStatus.SUCCESS.name())));

    // No Active records for the clients
    Integer newPendingOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert newPendingOrders == 0;

    // Check if Declined records added for the clients
    Integer successfulOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='DECLINE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NOT NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert successfulOrders == 1;
  }

  @Test
  void testProcessOrderSuccess_WithFailThirdPartyAPICall() throws Exception {

    //        -- Client Rose has Failed order which is more than 24 old, Payment API process will
    // process order with FAIL status
    //        INSERT INTO CLIENT(CLIENT_ID, CLIENT_NAME) VALUES(8, 'Rose');
    //        INSERT INTO PAYMENT_METHOD(PAYMENT_METHOD_ID, ENCRYPTED_CREDIT_CARD, ADDRESS, STATUS)
    // VALUES(11, '4356000000000011', 'Rose address' , 'ACTIVE');
    //        -- Rose Active Payment Order
    //        INSERT INTO ORDERS(ORDER_ID, CLIENT_ID, PAYMENT_METHOD_ID, TOTAL_AMOUNT,
    // ENCRYPTED_AUTHCODE) VALUES(11, 8, 11, 15.72, 'authcode11');
    //        -- Rose Failed payment record For current Active payment method which was processed
    // more than 24 hr back.
    //        INSERT INTO PAYMENT_RECORD(ORDER_ID, STATUS, PROCESS_DATETIME) VALUES(11, 'FAIL',
    // CURRENT_TIMESTAMP - INTERVAL 25 HOUR);
    //        -- Rose Failed payment record For current Active payment method which was processed
    // more than 24 hr back.
    //        INSERT INTO PAYMENT_RECORD(ORDER_ID, STATUS, PROCESS_DATETIME) VALUES(11, 'FAIL',
    // CURRENT_TIMESTAMP - INTERVAL 25 HOUR);

    // MOck third party API call with Declined.
    when(thirdPartyService.paymentApi(ArgumentMatchers.any())).thenReturn(PaymentAPIStatus.FAIL);
    int clientId = 8;
    Integer pendingOrdersBeforePaymentProcess =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert pendingOrdersBeforePaymentProcess == 0;
    this.mockMvc
        .perform(post("/processPayments/" + clientId))
        .andDo(print())
        .andExpect(status().isAccepted())
        .andExpect(content().string(containsString(PaymentProcessStatus.SUCCESS.name())));

    // No Active records for the clients
    Integer newPendingOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='ACTIVE' AND PAYMENT_RECORD.PROCESS_DATETIME IS NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert newPendingOrders == 0;

    // Check if Declined records added for the clients
    Integer successfulOrders =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM PAYMENT_RECORD \n"
                + "INNER JOIN ORDERS ON ORDERS.ORDER_ID= PAYMENT_RECORD.ORDER_ID\n"
                + "WHERE PAYMENT_RECORD.STATUS='FAIL' AND PAYMENT_RECORD.PROCESS_DATETIME IS NOT NULL AND CLIENT_ID=?",
            new Object[] {clientId},
            Integer.class);
    assert successfulOrders == 2;
  }
}
