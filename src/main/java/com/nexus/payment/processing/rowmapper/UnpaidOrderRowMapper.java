package com.nexus.payment.processing.rowmapper;

import com.nexus.payment.processing.model.UnpaidOrder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Row mapper to fetch UnpaidOrder using jdbctemplate.
 */
public class UnpaidOrderRowMapper implements RowMapper<UnpaidOrder> {

    @Override
    public UnpaidOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        UnpaidOrder unpaidOrder = new UnpaidOrder();
        unpaidOrder.setPaymentRecordId(rs.getLong("PAYMENT_RECORD_ID"));
        unpaidOrder.setTotalAmount(rs.getBigDecimal("TOTAL_AMOUNT"));
        unpaidOrder.setCreditCardNumber(rs.getString("ENCRYPTED_CREDIT_CARD"));
        unpaidOrder.setAuthCode(rs.getString("ENCRYPTED_AUTHCODE"));
        unpaidOrder.setClientId(rs.getLong("CLIENT_ID"));
        return unpaidOrder;

    }
}