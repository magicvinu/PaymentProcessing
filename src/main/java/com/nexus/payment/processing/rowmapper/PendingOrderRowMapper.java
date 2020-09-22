package com.nexus.payment.processing.rowmapper;

import com.nexus.payment.processing.model.PendingOrder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Row mapper to fetch PendingOrder using jdbctemplate.
 */
public class PendingOrderRowMapper implements RowMapper<PendingOrder> {

    @Override
    public PendingOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        PendingOrder pendingOrder = new PendingOrder();
        pendingOrder.setOrderId(rs.getLong("ORDER_ID"));
        pendingOrder.setClientId(rs.getLong("ORDERS_CLIENT_ID"));
        pendingOrder.setPaymentRecordId(rs.getLong("PAYMENT_RECORD_ID"));
        pendingOrder.setPaymentMethodStatus(rs.getString("PAYMENT_METHOD_STATUS"));
        pendingOrder.setPaymentRecordStatus(rs.getString("PAYMENT_RECORD_STATUS"));
        pendingOrder.setPaymentRecordProcessDate(rs.getString("PAYMENT_RECORD_PROCESS_DATETIME"));
        return pendingOrder;

    }
}