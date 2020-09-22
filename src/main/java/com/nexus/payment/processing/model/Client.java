package com.nexus.payment.processing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Client JPA entity mapping
 */
@Entity
@Table(name = "CLIENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    private long client_Id;
    @Column(name = "CLIENT_NAME")
    private String clientName;
    @Column(name = "VIOLATOR")
    private boolean violator;

}
