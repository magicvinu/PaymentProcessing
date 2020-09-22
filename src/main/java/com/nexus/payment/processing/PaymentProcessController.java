package com.nexus.payment.processing;

import com.nexus.payment.processing.dto.PaymentProcessResponse;
import com.nexus.payment.processing.model.OrderDetails;
import com.nexus.payment.processing.service.OrderDetailService;
import com.nexus.payment.processing.service.PaymentProcessStatus;
import com.nexus.payment.processing.service.impl.OrderDetailServiceImpl;
import com.nexus.payment.processing.service.PaymentProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * Controller for processing pending orders, Get list of orders for a given client.
 */
@RestController
@RequiredArgsConstructor(onConstructor=@__(@Autowired))
public class PaymentProcessController {

    Logger log = Logger.getLogger(getClass().getName());
    // One should avoid using field injection. It is always better to use constructor or setter injection.
    private final PaymentProcessService paymentProcessService;
    private final OrderDetailService orderDetailService;

    @GetMapping("/hello")
    public String hello(){
        log.info("Hello method called");
        return "Hello World";
    }

    /**
     * The processPayments does following task.
     * 1. Finds if client is available, Throws error if it is not found.
     * 2. Identifies and marks client as violator if they have multiple active payment method.
     * The client won't be considered for future payment processing once marked violator.
     * 3. If client is not a violator.
     *  Find list of unpaid orders for processing.
     *  Call Third Party API to process the order.
     *  Marked the payment_record(Unpaid orders) with the status from Third party API.
     * @param clientId
     * @return
     */
    @PostMapping(path = "/processPayments/{clientId}")
    public ResponseEntity<PaymentProcessResponse> processPayments(@PathVariable Long clientId){

        Pair<PaymentProcessStatus, String> paymentProcessStatusStringPair =  paymentProcessService.processPayment(clientId);
        PaymentProcessResponse paymentProcessResponse = new PaymentProcessResponse();
        paymentProcessResponse.setResponse(paymentProcessStatusStringPair.getFirst().name());
        if(!paymentProcessStatusStringPair.getFirst().equals(PaymentProcessStatus.VIOLATOR)){
            paymentProcessResponse.setOrderDetailsList(orderDetailService.getOrderDetails(clientId));
        }
        return new ResponseEntity<>(paymentProcessResponse, HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/orderDetails/{clientId}")
    public ResponseEntity<List<OrderDetails>> orderDetails(@PathVariable Long clientId){

        List<OrderDetails> orderDetailsList = orderDetailService.getOrderDetails(clientId);
        return new ResponseEntity<>(orderDetailsList, HttpStatus.ACCEPTED);
    }

}
