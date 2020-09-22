package com.nexus.payment.processing.service.impl;

import com.nexus.payment.processing.dao.OrderDetailsDao;
import com.nexus.payment.processing.model.OrderDetails;
import com.nexus.payment.processing.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor=@__(@Autowired))
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailsDao orderDetailsDao;
    public List<OrderDetails> getOrderDetails(Long clientId){
        return orderDetailsDao.getOrderDetailsFor(clientId);
    }
}
