package com.nexus.payment.processing.dao.impl;

import com.nexus.payment.processing.dao.OrderDetailsDao;
import com.nexus.payment.processing.model.OrderDetails;
import com.nexus.payment.processing.rowmapper.OrderDetailRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

/**
 * Repository class to get details of order for a client
 */
@Repository
@RequiredArgsConstructor(onConstructor=@__(@Autowired))
public class OrderDetailsDaoImpl extends JdbcDaoSupport implements OrderDetailsDao {

    private final DataSource dataSource;
    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }
    @Override
    public List<OrderDetails> getOrderDetailsFor(Long clientId){
        String orderDetailQuery = "SELECT CLIENT.CLIENT_ID, PAYMENT_RECORD.PAYMENT_RECORD_ID,ORDERS.ORDER_ID, CLIENT.CLIENT_NAME, TOTAL_AMOUNT, PAYMENT_RECORD.STATUS AS PAYMENT_RECORD_STATUS, PROCESS_DATETIME FROM PAYMENT_RECORD \n" +
                "INNER JOIN ORDERS ON ORDERS.ORDER_ID = PAYMENT_RECORD.ORDER_ID\n" +
                "INNER JOIN CLIENT ON CLIENT.CLIENT_ID=ORDERS.CLIENT_ID\n" +
                "INNER JOIN PAYMENT_METHOD ON PAYMENT_METHOD.PAYMENT_METHOD_ID = ORDERS.PAYMENT_METHOD_ID\n" +
                "WHERE CLIENT.CLIENT_ID=?";
        return getJdbcTemplate()
                .query(orderDetailQuery, new Object[] { clientId }, new OrderDetailRowMapper());
    }
}
