package com.nexus.payment.processing.rowmapper;

import com.nexus.payment.processing.model.OrderDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/** Row mapper to fetch OrderDetails using jdbctemplate. */
public class OrderDetailRowMapper implements RowMapper<OrderDetails> {

  @Override
  public OrderDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
    OrderDetails OrderDetails = new OrderDetails();
    OrderDetails.setClientId(rs.getLong("CLIENT_ID"));
    OrderDetails.setPaymentRecordId(rs.getLong("PAYMENT_RECORD_ID"));
    OrderDetails.setOrderId(rs.getLong("ORDER_ID"));
    OrderDetails.setClientName(rs.getString("CLIENT_NAME"));
    OrderDetails.setTotalAmount(rs.getBigDecimal("TOTAL_AMOUNT"));
    OrderDetails.setPaymentRecordStatus(rs.getString("PAYMENT_RECORD_STATUS"));
    OrderDetails.setProcessDate(rs.getString("PROCESS_DATETIME"));
    return OrderDetails;
  }
}
