package com.nexus.payment.processing.service.impl;

import com.nexus.payment.processing.dao.OrderDetailsDao;
import com.nexus.payment.processing.model.OrderDetails;
import com.nexus.payment.processing.service.OrderDetailService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderDetailServiceImpl implements OrderDetailService {

  private final OrderDetailsDao orderDetailsDao;

  public List<OrderDetails> getOrderDetails(Long clientId) {
    return orderDetailsDao.getOrderDetailsFor(clientId);
  }
}
